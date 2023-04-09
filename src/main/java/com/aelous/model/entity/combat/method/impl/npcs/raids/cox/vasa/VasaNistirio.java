package com.aelous.model.entity.combat.method.impl.npcs.raids.cox.vasa;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.raids.cox.vasa.objects.Crystals;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.RouteMisc;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.*;

/**
 * @author Origin
 * @Since April 11th, 2022
 */
public class VasaNistirio extends CommonCombatMethod {

    /**
     * Crystal Variables
     */
    int id, x, y, z;
    private final List<Crystals> crystals = Collections.singletonList(Crystals.find(ObjectManager.get(id)));

    public List<Crystals> getCrystals() {
        return crystals;
    }

    /**
     * Player List
     */
    public List<Player> list = new ArrayList<>();

    /**
     * Randomizing the crystal for entity to route to
     *
     * @return
     */
    public Tile getRandomCrystal() {
        GameObject object = crystals.get(Utils.rand(crystals.size() - 1)).object;
        if (object == null)
            return null;
        return object.tile();
    }

    /**
     * Removal of the crystals
     */
    public void removeCrystalObjects() {
        target.getAsPlayer().getPacketSender().sendObjectRemoval(new GameObject(getCrystalState(true).size(), new Tile(x, y, z)));
    }

    /**
     * State of the crystals
     *
     * @param vulnerable
     * @return
     */
    public List<Crystals> getCrystalState(boolean vulnerable) {
        if (vulnerable) {
            removeCrystalObjects();
            Crystals.getActiveCrystals(new GameObject((GameObject) getCrystals(), new Tile(x, y, z)));
        } else {
            removeCrystalObjects();
            Crystals.getInactiveCrystals(new GameObject((GameObject) getCrystals(), new Tile(x, y, z)));
        }
        return crystals;
    }

    /**
     * Crystal vulernability
     */
    private void setCrystalState(Player player, boolean vulnerable) {
        if (!vulnerable) {
            player.getPacketSender().sendObject(new GameObject(getCrystalState(false).size(), new Tile(x, y, z)));
            player.message("The crystals are currently invulnerable to damage!");
        } else {
            player.getPacketSender().sendObject(new GameObject(getCrystalState(true).size(), new Tile(x, y, z)));
        }
    }

    /**
     * vasa vulernability state
     */
    private void setVasaState(boolean vulnerable) {
        if (!vulnerable) {
            entity.getAsNpc().attackNpcListener = (mob, target, message) -> {
                if (message)
                    entity.message("Vasa Nistirio is invulnerable to attacks while channeling power from the crystal!");
                return false;
            };
        } else {
            entity.getAsNpc().attackNpcListener = null;
        }
    }

