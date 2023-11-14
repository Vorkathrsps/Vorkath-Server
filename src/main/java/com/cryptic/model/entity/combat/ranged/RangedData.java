package com.cryptic.model.entity.combat.ranged;

import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.weapon.FightType;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * A table of constants that hold data for all ranged ammo.
 * <p>
 * Edit: This is now purely only data.
 * Updated it and moved all methods to CombatFactory.
 *
 * @author Swiffy96
 * @author Professor Oak
 */
public class RangedData {

    public static int boltSpecialChance(boolean always_spec) {
        int percentage = 10;
        return always_spec ? 100 : percentage;
    }

    public static boolean zaryteCrossBowEvoke(Player p) {
        return p.getEquipment().contains(ItemIdentifiers.ZARYTE_CROSSBOW) && p.isSpecialActivated() && p.getCombatSpecial() == CombatSpecial.ZARYTE_CROSSBOW;
    }


    /**
     * A map of items and their respective interfaces.
     */
    private static final Map<Integer, RangedWeapon> rangedWeapons = new HashMap<>();

    public enum RangedWeapon {

        LONGBOW(new int[]{ItemIdentifiers.LONGBOW}, RangedWeaponType.LONGBOW, true),
        SHORTBOW(new int[]{ItemIdentifiers.SHORTBOW}, RangedWeaponType.SHORTBOW, true),
        OAK_LONGBOW(new int[]{ItemIdentifiers.OAK_LONGBOW}, RangedWeaponType.LONGBOW, true),
        OAK_SHORTBOW(new int[]{ItemIdentifiers.OAK_SHORTBOW}, RangedWeaponType.SHORTBOW, true),
        WILLOW_LONGBOW(new int[]{ItemIdentifiers.WILLOW_LONGBOW}, RangedWeaponType.LONGBOW, true),
        WILLOW_SHORTBOW(new int[]{ItemIdentifiers.WILLOW_SHORTBOW}, RangedWeaponType.SHORTBOW, true),
        MAPLE_LONGBOW(new int[]{ItemIdentifiers.MAPLE_LONGBOW}, RangedWeaponType.LONGBOW, true),
        MAPLE_SHORTBOW(new int[]{ItemIdentifiers.MAPLE_SHORTBOW}, RangedWeaponType.SHORTBOW, true),
        YEW_LONGBOW(new int[]{ItemIdentifiers.YEW_LONGBOW}, RangedWeaponType.LONGBOW, true),
        YEW_SHORTBOW(new int[]{ItemIdentifiers.YEW_SHORTBOW}, RangedWeaponType.SHORTBOW, true),
        MAGIC_LONGBOW(new int[]{ItemIdentifiers.MAGIC_LONGBOW}, RangedWeaponType.LONGBOW, true),
        MAGIC_SHORTBOW(new int[]{ItemIdentifiers.MAGIC_SHORTBOW, ItemIdentifiers.MAGIC_SHORTBOW_I}, RangedWeaponType.SHORTBOW, true),
        _3RD_AGE_BOW(new int[]{ItemIdentifiers._3RD_AGE_BOW}, RangedWeaponType.SHORTBOW, true),
        DARK_BOW(new int[]{ItemIdentifiers.DARK_BOW}, RangedWeaponType.LONGBOW, true),
        DARK_BOW_BH(new int[]{ItemIdentifiers.DARK_BOW_BH}, RangedWeaponType.LONGBOW, true),
        TWISTED_BOW(new int[]{ItemIdentifiers.TWISTED_BOW, CORRUPTED_TWISTED_BOW}, RangedWeaponType.TWISTED_BOW, true),
        BRONZE_CROSSBOW(new int[]{ItemIdentifiers.BRONZE_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        IRON_CROSSBOW(new int[]{ItemIdentifiers.IRON_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        STEEL_CROSSBOW(new int[]{ItemIdentifiers.STEEL_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        MITHRIL_CROSSBOW(new int[]{ItemIdentifiers.MITHRIL_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        ADAMANT_CROSSBOW(new int[]{ItemIdentifiers.ADAMANT_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        RUNE_CROSSBOW(new int[]{ItemIdentifiers.RUNE_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        ARMADYL_CROSSBOW(new int[]{ItemIdentifiers.ARMADYL_CROSSBOW}, RangedWeaponType.ARMADYL_CROSSBOW, true),
        ZARYTE_CROSSBOW(new int[]{ItemIdentifiers.ZARYTE_CROSSBOW}, RangedWeaponType.ZARYTE_CROSSBOW, true),

        DRAGON_CROSSBOW(new int[]{ItemIdentifiers.DRAGON_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        DRAGON_HUNTER_CROSSBOW(new int[]{ItemIdentifiers.DRAGON_HUNTER_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),

        HUNTERS_CROSSBOW(new int[]{ItemIdentifiers.HUNTERS_CROSSBOW}, RangedWeaponType.CROSSBOWS, true),
        KARILS_CROSSBOW(new int[]{ItemIdentifiers.KARILS_CROSSBOW}, RangedWeaponType.KARILS_CROSSBOW, true),

        BRONZE_DART(new int[]{ItemIdentifiers.BRONZE_DART}, RangedWeaponType.DARTS, false),
        IRON_DART(new int[]{ItemIdentifiers.IRON_DART}, RangedWeaponType.DARTS, false),
        STEEL_DART(new int[]{ItemIdentifiers.STEEL_DART}, RangedWeaponType.DARTS, false),
        MITHRIL_DART(new int[]{ItemIdentifiers.MITHRIL_DART}, RangedWeaponType.DARTS, false),
        ADAMANT_DART(new int[]{ItemIdentifiers.ADAMANT_DART}, RangedWeaponType.DARTS, false),
        RUNE_DART(new int[]{ItemIdentifiers.RUNE_DART}, RangedWeaponType.DARTS, false),
        DRAGON_DART(new int[]{ItemIdentifiers.DRAGON_DART}, RangedWeaponType.DARTS, false),

        BRONZE_KNIFE(new int[]{ItemIdentifiers.BRONZE_KNIFE, BRONZE_KNIFEP, BRONZE_KNIFEP_5654}, RangedWeaponType.KNIVES, false),
        IRON_KNIFE(new int[]{ItemIdentifiers.IRON_KNIFE, IRON_KNIFEP, IRON_KNIFEP_5655}, RangedWeaponType.KNIVES, false),
        STEEL_KNIFE(new int[]{ItemIdentifiers.STEEL_KNIFE, STEEL_KNIFEP, STEEL_KNIFEP_5656}, RangedWeaponType.KNIVES, false),
        BLACK_KNIFE(new int[]{ItemIdentifiers.BLACK_KNIFE, MITHRIL_KNIFEP, MITHRIL_KNIFEP_5657}, RangedWeaponType.KNIVES, false),
        MITHRIL_KNIFE(new int[]{ItemIdentifiers.MITHRIL_KNIFE, BLACK_KNIFEP, BLACK_KNIFEP_5658}, RangedWeaponType.KNIVES, false),
        ADAMANT_KNIFE(new int[]{ItemIdentifiers.ADAMANT_KNIFE, ADAMANT_KNIFEP, ADAMANT_KNIFEP_5659}, RangedWeaponType.KNIVES, false),
        RUNE_KNIFE(new int[]{ItemIdentifiers.RUNE_KNIFE, RUNE_KNIFEP, RUNE_KNIFEP_5660, RUNE_KNIFEP_5667}, RangedWeaponType.KNIVES, false),
        DRAGON_KNIFE(new int[]{ItemIdentifiers.DRAGON_KNIFE, DRAGON_KNIFEP, DRAGON_KNIFEP_22808, DRAGON_KNIFEP_22810}, RangedWeaponType.KNIVES, false),

        BRONZE_THROWNAXE(new int[]{ItemIdentifiers.BRONZE_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        IRON_THROWNAXE(new int[]{ItemIdentifiers.IRON_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        STEEL_THROWNAXE(new int[]{ItemIdentifiers.STEEL_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        MITHRIL_THROWNAXE(new int[]{ItemIdentifiers.MITHRIL_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        ADAMANT_THROWNAXE(new int[]{ItemIdentifiers.ADAMANT_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        RUNE_THROWNAXE(new int[]{ItemIdentifiers.RUNE_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        DRAGON_THROWNAXE(new int[]{ItemIdentifiers.DRAGON_THROWNAXE}, RangedWeaponType.THROWING_AXES, false),
        TOKTZ_XIL_UL(new int[]{TOKTZXILUL}, RangedWeaponType.THROWING_AXES, false),

        MORRIGANS_THROWING_AXE(new int[]{ItemIdentifiers.MORRIGANS_THROWING_AXE}, RangedWeaponType.THROWING_AXES, false),
        MORRIGANS_JAVALIN(new int[]{MORRIGANS_JAVELIN}, RangedWeaponType.THROWING_AXES, false),

        BALLISTA(new int[]{LIGHT_BALLISTA, HEAVY_BALLISTA}, RangedWeaponType.BALLISTA, true),

        TOXIC_BLOWPIPE(new int[]{ItemIdentifiers.TOXIC_BLOWPIPE}, RangedWeaponType.TOXIC_BLOWPIPE, false),

        CRAWS_BOW(new int[]{ItemIdentifiers.CRAWS_BOW_U, ItemIdentifiers.CRAWS_BOW}, RangedWeaponType.CRAWS_BOW, false),

        BOW_OF_FAERDHINEN(new int[]{ItemIdentifiers.BOW_OF_FAERDHINEN, ItemIdentifiers.BOW_OF_FAERDHINEN_27187, ItemIdentifiers.BOW_OF_FAERDHINEN_C, ItemIdentifiers.BOW_OF_FAERDHINEN_C_25869,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25884,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25886,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25888,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25890,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25892,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25892,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25896,ItemIdentifiers.BOW_OF_FAERDHINEN_C_25896}, RangedWeaponType.SHORTBOW, false),

        CRYSTAL_BOW(new int[]{ItemIdentifiers.NEW_CRYSTAL_BOW, ItemIdentifiers.NEW_CRYSTAL_BOW_4213, ItemIdentifiers.CRYSTAL_BOW_FULL, ItemIdentifiers.CRYSTAL_BOW_910, ItemIdentifiers.CRYSTAL_BOW_810, ItemIdentifiers.CRYSTAL_BOW_710, ItemIdentifiers.CRYSTAL_BOW_610, ItemIdentifiers.CRYSTAL_BOW_510, ItemIdentifiers.CRYSTAL_BOW_410, ItemIdentifiers.CRYSTAL_BOW_310, ItemIdentifiers.CRYSTAL_BOW_210, ItemIdentifiers.CRYSTAL_BOW_110, ItemIdentifiers.NEW_CRYSTAL_BOW_I, ItemIdentifiers.CRYSTAL_BOW_FULL_I, ItemIdentifiers.CRYSTAL_BOW_910_I, ItemIdentifiers.CRYSTAL_BOW_810_I, ItemIdentifiers.CRYSTAL_BOW_710_I, ItemIdentifiers.CRYSTAL_BOW_610_I, ItemIdentifiers.CRYSTAL_BOW_510_I, ItemIdentifiers.CRYSTAL_BOW_410_I, ItemIdentifiers.CRYSTAL_BOW_310_I, ItemIdentifiers.CRYSTAL_BOW_210_I, ItemIdentifiers.CRYSTAL_BOW_110_I, ItemIdentifiers.NEW_CRYSTAL_BOW_16888, ItemIdentifiers.NEW_CRYSTAL_BOW_I_16889}, RangedWeaponType.CRYSTAL_BOW, false),

        VENATOR_BOW(new int[]{ItemIdentifiers.VENATOR_BOW}, RangedWeaponType.VENATOR_BOW, true),

        WEBWEAVER_BOW(new int[]{ItemIdentifiers.WEBWEAVER_BOW}, RangedWeaponType.WEBWEAVER_BOW, false),

        CHINCHOMPA(new int[]{ItemIdentifiers.CHINCHOMPA_10033, RED_CHINCHOMPA_10034, ItemIdentifiers.BLACK_CHINCHOMPA}, RangedWeaponType.CHINCHOMPA, false);

        static {
            for (RangedWeapon data : RangedWeapon.values()) {
                for (int i : data.getWeaponIds()) {
                    rangedWeapons.put(i, data);
                }
            }
        }

        private final int[] weaponIds;
        private final RangedWeaponType type;
        private final boolean factorInAmmoRangeStr;

        RangedWeapon(int[] weaponIds, RangedWeaponType type, boolean factorInAmmoRangeStr) {
            this.weaponIds = weaponIds;
            this.type = type;
            this.factorInAmmoRangeStr = factorInAmmoRangeStr;
        }

        public static RangedWeapon getFor(Player player) {
            Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
            if (weapon == null) {
                player.message("This weapon isn't recognized as ranged weapon. Maybe I should report this.");
                return null;
            }
            //System.out.printf("%s item id set %s%n", player.getUsername(), weapon);
            return rangedWeapons.get(weapon.getId());
        }

        public int[] getWeaponIds() {
            return weaponIds;
        }

        public RangedWeaponType getType() {
            return type;
        }

        public boolean ignoreArrowsSlot() {
            return factorInAmmoRangeStr;
        }
    }

    public enum RangedWeaponType {
        DARTS(3, 5, FightType.THROWING_LONGRANGE, false),
        KNIVES(4, 6, FightType.THROWING_LONGRANGE, false),
        THROWING_AXES(4, 6, FightType.THROWING_LONGRANGE, false),
        TOXIC_BLOWPIPE(5, 7, FightType.THROWING_LONGRANGE, false),
        DORGESHUUN_CBOW(6, 8, FightType.BOLT_LONGRANGE, true),
        CROSSBOWS(7, 9, FightType.BOLT_LONGRANGE, true),
        SHORTBOW(7, 9, FightType.ARROW_LONGRANGE, true),
        ARMADYL_CROSSBOW(8, 10, FightType.BOLT_LONGRANGE, true),
        ZARYTE_CROSSBOW(8, 10, FightType.BOLT_LONGRANGE, true),
        ZARYTE_I(8, 10, FightType.BOLT_LONGRANGE, true),
        KARILS_CROSSBOW(8, 10, FightType.BOLT_LONGRANGE, true),
        SEERCULL_BOW(8, 10, FightType.ARROW_LONGRANGE, false),
        BALLISTA(9, 10, FightType.ARROW_LONGRANGE, true),
        CHINCHOMPA(9, 10, FightType.THROWING_LONGRANGE, false),
        TOKTZ_XIL_UL(6, 8, FightType.THROWING_RAPID, false),
        _3_AGE_BOW(9, 10, FightType.ARROW_LONGRANGE, false),
        CRAWS_BOW(9, 10, FightType.ARROW_LONGRANGE, false),
        BOW_OF_FAERDHINEN(10, 10, FightType.ARROW_LONGRANGE, false),
        LONGBOW(10, 10, FightType.ARROW_LONGRANGE, true),
        CRYSTAL_BOW(10, 10, FightType.ARROW_LONGRANGE, false),
        DARK_BOW(10, 10, FightType.ARROW_LONGRANGE, true),
        TWISTED_BOW(10, 10, FightType.ARROW_LONGRANGE, true),
        VENATOR_BOW(6, 6, FightType.ARROW_RAPID, true),
        WEBWEAVER_BOW(9, 9, FightType.ARROW_RAPID, false);

        private final FightType longRangeFightType;
        private final int defaultDistance;
        private final int longRangeDistance;
        private final boolean ammoRequired;

        RangedWeaponType(int defaultDistance, int longRangeDistance, FightType longRangeFightType, boolean ammoRequired) {
            this.defaultDistance = defaultDistance;
            this.longRangeDistance = longRangeDistance;
            this.longRangeFightType = longRangeFightType;
            this.ammoRequired = ammoRequired;
        }

        public int getDefaultDistance() {
            return defaultDistance;
        }

        public int getLongRangeDistance() {
            return longRangeDistance;
        }

        public FightType getLongRangeFightType() {
            return longRangeFightType;
        }

        public boolean isAmmoRequired() {
            return ammoRequired;
        }
    }

}
