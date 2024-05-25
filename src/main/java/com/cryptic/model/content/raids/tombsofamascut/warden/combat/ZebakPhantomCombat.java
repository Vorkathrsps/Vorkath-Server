package com.cryptic.model.content.raids.tombsofamascut.warden.combat;

import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

public class ZebakPhantomCombat extends CommonCombatMethod {
    final Tile LOCATION_ONE = new Tile(3941, 5159);
    TombsInstance instance;
    @Override
    public void init(NPC npc) {
        this.instance = npc.getTombsInstance();
    }

    @Override
    public void doFollowLogic() {
        this.entity.setEntityInteraction(null);
        this.entity.face(null);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        NPC zebak = (NPC) entity;
        if (!this.instance.initiatedPhaseOne) return false;
        if (this.instance.isWardenDead) {
            zebak.remove();
            return false;
        }
        boolean usingMage = Utils.rollDie(2, 1);
        zebak.animate(9626);
        if (usingMage) magic(zebak);
        else range(zebak);
        return true;
    }

    void magic(NPC npc) {
        BooleanSupplier cancel = () -> this.instance.isWardenDead || !npc.isRegistered();
        Tile tile = new Tile(3941, 5155, npc.getInstancedArea().getzLevel() + 1).center(npc.getSize() / 2);
        Tile hiddenTile = LOCATION_ONE.transform(0, 0, npc.getInstancedArea().getzLevel() + 1);
        double distance = tile.distanceTo(hiddenTile);
        int duration = (int) (30 + (90 + distance));
        Projectile projectileOne = new Projectile(tile, hiddenTile, 2176, 30, duration, 50, 125, 20, npc.getSize(), 90, 0);
        projectileOne.send(tile, hiddenTile);
        final int delay = getDelay(projectileOne);
        npc.getTombsInstance().getHiddenZebak().setGraphic(new Graphic(2186, GraphicHeight.HIGH_9, delay));
        Chain.noCtx().cancelWhen(cancel).runFn((int) (delay / 30D) + 1, () -> {
            for (var player : this.instance.getPlayers()) {
                if (player == null || !player.isRegistered() || player.dead()) continue;
                Projectile projectileTwo = new Projectile(hiddenTile, player, 2181, 0, duration, projectileOne.getEndHeight(), 22, 127, npc.getSize(), 90, 0);
                projectileTwo.sendProjectile();
                new Hit(npc, target, Utils.random(10, 15), (int) (projectileTwo.getSpeed() / 30D) + 1, CombatType.MAGIC).setHitMark(HitMark.HIT).checkAccuracy(false).submit();
                player.graphic(131, GraphicHeight.HIGH, projectileTwo.getSpeed());
            }
        });
    }

    void range(NPC npc) {
        BooleanSupplier cancel = () -> this.instance.isWardenDead || !npc.isRegistered();
        Tile tile = new Tile(3941, 5155, npc.getInstancedArea().getzLevel() + 1).center(npc.getSize() / 2);
        Tile hiddenTile = LOCATION_ONE.transform(0, 0, npc.getInstancedArea().getzLevel() + 1);
        double distance = tile.distanceTo(hiddenTile);
        int duration = (int) (30 + (90 + distance));
        Projectile projectileOne = new Projectile(tile, hiddenTile, 2178, 30, duration, 50, 125, 20, npc.getSize(), 90, 0);
        projectileOne.send(tile, hiddenTile);
        final int delay = getDelay(projectileOne);
        npc.getTombsInstance().getHiddenZebak().setGraphic(new Graphic(2185, GraphicHeight.HIGH_9, delay));
        Chain.noCtx().cancelWhen(cancel).runFn((int) (delay / 30D) + 1, () -> {
            for (var player : this.instance.getPlayers()) {
                if (player == null || !player.isRegistered() || player.dead()) continue;
                Projectile projectileTwo = new Projectile(hiddenTile, player, 2187, 0, duration, projectileOne.getEndHeight(), 22, 127, npc.getSize(), 90, 0);
                projectileTwo.sendProjectile();
                new Hit(npc, target, Utils.random(10, 15), (int) (projectileTwo.getSpeed() / 30D) + 1, CombatType.RANGED).setHitMark(HitMark.HIT).checkAccuracy(false).submit();
                player.graphic(1103, GraphicHeight.HIGH, projectileTwo.getSpeed());
            }
        });
    }

    int getDelay(Projectile projectile) {
        return projectile.getSpeed();
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 7;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
