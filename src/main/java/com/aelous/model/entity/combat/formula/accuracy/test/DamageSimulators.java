package com.aelous.model.entity.combat.formula.accuracy.test;

import com.aelous.GameServer;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.content.presets.PresetManager;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.weapon.AttackType;
import com.aelous.model.entity.combat.weapon.FightStyle;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.CommandManager;
import com.aelous.model.entity.player.rights.PlayerRights;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.network.Session;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.util.*;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.aelous.model.entity.combat.CombatType.MELEE;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.*;
import static com.aelous.model.entity.combat.prayer.default_prayer.Prayers.PIETY;
import static com.aelous.utility.ItemIdentifiers.SALVE_AMULET_E;
import static com.aelous.utility.ItemIdentifiers.VESTAS_BLIGHTED_LONGSWORD;
import static java.lang.System.out;

/**
 * Example output:
 * <br>
 * <br>damage map is:
 * <br>DMG		OCCURANCE
 * <br>0			18 times (1.7999999999999998%)
 * <br>1			22 times (2.1999999999999997%)
 * <br>2			20 times (2.0%)
 * <br>
 * <br>
 *
 * @author Shadowrs tardisfan121@gmail.com
 */
public class DamageSimulators {

    public static class HitSim {

        public ArrayList<Integer> hits = new ArrayList<>();

        /**
         * a map of damage and how many times that damage value appeared:
         * 1 : 100
         * 2 : 100 (hit 2 dmg 100 times)
         * 30 : 10 (hit 30 dmg 10 times)
         */

        public void addHit(int damage) {
            hits.add(damage);
        }

        public HashMap<Integer, Integer> commonality() {
            HashMap<Integer, Integer> integers = new HashMap<>();
            hits.forEach(i -> {
                integers.compute(i, (k, v) -> (v == null) ? 1 : v + 1);
            });
            return integers;
        }

        public String printAnalysis(Entity attacker, Entity defender, CombatType style) {
            // Initialize variables
            int totalHits = hits.size();
            int totalSuccessfulDamage = 0;
            int totalDamage = 0;
            int successfulHits = 0;
            int unsuccessfulHits = 0;

            // Calculate the damage values and their counts
            Map<Integer, Integer> commonalityMap = commonality();
            StringBuilder data = new StringBuilder();
            for (Map.Entry<Integer, Integer> entry : commonalityMap.entrySet()) {
                int damage = entry.getKey();
                int count = entry.getValue();
                double percent = ((double) count / totalHits) * 100;
                data.append(String.format("%-10d%-10d%.2f%%\n", damage, count, percent));
                totalDamage += damage * count; // Calculate the sum of the damage dealt by successful hits
                if (damage == 0) {
                    unsuccessfulHits += count; // Count the number of unsuccessful hits
                } else {
                    totalSuccessfulDamage += damage * count; // Calculate the sum of the damage dealt by successful hits
                    successfulHits += count; // Count the number of successful hits
                }
            }

            // Calculate the accuracy ratio
            double accuracyRatio = ((double) successfulHits / totalHits) * 100;

            // Debugging information
            String debugString = debugSuccessful(attacker, defender, style);

            // Check for logical errors in the formula
            if (totalHits != (successfulHits + unsuccessfulHits)) {
                System.out.println("Error: Total hits does not match the sum of successful and unsuccessful hits.");
            }
            if (totalHits != (successfulHits + unsuccessfulHits + commonalityMap.getOrDefault(0, 0))) {
                System.out.println("Error: Total hits does not match the sum of successful, unsuccessful, and 0-damage hits.");
            }
            if (totalSuccessfulDamage > totalDamage) {
                System.out.println("Error: Total successful damage is greater than total damage.");
            }

            // Format the data string and return it
            return String.format("%sDamage map is:%n" +
                    "DMG       OCCURANCE %%\n" +
                    "%s" +
                    "%n" +
                    "Total damage dealt: %d%n" +
                    "Total successful damage dealt: %d%n" +
                    "Total hits: %d%n" +
                    "Successful hits: %d (%.2f%%)%n" +
                    "Unsuccessful hits: %d (%.2f%%)%n" +
                    "Accuracy ratio: %.2f%%%n",
                debugString, data, totalDamage, totalSuccessfulDamage, totalHits, successfulHits,
                ((double) successfulHits / totalHits) * 100, unsuccessfulHits,
                ((double) unsuccessfulHits / totalHits) * 100, accuracyRatio);
        }


