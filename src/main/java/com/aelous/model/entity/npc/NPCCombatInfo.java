package com.aelous.model.entity.npc;

import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.npc.droptables.Droptable;
import lombok.Data;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Bart on 10/6/2015.
 */
public class NPCCombatInfo {

    private static final Logger logger = LogManager.getLogger(NPCCombatInfo.class);

    public int[] ids;

    @Getter public Bonuses bonuses = new Bonuses();
    public Bonuses originalBonuses;

    public Stats originalStats;
    @Getter public Stats stats;
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
    public boolean unstacked = false; // True means it won't stack on other npcs.
    public int respawntime = 50;
    public boolean unattackable = false;
    public int droprolls = 1;
    public boolean boss = false;

    public int poison;
    public int poisonchance = 100;
    public int combatFollowDistance = 7; //default is 7
    public boolean retreats = true;

    public boolean poisonous() {
        return poison > 0 && poisonchance > 0;
    }

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
        public int stabdefence;
        public int slashdefence;
        public int crushdefence;
        public int rangeddefence;
        public int magicdefence;

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
                combat_ = (CombatMethod) resolveClass(combat);
                if (combat != null && combat.length() > 0)
                    combatMethodClass = (Class<CombatMethod>) Class.forName(combat);
                droptable_ = (Droptable) resolveClass(droptable);
                agro_ = (AggressionCheck) resolveClass(aggression);
            } catch (ClassNotFoundException e) { // it could be one of the others not agro
                System.err.println("missing script, no such class: "+e);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public CombatMethod newCombatInstance() {
            if (combatMethodClass != null) {
                try {
                    return combatMethodClass.getDeclaredConstructor().newInstance();
                } catch (InstantiationException e) {
                    System.err.println("issue init "+combat+": "+e);
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    System.err.println("issue init "+combat+": "+e);
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    System.err.println("issue init "+combat+": "+e);
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    System.err.println("issue init "+combat+": "+e);
                    e.printStackTrace();
                }
            }
            return null;
        }

        private static Object resolveClass(String str) throws Exception {
            if (str == null)
                return null;

            try {
                return Class.forName(str).getDeclaredConstructor().newInstance();
            } catch (NullPointerException e) {
                logger.error("bad class name mapping: " + str);
                return null;
            }
        }
    }
}
