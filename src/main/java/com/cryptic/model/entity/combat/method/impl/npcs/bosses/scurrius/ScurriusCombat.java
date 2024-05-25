package com.cryptic.model.entity.combat.method.impl.npcs.bosses.scurrius;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BooleanSupplier;

public class ScurriusCombat extends CommonCombatMethod {
    @Setter HealState state = HealState.NONE;
    static final Area area = new Area(3291, 9860, 3307, 9875);
    @Override
    public void init(NPC npc) {
        npc.getCombatInfo().aggroradius = 50;
        npc.putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 50);
        npc.ignoreOccupiedTiles = true;
        npc.useSmartPath = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var random = Utils.getRandom(8);
        switch (random) {
            case 0, 1 -> rockFall(entity);
            case 2, 3 -> range(entity);
            case 4, 5 -> magic(entity);
            case 6, 7 -> {
                if (withinDistance(1)) melee(entity);
            }
        }
        return true;
    }

    void range(Entity entity) {
        entity.animate(10694);
        for (var target : this.getPossibleTargets(entity)) {
            if (target == null) continue;
            entity.face(target);
            var tile = entity.tile().transform(3, 3, 0);
            var tileDist = tile.distance(target.tile());
            var duration = 30 + 90 + tileDist;
            Projectile p = new Projectile(entity, target, 2642, 30, duration, 50, 50, 25, 40, entity.getSize(), 0);
            final int delay = (int) (p.getSpeed() / 30D);
            entity.executeProjectile(p);
            target.graphic(2643, GraphicHeight.HIGH, p.getSpeed());
            new Hit(entity, target, delay, CombatType.RANGED).checkAccuracy(true).submit();
        }
    }

    void magic(Entity entity) {
        entity.animate(10696);
        entity.graphic(2638);
        for (var target : this.getPossibleTargets(entity)) {
            if (target == null) continue;
            entity.face(target);
            var tile = entity.tile().transform(3, 3, 0);
            var tileDist = tile.distance(target.tile());
            var duration = 30 + 90 + tileDist;
            Projectile p = new Projectile(entity, target, 2640, 30, duration, 50, 50, 25, 40, entity.getSize(), 0);
            final int delay = (int) (p.getSpeed() / 30D);
            entity.executeProjectile(p);
            target.graphic(2641, GraphicHeight.HIGH, p.getSpeed());
            new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        }
    }

    void melee(Entity entity) {
        if (!withinDistance(1)) return;
        Entity target = Utils.randomElement(this.getPossibleTargets(entity));
        entity.face(target);
        entity.animate(10693);
        new Hit(entity, target, 0, CombatType.MELEE).checkAccuracy(true).submit();
    }

    void rockFall(Entity entity) {
        Set<Tile> tiles = new HashSet<>();
        for (var target : this.getPossibleTargets(entity)) {
            if (target == null) continue;
            tiles.add(target.tile());
        }
        BooleanSupplier cancelIf = entity::dead;
        entity.animate(10698);
        while (tiles.size() < 25) {
            var t = area.randomTile();
            if (t == null) continue;
            if (!t.inArea(area)) continue;
            if (World.getWorld().clipAt(t.x, t.y, t.level) == 0) tiles.add(t);
        }
        for (var t : tiles) {
            if (t == null) continue;
            World.getWorld().sendClippedTileGraphic(2644, t, 0, 60);
            Chain.noCtx().cancelWhen(cancelIf).runFn(9, () -> {
                for (var target : this.getPossibleTargets(entity)) {
                    if (target == null) continue;
                    if (target.tile().equals(t)) {
                        new Hit(entity, target, 0, CombatType.TYPELESS).checkAccuracy(false).setAccurate(true).setDamage(Utils.random(5, 13)).setHitMark(HitMark.HIT).submit();
                    }
                }
            });
        }
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    public enum HealState {
        NONE,
        ONE,
        TWO,
        THREE
    }
}