        public static int blyat = 1;

        public static void main(String[] args) {
            GameServer.properties().gamePort = 39999;
            GameServer.main(new String[]{});

            // run sims
            runMaxMeleeDhPeitySim();
            //runMaxMeleeDhNoPraySim();
            //nakedMaxMainsBoxPeity();
            //nakedMaxMainsBoxNoPrayer();
            //nakedBoxMaxMainsWith1def();
            //runMaxMeleeDhTurmoil();
            //runRangePresetSim();
            //runMagicPresetSim();

            System.exit(0);
        }

        public static Player makebot() {
            Player player = new Player(new Session(null));
            player.setUsername("bot").setLongUsername(Utils.stringToLong("bot")).setHostAddress("127.0.0.1");
            player.putAttrib(AttributeKey.MAC_ADDRESS, "OMEGALUL");
            player.onLogin();
            player.setIndex(1); // presets need to be fuckin registered user
            player.setPlayerRights(PlayerRights.DEVELOPER);
            return player;
        }

        private static void runMagicPresetSim() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            p1.getPresetManager().load(PresetManager.GLOBAL_PRESETS[2]); // tribird
            p2.getPresetManager().load(PresetManager.GLOBAL_PRESETS[2]); // tribird

            assert p1.getSkills().level(5) == 99; // 99 prayer
            assert p1.getEquipment().get(EquipSlot.WEAPON).matchesId(4675); // staff

            Prayers.togglePrayer(p1, DefaultPrayerData.MYSTIC_MIGHT.getButtonId());
            Prayers.togglePrayer(p2, DefaultPrayerData.MYSTIC_MIGHT.getButtonId());
            assert p1.getPrayerActive()[DefaultPrayerData.MYSTIC_MIGHT.ordinal()]; // activated

            applyMagicSim(p1, p2, "runMagicPresetSim", 94, CombatSpells.ICE_BARRAGE.getSpell());
        }

        private static void runRangePresetSim() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            p1.getPresetManager().load(PresetManager.GLOBAL_PRESETS[2]); // tribird
            p2.getPresetManager().load(PresetManager.GLOBAL_PRESETS[2]); // tribird
            p1.inventory().remove(385, 10);
            p2.inventory().remove(385, 10);
            p1.getEquipment().manualWear(new Item(9185), true);
            p1.getEquipment().manualWear(new Item(2503), true);

            assert p1.getSkills().level(5) == 99; // 99 prayer
            assert p1.getEquipment().get(EquipSlot.WEAPON) != null && p1.getEquipment().get(EquipSlot.WEAPON).matchesId(9185) : "what the fuck"; // bow
            out.println("range wep " + p1.getEquipment().get(3));

            Prayers.togglePrayer(p1, DefaultPrayerData.EAGLE_EYE.getButtonId());
            Prayers.togglePrayer(p2, DefaultPrayerData.EAGLE_EYE.getButtonId());
            assert p1.getPrayerActive()[DefaultPrayerData.EAGLE_EYE.ordinal()]; // activated

