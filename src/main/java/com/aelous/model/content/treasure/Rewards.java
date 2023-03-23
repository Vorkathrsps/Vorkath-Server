package com.aelous.model.content.treasure;

import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PVE
 * @Since juli 09, 2020
 */
public class Rewards {

    public static void generateReward(Player player) {
        int rewards = World.getWorld().random(1, 3);

        var diggingForTreasure = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.DIGGING_FOR_TREASURE);

        if(diggingForTreasure) {
            rewards = World.getWorld().random(2, 4);
        }

        for (int index = 0; index < rewards; index++) {
            double roll = Utils.RANDOM_GEN.nextDouble() * 100.0;
            List<StandardTable> possibles = Arrays.stream(StandardTable.values()).filter(r -> roll <= r.probability).collect(Collectors.toList());

            StandardTable reward = Utils.randomElement(possibles);
            if (reward != null)
                player.clueScrollReward().add(new Item(reward.reward, reward.reward.getAmount()), true);
        }

        //7 if lucky and got a rare
        if (World.getWorld().rollDie(2, 1)) {
            double roll = Utils.RANDOM_GEN.nextDouble() * 100.0;
            List<RareTable> possibles = Arrays.stream(RareTable.values()).filter(r -> roll <= r.probability).collect(Collectors.toList());
            RareTable reward = Utils.randomElement(possibles);
            if (reward != null) {
                player.clueScrollReward().add(new Item(reward.reward, reward.reward.getAmount()), true);
                if (reward.reward.getValue() >= 30000 && !player.getUsername().equalsIgnoreCase("Box test"))
                    World.getWorld().sendWorldMessage("<img=2013><col=0052cc>" + player.getUsername() + " has just received <col=" + Color.HOTPINK.getColorValue() + ">" + Utils.getAOrAn(reward.reward.name()) + " " + reward.reward.name() + " <col=0052cc>from a master casket!");
            }
        }
    }

    public enum StandardTable {
        BLOOD_MONEY(100.0, new Item(ItemIdentifiers.BLOOD_MONEY,15_000)),
        OPAL_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.OPAL_DRAGON_BOLTS_E, 1_000)),
        DRAGONSTONE_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS_E, 1_000)),
        RUBY_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.RUBY_DRAGON_BOLTS_E, 1_000)),
        DIAMOND_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.DIAMOND_DRAGON_BOLTS_E, 1_000)),
        ONYX_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.ONYX_DRAGON_BOLTS_E, 1_000)),
        ROYAL_GOWN_CROWN(50.0, new Item(ItemIdentifiers.ROYAL_CROWN)),
        ROYAL_GOWN_SCEPTRE(50.0, new Item(ItemIdentifiers.ROYAL_SCEPTRE)),
        ROYAL_GOWN_TOP(50.0, new Item(ItemIdentifiers.ROYAL_GOWN_TOP)),
        ROYAL_GOWN_BOTTOM(50.0, new Item(ItemIdentifiers.ROYAL_GOWN_BOTTOM)),
        DRAGON_CROSSBOW(40.0, new Item(ItemIdentifiers.DRAGON_CROSSBOW)),
        BANDOS_GODSWORD(40.0, new Item(ItemIdentifiers.BANDOS_GODSWORD)),
        ZAMORAK_GODSWORD(95.0, new Item(ItemIdentifiers.ZAMORAK_GODSWORD)),
        SARADOMIN_GODSWORD(90.0, new Item(ItemIdentifiers.SARADOMIN_GODSWORD)),
        ICE_ARROWS(85.0, new Item(ItemIdentifiers.ICE_ARROWS, 1_000)),
        DRAGON_JAVELIN(65.0, new Item(ItemIdentifiers.DRAGON_JAVELIN,100)),
        DRAGON_KNIFE(60.0, new Item(ItemIdentifiers.DRAGON_KNIFE,100)),
        DRAGON_KNIFE_P_PLUS_PLUS(55.0, new Item(ItemIdentifiers.DRAGON_KNIFEP_22810,100)),
        DRAGON_THROWNAXE(50.0, new Item(ItemIdentifiers.DRAGON_THROWNAXE, 100)),
        ABYSSAL_DAGGER_P_13271(45.0, new Item(ItemIdentifiers.ABYSSAL_DAGGER_P_13271)),
        ODIUM_WARD(40.0, new Item(ItemIdentifiers.ODIUM_WARD)),
        MALEDICTION_WARD(37.0, new Item(ItemIdentifiers.MALEDICTION_WARD)),
        ARMADYL_CROSSBOW(35.0, new Item(ItemIdentifiers.ARMADYL_CROSSBOW)),
        DRAGONFIRE_SHIELD(30.0, new Item(ItemIdentifiers.DRAGONFIRE_SHIELD)),
        ARMADYL_CHAINSKIRT(25.0, new Item(ItemIdentifiers.ARMADYL_CHAINSKIRT)),
        ARMADYL_CHESTPLATE(20.0, new Item(ItemIdentifiers.ARMADYL_CHESTPLATE)),
        BANDOS_CHESTPLATE(15.0, new Item(ItemIdentifiers.BANDOS_CHESTPLATE)),
        BANDOS_TASSETS(10.0, new Item(ItemIdentifiers.BANDOS_TASSETS)),
        ARMADYL_GODSWORD(5.0, new Item(ItemIdentifiers.ARMADYL_GODSWORD));

        private final double probability;
        private final Item reward;

        StandardTable(double probability, Item reward) {
            this.probability = probability;
            this.reward = reward;
        }
    }

    private enum RareTable {
        GILDED_MEDIUM_HELM(20.0, new Item(ItemIdentifiers.GILDED_MED_HELM)),
        GILDED_CHAIN_MAIL(20.0, new Item(ItemIdentifiers.GILDED_CHAINBODY)),
        GILDED_SQUARE_SHIELD(20.0, new Item(ItemIdentifiers.GILDED_SQ_SHIELD)),
        GILDED_TWO_HANDED(20.0, new Item(ItemIdentifiers.GILDED_2H_SWORD)),
        GILDED_SPEAR(20.0, new Item(ItemIdentifiers.GILDED_SPEAR)),
        GILDED_HASTA(20.0, new Item(ItemIdentifiers.GILDED_HASTA)),
        TEAM_CAPE_ZERO(30.0, new Item(ItemIdentifiers.TEAM_CAPE_ZERO)),
        TEAM_CAPE_X(30.0, new Item(ItemIdentifiers.TEAM_CAPE_X)),
        TEAM_CAPE_I(30.0, new Item(ItemIdentifiers.TEAM_CAPE_I)),
        WOODEN_SHIELD_G(30.0, new Item(ItemIdentifiers.WOODEN_SHIELD_G)),
        GOLDEN_CHEFS_HAT(20.0, new Item(ItemIdentifiers.GOLDEN_CHEFS_HAT)),
        GOLDEN_APRON(20.0, new Item(ItemIdentifiers.GOLDEN_APRON)),
        MONKS_ROBE_TOP_G(20.0, new Item(ItemIdentifiers.MONKS_ROBE_TOP_G)),
        MONKS_ROBE_BOT_G(20.0, new Item(ItemIdentifiers.MONKS_ROBE_G)),
        LARGE_SPADE(10.0, new Item(ItemIdentifiers.LARGE_SPADE)),
        HOLY_SANDALS(5.0, new Item(ItemIdentifiers.HOLY_SANDALS)),
        SAMURAI_KASA(60.0, new Item(ItemIdentifiers.SAMURAI_KASA)),
        SAMURAI_SHIRT(60.0, new Item(ItemIdentifiers.SAMURAI_SHIRT)),
        SAMURAI_GLOVES(60.0, new Item(ItemIdentifiers.SAMURAI_GLOVES)),
        SAMURAI_GREAVES(60.0, new Item(ItemIdentifiers.SAMURAI_GREAVES)),
        SAMURAI_BOOTS(60.0, new Item(ItemIdentifiers.SAMURAI_BOOTS)),
        MUMMYS_HEAD(50.0, new Item(ItemIdentifiers.MUMMYS_HEAD)),
        MUMMYS_BODY(50.0, new Item(ItemIdentifiers.MUMMYS_BODY)),
        MUMMYS_HANDS(50.0, new Item(ItemIdentifiers.MUMMYS_HANDS)),
        MUMMYS_LEGS(50.0, new Item(ItemIdentifiers.MUMMYS_LEGS)),
        MUMMYS_FEET(50.0, new Item(ItemIdentifiers.MUMMYS_FEET)),
        ANKOU_MASK(40.0, new Item(ItemIdentifiers.ANKOU_MASK)),
        ANKOU_TOP(40.0, new Item(ItemIdentifiers.ANKOU_TOP)),
        ANKOU_GLOVES(40.0, new Item(ItemIdentifiers.ANKOU_GLOVES)),
        ANKOU_LEGS(40.0, new Item(ItemIdentifiers.ANKOUS_LEGGINGS)),
        ANKOU_SOCKS(40.0, new Item(ItemIdentifiers.ANKOU_SOCKS)),
        HOOD_OF_DARKNESS(30.0, new Item(ItemIdentifiers.HOOD_OF_DARKNESS)),
        ROBE_TOP_OF_DARKNESS(30.0, new Item(ItemIdentifiers.ROBE_TOP_OF_DARKNESS)),
        GLOVES_OF_DARKNESS(30.0, new Item(ItemIdentifiers.GLOVES_OF_DARKNESS)),
        ROBE_BOTTOM_OF_DARKNESS(30.0, new Item(ItemIdentifiers.ROBE_BOTTOM_OF_DARKNESS)),
        BOOTS_OF_DARKNESS(30.0, new Item(ItemIdentifiers.BOOTS_OF_DARKNESS)),
        OBSIDIAN_CAPE_R(20.0, new Item(ItemIdentifiers.OBSIDIAN_CAPE_R)),
        HALF_MOON_SPECTACLES(10.0, new Item(ItemIdentifiers.HALF_MOON_SPECTACLES)),
        ALE_OF_THE_GODS(5.0, new Item(ItemIdentifiers.ALE_OF_THE_GODS)),
        BUCKET_HELM_G(5.0, new Item(ItemIdentifiers.BUCKET_HELM_G)),
        DRAGON_CLAWS(11.0, new Item(ItemIdentifiers.DRAGON_CLAWS)),
        THIRD_AGE_MELEE_HELM(13.0, new Item(ItemIdentifiers._3RD_AGE_FULL_HELMET)),
        THIRD_AGE_MELEE_BODY(12.0, new Item(ItemIdentifiers._3RD_AGE_PLATEBODY)),
        THIRD_AGE_MELEE_LEGS(12.0, new Item(ItemIdentifiers._3RD_AGE_PLATELEGS)),
        THIRD_AGE_MELEE_KITE(13.0, new Item(ItemIdentifiers._3RD_AGE_KITESHIELD)),
        THIRD_AGE_RANGE_COIF(13.0, new Item(ItemIdentifiers._3RD_AGE_RANGE_COIF)),
        THIRD_AGE_RANGE_BODY(12.0, new Item(ItemIdentifiers._3RD_AGE_RANGE_TOP)),
        THIRD_AGE_RANGE_LEGS(12.0, new Item(ItemIdentifiers._3RD_AGE_RANGE_LEGS)),
        THIRD_AGE_RANGE_VAMBS(13.0, new Item(ItemIdentifiers._3RD_AGE_VAMBRACES)),
        THIRD_AGE_HAT(13.0, new Item(ItemIdentifiers._3RD_AGE_MAGE_HAT)),
        THIRD_AGE_TOP(12.0, new Item(ItemIdentifiers._3RD_AGE_ROBE)),
        THIRD_AGE_SKIRT(12.0, new Item(ItemIdentifiers._3RD_AGE_ROBE_TOP)),
        THIRD_AGE_AMULET(15.0, new Item(ItemIdentifiers._3RD_AGE_AMULET)),
        THIRD_AGE_CLOAK(7.0, new Item(ItemIdentifiers._3RD_AGE_CLOAK)),
        THIRD_AGE_WAND(5.0, new Item(ItemIdentifiers._3RD_AGE_WAND)),
        THIRD_AGE_BOW(5.0, new Item(ItemIdentifiers._3RD_AGE_BOW)),
        THIRD_AGE_LONGSWORD(5.0, new Item(ItemIdentifiers._3RD_AGE_LONGSWORD)),
        THIRD_AGE_DRUIDIC_ROBE_TOP(2.0, new Item(ItemIdentifiers._3RD_AGE_DRUIDIC_ROBE_TOP)),
        THIRD_AGE_DRUIDIC_ROBE_BOTTOMS(2.0, new Item(ItemIdentifiers._3RD_AGE_DRUIDIC_ROBE_BOTTOMS)),
        THIRD_AGE_DRUIDIC_STAFF(2.0, new Item(ItemIdentifiers._3RD_AGE_DRUIDIC_STAFF)),
        THIRD_AGE_DRUIDIC_CLOAK(2.0, new Item(ItemIdentifiers._3RD_AGE_DRUIDIC_CLOAK)),
        ;

        private final double probability;
        private final Item reward;

        RareTable(double probability, Item reward) {
            this.probability = probability;
            this.reward = reward;
        }
    }
}
