package com.aelous.model.content.items.keys;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import static com.aelous.model.content.collection_logs.LogType.KEYS;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.REWARD_CHEST_36087;

public class EscapeKey extends PacketInteraction {
    private static final Item[] EXTREMELY_RARE = new Item[]{
        new Item(ANCESTRAL_HAT), new Item(ANCESTRAL_ROBE_TOP), new Item(ANCESTRAL_ROBE_BOTTOM)
    };

    private static final Item[] RARE = new Item[]{
        new Item(LEGENDARY_MYSTERY_BOX), new Item(MYSTERY_TICKET), new Item(VESTAS_CHAINBODY), new Item(VESTAS_PLATESKIRT),
    };

    private static final Item[] UNCOMMON = new Item[]{
           new Item(DONATOR_MYSTERY_BOX),  new Item(TOXIC_STAFF_OF_THE_DEAD),  new Item(ARMADYL_CROSSBOW),  new Item(DRAGON_CLAWS), new Item(PRIMORDIAL_BOOTS), new Item(PEGASIAN_BOOTS), new Item(ETERNAL_BOOTS)
    };

    private static final Item[] COMMON = new Item[]{
        new Item(ARMOUR_MYSTERY_BOX),  new Item(WEAPON_MYSTERY_BOX),  new Item(ANTIQUE_EMBLEM_TIER_1),
    };

    public Item rollReward() {
        var roll = World.getWorld().random(100);
        //Reward rarity to base the server message on
        if (roll >= 95 && roll <= 100) {
            return Utils.randomElement(EXTREMELY_RARE);
        } else if (roll >= 75 && roll <= 94) {
            return Utils.randomElement(RARE);
        } else if (roll >= 35 && roll <= 74) {
            return Utils.randomElement(UNCOMMON);
        } else {
            return Utils.randomElement(COMMON);
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        //Only perform actions if the object is a escape key chest and the option is 1.
        if (option == 1 && object.getId() == 36087 || object.getId() == REWARD_CHEST_36087) {
            //Do stuff here

            if (!player.inventory().contains(ESCAPE_KEY)) {
                player.message("You need an escape key to open this chest.");
                return true;
            }

            //Important to lock the player during the action
            player.lockDamageOk(); //TODO maybe a fix?

            Item rewardOne = rollReward();
            Item rewardTwo = rollReward();

            player.runFn(1, () -> {//TODO chest interface is nullifying damage if you stay on interface
                //Generate reward
                if(object.getId() == REWARD_CHEST_36087 && object.tile().equals(2337, 9904,1)) {
                    player.confirmDialogue(new String[]{"Are you sure you wish to open the chest?", "You will be " + Color.RED.wrap("red") + " skulled and teleblocked if you proceed."}, "", "proceed to open the chest.", "Nevermind.", () -> {
                        if(!player.inventory().contains(ESCAPE_KEY)) {
                            return;
                        }
                        open(player, rewardOne, rewardTwo,true);
                    });
                    return;
                }

                open(player, rewardOne,null,false);
            });
            return true;
        }
        return false;
    }

    private void open(Player player, Item rewardOne, Item rewardTwo, boolean wilderness) {
        player.message("You unlock the chest with your key.");
        player.animate(536);
        player.inventory().remove(new Item(ESCAPE_KEY));

        if(wilderness) {
            player.teleblock(250,true);
            Skulling.assignSkullState(player, SkullType.RED_SKULL);
        }

        int keysUsed = player.<Integer>getAttribOr(AttributeKey.WILDY_KEYS_OPENED, 0) + 1;
        player.putAttrib(AttributeKey.WILDY_KEYS_OPENED, keysUsed);

        if(rewardOne != null) {
            boolean amOverOne = rewardOne.getAmount() > 1;
            String amtString = amOverOne ? "x " + Utils.format(rewardOne.getAmount()) + "" : Utils.getAOrAn(rewardOne.name());
            String openedAt = wilderness ? "inside the Escape Zone" : "inside the Escape Zone";

            if(!player.getUsername().equalsIgnoreCase("Box test")) {
                String msg = "<img=2011><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">Escape key</col>]</shad>: " + "<col=AD800F>" + player.getUsername() + " has received " + amtString + " " + rewardOne.name() + " " + openedAt + "!";
                World.getWorld().sendWorldMessage(msg);
            }

            //Check if item exists in collection log items
            KEYS.log(player, ESCAPE_KEY, rewardOne);

            if(wilderness) {
                player.inventory().addOrDrop(rewardOne);
            } else {
                player.inventory().addOrBank(rewardOne);
            }
        }

        if(rewardTwo != null) {
            boolean amOverOne = rewardTwo.getAmount() > 1;
            String amtString = amOverOne ? "x " + Utils.format(rewardTwo.getAmount()) + "" : Utils.getAOrAn(rewardTwo.name());
            String openedAt = wilderness ? "inside the Escape Zone" : "inside the Escape Zone";

            //The user box test doesn't yell.
            if(!player.getUsername().equalsIgnoreCase("Box test")) {
                String msg = "<img=2011><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">Escape key</col>]</shad>: " + "<col=AD800F>" + player.getUsername() + " has received " + amtString + " " + rewardTwo.name() + " " + openedAt + "!";
                World.getWorld().sendWorldMessage(msg);
            }

            //Check if item exists in collection log items
            KEYS.log(player, ESCAPE_KEY, rewardTwo);

            if(wilderness) {
                player.inventory().addOrDrop(rewardTwo);
            } else {
                player.inventory().addOrBank(rewardTwo);
            }
        }

        //And unlock the player
        player.unlock();
    }
}
