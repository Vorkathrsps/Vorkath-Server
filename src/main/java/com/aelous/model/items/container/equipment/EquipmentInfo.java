package com.aelous.model.items.container.equipment;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.combat.weapon.FightType;
import com.aelous.model.entity.combat.weapon.WeaponType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCCombatInfo;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.JGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * Created by Bart on 8/14/2015.
 */
public class EquipmentInfo {

    private static final Logger logger = LogManager.getLogger(EquipmentInfo.class);

    // Stand, turn, walk, turn, sidestep, sidestep, run
    public static final int[] DEFAULT_RENDERPAIR = {808, 823, 819, 820, 821, 822, 824};
    private static final int[] DEFAULT_WEAPON_RENDERPAIR = {809, 823, 819, 820, 821, 822, 824};
    private static final Bonuses DEFAULT_BONUSES = new Bonuses();

    private final Map<Integer, int[]> renderMap = new LinkedHashMap<>();
    private static Map<Integer, Bonuses> bonuses = new LinkedHashMap<>();
    private final Map<Integer, WeaponType> weaponTypes = new LinkedHashMap<>();
    private final Map<Integer, Integer> weaponSpeeds = new LinkedHashMap<>();
    private final Map<Integer, Map<Integer, Integer>> itemRequirements = new LinkedHashMap<>();
    private Map<Integer, EquipmentDefinition> equipmentDefinitions = new LinkedHashMap<>();

    private static final Gson gson = JGson.get();

    public EquipmentInfo(File equipmentDefinitions, File renderPairs, File bonuses, File weaponTypes, File weaponSpeeds) {
        //// 5=shield, 6=full body (no arms), 8/11 = showing/hiding beard, hair
        loadEquipmentDefinitions(equipmentDefinitions);
        loadRenderPairs(renderPairs);
        loadBonuses(bonuses);
        loadWeaponTypes(weaponTypes);
        loadWeaponSpeeds(weaponSpeeds);
        loadEquipmentRequirements(new File("data/list/requirements.txt"));
    }

    public static Bonuses totalBonuses(Entity mob, EquipmentInfo infoo) {
        return totalBonuses(mob, infoo, false);
    }

    public static Bonuses totalBonuses(Entity entity, EquipmentInfo info, boolean ignoreAmmo) {
        Bonuses bonuses = new Bonuses();

        if (entity.isPlayer()) {
            Player player = ((Player) entity);
            Item wep = player.getEquipment().get(EquipSlot.WEAPON);
            int wepid = wep != null ? wep.getId() : -1;
            if (Equipment.hasAmmyOfDamned(player) && Equipment.hasVerac(player)) {
                bonuses.pray += 4;
            }

            /**
             * Dihns Bulwark
             */
            FightStyle style = player.getCombat().getFightType().getStyle();
            FightType type = player.getCombat().getFightType();

            if (player.getEquipment().hasAt(EquipSlot.WEAPON, DINHS_BULWARK)) {
            if (type.equals(FightType.DINHS_PUMMEL)) {
                    int formula2 = (int) (((bonuses.stabdef + bonuses.slashdef + bonuses.crushdef + bonuses.rangedef) / 12) - 104.66D);
                    bonuses.str += formula2;
                    System.out.println(formula2);
                }
            }

            for (int i = 0; i < 14; i++) {
                if (i == EquipSlot.AMMO && ignoreAmmo) {
                    continue;
                }

                Item equipped = player.getEquipment().get(i);
                if (equipped != null) {
                    if (i == EquipSlot.AMMO && ((wepid >= 4212 && wepid <= 4223) || wepid == TOXIC_BLOWPIPE)) { // crystal bow /blowpipe ignore ammo
                        // these don't fucking factor ammo
                        continue;
                    }

                    Bonuses equip = info.bonuses(equipped.getId());

                    bonuses.stab += equip.stab;
                    bonuses.slash += equip.slash;
                    bonuses.crush += equip.crush;
                    bonuses.range += equip.range;
                    bonuses.mage += equip.mage;

                    bonuses.stabdef += equip.stabdef;
                    bonuses.slashdef += equip.slashdef;
                    bonuses.crushdef += equip.crushdef;
                    bonuses.rangedef += equip.rangedef;
                    bonuses.magedef += equip.magedef;

                    bonuses.str += equip.str;
                    bonuses.rangestr += equip.rangestr;
                    bonuses.magestr += equip.magestr;
                    bonuses.pray += equip.pray;
                }
            }
        } else {
            NPC npc = (NPC) entity;
            if (npc.getCombatInfo() != null) {
                NPCCombatInfo.Bonuses i = npc.getCombatInfo().originalBonuses;
                bonuses.stabdef = i.stabdefence;
                bonuses.slashdef = i.slashdefence;
                bonuses.crushdef = i.crushdefence;
                bonuses.rangedef = i.rangeddefence;
                bonuses.magedef = i.magicdefence;
                bonuses.range = i.ranged;
                bonuses.mage = i.magic;
                bonuses.str = i.strength;
                bonuses.crush = i.attack;
                bonuses.stab = i.attack;
                bonuses.slash = i.attack;
            }
        }

        return bonuses;
    }