    private void fallingRocks() {
        target.getAsNpc().getLocalPlayers().forEach(p -> {
            if (entity.getAsPlayer().dead() || RouteMisc.getEffectiveDistance(target.getAsNpc(), entity) >= 10
                || !ProjectileRoute.allow(target.getAsNpc(), entity))
                return;
        });
        final var tile = target.tile().copy();
        final var distance = target.tile().getChevDistance(entity.tile());
        Projectile projectile = new Projectile(entity.tile().transform(1, 1, 0), tile, 1, 1329, 75, 25, 90, 0, 16);
        int delay = (int) projectile.getHitDelay(distance);
        World.getWorld().tileGraphic(1330, tile, 0, delay);
        entity.startEvent(2, () -> {
            if (entity.getCentrePosition().isWithinDistance(target.getCentrePosition(), 1)) {
                if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES)) {
                    target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 0, CombatType.RANGED);
                    target.hit(entity, 8 / 2);
                } else {
                    if (target.tile().equals(tile)) {
                        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 0, CombatType.RANGED);
                        target.hit(entity, 8);
                    }
                }
            }
        });
    }

    private void channel() {
        entity.startEvent(0, () -> {
            if (entity.getAsNpc().dead() || target == null) {
                setVasaState(true);
                entity.getAsNpc().transmog(7566);
            }
        });
        entity.getAsNpc().lock();
        final Tile loc = getRandomCrystal();
        entity.getAsNpc().getRouteFinder().routeAbsolute(loc.getX(), loc.getY());
        while (!entity.getAsNpc().getMovement().isAtDestination()) {
            int ticks = -2;
            if (ticks++ % 3 == 0)
                fallingRocks();
        }
        target.startEvent(1, () -> {
        });
        entity.animate(7412);
        entity.getAsNpc().transmog(7567);
    }

    public List<Player> getList() {
        return list;
    }

    public void addPlayer(Player player) {
        if (player == null || list.contains(player))
            return;
        list.add(player);
    }

    private void rangeAttack(Entity entity, Player target) {
        Party party = target.raidsParty;

        if (party == null) {
            return;
        }

        if (target.dead() || RouteMisc.getEffectiveDistance(entity, target) >= 10 || !ProjectileRoute.allow(entity, target)) {
            return;
        }

        for (Player member : party.getMembers()) {
            if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && member.tile().inArea(new Area(3298, 5282, 3399, 5308, member.raidsParty.getHeight()))) {
                final var tile = target.tile().copy();
                new Projectile(entity.tile().transform(1, 1, 0), tile, 1, 1329, 125, 30, 175, 6, 0).sendProjectile();

                Chain.bound(null).runFn(6, () -> {
                    World.getWorld().tileGraphic(1330, tile, 5, 0);
                    if (target.tile().equals(tile)) {
                        if (Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES)) {
                            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 0, CombatType.RANGED);
                            target.hit(entity, 8 / 2);
                        } else {
                            if (target.tile().equals(tile)) {
                                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 0, CombatType.RANGED);
                                target.hit(entity, 8);
                            }
                        }
                    }
                });
            }
        }
    }

    private void awaken(NPC npc) {
        if (npc.getId() == 7565) {
            npc.startEvent(0, () -> npc.animate(7408), 5, () -> npc.transmog(7566));
        }
    }

    private void teleportAttack(Entity entity, Player target) {
        entity.lockNoDamage();
        awaken(entity.getAsNpc());
        target.startEvent(5, () -> {
                //List<Player> players = target.getLocalPlayers().stream().filter(p -> ProjectileRoute.allow(mob.getAsNpc(), p)).collect(Collectors.toList());
                entity.animate(7409);
                target.animate(3865);
                target.graphic(1296, GraphicHeight.HIGH, 0);
            },
            6, () -> {
                target.lock();
                target.resetAnimation();
                target.stun(4);
                Prayers.closeAllPrayers(target);
                target.teleport(entity.tile().x, entity.tile().y, entity.tile().level);

                final List<Tile> tiles = Arrays.asList(
                    new Tile(3308, 5293, entity.tile().level),
                    new Tile(3308, 5295, entity.tile().level),
                    new Tile(3308, 5298, entity.tile().level),
                    new Tile(3311, 5293, entity.tile().level),
                    new Tile(3311, 5295, entity.tile().level),
                    new Tile(3311, 5298, entity.tile().level),
                    new Tile(3313, 5292, entity.tile().level),
                    new Tile(3313, 5295, entity.tile().level),
                    new Tile(3313, 5297, entity.tile().level)
                );

                for (Tile tile : tiles) {
                    new Projectile(entity.tile().transform(1, 1, 0), tile, 1, 1327, 165, 30, 200, 6, 0).sendProjectile();

                    target.startEvent(3, () -> {
                        World.getWorld().tileGraphic(1328, tile, 5, 0);
                        for (Player p : World.getWorld().getPlayers()) {
                            if (p != null && p.tile().inSqRadius(entity.tile(), 5))
                                addPlayer(p);
                        }
                    });

                    Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy();

                    for (Player p : World.getWorld().getPlayers()) {
                        if (p != null && p.tile().inSqRadius(entity.tile(), 3)) {
                            p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.MAGIC), 0, CombatType.MAGIC).setDamage(p.hp() - magicAttackAlgorithm(p));
                        }
                    }

                    hit.setDamage(target.hp() - magicAttackAlgorithm(target));
                    hit.submit();
                    target.unlock();
                    entity.unlock();
                    getList().clear();
                }
            });
        entity.setPositionToFace(target.tile());
    }

    public int magicAttackAlgorithm(Player player) {
        int count = 0;
        if (getList().size() == 1) {
            count = player.hp() - 5;
            if (getList().size() == 2)
                count = player.hp() - 10 / 2;
            if (getList().size() == 3)
                count = player.hp() - 20 / 3;
            if (getList().size() == 4)
                count = player.hp() - 40 / 4;
        }
        return count;
    }


    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());

        if (!withinDistance(16)) {
            return false;
        }

        if (target != null) {
            if (entity.getAsNpc().getId() == 7565) {
                teleportAttack(entity, target.getAsPlayer());
            }

            if (entity.getAsNpc().getId() == 7566) {
                rangeAttack(entity, target.getAsPlayer());
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

}
