package com.aelous.model.content.skill.impl.slayer;

import com.aelous.GameServer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;

import static com.aelous.model.content.collection_logs.LogType.KEYS;
import static com.aelous.model.entity.attributes.AttributeKey.*;
import static com.aelous.utility.CustomItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.SUPER_ANTIFIRE_POTION4;

/**
 * This class represents the slayer key. The slayer key is a random drop during a slayer task.
 * The key has various rewards including armour, supplies and weaponry.
 * @author Patrick van Elderen | May, 18, 2021, 14:44
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SlayerKey {

    private static final int RARE_ROLL = 35;
    private static final int UNCOMMON_ROLL = 10;

    private final Player player;

    public SlayerKey(Player player) {
        this.player = player;
    }

    public void drop(NPC npc) {
        var task_id = player.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        var roll = player.getPlayerRights().isOwner(player) && !GameServer.properties().production ? 1 : 25;
        if (task != null && Slayer.creatureMatches(player, npc.id())) {
            if (task.matches(task_id))
                if (World.getWorld().rollDie(roll, 1)) {
                    var keysReceived = player.<Integer>getAttribOr(SLAYER_KEYS_RECEIVED, 0) + 1;
                    player.putAttrib(SLAYER_KEYS_RECEIVED, keysReceived);
                    player.getPacketSender().sendString(QuestTab.InfoTab.SLAYER_KEYS_RECEIVED.childId, QuestTab.InfoTab.INFO_TAB.get(QuestTab.InfoTab.SLAYER_KEYS_RECEIVED.childId).fetchLineData(player));

                    GroundItem groundItem = new GroundItem(new Item(SLAYER_KEY), npc.tile(), player);
                    GroundItemHandler.createGroundItem(groundItem);
                    player.message(Color.PURPLE.wrap("A slayer key appeared, you have now collected a total of " + keysReceived + " slayer keys."));
                }
        }
    }

    public void open() {
        if(!player.inventory().contains(SLAYER_KEY)) {
            player.message("This chest wont budge, I need some sort of key.");
            return;
        }

        player.message("You unlock the chest with your key.");
        player.animate(536);
        player.inventory().remove(SLAYER_KEY,1);

        int roll = Utils.percentageChance(player.extraItemRollChance()) ? 2 : 1;
        for (int i = 0; i < roll; i++) {
            Item reward = reward();
            KEYS.log(player, SLAYER_KEY, reward);
            player.inventory().addOrBank(reward);

            var sendWorldMessage = reward.getValue() >= 30_000;
            var amount = reward.getAmount();
            var plural = amount > 1 ? "x" + amount : "x1";
            if (sendWorldMessage && !player.getUsername().equalsIgnoreCase("Box test")) {
                String worldMessage = "<img=2010><shad=0>[<col=" + Color.MEDRED.getColorValue() + ">Slayer Key</col>]</shad>:<col=AD800F> " + player.getUsername() + " received " + plural + " <shad=0>" + reward.name() + "</shad>!";
                World.getWorld().sendWorldMessage(worldMessage);
            }
        }

        var keysUsed = player.<Integer>getAttribOr(SLAYER_KEYS_OPENED,0) + 1;
        player.putAttrib(SLAYER_KEYS_OPENED, keysUsed);
        player.message(Color.PURPLE.wrap("You have now opened "+Utils.formatNumber(keysUsed)+" slayer keys!"));
    }

    private Item reward() {
        if (Utils.rollDie(RARE_ROLL, 1)) {
            return Utils.randomElement(RARE);
        } else if (Utils.rollDie(UNCOMMON_ROLL, 1)) {
            return Utils.randomElement(UNCOMMON);
        } else {
            return Utils.randomElement(COMMON);
        }
    }

    private static final List<Item> COMMON = Arrays.asList(
        new Item(DIVINE_SUPER_COMBAT_POTION4 + 1, 10),
        new Item(STAMINA_POTION4 + 1, 10),
        new Item(ANTIVENOM4 + 1, 10),
        new Item(SUPER_ANTIFIRE_POTION4 + 1, 10),
        new Item(REGEN_BRACELET + 1, 1),
        new Item(WARRIOR_RING + 1, 1),
        new Item(ARCHERS_RING + 1, 1),
        new Item(BERSERKER_RING + 1, 1),
        new Item(SEERS_RING + 1, 1),
        new Item(BLOOD_MONEY, World.getWorld().random(1_000, 2_000)),
        new Item(ABYSSAL_WHIP, 1),
        new Item(AMULET_OF_FURY, 1),
        new Item(DARK_BOW, 1),
        new Item(ABYSSAL_TENTACLE, 1),
        new Item(GRANITE_MAUL_24225, 1),
        new Item(OCCULT_NECKLACE, 1),
        new Item(DRAGON_BOOTS, 1),
        new Item(RANGER_BOOTS),
        new Item(ROBIN_HOOD_HAT),
        new Item(RANGER_GLOVES, 1),
        new Item(RANGERS_TIGHTS, 1),
        new Item(RANGERS_TUNIC, 1),
        new Item(SPIKED_MANACLES, 1),
        new Item(FREMENNIK_KILT, 1),
        new Item(DRAGON_CROSSBOW, 1),
        new Item(MYSTERY_BOX, 1),
        new Item(WEAPON_MYSTERY_BOX),
        new Item(ARMOUR_MYSTERY_BOX)
    );

    private static final List<Item> UNCOMMON = Arrays.asList(
        new Item(DONATOR_MYSTERY_BOX),
        new Item(DIVINE_SUPER_COMBAT_POTION4 + 1, 50),
        new Item(STAMINA_POTION4 + 1, 25),
        new Item(SUPER_ANTIFIRE_POTION4 + 1, 25),
        new Item(BLOOD_MONEY, World.getWorld().random(3_000, 5_000)),
        new Item(ABYSSAL_DAGGER_P_13271),
        new Item(SARADOMIN_GODSWORD),
        new Item(ZAMORAK_GODSWORD),
        new Item(BANDOS_GODSWORD),
        new Item(BANDOS_CHESTPLATE),
        new Item(BANDOS_TASSETS),
        new Item(ARMADYL_CHAINSKIRT),
        new Item(ARMADYL_CHAINSKIRT),
        new Item(ARMADYL_HELMET),
        new Item(DRAGONFIRE_SHIELD),
        new Item(ODIUM_WARD),
        new Item(MALEDICTION_WARD)
    );

    private static final List<Item> RARE = Arrays.asList(
        new Item(ARMADYL_GODSWORD),
        new Item(DRAGON_CLAWS),
        new Item(ABYSSAL_BLUDGEON),
        new Item(ARMADYL_CROSSBOW),
        new Item(PEGASIAN_BOOTS),
        new Item(ETERNAL_BOOTS),
        new Item(PRIMORDIAL_BOOTS),
        new Item(BLOOD_MONEY, World.getWorld().random(10_000, 75_000)),
        new Item(SERPENTINE_HELM),
        new Item(TOXIC_STAFF_OF_THE_DEAD),
        new Item(TOXIC_BLOWPIPE),
        new Item(TRIDENT_OF_THE_SWAMP)
    );
}