            applyRangeSim(p1, p2, "runRangePresetSim");
        }

        private static void runMaxMeleeDhPeitySim() {
            Player p1 = makebot();
            Player p2 = makebot();

            p1.getEquipment().manualWear(new Item(ItemIdentifiers.ABYSSAL_TENTACLE), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.AVERNIC_DEFENDER), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_HELM), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_PLATELEGS), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_PLATEBODY), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.DRAGON_BOOTS), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.BERSERKER_RING), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.AMULET_OF_FURY), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.INFERNAL_CAPE), true);
            p1.getEquipment().manualWear(new Item(ItemIdentifiers.BARROWS_GLOVES), true);

            p2.getEquipment().manualWear(new Item(ItemIdentifiers.ABYSSAL_TENTACLE), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.AVERNIC_DEFENDER), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_HELM), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_PLATELEGS), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.DHAROKS_PLATEBODY), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.DRAGON_BOOTS), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.BERSERKER_RING), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.AMULET_OF_FURY), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.INFERNAL_CAPE), true);
            p2.getEquipment().manualWear(new Item(ItemIdentifiers.BARROWS_GLOVES), true);


            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON).matchesId(12006); // tent

            Prayers.togglePrayer(p1, DefaultPrayerData.PIETY.getButtonId());
            Prayers.togglePrayer(p2, DefaultPrayerData.PIETY.getButtonId());
            assert p1.getPrayerActive()[DefaultPrayerData.PIETY.ordinal()]; // activated

            applyMeleeSim(p1, p2, "runMaxMeleeDhPeitySim");
        }


        private static void runMaxMeleeDhTurmoil() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON).matchesId(12006); // tent

            applyMeleeSim(p1, p2, "runMaxMeleeDhTurmoil");
        }

        private static void runMaxMeleeDhNoPraySim() {
            Player p1 = makebot();
            Player p2 = makebot();

            p1.getPresetManager().load(PresetManager.GLOBAL_PRESETS[1]);
            p2.getPresetManager().load(PresetManager.GLOBAL_PRESETS[1]);
            p1.inventory().remove(385, 10);
            p2.inventory().remove(385, 10);
            p1.getEquipment().manualWear(new Item(4151), true);
            p2.getEquipment().manualWear(new Item(4151), true);

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON).matchesId(12006); // tent

            assert !p1.getPrayerActive()[DefaultPrayerData.PIETY.ordinal()];

            applyMeleeSim(p1, p2, "runMaxMeleeDhNoPraySim");
        }

        private static void nakedMaxMainsBoxPeity() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON) == null; // tent

            Prayers.togglePrayer(p1, DefaultPrayerData.PIETY.getButtonId());
            Prayers.togglePrayer(p2, DefaultPrayerData.PIETY.getButtonId());
            assert p1.getPrayerActive()[DefaultPrayerData.PIETY.ordinal()]; // activated

            applyMeleeSim(p1, p2, "nakedMaxMainsBoxPeity");
        }

        private static void nakedMaxMainsBoxNoPrayer() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", null);

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", null);

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON) == null; // tent

            applyMeleeSim(p1, p2, "nakedMaxMainsBoxNoPrayer");
        }

        private static void nakedBoxMaxMainsWith1def() {
            Player p1 = makebot();
            Player p2 = makebot();

            //setup p1
            CommandManager.commands.get("master").execute(p1, "master", "master".split(" "));

            // setup p2
            CommandManager.commands.get("master").execute(p2, "master", "master".split(" "));

            assert p2.getSkills().level(5) == 99; // 99 prayer
            assert p2.getEquipment().get(EquipSlot.WEAPON) == null; // tent

            CommandManager.commands.get("setlevel").execute(p1, "setlevel 5 1", "setlevel 5 1".split(" "));
            CommandManager.commands.get("setlevel").execute(p2, "setlevel 5 1", "setlevel 5 1".split(" "));

            assert p2.getSkills().level(1) == 1; // 1 def

            applyMeleeSim(p1, p2, "nakedBoxMaxMainsWith1def");
        }

        private static void applyMeleeSim(Entity p1, Entity p2, String setupname) {
            applySim(p1, p2, setupname, CombatFactory.MELEE_COMBAT, CombatType.MELEE);
        }

        private static void applyMagicSim(Entity p1, Entity p2, String setupname, int spell, CombatSpell com) {
            applySim(p1, p2, setupname, CombatFactory.MAGIC_COMBAT, CombatType.MAGIC, spell, com);
        }

        private static void applyRangeSim(Entity p1, Entity p2, String setupname) {
            applySim(p1, p2, setupname, CombatFactory.RANGED_COMBAT, CombatType.RANGED);
        }

        private static void applySim(Entity p1, Entity p2, String setupname, CombatMethod mtd, CombatType type) {
            applySim(p1, p2, setupname, mtd, type, 1, null);
        }

        private static void applySim(Entity p1, Entity p2, String setupname, CombatMethod mtd, CombatType type, int spell, CombatSpell combatSpell) {
            CombatFactory.getMethod(p1.getAsPlayer());
            CombatFactory.getMethod(p2.getAsPlayer());
            // sim and record
            HitSim hitSim = new HitSim();
            int simCount = 100;
            p1.getCombat().setCastSpell(combatSpell);
            String acc = doesHit(p1.getAsPlayer(), p2.getAsPlayer(), type); // print to console for now
            System.out.println("Accuracy: " + acc); // Print accuracy

            for (int i = 0; i < simCount; i++) {
                p1.getCombat().setCastSpell(combatSpell);

                /**
                 * from {@link com.aelous.game.world.entity.combat.Combat#performNewAttack(boolean)}
                 */
                mtd.prepareAttack(p1.getAsPlayer(), p2.getAsPlayer());
                Hit hit = p2.hit(p1.getAsPlayer(), CombatFactory.calcDamageFromType(p1.getAsPlayer(), p2.getAsPlayer(), CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy();
                hit.submit();
                hitSim.addHit(hit.getDamage());
            }

            String breakdown = hitSim.printAnalysis(p1, p2, type);
            System.out.println("Breakdown: " + breakdown); // Print breakdown
            String out =
                "\n" + setupname +
                    "\np1 equip: " + Arrays.toString(p1.getAsPlayer().getEquipment().getValidItems().stream().map(Item::name).toArray()) +
                    "\np2 equip: " + Arrays.toString(p2.getAsPlayer().getEquipment().getValidItems().stream().map(Item::name).toArray()) +
                    "\n" + acc +
                    "\n" + breakdown;
            try {
                Files.writeString(Paths.get("combat-sims.txt"), "\n", StandardOpenOption.CREATE);
                Files.writeString(Paths.get("combat-sims.txt"), out, StandardOpenOption.APPEND);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static String debugSuccessful(Entity attacker, Entity defender, CombatType style) {
            final int attackBonus = getAttackRoll(attacker, defender, style);
            final int defenceBonus = getDefenceRoll(attacker, defender);
            double successfulRoll;

            byte[] seed = new byte[16];
            new SecureRandom().nextBytes(seed);
            SecureRandom random = new SecureRandom(seed);

            String debugString = "Calculating success chance...\n";

            debugString += "Attacker:\n";
            debugString += "  Attack level: " + getAttackLevel(attacker) + "\n";
            debugString += "  Prayer attack bonus: " + getPrayerAttackBonus(attacker, style) + "\n";
            debugString += "  Effective melee level: " + getEffectiveAttack(attacker, defender, style) + "\n";
            debugString += "  Gear attack bonus: " + getGearAttackBonus(attacker) + "\n";
            debugString += "  Total attack bonus: " + attackBonus + "\n";

            debugString += "Defender:\n";
            debugString += "  Defence level: " + getDefenceLevel(defender) + "\n";
            debugString += "  Prayer defence bonus: " + getPrayerDefenseBonus(defender) + "\n";
            debugString += "  Effective defence level: " + getEffectiveDefence(defender) + "\n";
            debugString += "  Gear defence bonus: " + getGearDefenceBonus(defender) + "\n";
            debugString += "  Total defence bonus: " + defenceBonus + "\n";

            if (attackBonus > defenceBonus) {
                debugString += "Attack bonus is greater than defence bonus, using formula: (1 - (defenceBonus + 2) / (2 * (attackBonus + 1)))\n";
                successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
            } else {
                debugString += "Defence bonus is greater than or equal to attack bonus, using formula: (attackBonus / (2 * (defenceBonus + 1)))\n";
                successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
            }

            debugString += "Calculated success chance: " + new DecimalFormat("0.000").format(successfulRoll) + "\n";

            double selectedChance = random.nextFloat();
            debugString += "Rolled chance: " + new DecimalFormat("0.000").format(selectedChance) + "\n";

            if (successfulRoll > selectedChance) {
                debugString += "Attack successful!\n";
            } else {
                debugString += "Attack failed.\n";
            }

            return debugString;
        }


        public static String doesHit(Entity attacker, Entity defender, CombatType style) {
            return successful(attacker, defender, style);
        }

        static byte[] seed = new byte[16];
        static SecureRandom random = new SecureRandom(seed);

        public static String successful(Entity attacker, Entity defender, CombatType style) {
            final int attackBonus = getAttackRoll(attacker, defender, style);
            final int defenceBonus = getDefenceRoll(attacker, defender);
            double successfulRoll;

            random.nextBytes(seed);

            if (attackBonus > defenceBonus) {
                successfulRoll = 1F - ((defenceBonus + 2F) / (2F * (attackBonus + 1F)));
            } else {
                successfulRoll = attackBonus / (2F * (defenceBonus + 1F));
            }

            double selectedChance = random.nextFloat();

            out.println("PlayerStats - Attack=" + attackBonus + " Def=" + defenceBonus + " chanceOfSucess=" + new DecimalFormat("0.000").format(successfulRoll) + " rolledChance=" + new DecimalFormat("0.000").format(selectedChance) + " successful=" + (successfulRoll > selectedChance ? "YES" : "NO"));

            return String.valueOf(successfulRoll > selectedChance);
        }

        private static double getPrayerDefenseBonus(final Entity defender) {
            double prayerBonus = 1F;
            if (Prayers.usingPrayer(defender, THICK_SKIN))
                prayerBonus *= 1.05F; // 5% def level boost
            else if (Prayers.usingPrayer(defender, ROCK_SKIN))
                prayerBonus *= 1.10F; // 10% def level boost
            else if (Prayers.usingPrayer(defender, STEEL_SKIN))
                prayerBonus *= 1.15F; // 15% def level boost
            if (Prayers.usingPrayer(defender, CHIVALRY))
                prayerBonus *= 1.20F; // 20% def level boost
            else if (Prayers.usingPrayer(defender, PIETY))
                prayerBonus *= 1.25F; // 25% def level boost
            return prayerBonus;
        }

        private static double getPrayerAttackBonus(final Entity attacker, CombatType style) {
            double prayerBonus = 1F;
            if (Prayers.usingPrayer(attacker, CLARITY_OF_THOUGHT))
                prayerBonus *= 1.05F; // 5% attack level boost
            else if (Prayers.usingPrayer(attacker, IMPROVED_REFLEXES))
                prayerBonus *= 1.10F; // 10% attack level boost
            else if (Prayers.usingPrayer(attacker, INCREDIBLE_REFLEXES))
                prayerBonus *= 1.15F; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, CHIVALRY))
                prayerBonus *= 1.15F; // 15% attack level boost
            else if (Prayers.usingPrayer(attacker, PIETY))
                prayerBonus *= 1.20F; // 20% attack level boost
            return prayerBonus;
        }


        private static int getEffectiveDefence(final Entity defender) {
            FightStyle fightStyle = defender.getCombat().getFightType().getStyle();
            int effectiveLevel = defender instanceof NPC ? ((NPC) defender).getCombatInfo().stats.defence : (int) Math.floor(getDefenceLevel(defender) * getPrayerDefenseBonus(defender));

            switch (fightStyle) {
                case DEFENSIVE -> effectiveLevel = effectiveLevel + 3;
                case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
            }

            effectiveLevel = effectiveLevel + 8;

            return effectiveLevel;
        }

        private static int getEffectiveAttack(final Entity attacker, final Entity defender, CombatType style) {
            var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
            var task = SlayerCreature.lookup(task_id);
            final Item weapon = attacker.isPlayer() ? attacker.getAsPlayer().getEquipment().get(EquipSlot.WEAPON) : null;
            FightStyle fightStyle = attacker.getCombat().getFightType().getStyle();
            double effectiveLevel = Math.floor(getAttackLevel(attacker) * getPrayerAttackBonus(attacker, style));

            if (attacker.isPlayer()) {
                Player player = attacker.getAsPlayer();
                if (player.getCombatSpecial() != null) {
                    double specialMultiplier = player.getCombatSpecial().getAccuracyMultiplier();
                    if (attacker.getAsPlayer().isSpecialActivated()) {
                        effectiveLevel *= specialMultiplier;
                    }
                }
            }

            switch (fightStyle) {
                case ACCURATE -> effectiveLevel = effectiveLevel + 3;
                case CONTROLLED -> effectiveLevel = effectiveLevel + 1;
            }

            effectiveLevel = effectiveLevel + 8;

            effectiveLevel = (int) Math.floor(effectiveLevel);

            if (attacker.isPlayer()) {
                if (style.equals(MELEE)) {
                    if (FormulaUtils.regularVoidEquipmentBaseMelee((Player) attacker)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.1F);
                    }
                    if (FormulaUtils.eliteVoidEquipmentMelee((Player) attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee((Player) attacker)) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.125F);
                    }
                    if (FormulaUtils.obbyArmour(attacker.getAsPlayer()) && FormulaUtils.hasObbyWeapon(attacker.getAsPlayer())) {
                        effectiveLevel = (int) Math.floor(effectiveLevel * 1.1F);
                    }
                    if (defender instanceof NPC) {
                        if (defender.isNpc() && defender.getAsNpc().id() == NpcIdentifiers.REVENANT_CYCLOPS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DEMON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DRAGON || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_GOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HELLHOUND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_DARK_BEAST || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_HOBGOBLIN || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_KNIGHT || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_PYREFIEND || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_MALEDICTUS || defender.getAsNpc().id() == NpcIdentifiers.REVENANT_IMP) {
                            if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
                                effectiveLevel = (int) Math.floor(effectiveLevel * 1.2F);
                            }
                            if (((Player) attacker).getEquipment().contains(ItemIdentifiers.SALVE_AMULET)) {
                                effectiveLevel = (int) Math.floor(effectiveLevel * 1.15F);
                            }
                        }
                        if (defender.isNpc() && WildernessArea.inWilderness(attacker.tile())) {
                            if (weapon != null && FormulaUtils.hasMeleeWildernessWeapon(attacker.getAsPlayer())) {
                                effectiveLevel = (int) Math.floor(effectiveLevel * 1.5F);
                            }
                        }
                    }
                }
            }
            return (int) Math.floor(effectiveLevel);
        }

        public static int getAttackLevel(Entity attacker) {
            return attacker instanceof NPC && attacker.getAsNpc().getCombatInfo().stats != null ? attacker.getAsNpc().getCombatInfo().stats.attack : attacker.getSkills().level(Skills.ATTACK);
        }

        public static int getDefenceLevel(Entity defender) {
            return defender instanceof NPC && defender.getAsNpc().getCombatInfo().stats != null ? defender.getAsNpc().getCombatInfo().stats.defence : defender.getSkills().level(Skills.DEFENCE);
        }

        private static int getGearDefenceBonus(Entity defender) {
            EquipmentInfo.Bonuses defenderBonus = EquipmentInfo.totalBonuses(defender, World.getWorld().equipmentInfo());
            final AttackType type = defender instanceof NPC ? AttackType.SLASH : defender.getCombat().getFightType().getAttackType();
            int bonus = 0;
            if (type == AttackType.STAB)
                bonus = defenderBonus.stabdef;
            else if (type == AttackType.CRUSH)
                bonus = defenderBonus.crushdef;
            else if (type == AttackType.SLASH)
                bonus = defenderBonus.slashdef;
            return bonus;
        }

        private static int getGearAttackBonus(Entity attacker) {
            final AttackType type = attacker.getCombat().getFightType().getAttackType();
            EquipmentInfo.Bonuses attackerBonus = EquipmentInfo.totalBonuses(attacker, World.getWorld().equipmentInfo());
            int bonus = 0;
            if (type == AttackType.STAB)
                bonus = attackerBonus.stab;
            else if (type == AttackType.CRUSH)
                bonus = attackerBonus.crush;
            else if (type == AttackType.SLASH)
                bonus = attackerBonus.slash;
            return bonus;
        }

        private static int getAttackRoll(final Entity attacker, final Entity defender, CombatType style) {
            return (int) Math.floor(getEffectiveAttack(attacker, defender, style) * (getGearAttackBonus(attacker) + 64));
        }

        private static int getDefenceRoll(final Entity attacker, final Entity defender) {
            if ((attacker.isPlayer() && attacker.getAsPlayer().getEquipment().contains(VESTAS_BLIGHTED_LONGSWORD) && attacker.isSpecialActivated())) {
                return (int) Math.floor((getEffectiveDefence(defender) * (getGearDefenceBonus(defender) + 64)) * 0.80F);
            }
            return (int) Math.floor(getEffectiveDefence(defender) * (getGearDefenceBonus(defender) + 64));
        }
    }
}

