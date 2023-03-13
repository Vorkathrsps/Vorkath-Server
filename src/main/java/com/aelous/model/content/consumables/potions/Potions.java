package com.aelous.model.content.consumables.potions;

import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.chainedwork.Chain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.consumables.potions.impl.*;
import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import java.util.*;

import static com.aelous.model.entity.attributes.AttributeKey.*;

/**
 * @author PVE
 * @Since augustus 27, 2020
 */
public class Potions {

    public enum Potion {
        ATTACK_POTION(Skills.ATTACK, 3, 10, true, "attack potion", 2428, 121, 123, 125),
        SUPER_ATTACK_POTION(Skills.ATTACK, 5, 15, true, "attack potion" /* sic */, 2436, 145, 147, 149),
        STRENGTH_POTION(Skills.STRENGTH, 3, 10, true, "strength potion", 113, 115, 117, 119),
        SUPER_STRENGTH_POTION(Skills.STRENGTH, 5, 15, true, "strength potion", 2440, 157, 159, 161),
        DEFENCE_POTION(Skills.DEFENCE, 3, 10, true, "defence potion", 2432, 133, 135, 137),
        SUPER_DEFENCE_POTION(Skills.DEFENCE, 5, 15, true, "defence potion", 2442, 163, 165, 167),
        MAGIC_POTION(Skills.MAGIC, 4, 0, true, "magic potion", 3040, 3042, 3044, 3046),
        SUPER_MAGIC_POTION(Skills.MAGIC, 2, 12, true, "magic potion", 11726, 11727, 11728, 11729),
        RANGING_POTION(Skills.RANGED, 4, 10, true, "ranging potion", 2444, 169, 171, 173),
        SUPER_RANGING_POTION(Skills.RANGED, 2, 12, true, "ranging potion", 11722, 11723, 11724, 11725),
        SARADOMIN_BREW(-1, -1, -1, false, "the foul liquid", 6685, 6687, 6689, 6691),
        SUPER_RESTORE(-1, -1, -1, false, "super restore potion", 3024, 3026, 3028, 3030),
        RESTORE_POTION(-1, -1, -1, false, "stat restoration potion", 2430, 127, 129, 131),
        COMBAT_POTION(-1, -1, -1, false, "combat potion", 9739, 9741, 9743, 9745),
        SUPER_COMBAT_POTION(-1, -1, -1, false, "super combat potion", 12695, 12697, 12699, 12701),
        PRAYER_POTION(-1, -1, -1, false, "restore prayer potion", 2434, 139, 141, 143),
        ANTIPOISON(-1, -1, -1, false, "antipoison potion", 2446, 175, 177, 179),
        SUPERANTIPOISON(-1, -1, -1, false, "antipoison" /*sic*/, 2448, 181, 183, 185),
        ANTIDOTE_PLUS(-1, -1, -1, false, "antidote plus" /*sic*/, 5943, 5945, 5947, 5949),
        ANTIDOTE_PLUSPLUS(-1, -1, -1, false, "antidote plusplus" /*sic*/, 5952, 5954, 5956, 5958),
        ANTIVENOM(-1, -1, -1, false, "anti-venom", 12905, 12907, 12909, 12911),
        ANTIVENOM_PLUS(-1, -1, -1, false, "anti-venom plus", 12913, 12915, 12917, 12919),
        STAMINA_POTION(-1, -1, -1, false, "stamina potion", 12625, 12627, 12629, 12631),
        SANFEW_SERUM(-1, -1, -1, false, "sanfew serum", 10925, 10927, 10929, 10931),
        ENERGY_POTION(-1, -1, -1, false, "energy potion", 3008, 3010, 3012, 3014),
        SUPER_ENERGY_POTION(-1, -1, -1, false, "super energy potion", 3016, 3018, 3020, 3022),
        ANTIFIRE_POTION(-1, -1, -1, false, "antifire potion", 2452, 2454, 2456, 2458),
        EXTENDED_ANTIFIRE_POTION(-1, -1, -1, false, "extended antifire potion", 11951, 11953, 11955, 11957),
        GUTHIX_REST(-1, -1, -1, false, "guthix rest", 4417, 4419, 4421, 4423),
        ZAMORAK_BREW(-1, -1, -1, false, "the foul liquid", 2450, 189, 191, 193),
        SUPER_ANTIFIRE(-1, -1, -1, false, "super antifire", 21978, 21981, 21984, 21987),
        EXTENDED_SUPER_ANTIFIRE(-1, -1, -1, false, "extended super antifire", 22209, 22212, 22215, 22218),
        BASTION_POTION(-1, -1, -1, false, "bastion potion", 22461, 22464, 22467, 22470),
        DIVINE_BASTION_POTION(-1, -1, -1, false, "divine bastion potion", 24635, 24638, 24641, 24644),
        BATTLEMAGE_POTION(-1, -1, -1, false, "battlemage potion", 22449, 22452, 22455, 22458),
        DIVINE_BATTLEMAGE_POTION(-1, -1, -1, false, "divine battlemage potion", 24623, 24626, 24626, 24632),
        DIVINE_MAGIC_POTION(-1, -1, -1, false, "divine magic potion", 23745, 23748, 23751, 23754),
        DIVINE_RANGING_POTION(-1, -1, -1, false, "divine ranging potion", 23733, 23736, 23739, 23742),
        DIVINE_SUPER_ATTACK_POTION(-1, -1, -1, false, "divine super attack potion", 23697, 23700, 23703, 23706),
        DIVINE_SUPER_COMBAT_POTION(-1, -1, -1, false, "divine super combat potion", 23685, 23688, 23691, 23694),
        DIVINE_SUPER_DEFENCE_POTION(-1, -1, -1, false, "divine super defence potion", 23721, 23724, 23727, 23730),
        DIVINE_SUPER_STRENGTH_POTION(-1, -1, -1, false, "divine super strength potion", 23709, 23712, 23715, 23718),
        //RECOVER_SPECIAL(-1, -1, -1, false, "recover special", 15300, 15301, 15302, 15303),
        OVERLOAD_POTION(-1, -1, -1, false, "overload potion", ItemIdentifiers.OVERLOAD_4, ItemIdentifiers.OVERLOAD_3, ItemIdentifiers.OVERLOAD_2, ItemIdentifiers.OVERLOAD_1),
        // ENUM END . Ids are (4) (3) (2) (1)
        ;

