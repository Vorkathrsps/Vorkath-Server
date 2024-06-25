package com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.instance.TheDukeInstance;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.ItemDrops;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.chainedwork.Chain;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BooleanSupplier;

public class DukeCombat extends CommonCombatMethod {

    Map<Player, Integer> damageMap = new HashMap<>();
    boolean isGasSent = false;
    public static final int
        ATTACK_ANIMATION = 10178,
        SLAM_ANIMATION = 10176,
        SLAM_GRAPHIC = 2439,
        DEATH_ANIMATION = 10181,
        DEATH_TRANSFORMATION = 12192;

    @Override
    public void init(NPC npc) {
        npc.putAttrib(AttributeKey.SLEEPING, true);
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
        npc.getCombatInfo().aggroradius = 50;
        Chain
            .noCtx()
            .runFn(15, () -> {
                npc.transmog(12166, true);
                npc.animate(10179);
            })
            .then(1, () -> npc.transmog(12191, true))
            .then(1, () -> {
                npc.clearAttrib(AttributeKey.SLEEPING);
                npc.getCombatInfo().setAggressive(true);
            });
    }

    @Override
    public void process(Entity entity, Entity target) {
        double healthAmount = this.entity.hp() * 1.0 / (this.entity.maxHp() * 1.0);
        if (healthAmount <= 0.30 && !isEnraged())
            this.entity.putAttrib(AttributeKey.BARON_ENRAGED, true);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.hasAttrib(AttributeKey.SLEEPING)) return true;
        final TheDukeInstance instance = this.target.getDukeInstance();
        if (instance != null) {
            int count = instance.getAttackCount();
            if (isSendMagic(instance)) {
                sendMagic(instance);
                return true;
            }
            if (count == 5) instance.setAttackCount(0);
            if (count == 0 || count == 1 || count == 2 || count == 3) sendSlam(instance);
            if (count == 4) {
                if (!isEnraged()) {
                    sendGas(instance);
                    instance.setAttackCount(instance.getAttackCount() + 1);
                    return false;
                } else {
                    if (isSendDoubleGas(instance)) return true;
                    else sendSlam(instance);
                }
            }
            instance.setAttackCount(instance.getAttackCount() + 1);
        }
        return true;
    }

    private void sendMagic(final TheDukeInstance instance) {
        final NPC duke = (NPC) this.entity;
        final Tile tile = instance.getOwner().getCentrePosition();
        final Tile entityCenterPosition = duke.getCentrePosition();
        final Tile transform = entityCenterPosition.transform(3, 3);
        final int size = duke.getSize();

        final int tileDist = transform.getChevDistance(tile);
        int duration = 20 + 70 + (tileDist * 2);
        duration -= 2;

        duke.animate(ATTACK_ANIMATION);
        final Projectile projectile = new Projectile(entityCenterPosition, target, 2434, 20, duration, 87, 25, 10, size, 32, 2);
        final int delay = duke.executeProjectile(projectile);

        new Hit(duke, target, delay, CombatType.MAGIC)
            .setAccurate(true)
            .setDamage(World.getWorld().random(1, 60))
            .setHitMark(HitMark.HIT)
            .submit()
            .postDamage(hit -> {
                hit.setAccurate(true);
                hit.setDamage(World.getWorld().random(1, 60));
            });
    }

    private boolean isSendMagic(TheDukeInstance instance) {
        return ProjectileRoute.hasLineOfSight(entity, target) && instance.getOwner().getY() <= this.entity.getY() - 3;
    }

    private boolean isSendDoubleGas(final TheDukeInstance instance) {
        final int gasCount = instance.getGasCount();
        if (gasCount >= 2) instance.setTwoSent(true);
        if (this.isEnraged() && !instance.isTwoSent()) {
            this.sendGas(instance);
            instance.setGasCount(instance.getGasCount() + 1);
            return true;
        } else if (instance.isTwoSent()) {
            instance.setTwoSent(false);
            instance.setGasCount(0);
            instance.setAttackCount(0);
            instance.setAttackCount(instance.getAttackCount() + 1);
            this.sendSlam(instance);
            return true;
        }
        return false;
    }

    final void sendGas(final TheDukeInstance instance) {
        final NPC duke = (NPC) this.entity;
        final Player owner = instance.getOwner();
        final int index = instance.getCurrentTileIndex();
        final Tile[] tiles = instance.getTiles();
        final Tile tile = tiles[index];

        duke.animate(ATTACK_ANIMATION);
        final Projectile projectile = buildToxicGasProjectile(tile);
        final NPC fume = this.isEnraged() ? this.getNpc(instance) : this.getNpcNonEnraged(instance);
        this.checkToxicGasDamage(instance, projectile, owner);
        if (index == tiles.length - 1) {
            instance.setIteratingForward(false);
        } else if (index == 0) {
            instance.setIteratingForward(true);
        }

        this.isGasSent = true;
        instance.setCurrentTileIndex(instance.isIteratingForward() ? index + 1 : index - 1);
    }

    final void sendSlam(final TheDukeInstance instance) {
        final Set<Tile> tiles = this.buildSlamTiles();
        final NPC duke = (NPC) this.entity;

        duke.animate(SLAM_ANIMATION);
        duke.graphic(SLAM_GRAPHIC);

        int finalIndex = 0;
        for (Tile tile : tiles) {
            int[] slamGraphics = instance.getSlamGraphics();
            finalIndex %= slamGraphics.length;
            World.getWorld().sendUnclippedTileGraphic(slamGraphics[finalIndex], tile, 0, 0);
            finalIndex++;
            sendHit(tile);
        }
    }

    @NotNull
    private Projectile buildToxicGasProjectile(final Tile tile) {
        final NPC duke = (NPC) this.entity;
        final Tile centerPosition = duke.getCentrePosition();
        final Tile transform = centerPosition.transform(3, 3);
        final Tile targetTileTransform = tile.transform(0, 0, duke.getZ());
        final int size = duke.getSize();

        final int tileDist = transform.getChevDistance(tile);
        int duration = 20 + 70 + (tileDist * 2);
        duration -= 2;

        Projectile p = new Projectile(centerPosition, targetTileTransform, 2436, 20, duration, 87, 0, 5, size, 32, 2);
        p.send(duke, tile);
        return p;
    }

    @NotNull
    private NPC getNpcNonEnraged(final TheDukeInstance instance) {
        final int index = instance.getCurrentTileIndex();
        final NPC fume = instance.getFumes()[index];
        int[] graphics = instance.getGasGraphics();
        NPC fumeOffset = getFumeOffset(instance, fume, graphics);
        if (fumeOffset != null) return fumeOffset;
        this.setGraphics(fume, List.of(
            new Graphic(graphics[0], GraphicHeight.LOW, 120),
            new Graphic(graphics[1], GraphicHeight.LOW, 180),
            new Graphic(graphics[2], GraphicHeight.LOW, 180),
            new Graphic(graphics[1], GraphicHeight.LOW, 239),
            new Graphic(graphics[2], GraphicHeight.LOW, 239),
            new Graphic(graphics[1], GraphicHeight.LOW, 299),
            new Graphic(graphics[2], GraphicHeight.LOW, 299),
            new Graphic(graphics[1], GraphicHeight.LOW, 357),
            new Graphic(graphics[2], GraphicHeight.LOW, 357),
            new Graphic(graphics[1], GraphicHeight.LOW, 415),
            new Graphic(graphics[2], GraphicHeight.LOW, 415),
            new Graphic(graphics[1], GraphicHeight.LOW, 473),
            new Graphic(graphics[2], GraphicHeight.LOW, 473),
            new Graphic(graphics[1], GraphicHeight.LOW, 531)));
        return fume;
    }

    void setGraphics(NPC fume, List<Graphic> graphics) {
        fume.setGraphics(graphics);
    }

    @NotNull
    private NPC getNpc(final TheDukeInstance instance) {
        final int index = instance.getCurrentTileIndex();
        final NPC fume = instance.getFumes()[index];
        int[] graphics = instance.getGasGraphics();
        NPC fumeOffset = getFumeOffset(instance, fume, graphics);
        if (fumeOffset != null) return fumeOffset;
        this.setGraphics(fume, List.of(
            new Graphic(graphics[0], GraphicHeight.LOW, 120),
            new Graphic(graphics[1], GraphicHeight.LOW, 180),
            new Graphic(graphics[2], GraphicHeight.LOW, 180),
            new Graphic(graphics[1], GraphicHeight.LOW, 239),
            new Graphic(graphics[2], GraphicHeight.LOW, 239),
            new Graphic(graphics[1], GraphicHeight.LOW, 299),
            new Graphic(graphics[2], GraphicHeight.LOW, 299),
            new Graphic(graphics[1], GraphicHeight.LOW, 357),
            new Graphic(graphics[2], GraphicHeight.LOW, 357),
            new Graphic(graphics[1], GraphicHeight.LOW, 415),
            new Graphic(graphics[2], GraphicHeight.LOW, 415),
            new Graphic(graphics[1], GraphicHeight.LOW, 473),
            new Graphic(graphics[2], GraphicHeight.LOW, 473),
            new Graphic(graphics[1], GraphicHeight.LOW, 531),
            new Graphic(graphics[2], GraphicHeight.LOW, 531),
            new Graphic(graphics[2], GraphicHeight.LOW, 589)));
        return fume;
    }

    @Nullable
    private NPC getFumeOffset(final TheDukeInstance instance, NPC fume, int[] graphics) {
        if (instance.getGasCount() == 0 && isEnraged()) {
            this.setGraphics(fume, List.of(
                new Graphic(graphics[0], GraphicHeight.LOW, 120),
                new Graphic(graphics[1], GraphicHeight.LOW, 180),
                new Graphic(graphics[2], GraphicHeight.LOW, 180),
                new Graphic(graphics[1], GraphicHeight.LOW, 239),
                new Graphic(graphics[2], GraphicHeight.LOW, 239),
                new Graphic(graphics[1], GraphicHeight.LOW, 299),
                new Graphic(graphics[2], GraphicHeight.LOW, 299),
                new Graphic(graphics[1], GraphicHeight.LOW, 357),
                new Graphic(graphics[2], GraphicHeight.LOW, 357),
                new Graphic(graphics[1], GraphicHeight.LOW, 415),
                new Graphic(graphics[2], GraphicHeight.LOW, 415),
                new Graphic(graphics[1], GraphicHeight.LOW, 473),
                new Graphic(graphics[2], GraphicHeight.LOW, 473),
                new Graphic(graphics[1], GraphicHeight.LOW, 531),
                new Graphic(graphics[2], GraphicHeight.LOW, 531),
                new Graphic(graphics[1], GraphicHeight.LOW, 589),
                new Graphic(graphics[2], GraphicHeight.LOW, 589),
                new Graphic(graphics[1], GraphicHeight.LOW, 647),
                new Graphic(graphics[2], GraphicHeight.LOW, 647),
                new Graphic(graphics[2], GraphicHeight.LOW, 705)));
            return fume;
        }
        return null;
    }

    private void checkToxicGasDamage(final TheDukeInstance instance, Projectile p, final Player owner) {
        final NPC duke = (NPC) this.entity;
        Chain.noCtx().runFn((int) (p.getSpeed() / 30D), () -> {
            final int[] tick = new int[1];
            final Tile realTile = duke.tile().transform(1, 1);
            Chain.noCtx().repeatingTask(1, task -> {
                if (tick[0] >= this.getToxicGasTick(instance)) {
                    task.stop();
                    return;
                }
                if (owner.tile().inSqRadius(realTile, 1)) {
                    new Hit(duke, owner, CombatFactory.calcDamageFromType(duke, owner, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy(false).submit();
                }
                tick[0]++;
            });
        });
    }

    final void sendHit(final Tile tile) {
        BooleanSupplier cancel = () -> !target.tile().equals(tile);
        Chain.noCtx().cancelWhen(cancel).runFn(2, () -> {
            if (target.tile().equals(tile)) {
                new Hit(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(false).submit();
            }
        });
    }

    final Set<Tile> buildSlamTiles() {
        final Set<Tile> temp = new HashSet<>();
        final Tile southwest = Tile.getSouthwestTile(this.entity);
        for (int y = 1; y <= 2; y++) {
            for (int x = 1; x <= 7; x++) {
                if (y != 2) continue;
                temp.add(southwest.transform(x, -y));
            }
        }
        return temp;
    }

    private int getToxicGasTick(final TheDukeInstance instance) {
        return isEnraged() && instance.getGasCount() == 0 ? 18 : isEnraged() && instance.getGasCount() == 1 ? 10 : 12;
    }

    private boolean isEnraged() {
        final NPC duke = (NPC) this.entity;
        return duke.hasAttrib(AttributeKey.BARON_ENRAGED);
    }

    final void computeDropTable(NPC npc, ItemDrops drops) {
        for (var entry : this.damageMap.entrySet()) {
            final Player player = entry.getKey();
            final int damage = entry.getValue();
            if (player == null || damage < 100) continue;
            drops.rollTheDropTable(player, npc);
        }
    }

    final void incrementDamageMap(Hit hit, Entity source, Entity target) {
        if (source instanceof Player player && target instanceof NPC) {
            if (!this.damageMap.containsKey(player)) this.damageMap.put(player, hit.getDamage());
            else this.damageMap.computeIfPresent(player, (_, v) -> v + hit.getDamage());
        }
    }

    @Override
    public void postDamage(Hit hit) {
        final Entity target = hit.getTarget();
        final Entity source = hit.getSource();
        this.incrementDamageMap(hit, source, target);
    }

    @Override
    public void onDeath(Player killer, NPC npc) {

    }

    @Override
    public boolean customOnDeath(Hit hit) {
        final ItemDrops drops = new ItemDrops();
        final NPC duke = (NPC) entity;

        duke.animate(DEATH_ANIMATION);
        Chain.noCtx().runFn(4, () -> {
            this.computeDropTable(duke, drops);
            duke.transmog(DEATH_TRANSFORMATION, false);
            this.damageMap.clear();
        });
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.hasAttrib(AttributeKey.BARON_ENRAGED) ? 4 : 5;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 64;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public void doFollowLogic() {
        final NPC duke = (NPC) this.entity;
        duke.setEntityInteraction(null);
        duke.face(null);
    }
}
