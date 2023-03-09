package com.aelous.model.content.skill.impl.thieving;

import com.aelous.GameServer;
import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 26, 2021, 11:13
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Pickpocketing extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (option == 2) {
            NpcDefinition npcDef = World.getWorld().definitions().get(NpcDefinition.class, npc.id());
            for (PickPocket pickpocket : PickPocket.values()) {
                if (npcDef.name.equalsIgnoreCase(pickpocket.name().replace("_", " ")) ||
                    npcDef.name.toLowerCase().contains(pickpocket.name().toLowerCase())) {
                    int pickpocketOption = npcDef.getOption("pickpocket");
                    if (pickpocketOption == -1) {
                        return false;
                    }

                    if(npc.def().name.equalsIgnoreCase(npcDef.name)) {
                        pickpocket(player, npc, pickpocket);
                        return true;
                    } else if(npc.def().name.equalsIgnoreCase("tzhaar-hur")) {
                        pickpocket(player, npc, PickPocket.TZHAAR_HUR);
                        return true;
                    }

                    final int[] HAM_MEMBERS = {2540, 2541};
                    for (int hamMember : HAM_MEMBERS) {
                        if(npc.id() == hamMember) {
                            pickpocket(player, npc, PickPocket.HAM);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void pickpocket(Player player, NPC npc, PickPocket pickpocket) {
        if (!player.getSkills().check(Skills.THIEVING, pickpocket.levelReq, "pickpocket the " + pickpocket.name + "."))
            return;
        if (player.inventory().isFull()) {
            player.message("Your inventory is too full to hold any more loot.");
            return;
        }
        if (player.stunned()) {
            player.message("You're stunned!");
            return;
        }
        
        player.runFn(1, () -> {
            player.lockDamageOk();
            player.message("You attempt to pick the " + pickpocket.identifier + " pocket.");
            if (successful(player, pickpocket)) {
                player.animate(881);
                player.runFn(2, () -> {
                    player.message("You pick the " + pickpocket.identifier + " pocket.");

                    player.inventory().add(pickpocket.lootTable.rollItem());
                    player.getSkills().addXp(Skills.THIEVING, pickpocket.exp, true);
                });
            } else {
                player.runFn(1, () -> {
                    player.message("You fail to pick the " + pickpocket.identifier + " pocket.");
                    npc.forceChat("What do you think you're doing?");
                    npc.setPositionToFace(player.tile());
                    npc.animate(pickpocket.stunAnimation);
                    player.hit(player, World.getWorld().random(pickpocket.stunDamage));
                }).then(1, () -> {
                    player.stun(pickpocket.stunSeconds, true);
                });
            }
        }).then(2, () -> {
            player.unlock();
        });
    }

    private static boolean successful(Player player, PickPocket pickpocket) {
        return World.getWorld().random(100) <= chance(player, pickpocket.levelReq);
    }

    private static int chance(Player player, int levelReq) {
        int slope = 2;
        int chance = 60; //Starts at a 60% chance
        int thievingLevel = player.getSkills().level(Skills.THIEVING);
        int requiredLevel = levelReq;

        if (player.getEquipment().contains(GLOVES_OF_SILENCE))
            chance += 5;
        if (player.getEquipment().wearingMaxCape())
            chance *= 1.1;
        if (thievingLevel > levelReq)
            chance += (thievingLevel - requiredLevel) * slope;
        return Math.min(chance, 95); //Caps at 95%
    }

    public enum PickPocket {

        MAN(1, 8.0, 422, 5, 1, 52000, "man's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 5 : 500, GameServer.properties().pvpMode ? 7 : 700, 1)  //Coins
            )),
        FARMER(10, 14.5, 433, 5, 1,43500,"farmer's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 6 : 600, GameServer.properties().pvpMode ? 10 : 1000, 6), //Coins
                new LootItem(5318, 1, 1) //Potato seed
            )),
        HAM(15, 18.5, 433, 4, 1, 43500,"H.A.M member's",
            new LootTable().addTable(1,
                new LootItem(882, 16, 60), //Coins
                new LootItem(1351, 1, 1), //Coins
                new LootItem(1265, 1, 1), //Coins
                new LootItem(1349, 1, 1), //Coins
                new LootItem(1267, 1, 1), //Coins
                new LootItem(886, 20, 1), //Coins
                new LootItem(1353, 1, 1), //Coins
                new LootItem(1207, 1, 1), //Coins
                new LootItem(1129, 1, 1), //Coins
                new LootItem(4302, 1, 1), //Coins
                new LootItem(4298, 1, 1), //Coins
                new LootItem(4300, 1, 1), //Coins
                new LootItem(4304, 1, 1), //Coins
                new LootItem(4306, 1, 1), //Coins
                new LootItem(4308, 1, 1), //Coins
                new LootItem(4310, 1, 1), //Coins
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 12 : 1200, 1), //Coins
                new LootItem(319, 1, 1), //Coins
                new LootItem(2138, 1, 1), //Coins
                new LootItem(453, 1, 1), //Coins
                new LootItem(440, 1, 1), //Coins
                new LootItem(1739, 1, 1), //Coins
                new LootItem(314, 5, 1), //Coins
                new LootItem(1734, 6, 1), //Coins
                new LootItem(1733, 1, 1), //Coins
                new LootItem(1511, 1, 1), //Coins
                new LootItem(686, 1, 1), //Coins
                new LootItem(697, 1, 1), //Coins
                new LootItem(1625, 1, 1), //Coins
                new LootItem(1627, 1, 1), //Coins
                new LootItem(199, 5, 1), //Coins
                new LootItem(201, 6, 1), //Coins
                new LootItem(203, 1, 1) //Coins
            )),
        WARRIOR(25, 26.0, 386, 5, 2, 39000,"warrior's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 9 : 900, GameServer.properties().pvpMode ? 12 : 1200, 1) //Coins
            )),
        ROGUE(32, 35.5, 422, 5, 2, 34500,"rogue's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 25 : 2500, GameServer.properties().pvpMode ? 50 : 5000, 10), //Coins
                new LootItem(556, 8, 5),  //Air runes
                new LootItem(1933, 1, 4), //Jug of wine
                new LootItem(1219, 1, 3), //Iron dagger(p)
                new LootItem(1523, 1, 1)  //Lockpick
            )),
        MASTER_FARMER(38, 43.0, 386, 5, 3, 27540,"master farmer's",
            new LootTable().addTable(1,
                new LootItem(5318, 1, 4, 8), //Potato seed
                new LootItem(5319, 1, 3, 5), //Onion seed
                new LootItem(5324, 1, 3, 5), //Cabbage seed
                new LootItem(5322, 1, 2, 5), //Tomato seed
                new LootItem(5320, 1, 2, 5), //Sweetcorn seed
                new LootItem(5096, 1, 5), //Marigold seed
                new LootItem(5097, 1, 5), //Rosemary seed
                new LootItem(5098, 1, 5), //Nasturtium seed
                new LootItem(5291, 1, 5), //Guam seed
                new LootItem(5292, 1, 5), //Marrentill seed
                new LootItem(5293, 1, 5), //Tarromin seed
                new LootItem(5294, 1, 5), //Harralander seed
                new LootItem(5323, 1, 3), //Strawberry seed
                new LootItem(5321, 1, 3), //Watermelon seed
                new LootItem(5100, 1, 3), //Limpwurt seed
                new LootItem(5295, 1, 2), //Ranarr seed
                new LootItem(5296, 1, 2), //Toadflax seed
                new LootItem(5297, 1, 2), //Irit seed
                new LootItem(5298, 1, 1), //Avantoe seed
                new LootItem(5299, 1, 1), //Kwuarm seed
                new LootItem(5300, 1, 1), //Snapdragon seed
                new LootItem(5301, 1, 1), //Cadantine seed
                new LootItem(5302, 1, 1), //Lantadyme seed
                new LootItem(5303, 1, 1), //Dwarf weed seed
                new LootItem(5304, 1, 1)  //Torstol seed
            )),
        MARTIN_THE_MASTER_GARDENER(38, 43.0, 386, 5, 3, 27540,"master gardener",
            new LootTable().addTable(1,
                new LootItem(5318, 1, 4, 8), //Potato seed
                new LootItem(5319, 1, 3, 5), //Onion seed
                new LootItem(5324, 1, 3, 5), //Cabbage seed
                new LootItem(5322, 1, 2, 5), //Tomato seed
                new LootItem(5320, 1, 2, 5), //Sweetcorn seed
                new LootItem(5096, 1, 5), //Marigold seed
                new LootItem(5097, 1, 5), //Rosemary seed
                new LootItem(5098, 1, 5), //Nasturtium seed
                new LootItem(5291, 1, 5), //Guam seed
                new LootItem(5292, 1, 5), //Marrentill seed
                new LootItem(5293, 1, 5), //Tarromin seed
                new LootItem(5294, 1, 5), //Harralander seed
                new LootItem(5323, 1, 3), //Strawberry seed
                new LootItem(5321, 1, 3), //Watermelon seed
                new LootItem(5100, 1, 3), //Limpwurt seed
                new LootItem(5295, 1, 2), //Ranarr seed
                new LootItem(5296, 1, 2), //Toadflax seed
                new LootItem(5297, 1, 2), //Irit seed
                new LootItem(5298, 1, 1), //Avantoe seed
                new LootItem(5299, 1, 1), //Kwuarm seed
                new LootItem(5300, 1, 1), //Snapdragon seed
                new LootItem(5301, 1, 1), //Cadantine seed
                new LootItem(5302, 1, 1), //Lantadyme seed
                new LootItem(5303, 1, 1), //Dwarf weed seed
                new LootItem(5304, 1, 1)  //Torstol seed
            )),
        GUARD(40, 46.8, 386, 5, 2, 23000,"guard's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 15 : 1500, GameServer.properties().pvpMode ? 20 : 2000, 1) //Coins
            )),
        BANDIT(53, 79.5, 422, 5, 3, 23000,"bandit's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 20 : 2000, GameServer.properties().pvpMode ? 28 : 2800, 8), //Coins
                new LootItem(175, 1, 3),  //Antipoison
                new LootItem(1523, 1, 1)  //Lockpick
            )),
        KNIGHT(55, 84.3, 386, 5, 3, 19000,"knight's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 30 : 3000, GameServer.properties().pvpMode ? 40 : 4000, 1) //Coins
            )),
        PALADIN(70, 151.75, 386, 5, 3, 12000,"paladin's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 40 : 4000, GameServer.properties().pvpMode ? 45 : 4500, 6), //Coins
                new LootItem(562, 2, 3)   //Chaos runes
            )),
        GNOME(75, 198.5, 201, 5, 1, 11540,"gnome's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 40 : 4000, GameServer.properties().pvpMode ? 45 : 4500, 16), //Coins
                new LootItem(5321, 3, 8),   //Watermelon seed
                new LootItem(5100, 1, 8),   //Limpwurt seed
                new LootItem(5295, 1, 7),   //Ranarr seed
                new LootItem(5296, 1, 7),   //Toadflax seed
                new LootItem(5297, 1, 7),   //Irit seed
                new LootItem(5298, 1, 7),   //Avantoe seed
                new LootItem(5299, 1, 7),   //Kwuarm seed
                new LootItem(5300, 1, 7),   //Snapdragon seed
                new LootItem(5301, 1, 7),   //Cadantine seed
                new LootItem(5302, 1, 6),   //Lantadyme seed
                new LootItem(5303, 1, 5),   //Dwarf weed seed
                new LootItem(5304, 1, 4),   //Torstol seed
                new LootItem(5312, 1, 4),   //Acorn
                new LootItem(5313, 1, 3),   //Willow seed
                new LootItem(5314, 1, 4),   //Maple seed
                new LootItem(5315, 1, 1),   //Yew seed
                new LootItem(5283, 1, 9),   //Apple tree seed
                new LootItem(5284, 1, 8),   //Banana tree seed
                new LootItem(5285, 1, 7),   //Orange tree seed
                new LootItem(5286, 1, 6),   //Curry tree seed
                new LootItem(5287, 1, 3),   //Pineapple seed
                new LootItem(5288, 1, 2)    //Papaya tree seed
            )),
        HERO(80, 275.0, 386, 6, 4, 9700,"hero's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 50 : 5000, GameServer.properties().pvpMode ? 60 : 6000, 16),  //Coins
                new LootItem(565, 1, 5),  //Blood rune
                new LootItem(560, 2, 5),  //Death runes
                new LootItem(1933, 1, 2), //Jug of wine
                new LootItem(569, 1, 2),  //Fire orb
                new LootItem(444, 1, 2),  //Gold ore
                new LootItem(1617, 1, 1)  //Uncut diamond
            )),
        ELF(85, 353.0, 422, 6, 5, 8500,"elf's",
            new LootTable().addTable(1,
                new LootItem(GameServer.properties().pvpMode ? BLOOD_MONEY : COINS_995, GameServer.properties().pvpMode ? 55 : 5500, GameServer.properties().pvpMode ? 65 : 6500, 16), //Coins
                new LootItem(561, 3, 5),  //Nature runes
                new LootItem(560, 2, 5),  //Death runes
                new LootItem(1933, 1, 2), //Jug of wine
                new LootItem(569, 1, 2),  //Fire orb
                new LootItem(444, 1, 2),  //Gold ore
                new LootItem(1617, 1, 1)  //Uncut diamond
            )),
        TZHAAR_HUR(90, 103.0, 2609, 6, 5,7700, "tzhaar-hur's",
            new LootTable().addTable(1,
                new LootItem(1755, 1, 6),                    //Chisel
                new LootItem(2347, 1, 5),                    //Hammer
                new LootItem(1935, 1, 5),                    //Jug
                new LootItem(946, 1, 2),                     //Knife
                new LootItem(1931, 1, 2),                    //Pot
                new LootItem(6529, 1, 16, 2),  //Tokkul
                new LootItem(1623, 1, 1),                    //Uncut Sapphire
                new LootItem(1619, 1, 1)                     //Uncut Ruby
            ));

        public final int levelReq, stunAnimation, stunSeconds, stunDamage, petOdds;
        private final String name, identifier;
        public final double exp;
        public final LootTable lootTable;

        PickPocket(int levelReq, double exp, int stunAnimation, int stunSeconds, int stunDamage, int petOdds, String identifier, LootTable lootTable) {
            this.levelReq = levelReq;
            this.exp = exp;
            this.stunAnimation = stunAnimation;
            this.stunSeconds = stunSeconds;
            this.stunDamage = stunDamage;
            this.petOdds = petOdds;
            this.name = identifier.replace("'s", "");
            this.identifier = identifier;
            this.lootTable = lootTable;
        }

    }
}