    public static Bonuses criticalBonuses(Player player) {
        EquipmentInfo info = World.getWorld().equipmentInfo();
        Bonuses bonuses = new Bonuses();
        for (int i : new int[]{EquipSlot.BODY, EquipSlot.LEGS, EquipSlot.SHIELD, EquipSlot.HEAD}) {
            Item equipped = player.getEquipment().get(i);
            if (equipped != null) {
                Bonuses equip = info.bonuses(equipped.getId());

                bonuses.stab += equip.stab;
                bonuses.slash += equip.slash;
                bonuses.crush += equip.crush;
                bonuses.range += equip.range;
                bonuses.mage += equip.mage;

                bonuses.stabdef += equip.stabdef;
                bonuses.slashdef += equip.slashdef;
                bonuses.crushdef += equip.crushdef;
                bonuses.rangedef += equip.rangedef;
                bonuses.magedef += equip.magedef;

                bonuses.str += equip.str;
                bonuses.rangestr += equip.rangestr;
                bonuses.magestr += equip.magestr;
                bonuses.pray += equip.pray;
            }
        }
        return bonuses;
    }

    public static int prayerBonuses(Player player) {
        int pray = 0;

        EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());

        if (Equipment.hasAmmyOfDamned(player) && Equipment.hasVerac(player)) {
            pray += 4;
        }

        for (int i = 0; i < 14; i++) {
            Item equipped = player.getEquipment().get(i);

            if (equipped != null) {

                pray += attackerBonus.pray;
            }
        }

