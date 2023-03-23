package com.aelous.model.content;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.CustomItemIdentifiers;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.content.collection_logs.LogType.KEYS;
import static com.aelous.utility.ItemIdentifiers.CRYSTAL_KEY;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 18, 2020
 */
public class CrystalKey extends PacketInteraction {

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if ((use.getId() == ItemIdentifiers.LOOP_HALF_OF_KEY && usedWith.getId() == ItemIdentifiers.TOOTH_HALF_OF_KEY) || (use.getId() == ItemIdentifiers.TOOTH_HALF_OF_KEY && usedWith.getId() == ItemIdentifiers.LOOP_HALF_OF_KEY)) {
            player.inventory().remove(new Item(ItemIdentifiers.LOOP_HALF_OF_KEY), true);
            player.inventory().remove(new Item(ItemIdentifiers.TOOTH_HALF_OF_KEY), true);
            player.inventory().add(new Item(CRYSTAL_KEY), true);
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 172 && option == 1) {
            if (player.inventory().contains(CRYSTAL_KEY)) {
                player.inventory().remove(new Item(CRYSTAL_KEY));
                player.message("You unlock the chest with your key.");
                player.animate(536);
                Chain.bound(null).runFn(1, () -> {
                    GameObject old = new GameObject(172, object.tile(), object.getType(), object.getRotation());
                    GameObject spawned = new GameObject(173, object.tile(), object.getType(), object.getRotation());
                    ObjectManager.replace(old, spawned, 2);
                    int roll = Utils.percentageChance(player.extraItemRollChance()) ? 2 : 1;
                    for (int i = 0; i < roll; i++) {
                        reward(player);
                    }
                });
            } else {
                player.message("You need a crystal key to open this chest.");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        if (object.getId() == 172) {
            if (player.inventory().contains(CRYSTAL_KEY)) {
                player.inventory().remove(new Item(CRYSTAL_KEY));
                player.message("You unlock the chest with your key.");
                player.animate(536);
                Chain.bound(null).runFn(1, () -> {
                    GameObject old = new GameObject(172, object.tile(), object.getType(), object.getRotation());
                    GameObject spawned = new GameObject(173, object.tile(), object.getType(), object.getRotation());
                    ObjectManager.replace(old, spawned, 2);
                    int roll = Utils.percentageChance(player.extraItemRollChance()) ? 2 : 1;
                    for (int i = 0; i < roll; i++) {
                        reward(player);
                    }
                });
            } else {
                player.message("You need a crystal key to open this chest.");
            }
            return true;
        }
        return false;
    }

    private enum PvpRewards {
        FIRST(80, new Item[]{new Item(ItemIdentifiers.BLOOD_MONEY, 15_000)}),
        //SECOND(60, new Item[]{new Item(CustomItemIdentifiers.BLOOD_MONEY_CASKET, 1)}),
        THIRD(50, new Item[]{new Item(ItemIdentifiers.DRAGONFIRE_SHIELD, 1)}),
        FOURTH(40, new Item[]{new Item(CustomItemIdentifiers.ARMOUR_MYSTERY_BOX, 1)}),
        FIFTH(30, new Item[]{new Item(CustomItemIdentifiers.WEAPON_MYSTERY_BOX, 1)}),
        SIXTH(20, new Item[]{new Item(CustomItemIdentifiers.DONATOR_MYSTERY_BOX, 1)}),
        EIGHT(10, new Item[]{new Item(ItemIdentifiers.BERSERKER_RING_I, 1)}),
        NINTH(8, new Item[]{new Item(ItemIdentifiers.SEERS_RING_I, 1)}),
        TENTH(7, new Item[]{new Item(ItemIdentifiers.ARCHERS_RING_I, 1)}),
        ELEVENTH(6, new Item[]{new Item(ItemIdentifiers.ARMADYL_CROSSBOW, 1)}),
        TWELFTH(5, new Item[]{new Item(ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD, 1)}),
        THIRTEENTH(2, new Item[]{new Item(ItemIdentifiers.ARMADYL_GODSWORD, 1)}),
        FOURTEENTH(1, new Item[]{new Item(ItemIdentifiers.DRAGON_CLAWS, 1)});

        private final int chance;
        private final Item[] rewards;

        PvpRewards(int chance, Item[] rewards) {
            this.chance = chance;
            this.rewards = rewards;
        }
    }

    private static void reward(Player player) {
        Item[] rewards = generateReward();
        
        Item drop = null;

        for (Item item : rewards) {
            player.getInventory().addOrDrop(item);
            KEYS.log(player, CRYSTAL_KEY, item);
            drop = item;
        }

        if(drop != null) {
            //The user box test doesn't yell.
            if(player.getUsername().equalsIgnoreCase("Box test")) {
                return;
            }
            System.out.println(drop.toString());
            World.getWorld().sendWorldMessage("<img=2013>" + player.getUsername() + " received <col=A30072>" + drop.name() + " </col>from the crystal key.");
        }

        int keysUsed = player.<Integer>getAttribOr(AttributeKey.CRYSTAL_KEYS_OPENED, 0) + 1;
        player.putAttrib(AttributeKey.CRYSTAL_KEYS_OPENED, keysUsed);

        player.message("You find some treasure in the chest!");
        AchievementsManager.activate(player, Achievements.CRYSTAL_LOOTER_I, 1);
        AchievementsManager.activate(player, Achievements.CRYSTAL_LOOTER_II, 1);
        AchievementsManager.activate(player, Achievements.CRYSTAL_LOOTER_III, 1);
    }

    private static Item[] generateReward() {
        int randomReward = Utils.random(80);
        Item[] rewardItem;
        for (PvpRewards reward : PvpRewards.values()) {
            if (reward.chance <= randomReward) {
                return reward.rewards;
            }
        }
        rewardItem = PvpRewards.FIRST.rewards;
        return rewardItem;
    }

}
