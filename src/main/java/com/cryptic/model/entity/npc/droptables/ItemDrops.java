package com.cryptic.model.entity.npc.droptables;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.skill.impl.prayer.Bone;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.NPCDeath;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.cryptic.model.entity.combat.method.impl.npcs.bosses.CorporealBeast.CORPOREAL_BEAST_AREA;
import static com.cryptic.utility.ItemIdentifiers.*;

public class ItemDrops {

    public static final List<Integer> BONES = Arrays.asList(ItemIdentifiers.BONES, BURNT_BONES, BAT_BONES, BIG_BONES, BABYDRAGON_BONES, DRAGON_BONES, JOGRE_BONES, ZOGRE_BONES, OURG_BONES, WYVERN_BONES, DAGANNOTH_BONES, LONG_BONE, CURVED_BONE, LAVA_DRAGON_BONES, SUPERIOR_DRAGON_BONES, WYRM_BONES, DRAKE_BONES, HYDRA_BONES);

    public static void dropAlwaysItems(Player player, NPC npc) {
        ScalarLootTable table = ScalarLootTable.forNPC(npc.id());
        var inWilderness = WildernessArea.inWilderness(player.tile());
        var dropUnderPlayer = npc.id() == NpcIdentifiers.KRAKEN || npc.id() == NpcIdentifiers.CAVE_KRAKEN || npc.id() >= NpcIdentifiers.ZULRAH && npc.id() <= NpcIdentifiers.ZULRAH_2044 || npc.id() >= NpcIdentifiers.VORKATH_8059 && npc.id() <= NpcIdentifiers.VORKATH_8061;
        Tile tile = dropUnderPlayer ? player.tile() : npc.tile();

        table.getGuaranteedDrops().forEach(tableItem -> {
            if (player.inventory().contains(BONECRUSHER) || player.getEquipment().hasAt(EquipSlot.AMULET, DRAGONBONE_NECKLACE)) {
                int itemId = tableItem.convert().getId();
                for (int bone : BONES) {
                    if (itemId == bone) {
                        Bone bones = Bone.get(itemId);
                        if (bones != null)
                            player.skills().addXp(Skills.PRAYER, bones.xp * 2);
                    }
                }
            } else {
                if (tableItem.min > 0) {
                    Item dropped = new Item(tableItem.id, World.getWorld().random(tableItem.min, tableItem.max));
                    GroundItemHandler.createGroundItem(new GroundItem(dropped, tile, player));
                    LogType.BOSSES.log(player, npc.id(), dropped);
                    LogType.OTHER.log(player, npc.id(), dropped);
                } else {
                    Item item = tableItem.convert();
                    GroundItemHandler.createGroundItem(new GroundItem(item, tile, player));
                    LogType.BOSSES.log(player, npc.id(), item);
                    LogType.OTHER.log(player, npc.id(), item);
                }
            }
        });
    }

    public static void rollTheDropTable(Player player, NPC npc) {
        int npcId = NpcDropRepository.getDropNpcId(npc.getId());
        NpcDropTable table = NpcDropRepository.forNPC(npcId);
        var dropUnderPlayer = npc.id() == NpcIdentifiers.KRAKEN || npc.id() == NpcIdentifiers.CAVE_KRAKEN || npc.id() >= NpcIdentifiers.ZULRAH && npc.id() <= NpcIdentifiers.ZULRAH_2044 || npc.id() >= NpcIdentifiers.VORKATH_8059 && npc.id() <= NpcIdentifiers.VORKATH_8061;
        Tile tile = dropUnderPlayer ? player.tile() : npc.tile();
        int dropRolls = npc.getCombatInfo().droprolls;
        List<Item> rewards = table.getDrops(player, dropRolls);
        for (var item : rewards) {
            if (npc.id() == 319) {
                World.getWorld().getPlayers().forEachInArea(CORPOREAL_BEAST_AREA, p -> {
                    String amtString = item.unnote().getAmount() == 1 ? item.unnote().name() : "" + item.getAmount() + " x " + item.unnote().getAmount() + ".";
                    p.message("<col=0B610B>" + player.getUsername() + " received a drop: " + amtString);
                });
                break;
            }
            System.out.println(item.name());
            LogType.BOSSES.log(player, npc.id(), item);
            LogType.OTHER.log(player, npc.id(), item);
            GroundItemHandler.createGroundItem(new GroundItem(item, tile, player)); //TODO add broadcast
        }
    }

    public static void dropCoins(Player player, NPC npc) {
        if (npc.getCombatInfo() != null && npc.getCombatInfo().boss && WildernessArea.inWilderness(player.tile())) {
            Item coins = new Item(COINS_995, World.getWorld().random(250_000, 1_000_000));
            GroundItem groundItem = new GroundItem(coins, player.tile(), player);
            GroundItemHandler.createGroundItem(groundItem);
            NPCDeath.notification(player, coins);
            player.message("<col=0B610B>You have received a " + Utils.formatRunescapeStyle(coins.getAmount()) + " coins drop!");
        }

        var task_id = player.<Integer>getAttribOr(AttributeKey.SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        boolean hasTask = player.slayerTaskAmount() > 0;
        if (task != null && Slayer.creatureMatches(player, npc.id()) && hasTask) {
            if (npc.getCombatInfo() != null && npc.getCombatInfo().boss) {
                Item coins = new Item(COINS_995, World.getWorld().random(100_000, 500_000));
                GroundItem groundItem = new GroundItem(coins, player.tile(), player);
                GroundItemHandler.createGroundItem(groundItem);
                NPCDeath.notification(player, coins);
                player.message("<col=0B610B>You have received a " + Utils.formatRunescapeStyle(coins.getAmount()) + " coins drop!");
            }
        }
    }

}
