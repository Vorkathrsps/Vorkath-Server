package com.cryptic.utility;

import com.cryptic.GameServer;
import com.cryptic.cache.DataStore;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.npc.NPCCombatInfo;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.io.FileNotFoundException;

public class NpcStatsConverter {

    static final Int2ObjectOpenHashMap<NPCCombatInfo> info = new Int2ObjectOpenHashMap<>();
    public static void main(String[] args) throws FileNotFoundException {
        MonsterLoader.load();
        GameServer.fileStore = new DataStore(GameServer.properties().fileStore);
        GameServer.definitions = new DefinitionRepository();
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


        System.out.println(info);

        System.out.println("bonuses size: " + info.size());
    }
}
