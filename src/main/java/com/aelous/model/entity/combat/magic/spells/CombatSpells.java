package com.aelous.model.entity.combat.magic.spells;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.impl.CombatEffectSpell;
import com.aelous.model.entity.combat.magic.impl.CombatNormalSpell;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;
import com.aelous.utility.Varbit;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.aelous.utility.ItemIdentifiers.*;

public enum
CombatSpells {

    WIND_STRIKE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Wind strike";
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }

        @Override
        public int baseMaxHit() {
            return 2;
        }

        @Override
        public int baseExperience() {
            return 5;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(MIND_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 1;
        }

        @Override
        public int spellId() {
            return 1152;
        }
    }),
    CONFUSE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Confuse";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {

        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(EARTH_RUNE, 2),
                Item.of(BODY_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 3;
        }

        @Override
        public int spellId() {
            return 1153;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WATER_STRIKE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Water strike";
        }

        @Override
        public int baseMaxHit() {
            return 4;
        }

        @Override
        public int baseExperience() {
            return 7;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE),
                Item.of(AIR_RUNE),
                Item.of(MIND_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 5;
        }

        @Override
        public int spellId() {
            return 1154;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    EARTH_STRIKE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Earth strike";
        }

        @Override
        public int baseMaxHit() {
            return 6;
        }


        @Override
        public int baseExperience() {
            return 9;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(MIND_RUNE),
                Item.of(EARTH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 9;
        }

        @Override
        public int spellId() {
            return 1156;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WEAKEN(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Weaken";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {

        }

        @Override
        public int baseExperience() {
            return 21;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(EARTH_RUNE, 2),
                Item.of(BODY_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 11;
        }

        @Override
        public int spellId() {
            return 1157;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FIRE_STRIKE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Fire strike";
        }

        @Override
        public int baseMaxHit() {
            return 8;
        }

        @Override
        public int baseExperience() {
            return 11;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(
                    Item.of(AIR_RUNE),
                    Item.of(MIND_RUNE));
            }
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(MIND_RUNE),
                Item.of(FIRE_RUNE, 3)
            );
        }

        @Override
        public int levelRequired() {
            return 13;
        }

        @Override
        public int spellId() {
            return 1158;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WIND_BOLT(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Wind bolt";
        }

        @Override
        public int baseMaxHit() {
            return 9;
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(CHAOS_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 17;
        }

        @Override
        public int spellId() {
            return 1160;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    CURSE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Curse";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
        }

        @Override
        public int baseExperience() {
            return 29;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE, 2),
                Item.of(EARTH_RUNE, 3),
                Item.of(BODY_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 19;
        }

        @Override
        public int spellId() {
            return 1161;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    BIND(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Bind";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, SWAMPBARK_HELM)) {
                count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, SWAMPBARK_BODY))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, SWAMPBARK_LEGS))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, SWAMPBARK_BOOTS))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, SWAMPBARK_GAUNTLETS))
                    count += 0.6;
                castOn.freeze((int) (8 + count), cast);
            }
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_BIND_SACK);
            if (spellSack) {
                return List.of(Item.of(BLIGHTED_BIND_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(EARTH_RUNE, 3),
                Item.of(NATURE_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 20;
        }

        @Override
        public int spellId() {
            return 1572;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WATER_BOLT(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Water bolt";
        }

        @Override
        public int baseMaxHit() {
            return 10;
        }

        @Override
        public int baseExperience() {
            return 16;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(CHAOS_RUNE),
                Item.of(WATER_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 23;
        }

        @Override
        public int spellId() {
            return 1163;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    EARTH_BOLT(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Earth bolt";
        }

        @Override
        public int baseMaxHit() {
            return 11;
        }

        @Override
        public int baseExperience() {
            return 19;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(CHAOS_RUNE),
                Item.of(EARTH_RUNE, 3)
            );
        }

        @Override
        public int levelRequired() {
            return 29;
        }

        @Override
        public int spellId() {
            return 1166;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FIRE_BOLT(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Fire bolt";
        }

        @Override
        public int baseMaxHit() {
            return 12;
        }

        @Override
        public int baseExperience() {
            return 22;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(
                    Item.of(AIR_RUNE, 3),
                    Item.of(CHAOS_RUNE));
            }
            return List.of(
                Item.of(AIR_RUNE, 3),
                Item.of(CHAOS_RUNE),
                Item.of(FIRE_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 35;
        }

        @Override
        public int spellId() {
            return 1169;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    CRUMBLE_UNDEAD(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Crumble Undead";
        }

        @Override
        public int baseMaxHit() {
            return 15;
        }

        @Override
        public int baseExperience() {
            return 24;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(CHAOS_RUNE),
                Item.of(EARTH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 39;
        }

        @Override
        public int spellId() {
            return 1171;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WIND_BLAST(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Wind blast";
        }

        @Override
        public int baseMaxHit() {
            return 13;
        }

        @Override
        public int baseExperience() {
            return 25;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 3),
                Item.of(DEATH_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 41;
        }

        @Override
        public int spellId() {
            return 1172;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WATER_BLAST(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Water blast";
        }

        @Override
        public int baseMaxHit() {
            return 14;
        }

        @Override
        public int baseExperience() {
            return 28;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(AIR_RUNE, 3),
                Item.of(DEATH_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 47;
        }

        @Override
        public int spellId() {
            return 1175;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    IBAN_BLAST(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Iban blast";
        }

        @Override
        public int baseMaxHit() {
            return 25;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of(
                Item.of(IBANS_STAFF),
                Item.of(IBANS_STAFF_U)
            );
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(DEATH_RUNE));
            }
            return List.of(
                Item.of(DEATH_RUNE),
                Item.of(FIRE_RUNE, 5)
            );
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1539;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    SNARE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Snare";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 2;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, SWAMPBARK_HELM)) {
                count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, SWAMPBARK_BODY))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, SWAMPBARK_LEGS))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, SWAMPBARK_BOOTS))
                    count += 0.6;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, SWAMPBARK_GAUNTLETS))
                    count += 0.6;
                castOn.freeze((int) (16 + count), cast);
            }
        }

        @Override
        public int baseExperience() {
            return 60;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_SNARE_SACK);
            if (spellSack) {
                return List.of(Item.of(BLIGHTED_SNARE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(EARTH_RUNE, 4),
                Item.of(NATURE_RUNE, 3)
            );
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1582;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    MAGIC_DART(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Magic Dart";
        }

        @Override
        public int baseMaxHit() {
            return 19;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of(
                Item.of(SLAYERS_STAFF),
                Item.of(STAFF_OF_THE_DEAD),
                Item.of(TOXIC_STAFF_OF_THE_DEAD)
            );
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(MIND_RUNE, 4),
                Item.of(DEATH_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12037;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    EARTH_BLAST(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Earth blast";
        }

        @Override
        public int baseMaxHit() {
            return 15;
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 3),
                Item.of(DEATH_RUNE),
                Item.of(EARTH_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 53;
        }

        @Override
        public int spellId() {
            return 1177;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FIRE_BLAST(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Fire blast";
        }

        @Override
        public int baseMaxHit() {
            return 16;
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE, 4), Item.of(DEATH_RUNE));
            }
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(DEATH_RUNE),
                Item.of(FIRE_RUNE, 5)
            );
        }

        @Override
        public int levelRequired() {
            return 59;
        }

        @Override
        public int spellId() {
            return 1181;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    SARADOMIN_STRIKE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Saradomin Strike";
        }

        @Override
        public int baseMaxHit() {
            return 20;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of(Item.of(SARADOMIN_STAFF), Item.of(STAFF_OF_LIGHT));
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE, 4), Item.of(BLOOD_RUNE, 2));
            }
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(BLOOD_RUNE, 2),
                Item.of(FIRE_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1190;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    CLAWS_OF_GUTHIX(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Claws of Guthix";
        }

        @Override
        public int baseMaxHit() {
            return 20;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of(Item.of(GUTHIX_STAFF), Item.of(STAFF_OF_BALANCE));
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE, 4), Item.of(BLOOD_RUNE, 2));
            }
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(BLOOD_RUNE, 2),
                Item.of(FIRE_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1191;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FLAMES_OF_ZAMORAK(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Flames of Zamorak";
        }

        @Override
        public int baseMaxHit() {
            return 20;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of(
                Item.of(ZAMORAK_STAFF),
                Item.of(STAFF_OF_THE_DEAD),
                Item.of(TOXIC_STAFF_OF_THE_DEAD)
            );
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE, 4), Item.of(BLOOD_RUNE, 2));
            }
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(BLOOD_RUNE, 2),
                Item.of(FIRE_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1192;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WIND_WAVE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Wind wave";
        }

        @Override
        public int baseMaxHit() {
            return 17;
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 5),
                Item.of(BLOOD_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 1183;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WATER_WAVE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Water wave";
        }

        @Override
        public int baseMaxHit() {
            return 18;
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 5),
                Item.of(BLOOD_RUNE),
                Item.of(WATER_RUNE, 7)
            );
        }

        @Override
        public int levelRequired() {
            return 65;
        }

        @Override
        public int spellId() {
            return 1185;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    VULNERABILITY(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Vulnerability";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            // Dealth elsewhere
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(EARTH_RUNE, 5),
                Item.of(WATER_RUNE, 5),
                Item.of(SOUL_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 66;
        }

        @Override
        public int spellId() {
            return 1542;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    EARTH_WAVE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Earth wave";
        }

        @Override
        public int baseMaxHit() {
            return 19;
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 5),
                Item.of(BLOOD_RUNE),
                Item.of(EARTH_RUNE, 7)
            );
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 1188;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    ENFEEBLE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Enfeeble";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
        }

        @Override
        public int baseExperience() {
            return 83;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(EARTH_RUNE, 8),
                Item.of(WATER_RUNE, 8),
                Item.of(SOUL_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 73;
        }

        @Override
        public int spellId() {
            return 1543;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FIRE_WAVE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Fire wave";
        }

        @Override
        public int baseMaxHit() {
            return 20;
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE, 5), Item.of(BLOOD_RUNE));
            }
            return List.of(
                Item.of(AIR_RUNE, 5),
                Item.of(BLOOD_RUNE),
                Item.of(FIRE_RUNE, 7)
            );
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1189;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    ENTANGLE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Entangle";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 5;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, SWAMPBARK_HELM)) {
                count += 0.6D;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, SWAMPBARK_BODY))
                    count += 0.6D;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, SWAMPBARK_LEGS))
                    count += 0.6D;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, SWAMPBARK_BOOTS))
                    count += 0.6D;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, SWAMPBARK_GAUNTLETS))
                    count += 0.6D;
                castOn.freeze((int) (25 + count), cast); // 15 second freeze timer
            }
        }

        @Override
        public int baseExperience() {
            return 91;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_ENTANGLE_SACK);
            if (spellSack) {
                return List.of(Item.of(BLIGHTED_ENTANGLE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 5),
                Item.of(EARTH_RUNE, 5),
                Item.of(NATURE_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 79;
        }

        @Override
        public int spellId() {
            return 1592;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    STUN(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Stun";
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills().level(Skills.ATTACK) < player.getSkills().xpLevel(Skills.ATTACK)) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketSender().sendMessage(
                            "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                int decrease = (int) (0.10 * (player.getSkills().level(Skills.ATTACK)));
                player.getSkills().setLevel(Skills.ATTACK, player.getSkills().level(Skills.ATTACK) - decrease);
                player.getSkills().update(Skills.ATTACK);
                player.message("You feel slightly weakened.");
            }
        }

        @Override
        public int baseExperience() {
            return 90;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(EARTH_RUNE, 12),
                Item.of(WATER_RUNE, 12),
                Item.of(AIR_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 1562;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    TELEBLOCK(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Teleblock";
        }

        @Override
        public boolean canCast(Player player, Entity target, boolean delete) {
            if (target.getTimers().has(TimerKey.TELEBLOCK) || target.getTimers().has(TimerKey.SPECIAL_TELEBLOCK) || target.getTimers().has(TimerKey.TELEBLOCK_IMMUNITY)) {
                player.message("That player is already being affected by this spell.");
                player.getCombat().reset();
                player.getCombat().setCastSpell(null);
                return false;
            }
            return super.canCast(player, target, delete);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            //Dealt elsewhere
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return -1;
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_TELEPORT_SPELL_SACK);
            if (spellSack) {
                return List.of(Item.of(BLIGHTED_TELEPORT_SPELL_SACK, 1));
            }
            return List.of(
                Item.of(LAW_RUNE),
                Item.of(CHAOS_RUNE),
                Item.of(DEATH_RUNE)
            );
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 12445;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    AIR_SURGE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Air surge";
        }

        @Override
        public int baseMaxHit() {
            return 21;
        }

        @Override
        public int baseExperience() {
            return 44;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 7),
                Item.of(WRATH_RUNE, 1)
            );
        }

        @Override
        public int levelRequired() {
            return 81;
        }

        @Override
        public int spellId() {
            return 22708;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    WATER_SURGE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Water surge";
        }

        @Override
        public int baseMaxHit() {
            return 22;
        }

        @Override
        public int baseExperience() {
            return 46;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(WATER_RUNE, 10),
                Item.of(AIR_RUNE, 7),
                Item.of(WRATH_RUNE, 1)
            );
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 22658;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    EARTH_SURGE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Earth surge";
        }

        @Override
        public int baseMaxHit() {
            return 23;
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(EARTH_RUNE, 10),
                Item.of(AIR_RUNE, 7),
                Item.of(WRATH_RUNE, 1)
            );
        }

        @Override
        public int levelRequired() {
            return 90;
        }

        @Override
        public int spellId() {
            return 22628;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    FIRE_SURGE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Fire surge";
        }

        @Override
        public int baseMaxHit() {
            return 24;
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(FIRE_RUNE, 10), Item.of(AIR_RUNE, 7));
            }
            return List.of(
                Item.of(FIRE_RUNE, 10),
                Item.of(AIR_RUNE, 7),
                Item.of(WRATH_RUNE, 1)
            );
        }

        @Override
        public int levelRequired() {
            return 95;
        }

        @Override
        public int spellId() {
            return 22608;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    SMOKE_RUSH(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Smoke rush";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (World.getWorld().rollDie(100, 25)) {
                castOn.poison(2);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 13;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(Item.of(AIR_RUNE), Item.of(CHAOS_RUNE, 2), Item.of(DEATH_RUNE, 2));
            }
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(FIRE_RUNE),
                Item.of(CHAOS_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12939;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SHADOW_RUSH(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Shadow rush";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills().level(Skills.ATTACK) < player.getSkills().xpLevel(Skills.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.1 * (player.getSkills().level(Skills.ATTACK)));
                player.getSkills().setLevel(Skills.ATTACK, player.getSkills().level(Skills.ATTACK) - decrease);
                player.getSkills().update(Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 14;
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(SOUL_RUNE),
                Item.of(CHAOS_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 52;
        }

        @Override
        public int spellId() {
            return 12987;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    BLOOD_RUSH(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Blood rush";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, BLOODBARK_HELM)) {
                count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, BLOODBARK_BODY))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, BLOODBARK_LEGS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, BLOODBARK_BOOTS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, BLOODBARK_GAUNTLETS))
                    count += .15;
                if (hit.isAccurate()) {
                    cast.heal((int) (hit.getDamage() / 4 + count)); // Heal for 25% with blood barr
                }
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 15;
        }

        @Override
        public int baseExperience() {
            return 33;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(BLOOD_RUNE),
                Item.of(CHAOS_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 56;
        }

        @Override
        public int spellId() {
            return 12901;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    ICE_RUSH(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Ice rush";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            castOn.freeze(8, cast);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 18;
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_ANCIENT_ICE_SACK);
            boolean onAncientsSpellbook = player.getSpellbook().equals(MagicSpellbook.ANCIENT);
            if (spellSack && onAncientsSpellbook && player.varps().varbit(Varbit.IN_WILDERNESS) != 0) {
                return List.of(Item.of(BLIGHTED_ANCIENT_ICE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 2),
                Item.of(CHAOS_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 58;
        }

        @Override
        public int spellId() {
            return 12861;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SMOKE_BURST(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Smoke rush";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (World.getWorld().rollDie(100, 25)) {
                castOn.poison(2);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 13;
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(
                    Item.of(AIR_RUNE, 2),
                    Item.of(CHAOS_RUNE, 4),
                    Item.of(DEATH_RUNE, 2)
                );
            }
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(FIRE_RUNE, 2),
                Item.of(CHAOS_RUNE, 4),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 12963;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SHADOW_BURST(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Shadow burst";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills().level(Skills.ATTACK) < player.getSkills().xpLevel(Skills.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.1 * (player.getSkills().level(Skills.ATTACK)));
                player.getSkills().setLevel(Skills.ATTACK, player.getSkills().level(Skills.ATTACK) - decrease);
                player.getSkills().update(Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 18;
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE),
                Item.of(SOUL_RUNE, 2),
                Item.of(CHAOS_RUNE, 4),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 64;
        }

        @Override
        public int spellId() {
            return 13011;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    BLOOD_BURST(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Blood burst";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, BLOODBARK_HELM)) {
                count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, BLOODBARK_BODY))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, BLOODBARK_LEGS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, BLOODBARK_BOOTS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, BLOODBARK_GAUNTLETS))
                    count += .15;
                if (hit.isAccurate()) {
                    cast.heal((int) (hit.getDamage() / 4 + count)); // Heal for 25% with blood barr
                }
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 21;
        }

        @Override
        public int baseExperience() {
            return 39;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(BLOOD_RUNE, 2),
                Item.of(CHAOS_RUNE, 4),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 68;
        }

        @Override
        public int spellId() {
            return 12919;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    ICE_BURST(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Ice burst";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            castOn.freeze(16, cast);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 22;
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_ANCIENT_ICE_SACK);
            boolean onAncientsSpellbook = player.getSpellbook().equals(MagicSpellbook.ANCIENT);
            if (spellSack && onAncientsSpellbook && player.varps().varbit(Varbit.IN_WILDERNESS) != 0) {
                return List.of(Item.of(BLIGHTED_ANCIENT_ICE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 4),
                Item.of(CHAOS_RUNE, 4),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 12881;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SMOKE_BLITZ(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Smoke blitz";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (World.getWorld().rollDie(100, 25)) {
                castOn.poison(4);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 23;
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(
                    Item.of(AIR_RUNE, 2),
                    Item.of(BLOOD_RUNE, 2),
                    Item.of(DEATH_RUNE, 2)
                );
            }
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(FIRE_RUNE, 2),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 74;
        }

        @Override
        public int spellId() {
            return 12951;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SHADOW_BLITZ(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Shadow blitz";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills().level(Skills.ATTACK) < player.getSkills().xpLevel(Skills.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.15 * (player.getSkills().level(Skills.ATTACK)));
                player.getSkills().setLevel(Skills.ATTACK, player.getSkills().level(Skills.ATTACK) - decrease);
                player.getSkills().update(Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 24;
        }

        @Override
        public int baseExperience() {
            return 43;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 2),
                Item.of(SOUL_RUNE, 2),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 76;
        }

        @Override
        public int spellId() {
            return 12999;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    BLOOD_BLITZ(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Blood blitz";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, BLOODBARK_HELM)) {
                count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, BLOODBARK_BODY))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, BLOODBARK_LEGS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, BLOODBARK_BOOTS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, BLOODBARK_GAUNTLETS))
                    count += .15;
                if (hit.isAccurate()) {
                    cast.heal((int) (hit.getDamage() / 4 + count)); // Heal for 25% with blood barr
                }
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 25;
        }

        @Override
        public int baseExperience() {
            return 45;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(BLOOD_RUNE, 4),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 12911;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    ICE_BLITZ(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Ice blitz";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            castOn.freeze(25, cast); // 15 second freeze timer
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public int baseMaxHit() {
            return 26;
        }

        @Override
        public int baseExperience() {
            return 46;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_ANCIENT_ICE_SACK);
            boolean onAncientsSpellbook = player.getSpellbook().equals(MagicSpellbook.ANCIENT);
            if (spellSack && onAncientsSpellbook && player.varps().varbit(Varbit.IN_WILDERNESS) != 0) {
                return List.of(Item.of(BLIGHTED_ANCIENT_ICE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 3),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 2)
            );
        }

        @Override
        public int levelRequired() {
            return 82;
        }

        @Override
        public int spellId() {
            return 12871;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SMOKE_BARRAGE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Smoke barrage";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (World.getWorld().rollDie(100, 25)) {
                castOn.poison(4);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 27;
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean hasTomeOfFire = player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE);
            if (hasTomeOfFire) {
                return List.of(
                    Item.of(AIR_RUNE, 4),
                    Item.of(BLOOD_RUNE, 2),
                    Item.of(DEATH_RUNE, 4)
                );
            }
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(FIRE_RUNE, 4),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 86;
        }

        @Override
        public int spellId() {
            return 12975;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    SHADOW_BARRAGE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Shadow barrage";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills().level(Skills.ATTACK) < player.getSkills().xpLevel(Skills.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.15 * (player.getSkills().level(Skills.ATTACK)));
                player.getSkills().setLevel(Skills.ATTACK, player.getSkills().level(Skills.ATTACK) - decrease);
                player.getSkills().update(Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 28;
        }

        @Override
        public int baseExperience() {
            return 49;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(AIR_RUNE, 4),
                Item.of(SOUL_RUNE, 3),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 88;
        }

        @Override
        public int spellId() {
            return 13023;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    BLOOD_BARRAGE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Blood barrage";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            double count = 0;
            if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HEAD, BLOODBARK_HELM)) {
                count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.BODY, BLOODBARK_BODY))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.LEGS, BLOODBARK_LEGS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.FEET, BLOODBARK_BOOTS))
                    count += .15;
                if (cast.getAsPlayer().getEquipment().hasAt(EquipSlot.HANDS, BLOODBARK_GAUNTLETS))
                    count += .15;
                if (hit.isAccurate()) {
                    cast.heal((int) (hit.getDamage() / 4 + count));
                }
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 29;
        }

        @Override
        public int baseExperience() {
            return 51;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of(
                Item.of(DEATH_RUNE, 4),
                Item.of(SOUL_RUNE),
                Item.of(BLOOD_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 92;
        }

        @Override
        public int spellId() {
            return 12929;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    ICE_BARRAGE(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Ice barrage";
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            castOn.freeze(32, cast);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public int baseMaxHit() {
            return 30;
        }

        @Override
        public int baseExperience() {
            return 52;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            boolean spellSack = player.inventory().contains(BLIGHTED_ANCIENT_ICE_SACK);
            boolean onAncientsSpellbook = player.getSpellbook().equals(MagicSpellbook.ANCIENT);
            if (spellSack && onAncientsSpellbook && player.varps().varbit(Varbit.IN_WILDERNESS) != 0) {
                return List.of(Item.of(BLIGHTED_ANCIENT_ICE_SACK, 1));
            }
            return List.of(
                Item.of(WATER_RUNE, 6),
                Item.of(BLOOD_RUNE, 2),
                Item.of(DEATH_RUNE, 4)
            );
        }

        @Override
        public int levelRequired() {
            return 94;
        }

        @Override
        public int spellId() {
            return 12891;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.ANCIENT;
        }
    }),
    TRIDENT_OF_THE_SEAS(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Trident of the seas";
        }

        @Override
        public int baseMaxHit() {
            return 28;
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    TRIDENT_OF_THE_SWAMP(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Trident of the swamp";
        }

        @Override
        public int baseMaxHit() {
            return 33;
        }

        @Override
        public int baseExperience() {
            return 50;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 2;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    SANGUINESTI_STAFF(new CombatEffectSpell() {
        @Override
        public String name() {
            return "Sanguinesti spell";
        }

        @Override
        public int baseMaxHit() {
            return 34;
        }

        @Override
        public int baseExperience() {
            return 2;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn, Hit hit) {
            if (Utils.securedRandomChance(67.0 / 100)) { //0.67
                if (hit.isAccurate()) {
                    cast.heal(hit.getDamage() / 2);
                }
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 3;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    TUMEKENS_SHADOW(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Tumekens Spell";
        }

        @Override
        public int baseMaxHit() {
            return 0;
        }

        @Override
        public int baseExperience() {
            return 2;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 6;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    ACCURSED_SCEPTRE(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Sceptre Spell";
        }

        @Override
        public int baseMaxHit() {
            return 0;
        }

        @Override
        public int baseExperience() {
            return 2;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 7;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    VOLATILE_NIGHTMARE_STAFF(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Volatile spell";
        }

        @Override
        public int baseMaxHit() {
            return 0;
        }

        @Override
        public int baseExperience() {
            return 2;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 4;
        }

        @Override
        public MagicSpellbook spellbook() {
            return MagicSpellbook.NORMAL;
        }
    }),
    ELDRITCH_NIGHTMARE_STAFF(new CombatNormalSpell() {
        @Override
        public String name() {
            return "Eldritch spell";
        }

        @Override
        public int baseMaxHit() {
            return 50;
        }

        @Override
        public int baseExperience() {
            return 2;
        }

        @Override
        public List<Item> equipmentRequired(Player player) {
            return List.of();
        }

        @Override
        public List<Item> itemsRequired(Player player) {
            return List.of();
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 5;
        }

        @Override
        public MagicSpellbook spellbook() {
            return null;
        }
    });

    /**
     * The spell attached to this element.
     */
    private final CombatSpell spell;

    /**
     * Creates a new {@link CombatSpells}.
     *
     * @param spell the spell attached to this element.
     */
    CombatSpells(CombatSpell spell) {
        this.spell = spell;
    }

    /**
     * Gets the spell attached to this element.
     *
     * @return the spell.
     */
    public final CombatSpell getSpell() {
        return spell;
    }

    /**
     * Gets the spell with a {@link CombatSpell#spellId()} of {@code id}.
     *
     * @param id the identification of the combat spell.
     * @return the combat spell with that identification.
     */
    public static Optional<CombatSpells> getCombatSpells(int id) {
        return Arrays.stream(CombatSpells.values()).filter(s -> s != null && s.getSpell().spellId() == id).findFirst();
    }

    public static CombatSpell getCombatSpell(int spellId) {
        Optional<CombatSpells> spell = getCombatSpells(spellId);
        return spell.map(CombatSpells::getSpell).orElse(null);
    }
}
