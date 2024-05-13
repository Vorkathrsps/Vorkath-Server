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
    boolean isGasSent = false;
    Map<Player, Integer> damageMap = new HashMap<>();
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
        final Tile tile = instance.getOwner().getCentrePosition();
        this.entity.animate(10178);
        var tileDist = entity.getCentrePosition().transform(3, 3).getChevDistance(tile);
        var duration = 20 + 70 + (tileDist * 2);
        duration -= 2;
        Projectile p = new Projectile(entity.getCentrePosition(), target, 2434, 20, duration, 87, 25, 10, this.entity.getSize(), 32, 2);
        final int delay = this.entity.executeProjectile(p);
        new Hit(entity, target, delay + 1, CombatType.MAGIC)
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
        int gasCount = instance.getGasCount();
        if (gasCount >= 2) instance.setTwoSent(true);
        if (isEnraged() && !instance.isTwoSent()) {
            sendGas(instance);
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
        int index = instance.getCurrentTileIndex();
        Tile tile = instance.getTiles()[index];
        final Player owner = instance.getOwner();
        entity.animate(10178);
        Projectile projectile = buildToxicGasProjectile(tile);
        NPC fume = isEnraged() ? getNpc(instance) : getNpcNonEnraged(instance);
        checkToxicGasDamage(instance, projectile, fume, owner);
        if (index == instance.getTiles().length - 1) {
            instance.setIteratingForward(false);
        } else if (index == 0) {
            instance.setIteratingForward(true);
        }
        isGasSent = true;
        instance.setCurrentTileIndex(instance.isIteratingForward() ? index + 1 : index - 1);
    }

    final void sendSlam(final TheDukeInstance instance) {
        int finalIndex = 0;
        final Set<Tile> tiles = this.buildSlamTiles();
        entity.animate(10176);
        entity.graphic(2439);
        for (Tile tile : tiles) {
            finalIndex %= instance.getSlamGraphics().length;
            World.getWorld().sendUnclippedTileGraphic(instance.getSlamGraphics()[finalIndex], tile, 0, 0);
            finalIndex++;
            sendHit(tile);
        }
    }

    @NotNull
    private Projectile buildToxicGasProjectile(final Tile tile) {
        var tileDist = entity.getCentrePosition().transform(3, 3).getChevDistance(tile);
        var duration = 20 + 70 + (tileDist * 2);
        duration -= 2;
        Projectile p = new Projectile(entity.getCentrePosition(), tile.transform(0, 0, this.entity.getZ()), 2436, 20, duration, 87, 0, 5, this.entity.getSize(), 32, 2);
        p.send(entity, tile);
        return p;
    }

    @NotNull
    private NPC getNpcNonEnraged(final TheDukeInstance instance) {
        final int index = instance.getCurrentTileIndex();
        final NPC fume = instance.getFumes()[index];
        int[] graphics = instance.getGasGraphics();
        NPC fumeOffset = getFumeOffset(instance, fume, graphics);
        if (fumeOffset != null) return fumeOffset;
        fume.setGraphics(List.of(
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
            new Graphic(graphics[1], GraphicHeight.LOW, 531))
        );
        return fume;
    }

    @NotNull
    private NPC getNpc(final TheDukeInstance instance) {
        final int index = instance.getCurrentTileIndex();
        final NPC fume = instance.getFumes()[index];
        int[] graphics = instance.getGasGraphics();
        NPC fumeOffset = getFumeOffset(instance, fume, graphics);
        if (fumeOffset != null) return fumeOffset;
        fume.setGraphics(List.of(
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
            new Graphic(graphics[2], GraphicHeight.LOW, 589))
        );
        return fume;
    }

    @Nullable
    private NPC getFumeOffset(final TheDukeInstance instance, NPC fume, int[] graphics) {
        if (instance.getGasCount() == 0 && isEnraged()) {
            fume.setGraphics(List.of(
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
                new Graphic(graphics[2], GraphicHeight.LOW, 705))
            );
            return fume;
        }
        return null;
    }

    private void checkToxicGasDamage(final TheDukeInstance instance, Projectile p, NPC npc, final Player owner) {
        Chain.noCtx().runFn((int) (p.getSpeed() / 30D), () -> {
            final int[] tick = new int[1];
            final Tile realTile = npc.tile().transform(1, 1);
            Chain.noCtx().repeatingTask(1, task -> {
                if (tick[0] >= getToxicGasTick(instance)) {
                    task.stop();
                    return;
                }
                if (owner.tile().inSqRadius(realTile, 1)) {
                    new Hit(entity, owner, CombatFactory.calcDamageFromType(entity, owner, CombatType.MAGIC), 0, CombatType.MAGIC).checkAccuracy(false).submit();
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
        Set<Tile> temp = new HashSet<>();
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
        return this.entity.hasAttrib(AttributeKey.BARON_ENRAGED);
    }

    final void computeDropTable(NPC npc, ItemDrops drops) {
        for (var entry : damageMap.entrySet()) {
            var player = entry.getKey();
            var damage = entry.getValue();
            if (player == null || damage < 100) continue;
            drops.rollTheDropTable(player, npc);
        }
    }

    final void incrementDamageMap(Hit hit, Entity source, Entity target) {
        if (source instanceof Player player && target instanceof NPC) {
            if (!damageMap.containsKey(player)) damageMap.put(player, hit.getDamage());
            else damageMap.computeIfPresent(player, (_, v) -> v + hit.getDamage());
        }
    }

    @Override
    public void postDamage(Hit hit) {
        var target = hit.getTarget();
        var source = hit.getSource();
        incrementDamageMap(hit, source, target);
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        NPC npc = (NPC) entity;
        npc.animate(10181);
        final ItemDrops drops = new ItemDrops();
        Chain.noCtx().runFn(4, () -> {
            computeDropTable(npc, drops);
            npc.transmog(12192, false);
            damageMap.clear();
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
        this.entity.setEntityInteraction(null);
        this.entity.face(null);
    }
}