        private final int skill;
        private final int base;
        private final int percentage;
        private final boolean defaultalgo;
        private final String message;
        public final int[] ids;

        Potion(int skill, int base, int percentage, boolean defaultalgo, String message, int... ids) {
            this.skill = skill;
            this.base = base;
            this.percentage = percentage;
            this.defaultalgo = defaultalgo;
            this.message = message;
            this.ids = ids;
        }

        public int nextDose(int id) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id)
                    return ids[i + 1];
            }
            return 0;
        }

        public boolean isLastDose(int id) {
            return ids[ids.length - 1] == id;
        }

        public int dosesLeft(int id) {
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] == id)
                    return ids.length - i - 1;
            }
            return 0;
        }

        /**
         * Gets the item id for the specified dose.
         *
         * @param dose the dose to get the item id from.
         * @return the item id.
         */
        public int getIdForDose(int dose) {
            return ids[ids.length - dose];
        }

        /**
         * Caches our enum values.
         */
        private static final ImmutableSet<Potion> VALUES = Sets.immutableEnumSet(EnumSet.allOf(Potion.class));

        /**
         * Retrieves the potion consumable element for {@code id}.
         *
         * @param id the id that the potion consumable is attached to.
         * @return the potion consumable wrapped in an optional, or an empty
         * optional if no potion consumable was found.
         */
        public static Optional<Potion> forId(int id) {
            for (Potion potion : VALUES) {
                for (int potionId : potion.ids) {
                    if (id == potionId) {
                        return Optional.of(potion);
                    }
                }
            }
            return Optional.empty();
        }
    }

    public static void consume(Player player, Potion potion, int id) {
        if (player.getTimers().has(TimerKey.POTION))
            return;

        if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_POTIONS.ordinal()]) {
            player.message("Drinks are disabled for this duel.");
            return;
        }

        if (player.getController() != null) {
            if (!player.getController().canDrink(player, id)) {
                player.message("You cannot use potions here.");
                return;
            }
            if (potion == Potion.GUTHIX_REST || potion == Potion.SARADOMIN_BREW) {
                if (!player.getController().canEat(player, id)) {
                    player.message("You cannot eat here.");
                    return;
                }
            }
        }

        if(potion == Potion.DIVINE_SUPER_COMBAT_POTION || potion == Potion.DIVINE_BASTION_POTION || potion == Potion.DIVINE_RANGING_POTION
            || potion == Potion.DIVINE_BATTLEMAGE_POTION || potion == Potion.DIVINE_SUPER_ATTACK_POTION || potion == Potion.DIVINE_SUPER_DEFENCE_POTION ||
            potion == Potion.DIVINE_SUPER_STRENGTH_POTION || potion == Potion.DIVINE_MAGIC_POTION) {
            if(!player.dead()) {
                if(player.hp() < 11) {
                    player.message("You need at least 11 hitpoints to drink this potion");
                    return;
                }
            }
        }

        if (potion == Potion.OVERLOAD_POTION) {
            if(player.<Boolean>getAttribOr(AttributeKey.OVERLOAD_TASK_RUNNING,false) && !player.getPlayerRights().isAdministrator(player)) {
                player.message(Color.RED.wrap("The overload effect is still running."));
                return;
            }

            if(!player.dead()) {
                if (player.hp() < 51) {
                    player.message("You need at least 51 hitpoints to drink this potion.");
                    return;
                }
            }
            if (WildernessArea.inWild(player)) {
                player.message("You can't use this potion in the wilderness.");
                return;
            }
        }

        if (player.stunned()) {
            player.message("You're currently stunned and cannot use potions.");
            return;
        }
        player.playSound(334);
        player.getTimers().register(TimerKey.POTION, 3);
        player.getTimers().register(TimerKey.FOOD, 3);
        int eatAnim;
        if (player.getEquipment().contains(4084))
            eatAnim = 1469;
        else
            eatAnim = 829;
        player.animate(eatAnim);

        if (potion != Potion.ANTIVENOM && potion != Potion.ANTIVENOM_PLUS && potion != Potion.GUTHIX_REST) {
            player.message("You drink some of your " + potion.message + ".");
        }

        deductDose(player, potion, id);
    }

    private final static int VIAL = 229;
    private final static int EMPTY_CUP = 1980;

    public static int getBrewStat(int skill, double amount) {
        Player player = new Player();
        return (int) (player.getSkills().xpLevel(skill) * amount);
    }


    private static void deductDose(Player player, Potion potion, int id) {
        if (potion.isLastDose(id)) {
            boolean giveEmptyVials = player.getAttribOr(AttributeKey.GIVE_EMPTY_POTION_VIALS, true);
            int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);

            if (giveEmptyVials) {
                player.inventory().set(slot, new Item(potion == Potion.GUTHIX_REST ? EMPTY_CUP : VIAL), true);
            } else {
                player.inventory().remove(new Item(id, 1), slot, true);
            }

            if (potion == Potion.ANTIVENOM) {
                player.message("You drink the rest of your antivenom potion.");
            } else if (potion == Potion.ANTIVENOM_PLUS) {
                player.message("You drink the rest of your super antivenom potion.");
            } else if (potion != Potion.GUTHIX_REST) {
                player.message("You have finished your potion.");
            }
        } else {
            int slot = player.getAttribOr(AttributeKey.ITEM_SLOT, -1);
            player.inventory().set(slot, new Item(potion.nextDose(id)), true);
            int left = potion.dosesLeft(id);

            //Sipping a potion stops combat
            player.getCombat().reset();

            String doses = left == 1 ? "dose" : "doses";
            if (potion == Potion.ANTIVENOM) {
                player.message("You drink some of your antivenom potion, leaving " + left + " " + doses + ".");
            } else if (potion == Potion.ANTIVENOM_PLUS) {
                player.message("You drink some of your super antivenom potion, leaving " + left + " " + doses + ".");
            } else if (potion != Potion.GUTHIX_REST) {
                player.message("You have " + left + " " + doses + " of your potion left.");
            }
        }

        if (potion.defaultalgo) {
            double change = potion.base + (player.getSkills().xpLevel(potion.skill) * potion.percentage / 100.0);
            player.getSkills().alterSkill(potion.skill, (int) change);
        } else if (potion == Potion.SARADOMIN_BREW) {
            double heal = (int) ((player.getSkills().xpLevel(Skills.HITPOINTS) * 0.15) + 2);
            double def = (int) ((player.getSkills().xpLevel(Skills.DEFENCE) * 0.2) + 2);
            int increase = player.getEquipment().hpIncrease();
            player.heal((int) heal, increase > 0 ? increase : 16);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) def);

            int[] ids = new int[]{Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC, Skills.RANGED};

            for (int i : ids) {
                int lvl = player.getSkills().xpLevel(i);
                player.getSkills().alterSkill(i, (int) -(lvl * 0.1) + 2);
            }
        } else if (potion == Potion.ZAMORAK_BREW) {
            double attackIncrease = (player.getSkills().xpLevel(Skills.ATTACK) * 0.2) + 2;
            double strengthIncrease = (player.getSkills().xpLevel(Skills.STRENGTH) * 0.12) + 2;
            double prayerIncrease = (player.getSkills().xpLevel(Skills.PRAYER) * 0.1);

            double defenceDecrease = (player.getSkills().xpLevel(Skills.DEFENCE) * 0.1) + 2;
            double hpDecrease = (player.hp() * 0.1) + 2;

            player.getSkills().alterSkill(Skills.ATTACK, (int) attackIncrease);
            player.getSkills().alterSkill(Skills.STRENGTH, (int) strengthIncrease);
            player.getSkills().replenishSkill(Skills.PRAYER, (int) prayerIncrease);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) -defenceDecrease);
            player.hit(player,(int) hpDecrease);

        } else if (potion == Potion.SUPER_RESTORE) {
            for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                if (i != Skills.HITPOINTS) {
                    double current_flat = player.getSkills().xpLevel(i);
                    double restorable = (int) (current_flat * 0.25 + 8);

                    if (i == Skills.PRAYER) {
                        if (player.inventory().contains(6714) && player.getEquipment().wearingMaxCape()) { // Max cape holy wrench effect
                            if (current_flat > 25 && current_flat <= 85) {
                                restorable += 1;
                            } else if (current_flat > 85) {
                                restorable += 2;
                            }
                        }
                    }
                    player.getSkills().replenishSkill(i, (int) restorable);
                }
            }
        } else if (potion == Potion.RESTORE_POTION) {
            for (int i : new int[]{Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.MAGIC, Skills.RANGED}) {
                if (i != Skills.HITPOINTS && i != Skills.PRAYER) {
                    double current_flat = player.getSkills().xpLevel(i);
                    double restorable = (int) (current_flat * 0.30 + 10);

                    player.getSkills().replenishSkill(i, (int) restorable);
                }
            }
        } else if (potion == Potion.SUPER_COMBAT_POTION) {
            int curStr = player.getSkills().xpLevel(Skills.STRENGTH);
            int curAtk = player.getSkills().xpLevel(Skills.ATTACK);
            int curDef = player.getSkills().xpLevel(Skills.DEFENCE);

            player.getSkills().alterSkill(Skills.ATTACK, (int) ((curAtk * 0.1) + 10));
            player.getSkills().alterSkill(Skills.STRENGTH, (int) ((curStr * 0.1) + 10));
            player.getSkills().alterSkill(Skills.DEFENCE, (int) ((curDef * 0.1) + 10));
        } else if (potion == Potion.COMBAT_POTION) {
            int curStr = player.getSkills().xpLevel(Skills.STRENGTH);
            int curAtk = player.getSkills().xpLevel(Skills.ATTACK);

            player.getSkills().alterSkill(Skills.ATTACK, (int) ((curAtk * 0.1) + 3));
            player.getSkills().alterSkill(Skills.STRENGTH, (int) ((curStr * 0.1) + 3));
        } else if (potion == Potion.PRAYER_POTION) {
            int lv = player.getSkills().xpLevel(Skills.PRAYER);
            int cur = player.getSkills().level(Skills.PRAYER);
            int addition = 7 + lv / 4;
            var newval = cur + addition;
            if (player.inventory().contains(6714) && player.getEquipment().wearingMaxCape()) { // Max cape holy wrench effect
                if (lv > 25 && lv <= 85) {
                    newval += 1;
                } else if (lv > 85) {
                    newval += 2;
                }
            }
            player.getSkills().setLevel(Skills.PRAYER, Math.min(newval, lv));
        } else if (potion == Potion.ANTIPOISON) {
            if (Venom.venomed(player))
                Venom.cure(1, player);
            else {
                if(Poison.poisoned(player))
                    player.message("It grants you immunity from poison for 90 seconds.");
                Poison.cureAndImmune(player, 6);
            }
        } else if (potion == Potion.SUPERANTIPOISON) {
            if (Venom.venomed(player))
                Venom.cure(1, player);
            else {
                if(Poison.poisoned(player))
                    player.message("It grants you immunity from poison for six minutes.");
                Poison.cureAndImmune(player, 23); // Longer immunity is the only difference
            }
        } else if (potion == Potion.ANTIDOTE_PLUS) { // basicaly super super anti poison - doesnt cure venom
            if(Poison.poisoned(player))
                player.message("It grants you immunity from poison for nine minutes.");
            Poison.cureAndImmune(player, 35); // 8 mins 45s
        } else if (potion == Potion.ANTIDOTE_PLUSPLUS) {
            if(Poison.poisoned(player))
                player.message("It grants you immunity from poison for twelve minutes.");
            Poison.cureAndImmune(player, 48); // 12 mins
        } else if (potion == Potion.ANTIVENOM) {
            Venom.cure(1, player);
            Poison.cureAndImmune(player, 12); // 3 mins poison immunity
        } else if (potion == Potion.ANTIVENOM_PLUS) {
            Venom.cure(3, player); // 3 mins venom immunity
            Poison.cureAndImmune(player, 60); // 15 mins poison immunity
            player.getPacketSender().sendEffectTimer(180, EffectTimer.VENOM);//3 mins venom immunity timer
        } else if (potion == Potion.STAMINA_POTION) {
            player.setRunningEnergy((double) player.getAttribOr(AttributeKey.RUN_ENERGY, 100.0) + 20.0, true);
            player.getPacketSender().sendStamina(true);
            player.putAttrib(AttributeKey.STAMINA_POTION_TICKS, 200);
            player.getPacketSender().sendEffectTimer(120, EffectTimer.STAMINA);// 2 Minutes
        } else if (potion == Potion.ANTIFIRE_POTION) {
            if ((int) player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0) < 1)
                player.message("It grants you partial protection from dragonfire for six minutes.");
            player.putAttrib(AttributeKey.ANTIFIRE_POTION, 600);
            AntifirePotion.setTimer(player);
            player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(600), EffectTimer.ANTIFIRE);
        } else if (potion == Potion.EXTENDED_ANTIFIRE_POTION) {
            if ((int) player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0) < 1)
                player.message("It grants you partial protection from dragonfire for twelve minutes.");
            player.putAttrib(AttributeKey.ANTIFIRE_POTION, 1200);
            AntifirePotion.setTimer(player);
            player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(1200), EffectTimer.ANTIFIRE);
        } else if (potion == Potion.SUPER_ANTIFIRE) {
            if ((int) player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0) < 1)
                player.message("It grants you complete protection from dragonfire for three minutes.");
            player.putAttrib(AttributeKey.ANTIFIRE_POTION, 300);
            player.putAttrib(AttributeKey.SUPER_ANTIFIRE_POTION, true);
            AntifirePotion.setTimer(player);
            player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(300), EffectTimer.ANTIFIRE);
        } else if (potion == Potion.EXTENDED_SUPER_ANTIFIRE) {
            if ((int) player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0) < 1)
                player.message("It grants you complete protection from dragonfire for six minutes.");
            player.putAttrib(AttributeKey.ANTIFIRE_POTION, 600);
            player.putAttrib(AttributeKey.SUPER_ANTIFIRE_POTION, true);
            AntifirePotion.setTimer(player);
            player.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(600), EffectTimer.ANTIFIRE);
        } else if (potion == Potion.SANFEW_SERUM) {
            // Literally super restore & super antipoison in one.
            // Restore effect:
            for (int i = 0; i < Skills.SKILL_COUNT; i++) {
                if (i != Skills.HITPOINTS) {
                    int current_flat = player.getSkills().xpLevel(i);
                    double restorable = (int) (current_flat * 0.25 + 8);

                    if (i == Skills.PRAYER) {
                        if (player.inventory().contains(6714) && player.getEquipment().wearingMaxCape()) { // Max cape holy wrench effect
                            if (current_flat > 25 && current_flat <= 85) {
                                restorable += 1;
                            } else if (current_flat > 85) {
                                restorable += 2;
                            }
                        }
                    }
                    player.getSkills().replenishSkill(i, (int) restorable);
                }
            }

            if (Venom.venomed(player)) {
                // Venom turns to poison.
                Venom.cure(1, player);
            } else {
                Poison.cureAndImmune(player, 24);
                player.message("It grants you immunity from poison for six minutes.");
            }
        } else if (potion == Potion.ENERGY_POTION) {
            player.setRunningEnergy((double) player.getAttribOr(AttributeKey.RUN_ENERGY, 100.0) + 10.0, true);
        } else if (potion == Potion.SUPER_ENERGY_POTION) {
            player.setRunningEnergy((double) player.getAttribOr(AttributeKey.RUN_ENERGY, 100.0) + 20.0, true);
        } else if (potion == Potion.GUTHIX_REST) {
            player.message("You drink the herbal tea.");
            // Source: https://www.youtube.com/watch?v=IskZmEHfFtM
            // Jak: have also tested on tourny worlds 25/8/16
            // Re-examined 25/11/2017 added venom/poison support as of this video.
            if (Venom.venomed(player)) {
                // Venom turns to poison. doesn't cure poison.
                Venom.cure(1, player, false);
                player.message("The tea dilutes the venom.");
            } else if(Poison.poisoned(player)) {
                player.message("The tea dilutes some of the poison.");
                Poison.cure(player);
            }
            player.heal(5, 5);
            player.setRunningEnergy((double) player.getAttribOr(AttributeKey.RUN_ENERGY, 0.0) + 5, true);
            player.message("The tea boosts your hitpoints.");
        } else if (potion == Potion.BASTION_POTION) {
            double rangeChange = 4 + (player.getSkills().xpLevel(Skills.RANGED) * 10 / 100.0);
            player.getSkills().alterSkill(Skills.RANGED, (int) rangeChange);
            double defenceChange = 5 + (player.getSkills().xpLevel(Skills.DEFENCE) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) defenceChange);
        } else if (potion == Potion.BATTLEMAGE_POTION) {
            double magicChange = 4 + (player.getSkills().xpLevel(Skills.MAGIC) / 100.0);
            player.getSkills().alterSkill(Skills.MAGIC, (int) magicChange);
            double defenceChange = 5 + (player.getSkills().xpLevel(Skills.DEFENCE) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) defenceChange);
        } else if (potion == Potion.DIVINE_BASTION_POTION) {
            double rangeChange = 4 + (player.getSkills().xpLevel(Skills.RANGED) * 10 / 100.0);
            player.getSkills().alterSkill(Skills.RANGED, (int) rangeChange);

            double defenceChange = 5 + (player.getSkills().xpLevel(Skills.DEFENCE) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) defenceChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_BASTION_POTION);
        } else if (potion == Potion.DIVINE_BATTLEMAGE_POTION) {
            double magicChange = 4 + (player.getSkills().xpLevel(Skills.MAGIC) / 100.0);
            player.getSkills().alterSkill(Skills.MAGIC, (int) magicChange);

            double defenceChange = 5 + (player.getSkills().xpLevel(Skills.DEFENCE) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) defenceChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_BATTLEMAGE_POTION);
        } else if (potion == Potion.DIVINE_MAGIC_POTION) {
            double magicChange = 4 + (player.getSkills().xpLevel(Skills.MAGIC) / 100.0);
            player.getSkills().alterSkill(Skills.MAGIC, (int) magicChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_MAGIC_POTION);
        } else if (potion == Potion.DIVINE_RANGING_POTION) {
            double rangeChange = 4 + (player.getSkills().xpLevel(Skills.RANGED) * 10 / 100.0);
            player.getSkills().alterSkill(Skills.RANGED, (int) rangeChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_RANGING_POTION);
        } else if (potion == Potion.DIVINE_SUPER_ATTACK_POTION) {
            double attackChange = 5 + (player.getSkills().xpLevel(Skills.ATTACK) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.ATTACK, (int) attackChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_SUPER_ATTACK_POTION);
        } else if (potion == Potion.DIVINE_SUPER_COMBAT_POTION) {
            int curStr = player.getSkills().xpLevel(Skills.STRENGTH);
            int curAtk = player.getSkills().xpLevel(Skills.ATTACK);
            int curDef = player.getSkills().xpLevel(Skills.DEFENCE);
            player.getSkills().alterSkill(Skills.ATTACK, (int) ((curAtk * 0.1) + 10));
            player.getSkills().alterSkill(Skills.STRENGTH, (int) ((curStr * 0.1) + 10));
            player.getSkills().alterSkill(Skills.DEFENCE, (int) ((curDef * 0.1) + 10));
            onDivinePotionEffect(player, DivinePotion.DIVINE_SUPER_COMBAT_POTION);
        } else if (potion == Potion.DIVINE_SUPER_DEFENCE_POTION) {
            double defChange = 5 + (player.getSkills().xpLevel(Skills.DEFENCE) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.DEFENCE, (int) defChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_SUPER_DEFENCE_POTION);
        } else if (potion == Potion.DIVINE_SUPER_STRENGTH_POTION) {
            double strChange = 5 + (player.getSkills().xpLevel(Skills.STRENGTH) * 15 / 100.0);
            player.getSkills().alterSkill(Skills.STRENGTH, (int) strChange);
            onDivinePotionEffect(player, DivinePotion.DIVINE_SUPER_STRENGTH_POTION);
        } else if (potion == Potion.OVERLOAD_POTION) {
            Chain.bound(null).runFn(1, () -> {
                for (int i = 0; i < 5; i++) {
                    Chain.bound(null).name("overloadTask").runFn(i * 2, () -> {
                        player.animate(3170);
                        player.graphic(560);
                        if(!player.getMemberRights().isEliteMemberOrGreater(player)) {
                            player.hit(player,10);
                        }
                    });
                }
                player.putAttrib(OVERLOAD_POTION, 500);
                player.getSkills().overloadPlusBoost(Skills.ATTACK);
                player.getSkills().overloadPlusBoost(Skills.STRENGTH);
                player.getSkills().overloadPlusBoost(Skills.DEFENCE);
                player.getSkills().overloadPlusBoost(Skills.RANGED);
                player.getSkills().overloadPlusBoost(Skills.MAGIC);
                OverloadPotion.apply(player);
                int timer = (int) Utils.ticksToSeconds(500);
                player.getPacketSender().sendEffectTimer(timer, EffectTimer.OVERLOAD);
            });
        }
    }

    public static void onRecoverSpecialEffect(Player player) {
        player.putAttrib(AttributeKey.LAST_RECOVER_SPECIAL_POTION, System.currentTimeMillis());
        player.restoreSpecialAttack(100);
    }

    private enum DivinePotion {
        DIVINE_BASTION_POTION(DIVINE_BASTION_POTION_EFFECT_ACTIVE, DIVINE_BASTION_POTION_TICKS),
        DIVINE_BATTLEMAGE_POTION(DIVINE_BATTLEMAGE_POTION_EFFECT_ACTIVE, DIVINE_BATTLEMAGE_POTION_TICKS),
        DIVINE_MAGIC_POTION(DIVINE_MAGIC_POTION_EFFECT_ACTIVE, DIVINE_MAGIC_POTION_TICKS),
        DIVINE_RANGING_POTION(DIVINE_RANGING_POTION_EFFECT_ACTIVE, DIVINE_RANGING_POTION_TICKS),
        DIVINE_SUPER_ATTACK_POTION(DIVINE_SUPER_ATTACK_POTION_EFFECT_ACTIVE, DIVINE_SUPER_ATTACK_POTION_TICKS),
        DIVINE_SUPER_COMBAT_POTION(DIVINE_SUPER_COMBAT_POTION_EFFECT_ACTIVE, DIVINE_SUPER_COMBAT_POTION_TICKS),
        DIVINE_SUPER_DEFENCE_POTION(DIVINE_SUPER_DEFENCE_POTION_EFFECT_ACTIVE, DIVINE_SUPER_DEFENCE_POTION_TICKS),
        DIVINE_SUPER_STRENGTH_POTION(DIVINE_SUPER_STRENGTH_POTION_EFFECT_ACTIVE, DIVINE_SUPER_STRENGTH_POTION_TICKS);

        private final AttributeKey keyActive;
        private final AttributeKey keyTimeElapsed;

        DivinePotion(AttributeKey keyActive, AttributeKey keyTimeElapsed) {
            this.keyActive = keyActive;
            this.keyTimeElapsed = keyTimeElapsed;
        }
    }

    /**
     * The method that executes the divine potion action.
     *
     * @param player       the player to do this action for.
     * @param divinePotion the potion in question.
     */
    public static void onDivinePotionEffect(Player player, DivinePotion divinePotion) {
        player.hit(player,10);
        player.graphic(560);
        switch (divinePotion) {
            case DIVINE_BASTION_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_BASTION_POTION_TASK_RUNNING, false)) {
                    return;
                }
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineBastionPotion.setTimer(player);
            }

            case DIVINE_BATTLEMAGE_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_BATTLEMAGE_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineBattleMagePotion.setTimer(player);
            }

            case DIVINE_MAGIC_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_MAGIC_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineMagicPotion.setTimer(player);
            }

            case DIVINE_RANGING_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_RANGING_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineRangingPotion.setTimer(player);
            }

            case DIVINE_SUPER_ATTACK_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_SUPER_ATTACK_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineSuperAttackPotion.setTimer(player);
            }

            case DIVINE_SUPER_COMBAT_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_SUPER_COMBAT_POTION_TASK_RUNNING, false)) {
                    return;
                }
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineSuperCombatPotion.setTimer(player);
            }

            case DIVINE_SUPER_DEFENCE_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_SUPER_DEFENCE_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineSuperDefencePotion.setTimer(player);
            }

            case DIVINE_SUPER_STRENGTH_POTION -> {
                if (player.getAttribOr(AttributeKey.DIVINE_SUPER_STRENGTH_POTION_TASK_RUNNING, false))
                    return;
                player.putAttrib(divinePotion.keyActive, true);
                player.putAttrib(divinePotion.keyTimeElapsed, 500);
                DivineSuperStrengthPotion.setTimer(player);
            }
        }
    }

    public static boolean onItemOption1(Player player, Item item) {
        for (Potion pot : Potion.values()) {
            for (int id : pot.ids) {
                if (id == item.getId()) {
                    consume(player, pot, id);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean onItemOption2(Player player, Item item) {
        for (Potion pot : Potion.values()) {
            for (int id : pot.ids) {
                if (id == item.getId()) {
                    player.inventory().replace(id, 229, true);
                    player.message("You empty the vial.");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean onItemOnItem(Player player, Item used, Item with) {
        final Potion first = Potion.forId(used.getId()).orElse(null);

        if (first == null) {
            return false;
        }

        final Potion second = Potion.forId(with.getId()).orElse(null);

        if (second == null) {
            return false;
        }

        int usedSlot = player.getAttribOr(ITEM_SLOT, -1);
        int withSlot = player.getAttribOr(ALT_ITEM_SLOT, -1);

        if (first != second) {
            player.message("You can't mix two different types of potions.");
            return true;
        }

        if (first.ids[0] == used.getId() || second.ids[0] == with.getId()) {
            player.message("You can't combine these potions as one of them is already full.");
            return true;
        }

        int doses = getDoses(used.getId()) + getDoses(with.getId());
        int remainder = doses > 4 ? doses % 4 : 0;
        doses -= remainder;

        player.inventory().replace(used.getId(), first.getIdForDose(doses), withSlot, false);

        if (remainder > 0) {
            player.inventory().replace(with.getId(), first.getIdForDose(remainder), usedSlot, false);
        } else {
            player.inventory().replace(with.getId(), 229, usedSlot, false);
        }

        player.inventory().refresh();

        player.message("You carefully combine the potions.");
        return true;
    }

    private static int getDoses(int id) {
        ItemDefinition definition = World.getWorld().definitions().get(ItemDefinition.class, id);
        int index = definition.name.lastIndexOf(')');
        return Integer.parseInt(definition.name.substring(index - 1, index));
    }

}
