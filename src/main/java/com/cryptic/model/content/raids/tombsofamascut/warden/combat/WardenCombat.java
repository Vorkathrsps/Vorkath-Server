package com.cryptic.model.content.raids.tombsofamascut.warden.combat;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.tombsofamascut.TombsInstance;
import com.cryptic.model.content.raids.tombsofamascut.warden.FloorSection;
import com.cryptic.model.content.raids.tombsofamascut.warden.phase.WardenPhase;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BooleanSupplier;

public class WardenCombat extends CommonCombatMethod {
    public static final GameObject[][] floorObjects = {
        {new GameObject(45730, 22, 1, new Tile(3933, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3930, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3928, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3936, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3939, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3941, 5165, 1))},
        {new GameObject(45730, 22, 1, new Tile(3935, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3931, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3929, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3937, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3944, 5165, 1)), new GameObject(45732, 22, 1, new Tile(3946, 5165, 1))},
        {new GameObject(45731, 22, 1, new Tile(3926, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3932, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3943, 5165, 1))},
        {new GameObject(45730, 22, 1, new Tile(3927, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3934, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3938, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3940, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3942, 5165, 1)), new GameObject(45730, 22, 1, new Tile(3945, 5165, 1))},
        {new GameObject(45726, 22, 3, new Tile(3927, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3927, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3933, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3933, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3930, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3930, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3936, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3936, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3941, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3941, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3945, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3945, 5165, 1))},
        {new GameObject(45726, 22, 3, new Tile(3934, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3934, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3931, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3931, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3928, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3928, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3943, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3943, 5165, 1))},
        {new GameObject(45726, 22, 3, new Tile(3935, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3935, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3929, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3929, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3937, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3937, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3939, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3939, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3942, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3942, 5165, 1)), new GameObject(45736, 22, 3, new Tile(3946, 5164, 1)), new GameObject(45737, 22, 1, new Tile(3946, 5165, 1))},
        {new GameObject(45736, 22, 0, new Tile(3926, 5164, 1)), new GameObject(45735, 22, 1, new Tile(3926, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3932, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3932, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3938, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3938, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3940, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3940, 5165, 1)), new GameObject(45726, 22, 3, new Tile(3944, 5164, 1)), new GameObject(45727, 22, 1, new Tile(3944, 5165, 1))},
        {new GameObject(45726, 22, 3, new Tile(3927, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3927, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3934, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3931, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3936, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3942, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3942, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3944, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3944, 5164, 1)), new GameObject(45736, 22, 3, new Tile(3946, 5163, 1)), new GameObject(45726, 22, 2, new Tile(3946, 5164, 1))},
        {new GameObject(45736, 22, 0, new Tile(3926, 5163, 1)), new GameObject(45726, 22, 0, new Tile(3926, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3935, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3935, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3932, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3939, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3941, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3941, 5164, 1)), new GameObject(45726, 22, 3, new Tile(3943, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3943, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3945, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3945, 5164, 1))},
        {new GameObject(45728, 22, 0, new Tile(3933, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3933, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3929, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3929, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3938, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3940, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3940, 5164, 1))},
        {new GameObject(45728, 22, 0, new Tile(3930, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3930, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3928, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3928, 5164, 1)), new GameObject(45728, 22, 0, new Tile(3937, 5163, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5164, 1))},
        {new GameObject(45736, 22, 0, new Tile(3927, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3927, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3933, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3933, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3936, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3941, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3941, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3944, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3944, 5163, 1))},
        {new GameObject(45728, 22, 0, new Tile(3934, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3928, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3928, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3940, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3940, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3943, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3943, 5163, 1))},
        {new GameObject(45726, 22, 3, new Tile(3935, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3935, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3931, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3929, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3929, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3938, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3942, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3942, 5163, 1)), new GameObject(45736, 22, 3, new Tile(3945, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3945, 5163, 1))},
        {new GameObject(45728, 22, 0, new Tile(3932, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5163, 1)), new GameObject(45726, 22, 3, new Tile(3930, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3930, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3937, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5163, 1)), new GameObject(45728, 22, 0, new Tile(3939, 5162, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5163, 1))},
        {new GameObject(45726, 22, 3, new Tile(3933, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3933, 5162, 1)), new GameObject(45726, 22, 3, new Tile(3930, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3930, 5162, 1)), new GameObject(45726, 22, 3, new Tile(3936, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5162, 1)), new GameObject(45726, 22, 3, new Tile(3939, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5162, 1))},
        {new GameObject(45728, 22, 0, new Tile(3934, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3931, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3938, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3941, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3941, 5162, 1)), new GameObject(45736, 22, 3, new Tile(3944, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3944, 5162, 1))},
        {new GameObject(45728, 22, 0, new Tile(3935, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3935, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3932, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5162, 1)), new GameObject(45736, 22, 0, new Tile(3928, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3928, 5162, 1)), new GameObject(45726, 22, 3, new Tile(3940, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3940, 5162, 1)), new GameObject(45726, 22, 3, new Tile(3943, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3943, 5162, 1))},
        {new GameObject(45728, 22, 0, new Tile(3929, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3929, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3937, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5162, 1)), new GameObject(45728, 22, 0, new Tile(3942, 5161, 1)), new GameObject(45738, 22, 0, new Tile(3942, 5162, 1))},
        {new GameObject(45728, 22, 0, new Tile(3935, 5160, 1)), new GameObject(45738, 22, 3, new Tile(3935, 5161, 1)), new GameObject(45728, 22, 0, new Tile(3936, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5161, 1)), new GameObject(45726, 22, 3, new Tile(3938, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5161, 1)), new GameObject(45728, 22, 0, new Tile(3940, 5160, 1)), new GameObject(45738, 22, 1, new Tile(3940, 5161, 1))},
        {new GameObject(45728, 22, 0, new Tile(3932, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5161, 1)), new GameObject(45728, 22, 0, new Tile(3930, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3930, 5161, 1)), new GameObject(45728, 22, 0, new Tile(3937, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5161, 1)), new GameObject(45728, 22, 0, new Tile(3939, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5161, 1)), new GameObject(45736, 22, 3, new Tile(3943, 5160, 1)), new GameObject(45738, 22, 2, new Tile(3943, 5161, 1))},
        {new GameObject(45728, 22, 0, new Tile(3933, 5160, 1)), new GameObject(45738, 22, 2, new Tile(3933, 5161, 1)), new GameObject(45726, 22, 3, new Tile(3931, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5161, 1)), new GameObject(45736, 22, 0, new Tile(3929, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3929, 5161, 1)), new GameObject(45726, 22, 3, new Tile(3942, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3942, 5161, 1))},
        {new GameObject(45726, 22, 3, new Tile(3934, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5161, 1)), new GameObject(45726, 22, 3, new Tile(3941, 5160, 1)), new GameObject(45738, 22, 2, new Tile(3941, 5161, 1))},
        {new GameObject(45728, 22, 0, new Tile(3935, 5159, 1)), new GameObject(45726, 22, 3, new Tile(3933, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3935, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3933, 5160, 1)), new GameObject(45728, 22, 0, new Tile(3936, 5159, 1)), new GameObject(45736, 22, 3, new Tile(3942, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3942, 5160, 1))},
        {new GameObject(45728, 22, 0, new Tile(3932, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5160, 1)), new GameObject(45726, 22, 3, new Tile(3937, 5159, 1)), new GameObject(45726, 22, 3, new Tile(3940, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3940, 5160, 1))},
        {new GameObject(45726, 22, 3, new Tile(3931, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5160, 1)), new GameObject(45728, 22, 0, new Tile(3939, 5159, 1)), new GameObject(45728, 22, 0, new Tile(3941, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3941, 5160, 1))},
        {new GameObject(45728, 22, 0, new Tile(3934, 5159, 1)), new GameObject(45736, 22, 0, new Tile(3930, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5160, 1)), new GameObject(45738, 22, 0, new Tile(3930, 5160, 1)), new GameObject(45728, 22, 0, new Tile(3938, 5159, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5160, 1))},
        {new GameObject(45728, 22, 0, new Tile(3932, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3932, 5159, 1)), new GameObject(45728, 22, 0, new Tile(3936, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3936, 5159, 1)), new GameObject(45726, 22, 3, new Tile(3938, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3938, 5159, 1))},
        {new GameObject(45726, 22, 3, new Tile(3935, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3935, 5159, 1)), new GameObject(45726, 22, 3, new Tile(3933, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3933, 5159, 1)), new GameObject(45728, 22, 0, new Tile(3937, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3937, 5159, 1))},
        {new GameObject(45734, 22, 0, new Tile(3931, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3931, 5159, 1)), new GameObject(45726, 22, 3, new Tile(3940, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3940, 5159, 1))},
        {new GameObject(45728, 22, 0, new Tile(3934, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3934, 5159, 1)), new GameObject(45728, 22, 0, new Tile(3939, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3939, 5159, 1)), new GameObject(45736, 22, 3, new Tile(3941, 5158, 1)), new GameObject(45738, 22, 0, new Tile(3941, 5159, 1))},
    };
    WardenPhase phase = WardenPhase.ONE;
    boolean initiatePhaseOne = false;
    boolean initiatePhaseTwo = false;
    FloorSection[] values = new FloorSection[]{FloorSection.LEFT, FloorSection.RIGHT};
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
        NPC warden = (NPC) entity;
        FloorSection random = Utils.randomElement(values);
        if (this.phase.equals(WardenPhase.ONE) && !this.initiatePhaseOne) {
            this.initiatePhaseOne = true;
            this.floorAttack(warden, random);
        } else if (this.phase.equals(WardenPhase.TWO) && !this.initiatePhaseTwo) {
            this.initiatePhaseTwo = true;
            this.insanity(warden);
        }
        return true;
    }

    public void floorAttack(NPC npc, FloorSection type) {
        BooleanSupplier cancel = () -> this.phase.equals(WardenPhase.TWO) || npc.dead() || npc.getInstancedArea() == null || npc.id() != 11762;
        Chain.noCtx().cancelWhen(cancel).runFn(6, () -> {
            this.instance.initiatedPhaseOne = true;
            npc.animate(9674);
            if (npc.getInstancedArea() != null) {
                type.buildEast(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        }).cancelWhen(cancel).then(6, () -> {
            npc.animate(9676);
            if (npc.getInstancedArea() != null) {
                type.buildWest(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        }).cancelWhen(cancel).then(6, () -> {
            npc.animate(9678);
            if (npc.getInstancedArea() != null) {
                type.buildCenter(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        });
        Chain.noCtxRepeat().cancelWhen(cancel).repeatingTask(18, floorTask -> Chain.noCtx().runFn(6, () -> {
            npc.animate(9674);
            if (npc.getInstancedArea() != null) {
                type.buildEast(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        }).cancelWhen(cancel).then(6, () -> {
            npc.animate(9676);
            if (npc.getInstancedArea() != null) {
                type.buildWest(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        }).cancelWhen(cancel).then(6, () -> {
            npc.animate(9678);
            if (npc.getInstancedArea() != null) {
                type.buildCenter(npc, new Tile(3936, 5156), false).forEach(tile -> {
                    World.getWorld().tileGraphic(new Graphic(Utils.randomInclusive(2220, 2223), GraphicHeight.LOW, tile.delay).id(), tile, 0, tile.delay);
                    checkDamage(npc, tile);
                });
            }
        }));
    }

    void checkDamage(NPC npc, RaisedFloor tile) {
        BooleanSupplier cancel = () -> npc.dead() || npc.getInstancedArea() == null || npc.id() != 11762;
        Chain.noCtx().cancelWhen(cancel).runFn((int) (tile.delay / 30.0D), () -> {
            for (var player : this.instance.getPlayers()) {
                if ((player == null || player.dead() || !player.isRegistered())) continue;
                if (!player.tile().equals(tile)) continue;
                new Hit(npc, player, Utils.random(10, 18), 0, CombatType.MELEE).setHitMark(HitMark.DEFAULT).submit();
            }
        });
    }

    void insanity(NPC npc) {
        npc.animate(9684);
        BooleanSupplier cancel = () -> npc.dead() || npc.getInstancedArea() == null || npc.id() != 11762;
        Chain.noCtx().cancelWhen(cancel).runFn(3, () -> {
            this.instance.initiatedPhaseTwo = true;
            npc.animate(9685);
            final List<Tile> occupiedThunderLocations = new ArrayList<>();
            int[] floorRemovalIndex = new int[]{0};
            final Tile thunderTile = new Tile(3926, 5157, this.instance.getzLevel() + 1);
            final Projectile projectile = new Projectile(2228, 0, 200, 0, 20, 90, 0, 0);
            final Tile voidTile = new Tile(3936, 5130, this.instance.getzLevel() + 1);
            Chain.noCtxRepeat().repeatingTask(3, task -> {
                int currentRow = (thunderTile.getY() + 8) - (floorRemovalIndex[0] / 4);
                for (GameObject object : floorObjects[floorRemovalIndex[0]]) {
                    final Tile location = object.tile();
                    if (location.transform(0,0, this.instance.getzLevel() + 1).getY() == currentRow) {
                        projectile.send(location, voidTile);
                    }
                    object.spawn();
                }
                floorRemovalIndex[0]++;
            });

            Chain.noCtxRepeat().cancelWhen(cancel).repeatingTask(5, task -> {
                occupiedThunderLocations.clear();
                List<Tile> availableLocations = new ArrayList<>();
                for (int y = 0; y <= 8 - (floorRemovalIndex[0] / 4); y++) {
                    for (int x = 0; x <= 20; x++) {
                        final Tile loc = thunderTile.transform(x, y, this.instance.getzLevel() + 1);
                        if (World.getWorld().clipAt(loc) == 0 && !occupiedThunderLocations.contains(loc)) {
                            availableLocations.add(loc);
                        }
                    }
                }
                Collections.shuffle(availableLocations);
                List<Tile> thunderTiles = availableLocations.subList(0, Math.max(2, (int) (availableLocations.size() * .3)));
                thunderTiles.forEach(loc -> {
                    World.getWorld().tileGraphic(1446, loc, 0, 0);
                    occupiedThunderLocations.add(loc);
                });
                Chain.noCtx().cancelWhen(cancel).runFn(2, () -> thunderTiles.forEach(loc -> {
                    World.getWorld().tileGraphic(2197, loc, 0, 0);
                    for (var player : this.instance.getPlayers()) {
                        if ((player == null || player.dead() || !player.isRegistered())) continue;
                        if (!player.tile().equals(loc)) continue;
                        new Hit(npc, player, Utils.random(10, 18), 0, CombatType.MELEE).submit();
                    }
                }));
            });
        });
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        this.instance.isWardenDead = true;
        NPC warden = (NPC) this.entity;
        warden.animate(-1);
        Chain.noCtx().runFn(1, () -> {
            warden.transmog(11765, true);
            warden.animate(9691);
            warden.setInstancedArea(this.instance);
        }).then(22, () -> {
            this.instance.teleporter.spawn();
            warden.remove();
        });
        return true;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
