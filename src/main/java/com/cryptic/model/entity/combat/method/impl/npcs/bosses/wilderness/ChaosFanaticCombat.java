package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.autocasting.Autocasting;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.weapon.WeaponInterfaces;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ChaosFanaticCombat extends CommonCombatMethod {

    private static final String[] QUOTES = {
        "Burn!",
        "WEUGH!",
        "Develish Oxen Roll!",
        "All your wilderness are belong to them!",
        "AhehHeheuhHhahueHuUEehEahAH",
        "I shall call him squidgy and he shall be my squidgy!",
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!entity.isNpc() || !target.isPlayer())
            return false;

        if (!withinDistance(15)) {
            return false;
        }

        entity.forceChat(QUOTES[Utils.getRandom(QUOTES.length)]);

        NPC npc = (NPC) entity;
        if (Utils.securedRandomChance(0.10)) {
            explosion(entity, target);
        } else if (Utils.securedRandomChance(0.05)) {
            disarm(target);
        } else {
            attack(npc, target);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 2;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

    private void attack(NPC npc, Entity target) {
        var tileDist = npc.tile().distance(target.tile());
        int duration = (41 + 5 + (5 * tileDist));
        Projectile p = new Projectile(npc, target, 554, 41, duration, 40, 36, 15, 1, 5);
        final int delay = npc.executeProjectile(p);
        npc.animate(811);
        new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
        target.graphic(305, GraphicHeight.MIDDLE, p.getSpeed());
    }

    private void disarm(Entity target) {
        Player player = (Player) target;
        final Item item = player.getEquipment().get(EquipSlot.WEAPON);
        if (item != null && player.inventory().hasCapacity(item)) {
            player.getEquipment().remove(item, EquipSlot.WEAPON, true);
            player.getEquipment().unequip(EquipSlot.WEAPON);
            WeaponInterfaces.updateWeaponInterface(player);
            Autocasting.setAutocast(player, null);
            player.looks().resetRender();
            if (player.inventory().isFull()) {
                return;
            } else {
                player.inventory().add(item);
            }
            target.message("The fanatic disarms you!");
        }
    }

    private void explosion(Entity npc, Entity target) {
        var tile = target.tile().copy();
        var distance = npc.tile().distance(target.tile());
        int duration1 = 41 + 11 + (4 * distance);
        int duration2 = 41 + 11 + (8 * distance);
        int duration3 = 41 + 11 + (12 * distance);

        npc.animate(811);

        Tile randomTile;

        List<Tile> tileList = new ArrayList<>();

        for (int index = 0; index < 2; index++) {
            randomTile = World.getWorld().randomTileAround(tile, 1);
            if (randomTile != null) {
                tileList.add(randomTile);
            }
        }

        Collections.shuffle(tileList);

        Projectile[] projectiles = new Projectile[]
            {
                new Projectile(npc, tile, 551, 41, duration1, 40, 2, 40, 1, 5),
                new Projectile(npc, Utils.randomElement(tileList), 551, 43, duration2, 40, 2, 40, 1, 10),
                new Projectile(npc, Utils.randomElement(tileList), 551, 45, duration3, 40, 2, 40, 1, 10)
            };

        Arrays.stream(projectiles).forEach(p -> {
            final int delay = npc.executeProjectile(p);
            World.getWorld().tileGraphic(157, p.getEnd(), 0, p.getSpeed());

            Chain.noCtx().runFn((int) (p.getSpeed() / 30D), () -> {
                if (target.tile().equals(p.getEnd())) {
                    new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                }
                tileList.clear();
            });
        });
    }
}
