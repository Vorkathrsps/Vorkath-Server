package com.cryptic.model.entity.npc;

import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.CombatMethod;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.utility.DynamicClassLoader;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Bart on 10/6/2015.
 */
@Data
public class NPCCombatInfo {
    private static final Logger logger = LogManager.getLogger(NPCCombatInfo.class);

    public int[] ids;

    public Bonuses bonuses = new Bonuses();
    public Bonuses originalBonuses;
    public Stats originalStats;
    public Stats stats;
    public Animations animations;
    public Sounds sounds;
    public Scripts scripts;
    public int maxhit;
    public int projectile;
    public int attackspeed = 4;
    public double slayerxp = 0;
    public int slayerlvl = 1;
    public int deathlen = 5;
    public boolean aggressive;
    public int aggroradius = 1;
    public boolean retaliates = true;
    public boolean unstacked = false;
    public int respawntime = 50;
    public boolean unattackable = false;
    public boolean immunePoison = false;
    public boolean immuneVenom = false;
    public int droprolls = 1;
    public boolean boss = false;
    public CombatType combattype;
    public AttackType attackType;

    public int poison;
    public int poisonchance = 100;
    public boolean retreats = true;

    public boolean poisonous() {
        return poison > 0 && poisonchance > 0;
    }

    @Data
    public static class Stats {
        public int attack = 1;
        public int strength = 1;
        public int defence = 1;
        public int magic = 1;
        public int ranged = 1;
        public int hitpoints = 1;

        public Stats clone() {
            Stats stats = new Stats();
            stats.attack = attack;
            stats.strength = strength;
            stats.defence = defence;
            stats.magic = magic;
            stats.ranged = ranged;
            stats.hitpoints = hitpoints;
            return stats;
        }

        @Override
        public String toString() {
            return String.format("[%d, %d, %d, %d, %d, %d]", attack, strength, defence, hitpoints, ranged, 0, magic);
        }
    }


    @Data
    public static class Bonuses {
        public int attack;
        public int magic;
        public int ranged;
        public int strength;
        public int magicstrength;
        public int stabdefence;
        public int slashdefence;
        public int crushdefence;
        public int rangeddefence;
        public int magicdefence;
        public int rangestrength;

        public Bonuses clone() {
            Bonuses bonuses = new Bonuses();
            bonuses.attack = attack;
            bonuses.magic = magic;
            bonuses.ranged = ranged;
            bonuses.strength = strength;
            bonuses.stabdefence = stabdefence;
            bonuses.slashdefence = slashdefence;
            bonuses.crushdefence = crushdefence;
            bonuses.rangeddefence = rangeddefence;
            bonuses.magicdefence = magicdefence;
            bonuses.rangestrength = rangestrength;
            bonuses.magicstrength = magicstrength;
            return bonuses;
        }
    }

    public static class Animations {
        public int attack;
        public int block;
        public int death;
    }

    public static class Sounds {
        public int[] attack;
        public int[] block;
        public int[] death;
    }

    @SuppressWarnings("ALL")
    public static class Scripts {
        public String hit;
        public String combat;
        public String droptable;
        public String death;
        public String aggression;
        public CombatMethod combat_;
        public Class<CombatMethod> combatMethodClass;
        public Droptable droptable_;
        public AggressionCheck agro_;

        public void resolve() {
            try {
                combat_ = (CombatMethod) resolveCombat(combat);
                if (combat != null) combatMethodClass = (Class<CombatMethod>) resolveCCM(combat);
                droptable_ = (Droptable) resolveClass(droptable);
                agro_ = (AggressionCheck) resolveClass(aggression);
            } catch (ClassNotFoundException e) {
                System.err.println("Missing script, no such class: " + e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public CombatMethod newCombatInstance() {
            if (combatMethodClass != null) {
                try {
                    return combatMethodClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         NoSuchMethodException e) {
                    System.err.println("issue init " + combat + ": " + e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        public Class<? extends CombatMethod> resolveCCM(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
            Class<? extends CombatMethod> clazz = null;
            for (var v : DynamicClassLoader.scriptmap.values()) {
                if (v.getSimpleName().equalsIgnoreCase(className)) {
                    clazz = v;
                    break;
                }
            }
            return (Class<? extends CombatMethod>) clazz;
        }

        public static CombatMethod resolveCombat(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
            CombatMethod result = null;
            for (var c : DynamicClassLoader.scriptmap.keySet()) {
                if (c == null) continue;
                if (c.getSimpleName().equalsIgnoreCase(className)) {
                    result = (CombatMethod) c.getDeclaredConstructor().newInstance();
                    break;
                }
            }
            return result;
        }

        private static Object resolveClass(String str) throws Exception {
            if (str == null) return null;
            try {
                return Class.forName(str).getDeclaredConstructor().newInstance();
            } catch (NullPointerException e) {
                logger.error("bad class name mapping: " + str);
                return null;
            }
        }
    }
}
