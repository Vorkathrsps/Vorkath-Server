package com.cryptic.utility;

import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Data;

import java.util.List;

@Data
public class Monster {
    private int id;
    private String name;
    @JsonProperty("last_updated")
    private String lastUpdated;
    private boolean incomplete;
    private boolean members;
    @JsonProperty("release_date")
    private String releaseDate;
    @JsonProperty("combat_level")
    private int combatLevel;
    private int size;
    private int hitpoints;
    @JsonProperty("max_hit")
    private int maxHit;
    @JsonProperty("attack_type")
    private List<String> attackType;
    @JsonProperty("attack_speed")
    private int attackSpeed;
    private boolean aggressive;
    private boolean poisonous;
    private boolean venomous;
    @JsonProperty("immune_poison")
    private boolean immunePoison;
    @JsonProperty("immune_venom")
    private boolean immuneVenom;
    private List<String> attributes;
    private List<String> category;
    @JsonProperty("slayer_monster")
    private boolean slayerMonster;
    @JsonProperty("slayer_level")
    private int slayerLevel;
    @JsonProperty("slayer_xp")
    private int slayerXp;
    @JsonProperty("slayer_masters")
    private List<String> slayerMasters;
    private boolean duplicate;
    private String examine;
    @JsonProperty("wiki_name")
    private String wikiName;
    @JsonProperty("wiki_url")
    private String wikiUrl;
    @JsonProperty("attack_level")
    private int attackLevel;
    @JsonProperty("strength_level")
    private int strengthLevel;
    @JsonProperty("defence_level")
    private int defenceLevel;
    @JsonProperty("magic_level")
    private int magicLevel;
    @JsonProperty("ranged_level")
    private int rangedLevel;
    @JsonProperty("attack_bonus")
    private int attackBonus;
    @JsonProperty("strength_bonus")
    private int strengthBonus;
    @JsonProperty("attack_magic")
    private int attackMagic;
    @JsonProperty("magic_bonus")
    private int magicBonus;
    @JsonProperty("attack_ranged")
    private int attackRanged;
    @JsonProperty("ranged_bonus")
    private int rangedBonus;
    @JsonProperty("defence_stab")
    private int defenceStab;
    @JsonProperty("defence_slash")
    private int defenceSlash;
    @JsonProperty("defence_crush")
    private int defenceCrush;
    @JsonProperty("defence_magic")
    private int defenceMagic;
    @JsonProperty("defence_ranged")
    private int defenceRanged;
    private List<Drop> drops;

    @Data
    public static class Drop {
        private int id;
        private String name;
        private boolean members;
        private String quantity;
        private boolean noted;
        private double rarity;
        private int rolls;
    }


    public static final Int2ObjectOpenHashMap<NPCCombatInfo> info = new Int2ObjectOpenHashMap<>();

    public static void loadStats() {
        MonsterLoader.monsters.int2ObjectEntrySet().fastForEach(monster -> {
            var npc = monster.getValue();
            var cbinfo = new NPCCombatInfo();
            var id = npc.getId();
            var attackspeed = npc.getAttackSpeed();
            var aggressive = npc.isAggressive();
            var immunePoison = npc.isImmunePoison();
            var immuneVenom = npc.isImmuneVenom();
            var slayerLevel = npc.getSlayerLevel();
            var maxhit = npc.getMaxHit();
            var attackType = npc.getAttackType();
            var hp = npc.getHitpoints();
            var att = npc.getAttackLevel();
            var str = npc.getStrengthLevel();
            var def = npc.getDefenceLevel();
            var magic = npc.getMagicLevel();
            var range = npc.getRangedLevel();
            var attBonus = npc.getAttackBonus();
            var strBonus = npc.getStrengthBonus();
            var attMagic = npc.getAttackMagic();
            var magicBonus = npc.getMagicBonus();
            var attackRanged = npc.getAttackRanged();
            var rangedBonus = npc.getRangedBonus();
            var defStab = npc.getDefenceStab();
            var defSlash = npc.getDefenceSlash();
            var defCrush = npc.getDefenceCrush();
            var defMagic = npc.getDefenceMagic();
            var defRange = npc.getDefenceRanged();
            cbinfo.setIds(new int[]{id});
            cbinfo.setAttackspeed(attackspeed);
            cbinfo.setAggressive(aggressive);
            cbinfo.setImmunePoison(immunePoison);
            cbinfo.setImmuneVenom(immuneVenom);
            cbinfo.setSlayerlvl(slayerLevel);
            cbinfo.setMaxhit(maxhit);
            if (attackType.contains("crush") || attackType.contains("slash") || attackType.contains("melee")) {
                cbinfo.setCombattype(CombatType.MELEE);
            } else if (attackType.contains("magic")) {
                cbinfo.setCombattype(CombatType.MAGIC);
            } else if (attackType.contains("range")) {
                cbinfo.setCombattype(CombatType.RANGED);
            }
            NPCCombatInfo.Stats stats = new NPCCombatInfo.Stats();
            stats.setHitpoints(hp);
            stats.setAttack(att);
            stats.setStrength(str);
            stats.setDefence(def);
            stats.setMagic(magic);
            stats.setRanged(range);
            cbinfo.setStats(stats);
            NPCCombatInfo.Bonuses bonuses = new NPCCombatInfo.Bonuses();
            bonuses.setAttack(attBonus);
            bonuses.setMagic(magicBonus);
            bonuses.setRanged(rangedBonus);
            bonuses.setStrength(strBonus);
            bonuses.setMagicstrength(attMagic);
            bonuses.setStabdefence(defStab);
            bonuses.setSlashdefence(defSlash);
            bonuses.setCrushdefence(defCrush);
            bonuses.setRangeddefence(defRange);
            bonuses.setMagicdefence(defMagic);
            bonuses.setRangestrength(attackRanged);
            cbinfo.setBonuses(bonuses);
            info.put(npc.getId(), cbinfo);
        });
    }



}



