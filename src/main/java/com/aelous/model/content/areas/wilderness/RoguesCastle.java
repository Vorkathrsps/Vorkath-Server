package com.aelous.model.content.areas.wilderness;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CHEST_26757;

public class RoguesCastle extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == CHEST_26757) {

            // Object ID's for the opened & closed chest
            int closed_chest = 26758;

            if (option == 1) {
                //Handle the first chest option: "Open".
                player.animate(537);
                generateHit(player);
                player.message("You have activated a trap on the chest.");
            } else if (option == 2) {
                //Handle the second chest option: "Search for traps".
                if (player.getSkills().level(Skills.THIEVING) < 84) {
                    player.message("You need a Thieving level of 84 to successfully loot this chest.");
                } else {
                    // Else we must be high enough thieving to crack the chest!
                    player.message("You cycle the chest for traps.");
                    player.message("You find a trap on the chest.");
                    Chain.bound(null).runFn(1, () -> {
                        player.message("You disable the trap");
                        player.animate(535);
                        player.getSkills().addXp(Skills.THIEVING, 100);
                    });

                    // Grabs a reward from our array lists
                    Item eco_reward = Utils.randomElement(eco_rewards);
                    Item pvp_reward = Utils.randomElement(pvp_rewards);

                    // For every level 135 Rogue inside the Rogues Castle we..
                    World.getWorld().getNpcs().forEach(npc -> {
                        if (WildernessArea.inside_rouges_castle(npc.tile()) && npc.id() == 6603) {
                            npc.forceChat("Someone's stealing from us, get them!!");
                            npc.getCombat().attack(player);
                        }
                    });
                    Chain.bound(null).runFn(2, () -> {
                        // Handle replacing the chest with an open chest, then back to the original object..
                        GameObject old = new GameObject(CHEST_26757, obj.tile(), obj.getType(), obj.getRotation());
                        GameObject spawned = new GameObject(closed_chest, obj.tile(), obj.getType(), obj.getRotation());
                        ObjectManager.replace(old, spawned, 60);
                        String name = GameServer.properties().pvpMode ? pvp_reward.name() : eco_reward.name();

                        // If our player is on the PVP world, give them the appropriate reward.
                        if (GameServer.properties().pvpMode) {
                            player.inventory().addOrDrop(pvp_reward);
                        } else {
                            // Else we must be on the economy server..
                            player.inventory().addOrDrop(eco_reward);
                        }
                        player.message("You find some " + name + " inside.");
                    });
                }
            }
            return true;
        }
        return false;
    }

    //Array used to store the economy server chest rewards.
    private static final Item[] eco_rewards = {new Item(1622, 5), //Uncut emerald
        new Item(1624, 6), //Uncut sapphire
        new Item(995, 1000), //Coins
        new Item(360, 15), //Raw Tuna
        new Item(593, 25), //Ashes
        new Item(591, 3), //Tinderbox
        new Item(558, 25), //Mind runes
        new Item(1602, 3), //Diamond
        new Item(562, 40), //Chaos runes
        new Item(560, 30), //Death runes
        new Item(554, 30), //Fire runes
        new Item(352, 10), //Pike
        new Item(454, 13), //Coal
        new Item(441, 13), //Iron ore
        new Item(386, 10), //Shark
        new Item(1616, 3)}; //Dragonstone

    //Array used to store the chest rewards.
    private final Item[] pvp_rewards = {
        new Item(BLOOD_MONEY, 80),
        new Item(BLOOD_MONEY, 100),
        new Item(BLOOD_MONEY, 120),
        new Item(23583, 5), //Stam Pot
        new Item(22124, 5), //Superior Bones
        new Item(22124, 10), //Superior Bones
        new Item(6585, 1) //Fury
    };

    private void generateHit(Player player) {
        int current_hp = player.hp();

        //Generate the hit, and apply it to the player.
        if (current_hp >= 90) {
            player.hit(player,17);
        } else if (current_hp >= 80) {
            player.hit(player,15);
        } else if (current_hp >= 70) {
            player.hit(player,14);
        } else if (current_hp >= 60) {
            player.hit(player,12);
        } else if (current_hp >= 50) {
            player.hit(player,11);
        } else if (current_hp >= 40) {
            player.hit(player,9);
        } else if (current_hp >= 30) {
            player.hit(player,7);
        } else if (current_hp >= 20) {
            player.hit(player,6);
        } else if (current_hp >= 10) {
            player.hit(player,5);
        } else if (current_hp >= 7) {
            player.hit(player,4);
        } else if (current_hp >= 3) {
            player.hit(player,3);
        } else player.hit(player,1);
    }
}
