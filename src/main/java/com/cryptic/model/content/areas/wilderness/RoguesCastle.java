package com.cryptic.model.content.areas.wilderness;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CHEST_26757;

public class RoguesCastle extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == CHEST_26757) {
            int closed_chest = 26758;
            if (option == 1) {
                player.animate(537);
                generateHit(player);
                player.message("You have activated a trap on the chest.");
            } else if (option == 2) {
                if (player.getSkills().level(Skills.THIEVING) < 84) {
                    player.message("You need a Thieving level of 84 to successfully loot this chest.");
                } else {
                    player.message("You cycle the chest for traps.");
                    player.message("You find a trap on the chest.");
                    Chain.bound(null).runFn(1, () -> {
                        player.message("You disable the trap");
                        player.animate(535);
                        player.getSkills().addXp(Skills.THIEVING, 100);
                    });

                    Item eco_reward = Utils.randomElement(eco_rewards);

                    boolean hasWildernessSword = player.getEquipment().containsAny(ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4) || player.getInventory().containsAny(ItemIdentifiers.WILDERNESS_SWORD_2, ItemIdentifiers.WILDERNESS_SWORD_3, ItemIdentifiers.WILDERNESS_SWORD_4);
                    if (hasWildernessSword) {
                        int amount = eco_reward.getAmount();
                        amount *= 2;
                        eco_reward.setAmount(amount);
                    }

                    for (var region : player.getSurroundingRegions()) {
                        for (var npc : region.getNpcs()) {
                            if (npc.id() != 6603) continue;
                            npc.forceChat("Someone's stealing from us, get them!!");
                            npc.setEntityInteraction(player);
                            npc.getCombat().attack(player);
                        }
                    }

                    Chain.bound(null).runFn(2, () -> {
                        GameObject old = new GameObject(CHEST_26757, obj.tile(), obj.getType(), obj.getRotation());
                        GameObject spawned = new GameObject(closed_chest, obj.tile(), obj.getType(), obj.getRotation());
                        ObjectManager.replace(old, spawned, 20);
                        String name = eco_reward.name();
                        player.inventory().addOrDrop(eco_reward);
                        player.message("You find some " + name + " inside.");
                    });
                }
            }
            return true;
        }
        return false;
    }

    private static final Item[] eco_rewards = {new Item(1622, 5),
        new Item(ItemIdentifiers.UNCUT_SAPPHIRE + 1, 6),
        new Item(ItemIdentifiers.COINS_995, 1000),
        new Item(ItemIdentifiers.RAW_TUNA + 1, 15),
        new Item(ItemIdentifiers.ASHES + 1, 25),
        new Item(ItemIdentifiers.TINDERBOX + 1, 3),
        new Item(ItemIdentifiers.MIND_RUNE, 25),
        new Item(ItemIdentifiers.DIAMOND + 1, 3),
        new Item(ItemIdentifiers.CHAOS_RUNE, 40),
        new Item(ItemIdentifiers.DEATH_RUNE, 30),
        new Item(ItemIdentifiers.FIRE_RUNE, 30),
        new Item(ItemIdentifiers.PIKE + 1, 10),
        new Item(ItemIdentifiers.COAL + 1, 13),
        new Item(ItemIdentifiers.IRON_ORE + 1, 13),
        new Item(ItemIdentifiers.SHARK + 1, 10),
        new Item(ItemIdentifiers.BLIGHTED_ANGLERFISH + 1, 15),
        new Item(ItemIdentifiers.BLIGHTED_MANTA_RAY + 1, 20),
        new Item(ItemIdentifiers.BLIGHTED_ANCIENT_ICE_SACK, 13),
        new Item(ItemIdentifiers.PRAYER_POTION2 + 1, 1),
        new Item(ItemIdentifiers.UNCUT_EMERALD + 1, 10),
        new Item(ItemIdentifiers.UNCUT_DIAMOND + 1, 5),
        new Item(ItemIdentifiers.VILE_ASHES + 1, 18),
        new Item(ItemIdentifiers.CHAOS_RUNE, 75),
        new Item(ItemIdentifiers.DEATH_RUNE, 62),
        new Item(ItemIdentifiers.UNCUT_SAPPHIRE + 1, 18),
        new Item(ItemIdentifiers.RED_SPIDERS_EGGS + 1, 6),
        new Item(ItemIdentifiers.LAW_RUNE, 40),
        new Item(ItemIdentifiers.NATURE_RUNE, 40),
        new Item(ItemIdentifiers.CLUE_SCROLL_HARD, 1)
    };

    private void generateHit(Player player) {
        int current_hp = player.hp();
        if (current_hp >= 90) {
            player.hit(null, 17);
        } else if (current_hp >= 80) {
            player.hit(null, 15);
        } else if (current_hp >= 70) {
            player.hit(null, 14);
        } else if (current_hp >= 60) {
            player.hit(null, 12);
        } else if (current_hp >= 50) {
            player.hit(null, 11);
        } else if (current_hp >= 40) {
            player.hit(null, 9);
        } else if (current_hp >= 30) {
            player.hit(null, 7);
        } else if (current_hp >= 20) {
            player.hit(null, 6);
        } else if (current_hp >= 10) {
            player.hit(null, 5);
        } else if (current_hp >= 7) {
            player.hit(null, 4);
        } else if (current_hp >= 3) {
            player.hit(null, 3);
        } else player.hit(null, 1);
    }
}