        return pray;
    }

    private void loadEquipmentDefinitions(File file) {
        try {
            equipmentDefinitions = gson.fromJson(new FileReader(file), new TypeToken<HashMap<Integer, EquipmentDefinition>>() {
            }.getType());

            logger.info("Loaded {} equipment information definitions.", equipmentDefinitions.size());
        } catch (FileNotFoundException e) {
            logger.error("Could not load equipment information", e);
        }
    }

    private void loadRenderPairs(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int numdef = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int id = Integer.parseInt(line.split(":")[0]);
                String[] params = line.split(":")[1].split(",");
                int[] pair = new int[7];
                for (int i = 0; i < 7; i++)
                    pair[i] = Integer.parseInt(params[i]);
                renderMap.put(id, pair);
                numdef++;
            }

            logger.info("Loaded {} equipment render pairs.", numdef);
        } catch (FileNotFoundException e) {
            logger.error("Could not load render pairs", e);
        }
    }

    public static void loadBonuses(File file) {
        try {
            bonuses = gson.fromJson(new FileReader(file), new TypeToken<HashMap<Integer, Bonuses>>() {
            }.getType());

            logger.info("Loaded {} equipment bonuses.", bonuses.size());
        } catch (FileNotFoundException e) {
            logger.error("Could not load bonuses", e);
        }
    }

    private void loadWeaponTypes(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int numdef = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int id = Integer.parseInt(line.split(":")[0]);
                WeaponType type = WeaponType.valueOf((line.split(":")[1]));

                weaponTypes.put(id, type);
                numdef++;
            }

            logger.info("Loaded {} weapon types.", numdef);
        } catch (FileNotFoundException e) {
            logger.error("Could not load weapon types.", e);
        }
    }

    private void loadWeaponSpeeds(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int numdef = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int id = Integer.parseInt(line.split(":")[0]);
                int type = Integer.parseInt(line.split(":")[1]);

                weaponSpeeds.put(id, type);
                numdef++;
            }

            logger.info("Loaded {} weapon speeds.", numdef);
        } catch (FileNotFoundException e) {
            logger.error("Could not load weapon speeds.", e);
        }
    }

    private void loadEquipmentRequirements(File file) {
        try (Scanner scanner = new Scanner(file)) {
            int numdef = 0;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                int id = Integer.parseInt(line.split(":")[0]);

                String reqs = line.split(":")[1];
                Map<Integer, Integer> map = new HashMap<>();
                for (String req : reqs.split(",")) {
                    int lvl = Integer.parseInt(req.split("=")[0]);
                    int needed = Integer.parseInt(req.split("=")[1]);
                    map.put(lvl, needed);
                }

                itemRequirements.put(id, map);
                numdef++;
            }

            logger.info("Loaded {} item requirements.", numdef);
        } catch (FileNotFoundException e) {
            logger.error("Could not load item requirements.", e);
        }
    }

    public int slotFor(int id) {
        EquipmentDefinition def = equipmentDefinitions.get(id);
        if (def == null)
            return -1;

        return def.slot;
    }

    public int typeFor(int id) {
        EquipmentDefinition def = equipmentDefinitions.get(id);
        if (def == null)
            return 0;

        return def.type;
    }

    public boolean showBeard(int id) {
        EquipmentDefinition def = equipmentDefinitions.get(id);
        return def != null && (def.showBeard || typeFor(id) != 8);
    }

    public WeaponType weaponType(int id) {
        return weaponTypes.getOrDefault(id, WeaponType.UNARMED);
    }

    public int[] renderPair(int id) {
        if (id == -1)
            return DEFAULT_RENDERPAIR;
        return renderMap.getOrDefault(id, DEFAULT_WEAPON_RENDERPAIR);
    }

    public Bonuses bonuses(int id) {
        return bonuses.getOrDefault(id, DEFAULT_BONUSES);
    }

    public int weaponSpeed(int id) {
        return weaponSpeeds.getOrDefault(id, 4);
    }

    public Map<Integer, Integer> requirementsFor(int id) {
        return itemRequirements.get(id);
    }

    @Data
    public static class Bonuses {

        public int stab;
        public int slash;
        public int crush;
        public int range;
        public int mage;
        public int stabdef;
        public int slashdef;
        public int crushdef;
        public int rangedef;
        public int magedef;
        public int str;
        public int rangestr;
        public int magestr;
        public int pray;

        public int[] bonuses() {
            return new int[]{stab, slash, crush, range, mage};
        }

        public String[] bonusesAtk() {
            return new String[]{"Stab", "Slash", "Crush", "Range", "Mage"};
        }

        @Override
        public String toString() {
            return "Bonuses{" +
                "stab=" + stab +
                ", slash=" + slash +
                ", crush=" + crush +
                ", range=" + range +
                ", mage=" + mage +
                ", stabdef=" + stabdef +
                ", slashdef=" + slashdef +
                ", crushdef=" + crushdef +
                ", rangedef=" + rangedef +
                ", magedef=" + magedef +
                ", str=" + str +
                ", rangestr=" + rangestr +
                ", magestr=" + magestr +
                ", pray=" + pray +
                '}';
        }

    }

    public static class EquipmentDefinition {

        public int slot = -1;
        public int type;
        public boolean showBeard;
    }

    public static int attackAnimationFor(Player player) {
        return attackAnimationFor(player, player.getEquipment().hasAt(EquipSlot.WEAPON) ? player.getEquipment().get(EquipSlot.WEAPON).getId() : 0);
    }

    public static int attackAnimationFor(Player player, int weapon) {
        int style = player.getCombat().getFightType().getChildId();

        // Handle individual cases first
        if (weapon != 0) {
            switch (weapon) {
                case IVANDIS_FLAIL:
                case BLISTERWOOD_FLAIL:
                    return 8010;
                case DRAGON_CLAWS:
                    return style == 2 ? 1067 : 393;
                case 15241:
                    return 12175;
                case 18349:
                    return 386;
                case 18353:
                    return 13055;
                case SCYTHE:
                    return 408;
                case SCYTHE_OF_VITUR:
                    return 8056;
                case GHRAZI_RAPIER: // Rapier
                    return style == 2 ? 390 : 8145;
                case LEAFBLADED_BATTLEAXE:
                    return style == 2 ? 3852 : 7004;
                case 12727:
                    return 2323;
                case 10887: //anchor
                    return 5865;
                case 13263: // Abyssal bludgeon
                    return 3298;
                case 7671:
                case 7673:
                case 11705:
                case 11706: // Boxing gloves
                    return 3678;
                case TUMEKENS_SHADOW:
                    return 9493;
                case 12924: // Toxic blowpipe
                case TOXIC_BLOWPIPE: // Toxic blowpipe
                    return 5061;
                case 10033: // grey chins
                case 10034: // red chins
                case 11959: // black chins
                    return 2779;
                case KARILS_CROSSBOW:
                    return 2075;
                case 6522:
                    return 3353;
                case STAFF_OF_THE_DEAD:
                case TOXIC_STAFF_UNCHARGED:
                case TOXIC_STAFF_OF_THE_DEAD:
                    return style == 0 ? 428 : style == 1 ? 440 : 419;
                case 1215: // dd
                case 1231: // ddp
                case 5680: // ddpp
                case 5698: // dds
                    return style == 2 ? 390 : 402; // Dragon daggers
                case ABYSSAL_DAGGER:
                case ABYSSAL_DAGGER_P:
                case ABYSSAL_DAGGER_P_13269:
                case ABYSSAL_DAGGER_P_13271:
                    return style == 2 ? 3294 : 3297;
                case 20593:
                case 14487:
                case 30003: // gs
                case 11802: // gs
                case 11804: // gs
                case 11806: // gs
                case 11808: // gs
                case 20368: // gs
                case 20370: // gs
                case 20372: // gs
                case 20374: // gs
                case 11838: // sara sword
                case 12808: // blessed ss (full)
                case 12809: // blessed ss
                case 26233:
                    return style == 2 ? 7054 : 7045;
                case 4718: // Dharok's greataxe
                case 4886: // Dharok's greataxe
                case 4887: // Dharok's greataxe
                case 4888: // Dharok's greataxe
                case 4889: // Dharok's greataxe
                    return style == 3 ? 2066 : 2067;
                case 4755: // Verac's flail
                case 4982: // Verac's flail
                case 4983: // Verac's flail
                case 4984: // Verac's flail
                case 4985: // Verac's flail
                    return 2062;
                case 4910: // Guthan's warspear
                case 4911: // Guthan's warspear
                case 4912: // Guthan's warspear
                case 4913: // Guthan's warspear
                case 4914: // Guthan's warspear
                    return style == 1 ? 2081 : style == 2 ? 2082 : 2080;
                case AHRIMS_STAFF:
                case AHRIMS_STAFF_0:
                case AHRIMS_STAFF_25:
                case AHRIMS_STAFF_50:
                case AHRIMS_STAFF_75:
                case AHRIMS_STAFF_100:
                    return 2078;
                case NIGHTMARE_STAFF:
                case HARMONISED_NIGHTMARE_STAFF:
                case VOLATILE_NIGHTMARE_STAFF:
                case ELDRITCH_NIGHTMARE_STAFF:
                    return 4505;
                case 4747: // Torag's hamers
                case 4958: // Torag's hamers
                case 4959: // Torag's hamers
                case 4960: // Torag's hamers
                case 4961: // Torag's hamers
                    return 2068;
                case 24225: // Granite maul
                case 24944:
                case 7668: //Gadderhammer
                case 12848: // Granite clamp
                case 16200:
                case 16201:
                case 16202:
                case 16203:
                case 16204:
                case 16205:
                case 16206:
                case 16207:
                case 16208:
                case 4153:
                    return 1665;
                case 6528: // Obsidian maul
                case 20756: //hill giant club
                    return 2661;
                case 11824: // Zammy spear
                case ZAMORAKIAN_HASTA:
                    switch (style) {
                        case 0, 3 -> {
                            return 1711;
                        }
                        case 1 -> {
                            return 1712;
                        }
                        case 2 -> {
                            return 1710;
                        }
                    }
                case 20779: // H'ween 2016 hunting knife
                    return 7328;
                case 21003: // Elder maul
                case 30005: // Elder ice maul
                case 21205:
                case 7808: // Ancient warrior maul
                    return 7516;

                case 7806:
                    return 390;

                case 7807:
                    return 2066;

                case 21015: // Dinh's Bulwark
                    return 7511;

                case DRAGON_HUNTER_LANCE:
                    return style == 2 ? 8290 : 8288;

                case DRAGON_KNIFE:
                case DRAGON_KNIFEP_22808:
                case DRAGON_KNIFEP_22810:
                case DRAGON_KNIFE_22812:
                    return 8194;

                case OSMUMTENS_FANG:
                case OSMUMTENS_FANG_OR:
                    return 9471;

                case MORRIGANS_THROWING_AXE:
                    return 929;

                case MORRIGANS_JAVELIN:
                case MORRIGANS_JAVELIN_23619:
                    return 806;

                case DRAGON_2H_SWORD:
                    return style == 2 ? 406 : 407;

                case DRAGON_HASTA:
                    return style == 1 ? 440 : style == 2 ? 429 : 428;

                case DRAGON_DART:
                case DRAGON_DARTP:
                case DRAGON_DARTP_11233:
                case DRAGON_DARTP_11234:
                    return 7554;

                case DRAGON_THROWNAXE:
                case DRAGON_THROWNAXE_21207:
                    return 7617;

                case KODAI_WAND:
                case KODAI_WAND_23626:
                case _3RD_AGE_WAND:
                    return 414;

                case INQUISITORS_MACE:
                    return style == 2 ? 400 : 4503;

                case LIGHT_BALLISTA:
                case HEAVY_BALLISTA:
                    return 7555;

                case VIGGORAS_CHAINMACE:
                case VIGGORAS_CHAINMACE_U:
                case URSINE_CHAINMACE:
                    return 245;

                case VENATOR_BOW:
                    return 9858;

                case THAMMARONS_SCEPTRE:
                case THAMMARONS_SCEPTRE_U:
                case ACCURSED_SCEPTRE_AU:
                case ACCURSED_SCEPTRE:
                case ACCURSED_SCEPTRE_A:
                    return 1058;

                case BRONZE_CROSSBOW:
                case IRON_CROSSBOW:
                case STEEL_CROSSBOW:
                case ADAMANT_CROSSBOW:
                case RUNE_CROSSBOW:
                case DRAGON_CROSSBOW:
                case ARMADYL_CROSSBOW:
                    return 4230;
                case ZARYTE_CROSSBOW:
                    return 9166;
                case DRAGON_HUNTER_CROSSBOW:
                case 18357:
                    return 7552;
            }
        }

        // Then resolve the remaining ones from the guessing based on book type

        EquipmentInfo info = World.getWorld().equipmentInfo();
        WeaponType book = info.weaponType(weapon);
        if (book == null) {//Unarmed
            return style == 1 ? 423 : 422;
        }
        return switch (book) {
            case UNARMED -> style == 1 ? 423 : 422;
            case CHINCHOMPA -> 7618;
            case AXE -> style == 2 ? 401 : 395;
            case HAMMER -> 401;
            case BOW -> 426;
            case CROSSBOW -> 4230;
            case LONGSWORD -> style == 2 ? 386 : 390;
            case TWOHANDED -> style == 2 ? 406 : 407;
            case PICKAXE, MACE -> style == 2 ? 400 : 401;
            case DAGGER -> style == 2 ? 377 : 376;
            case MAGIC_STAFF -> 419;
            case THROWN -> 929;
            case WHIP -> 1658;
            case SPEAR -> switch (style) {
                case 1 -> 380;
                case 2 -> 382;
                default -> 381;
            };
            case HALBERD -> style == 1 ? 440 : 428;
            case CLAWS -> 393;
            default -> 422;
        };
        // Fall back to fist fighting so people know it's a wrong anim and (hopefully) report it.
    }

    public static int blockAnimationFor(Player player) {
        int weapon = player.getEquipment().hasAt(EquipSlot.WEAPON) ? player.getEquipment().get(EquipSlot.WEAPON).getId() : 0;
        int shield = player.getEquipment().hasAt(EquipSlot.SHIELD) ? player.getEquipment().get(EquipSlot.SHIELD).getId() : 0;
        ItemDefinition shielddef = World.getWorld().definitions().get(ItemDefinition.class, shield);
        boolean godbook = shield != 0 && shielddef.name != null && shielddef.name.toLowerCase().contains("book");

        if (weapon == 4084) { // Sled
            return 1466;
        }
        if (shield != 0) {
            // Defender?
            if ((shield >= 8844 && shield <= 8850) || shield == 12954 || shield == 19722) {
                return 4177;
            }
            if (shield == ELYSIAN_SPIRIT_SHIELD) {
                return 1156;
            }
            // Metal shields prioritise over weapons. Not books though. Weapons can prioritise over those.
            if (!godbook)
                return 1156;
        }

        // If no weapon, return 424
        if (weapon == 0) {
            return 424;
        }

        // Individual cases here.
        switch (weapon) {
            case INQUISITORS_MACE -> {
                return 403;
            }
            case VIGGORAS_CHAINMACE, VIGGORAS_CHAINMACE_U, URSINE_CHAINMACE -> {
                return 7200;
            }
            // case ANATHEMATIC_WAND:
            case KODAI_WAND, KODAI_WAND_23626, _3RD_AGE_WAND -> {
                return 415;
            }
            case GHRAZI_RAPIER, DRAGON_2H_SWORD -> {
                return 410;
            }
            case SCYTHE_OF_VITUR -> {
                return 435;
            }
            case DRAGON_HUNTER_LANCE, STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, TOXIC_STAFF_OF_THE_DEAD, TRIDENT_OF_THE_SEAS, TRIDENT_OF_THE_SEAS_FULL, TRIDENT_OF_THE_SEAS_E, UNCHARGED_TOXIC_TRIDENT, TRIDENT_OF_THE_SWAMP, TRIDENT_OF_THE_SWAMP_E, THAMMARONS_SCEPTRE, THAMMARONS_SCEPTRE_U, NIGHTMARE_STAFF, HARMONISED_NIGHTMARE_STAFF, VOLATILE_NIGHTMARE_STAFF, ELDRITCH_NIGHTMARE_STAFF, SANGUINESTI_STAFF -> {
                return 420;
            }
            case ABYSSAL_DAGGER, ABYSSAL_DAGGER_P, ABYSSAL_DAGGER_P_13269, ABYSSAL_DAGGER_P_13271 -> {
                return 3295;
            }
            case 13263, GRANITE_MAUL_24225, 24944, GRANITE_MAUL_12848, 16200, 16201, 16202, 16203, 16204, 16205, 16206, 16207, 16208, 4153 -> {
                return 1666;
            }
            case 7671, 7673, 11705, 11706 -> {
                return 3679;
            }
            case 20593, 14487, 30003, 11802, 11804, 11806, 11808, 20368, 20370, 20372, 20374, 26233, 11838, 12808, 12809 -> { // blessed ss
                return 7056;
            }
            case LIGHT_BALLISTA, HEAVY_BALLISTA -> {
                return 7219;
            }
            case 11824, TUMEKENS_SHADOW -> { // Zammy spear
                return 1709;
            }
            case 20779 -> { // H'ween 2016 hunting knife
                return -1;
            } // Elder maul
            //Elder ice maul
            case 21003, 30005, 21205 -> {
                return 7517;
            }
            case 21015 -> {
                return 7512;
            }
            case DRAGON_HASTA, TOXIC_BLOWPIPE -> {
                return 430;
            } // Torag's hamers
            // Torag's hamers
            // Torag's hamers
            // Torag's hamers
            // Torag's hamers
            // Dharok's greataxe
            // Dharok's greataxe
            // Dharok's greataxe
            // Dharok's greataxe
            case DRAGON_CLAWS, BRONZE_CROSSBOW, IRON_CROSSBOW, STEEL_CROSSBOW, ADAMANT_CROSSBOW, RUNE_CROSSBOW, DRAGON_CROSSBOW, DRAGON_HUNTER_CROSSBOW, KARILS_CROSSBOW, ZARYTE_CROSSBOW, 4747, 4958, 4959, 4960, 4961, 4718, 4886, 4887, 4888, 4889 -> { // Dharok's greataxe
                return 424;
            }
            case LEAFBLADED_BATTLEAXE -> {
                return 397;
            }
            case 4755, 4982, 4983, 4984, 4985 -> { // Verac's flail
                return 2063;
            }
            case 4910, 4911, 4912, 4913, 4914 -> { // Guthan's warspear
                return 430;
            }
            case AHRIMS_STAFF, AHRIMS_STAFF_0, AHRIMS_STAFF_25, AHRIMS_STAFF_50, AHRIMS_STAFF_75, AHRIMS_STAFF_100 -> {
                return 2079;
            }
        }

        // Book-based defaults
        EquipmentInfo info = World.getWorld().equipmentInfo();
        WeaponType book = info.weaponType(weapon);
        return switch (book) {
            case HAMMER, MACE -> 403;
            case WHIP -> 1659;
            case PICKAXE, CLAWS -> 397;
            case LONGSWORD, DAGGER -> 4177;
            // Heh blaze it fkn fagt
            case MAGIC_STAFF -> 420;
            case TWOHANDED -> 410;
            case SPEAR, HALBERD -> 430;
            case CHINCHOMPA -> 3176;
            default -> 424;
        };

    }

}
