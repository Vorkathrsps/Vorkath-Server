package com.aelous.model.entity.player;

import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.areas.edgevile.Mac;
import com.aelous.model.content.skill.Skillable;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.content.bountyhunter.BountyHunter;
import com.aelous.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.aelous.model.entity.attributes.AttributeKey.DOUBLE_EXP_TICKS;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
public class Skills {

    public static final int SKILL_COUNT = 23;
    private static final int[] XP_TABLE = new int[100];
    public static boolean USE_EXPERIMENTAL_PERFORMANCE = false;
    public double[] xps = new double[SKILL_COUNT];
    public int[] levels = new int[SKILL_COUNT];
    private final Player player;
    private int combat;

    public boolean test;
    public final boolean[] dirty = new boolean[SKILL_COUNT];

    public Skills(Player player) {
        this.player = player;

        Arrays.fill(levels, 1);

        /* Hitpoints differs :) */
        xps[3] = levelToXp(10);
        levels[3] = 10;
    }

    private double expModifiers(int skill) {
        switch(skill) {
            case PRAYER -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 50.0;
            }
            case COOKING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case WOODCUTTING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case FLETCHING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case FISHING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 40.0;
            }
            case FIREMAKING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case CRAFTING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case SMITHING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 60.0;
            }
            case MINING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 35 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 50 : 50.0;
            }
            case HERBLORE -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case AGILITY -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
            case THIEVING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 35.0;
            }
            case SLAYER -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 25.0;
            }
            case FARMING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 55.0;
            }
            case RUNECRAFTING -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 50.0;
            }
            case HUNTER -> {
                return player.<Boolean>getAttribOr(AttributeKey.HARD_EXP_MODE,false) ? 15 : player.getIronManStatus() != IronMode.NONE || player.getGameMode().isDarklord() ? 20 : 30.0;
            }
        }
        return 1.0;
    }

    public void update() {
        update(false);
    }

    public void update(boolean ignore) {
        if(!ignore) {
            for (int skill = 0; skill < SKILL_COUNT; skill++) {
                //Send the skill
                player.getPacketSender().updateSkill(skill, levels[skill], (int) xps[skill]);
            }

            updatePrayerText();
            recalculateCombat();

            //Update prayer orb
            player.getPacketSender().sendString(4012, "" + levels[PRAYER]+"");
            player.getPacketSender().sendString(4013, "" + xpToLevel((int) xps[PRAYER])+"");

            //Update hp orb
            player.getPacketSender().sendString(4016, "" + levels[HITPOINTS]+"");
            player.getPacketSender().sendString(4017, "" + xpToLevel((int) xps[HITPOINTS])+"");

            //Send total level
            player.getPacketSender().sendString(10121, "" + totalLevel());
        }
    }

    public void updatePrayerText() {
        int currentLevel = levels[PRAYER];
        int maxLevel = xpToLevel((int) xps[PRAYER]);
        player.getPacketSender().sendString(687,Color.ORANGE.tag()+currentLevel + "/" + maxLevel);
    }

    public void syncDirty() {
        for (int skill = 0; skill < SKILL_COUNT; skill++) {
            if (dirty[skill]) {
                player.getPacketSender().updateSkill(skill, levels[skill], (int) xps[skill]);
                dirty[skill] = false;
            }
        }
    }

    public void makeDirty(int skill) {
        this.makeDirty(skill,false);
    }

    public void makeDirty(int skill, boolean ignore) {
        if (USE_EXPERIMENTAL_PERFORMANCE) {
            dirty[skill] = true;
        } else {
            player.getPacketSender().updateSkill(skill, levels[skill], (int) xps[skill]);
        }

        if (player.getSkills().combatLevel() >= 126 && player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
            player.putAttrib(AttributeKey.COMBAT_MAXED, true);
        }

        //Only unlockable for trained accounts.
        if(player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
            if (totalLevel() >= 750) {
               // AchievementsManager.activate(player, Achievements.SKILLER_I, 1);
            }
            if (totalLevel() >= 1000) {
               // AchievementsManager.activate(player, Achievements.SKILLER_II, 1);
            }
            if (totalLevel() >= 1500) {
               // AchievementsManager.activate(player, Achievements.SKILLER_III, 1);
            }
            if (totalLevel() >= Mac.TOTAL_LEVEL_FOR_MAXED) {
               // AchievementsManager.activate(player, Achievements.SKILLER_IV, 1);
            }
        }

        //Update prayer orb
        player.getPacketSender().sendString(4012, "" + levels[PRAYER]+"");
        player.getPacketSender().sendString(4013, "" + xpToLevel((int) xps[PRAYER])+"");

        //Update hp orb
        player.getPacketSender().sendString(4016, "" + levels[HITPOINTS]+"");
        player.getPacketSender().sendString(4017, "" + xpToLevel((int) xps[HITPOINTS])+"");

        if(!ignore) {
            player.getSkills().updatePrayerText();
        }
    }

    /**
     * Returns the current level a stat is at, could be 50/99 for HP.
     * <br>Use XP to get the real level.
     *
     * @param skill
     * @return
     */
    public int level(int skill) {
        return levels[skill];
    }

    /**
     * Gets the level which your XP qualifies you for
     */
    public int xpLevel(int skill) {
        return xpToLevel((int) xps[skill]);
    }

    public int[] levels() {
        return levels;
    }

    public void setAllLevels(int[] levels) {
        this.levels = levels;
    }

    public double[] xp() {
        return xps;
    }

    public void setAllXps(double[] xps) {
        this.xps = xps;
    }

    public void setXp(int skill, double amt) {
        this.setXp(skill, amt,false);
    }

    public void setXp(int skill, double amt, boolean ignore) {
        xps[skill] = Math.min(200000000, amt);
        int newLevel = xpToLevel((int) xps[skill]);
        levels[skill] = newLevel;

        if(!ignore) {
            recalculateCombat();
            makeDirty(skill);
        }
    }

    public void setLevel(int skill, int lvtemp) {
        this.setLevel(skill, lvtemp,false);
    }

    public void setLevel(int skill, int lvtemp, boolean ignore) {
        levels[skill] = lvtemp;
        if(!ignore) {
            makeDirty(skill);
        }
    }

    public boolean addXp(int skill, double amt) {
        return addXp(skill, amt, true, true);
    }

    public boolean addXp(int skill, double amt, boolean multiplied) {
        return addXp(skill, amt, multiplied, true);
    }

    public boolean isCombatSkill(int skillId) {
        return Arrays.asList(0, 1, 2, 4, 6).stream().anyMatch(s -> s.intValue() == skillId);
    }

    public boolean addXp(int skill, double amount, boolean multiplied, boolean counter) {
        //System.err.println("blocked xp drop 0.. skillId="+skill);
        Entity target = ((WeakReference<Entity>) player.getAttribOr(AttributeKey.TARGET, new WeakReference<>(null))).get();
        // Active target and facing. Can't tell if combat script is running.
        boolean pvp = target != null && target.isPlayer() && target.getIndex() + 32768 == (int) player.getAttribOr(AttributeKey.LAST_FACE_ENTITY_IDX, 0);
        boolean combatxp = skill == ATTACK || skill == STRENGTH || skill == DEFENCE || skill == RANGED || skill == MAGIC || skill == HITPOINTS;
        boolean locked = player.getAttribOr(AttributeKey.XP_LOCKED, false);
        /**
         * player.inActiveTournament() || player.isInTournamentLobby()
         */
        boolean x1xp = player.getAttribOr(AttributeKey.X1XP, false);
        boolean inWilderness = WildernessArea.inWild(player);

        if (target != null && target.isNpc() && combatxp) { // Don't add exp if the target is hidden or locked.
            NPC npc = (NPC) target;
            if (npc.hidden() || (npc.locked() && !npc.isDamageOkLocked()))
                return false;
        }


        if (multiplied) {
            if (player.getIronManStatus() == IronMode.NONE) {
                if (combatxp) {
                    if (!x1xp) {//if x1, no multiplier is applied.
                        amount *= player.getGameMode().combatXpRate();
                    }
                } else {
                    amount *= expModifiers(skill);
                }
            } else { // Iron Man mode is always x20.
                if (!(combatxp && x1xp)) {//iron men, if x1 set, don't get multipler.
                    amount *= player.getIronManStatus() != IronMode.NONE ? 20 : player.getGameMode().combatXpRate();
                }
            }
        }

        //Double exp in wilderness is only in the pvp world.
        amount *= inWilderness && GameServer.properties().pvpMode ? 2.0 : 1.0;

        var double_exp_ticks = player.<Integer>getAttribOr(DOUBLE_EXP_TICKS,0) > 0;

        var donator_zone = player.tile().memberZone() || player.tile().memberCave();

        //Genie pet gives x2 exp
        amount *= donator_zone || double_exp_ticks ? 2.0 : 1.0;

        //World multiplier exp gives x2 exp.
        amount *= World.getWorld().xpMultiplier > 1 ? 2.0 : 1.0;

        //System.out.println("earning combat xp: " + amount);

        //System.err.println("SkillId="+skill);

        if (combatxp && locked) { //don't get combat exp when locked.
            player.getPacketSender().sendFakeXPDrop(skill, amount);
            return false;
        }

        player.getPacketSender().sendXPDrop(skill, amount, counter);

        int oldLevel = xpToLevel((int) xps[skill]);
        xps[skill] = Math.min(200000000, xps[skill] + amount);
        int newLevel = xpToLevel((int) xps[skill]);

        if (newLevel > oldLevel) {
            if (levels[skill] < newLevel)
                levels[skill] += newLevel - oldLevel;
            player.graphic(199, GraphicHeight.HIGH, 0);
        }

        makeDirty(skill);

        //Send total level
        player.getPacketSender().sendString(10121, "" + totalLevel());

        if (oldLevel != newLevel) {
            int levels = newLevel - oldLevel;
            if (levels == 1) {
                player.message("Congratulations, you just advanced %s %s level.", SKILL_INDEFINITES[skill], SKILL_NAMES[skill]);
            } else {
                player.message("Congratulations, you just advanced %d %s levels.", levels, SKILL_NAMES[skill]);
            }

            if(newLevel == 99) {
                player.graphic(1388, GraphicHeight.HIGH, 0);
                player.message(Color.ORANGE_RED.tag() + "Congratulations on achieving level 99 in " + SKILL_NAMES[skill] + "!");
                player.message(Color.ORANGE_RED.tag() + "You may now purchase a skillcape from Mac who can be found at home.");
                World.getWorld().sendWorldMessage("<img=2013> <col=" + Color.HOTPINK.getColorValue() + ">" + player.getUsername() + "</col> has just achieved level 99 in "+Color.BLUE.tag()+"" + SKILL_NAMES[skill] + "</col> on a "+Color.BLUE.tag()+" "+ Utils.gameModeToString(player)+"</col>!");
            }

            if(totalLevel() >= Mac.TOTAL_LEVEL_FOR_MAXED) {
                World.getWorld().sendWorldMessage("<img=2013> <col=" + Color.HOTPINK.getColorValue() + ">" + player.getUsername() + "</col> has just maxed out on a "+Color.BLUE.tag()+" "+ Utils.gameModeToString(player)+"</col>!");
            }

            recalculateCombat();

            if (player.getSkills().combatLevel() >= 126 && player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
                player.putAttrib(AttributeKey.COMBAT_MAXED, true);
            }

            //Only unlockable for trained accounts.
            if(player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
                if (totalLevel() >= 750) {
                    AchievementsManager.activate(player, Achievements.SKILLER_I, 1);
                }
                if (totalLevel() >= 1000) {
                    AchievementsManager.activate(player, Achievements.SKILLER_II, 1);
                }
                if (totalLevel() >= 1500) {
                    AchievementsManager.activate(player, Achievements.SKILLER_III, 1);
                }
                if (totalLevel() >= Mac.TOTAL_LEVEL_FOR_MAXED) {
                    AchievementsManager.activate(player, Achievements.SKILLER_IV, 1);
                }
            }

            var levelUpActive = player.<Boolean>getAttribOr(AttributeKey.LEVEL_UP_INTERFACE, false);

            if(levelUpActive) {
                //Send custom interfaces 377 doesn't have these
                if (skill == FARMING) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... options) {
                            send(DialogueType.ITEM_STATEMENT, 5340, "", "Congratulations! You've just advanced Farming level!", "You have reached level " + newLevel + "!");
                            setPhase(0);
                        }
                    });
                } else if (skill == CONSTRUCTION) {

                } else if (skill == HUNTER) {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... options) {
                            send(DialogueType.ITEM_STATEMENT, 9951, "", "Congratulations! You've just advanced Hunter level!", "You have reached level " + newLevel + "!");
                            setPhase(0);
                        }
                    });
                } else {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            player.getPacketSender().sendString(LEVEL_UP[skill][1], "<col=128>Congratulations, you just advanced a " + SKILL_NAMES[skill] + " level!");
                            player.getPacketSender().sendString(LEVEL_UP[skill][2], "Your " + SKILL_NAMES[skill] + " level is now " + newLevel + ".");
                            player.getPacketSender().sendChatboxInterface(LEVEL_UP[skill][0]);
                            setPhase(0);
                        }

                        @Override
                        protected void next() {
                            if (isPhase(0)) {
                                stop();
                            }
                        }
                    });
                }
            }
        }
        update();//Force an update
        return oldLevel != newLevel;
    }

    private static final int[][] LEVEL_UP = {
        {6247, 6248, 6249},
        {6253, 6254, 6255},
        {6206, 6207, 6208},
        {6216, 6217, 6218},
        {4443, 5453, 6114},
        {6242, 6243, 6244},
        {6211, 6212, 6213},
        {6226, 6227, 6228},
        {4272, 4273, 4274},
        {6231, 6232, 6233},
        {6258, 6259, 6260},
        {4282, 4283, 4284},
        {6263, 6264, 6265},
        {6221, 6222, 6223},
        {4416, 4417, 4438},
        {6237, 6238, 6239},
        {4277, 4278, 4279},
        {4261, 4263, 4264},
        {12122, 12123, 12124},
        {8267, 4268, 4269}, //farming
        {4267, 4268, 4269}, //rc
        {8267, 4268, 4269}, //construction
        {8267, 4268, 4269}}; //hunter

    /**
     * Checks if the player is maxed in all combat skills.
     */
    public boolean isCombatMaxed() {
        int maxCount = 7;
        int count = 0;
        for (int index = 0; index < maxCount; index++) {
            if (player.getSkills().level(index) >= 99) {
                count++;
            }
        }
        return count == maxCount;
    }

    public void update(int skill) {
        makeDirty(skill);
    }

    /**
     * @param skill
     * @param change
     */
    public void alterSkill(int skill, int change) {
        levels[skill] += change;
        if (change > 0 && levels[skill] > xpLevel(skill) + change) { // Cap at realLvl (99) + boost (20) = 118
            levels[skill] = xpLevel(skill) + change;
        }
        if (levels[skill] < 0) { // Min 0
            levels[skill] = 0;
        }
        update(skill);
    }

    public void hpEventLevel(int increaseBy) {
        levels[Skills.HITPOINTS] = increaseBy;
        update(Skills.HITPOINTS);
    }

    public void overloadPlusBoost(int skill) {
        int boost = 6;
        int boostedLevel = (int) ((player.getSkills().xpLevel(skill) * 0.16) + boost);
        levels[skill] += boostedLevel;
        if (boostedLevel > 0 && levels[skill] > xpLevel(skill) + boostedLevel) { // Cap at realLvl (99) + boost (20) = 118
            levels[skill] = xpLevel(skill) + boostedLevel;
        }
        update(skill);
    }

    public void replenishSkill(int skill, int change) {
        if (levels[skill] < xpLevel(skill)) // Current level under real level
            levels[skill] = Math.min(xpLevel(skill), level(skill) + change);//cap replenish at 99
        update(skill);
    }

    public void replenishStats() {
        if (player.dead() || player.hp() < 1)
            return;

        for (int i = 0; i < SKILL_COUNT; i++) {
            if (levels[i] < xpLevel(i)) {
                levels[i]++;
                update(i);
            } else if (levels[i] > xpLevel(i)) {
                levels[i]--;
                update(i);
            }
        }
    }

    public void replenishStatsToNorm() {
        for (int i = 0; i < SKILL_COUNT; i++) {
            if (levels[i] < xpLevel(i)) {
                levels[i] = xpLevel(i);
                update(i);
            }
        }
    }

    public void resetStats() {
        for (int i = 0; i < SKILL_COUNT; i++) {
            levels[i] = xpLevel(i);
        }
        update();
    }

    public void recalculateCombat() {
        int old = combat;
        double defence = xpLevel(Skills.DEFENCE);
        double attack = xpLevel(Skills.ATTACK);
        double strength = xpLevel(Skills.STRENGTH);
        double prayer = xpLevel(Skills.PRAYER);
        double ranged = xpLevel(Skills.RANGED);
        double magic = xpLevel(Skills.MAGIC);
        double hp = xpLevel(Skills.HITPOINTS);

        int baseMelee = (int) Math.floor(0.25 * (defence + hp + Math.floor(prayer / 2d)) + 0.325 * (attack + strength));
        int baseRanged = (int) Math.floor(0.25 * (defence + hp + Math.floor(prayer / 2d)) + 0.325 * (Math.floor(ranged / 2) + ranged));
        int baseMage = (int) Math.floor(0.25 * (defence + hp + Math.floor(prayer / 2d)) + 0.325 * (Math.floor(magic / 2) + magic));
        combat = Math.max(Math.max(baseMelee, baseMage), baseRanged);

        // If our combat changed, we need to update our looks as that contains our cb level too.
        if (combat != old && player.looks() != null) {
            player.getUpdateFlag().flag(Flag.APPEARANCE);
            player.getPacketSender().sendString(19000, "Combat level: " + combat).sendString(5858, "Combat level: " + combat);
        }
    }

    public long getTotalExperience() {
        return Arrays.stream(xps).mapToLong(e -> (long)e).sum();
    }


    public int combatLevel() {
        return combat;
    }

    public int totalLevel() {
        int total = 0;

        for (int i = 0; i < xps.length; i++) {
            total += xpLevel(i);
        }

        //Max total level is 2277 in osrs however this calculates slightly over.
        if (total > 2277) {
            total = 2277;
        }

        return total;
    }

    /**
     * Converts given XP to the equivilent skill level.
     *
     * @param xp
     * @return
     */
    public static int xpToLevel(int xp) {
        // Most-frequently used variants.
        if (xp >= 13_034_431)//13,034,431 exp is level 99
            return 99;
        if (xp < 83)//83 exp is level 2
            return 1;

        int lvl = 1;
        for (; lvl < 100; lvl++) {
            if (xp < XP_TABLE[lvl])
                break;
        }

        return Math.min(lvl, 99);
    }

    /**
     * Converts skill level to EXP
     *
     * @param level skill id
     * @return XP equivalent to given skill level
     */
    public static int levelToXp(int level) {
        return XP_TABLE[Math.min(99, Math.max(0, level - 1))];
    }

    static {
        // Calculate XP table
        for (int lv = 1, points = 0; lv < 100; lv++) {
            points += Math.floor(lv + 300 * Math.pow(2, lv / 7D));
            XP_TABLE[lv] = points / 4;
        }
    }

    public static final int ATTACK = 0;
    public static final int DEFENCE = 1;
    public static final int STRENGTH = 2;
    public static final int HITPOINTS = 3;
    public static final int RANGED = 4;
    public static final int PRAYER = 5;
    public static final int MAGIC = 6;
    public static final int COOKING = 7;
    public static final int WOODCUTTING = 8;
    public static final int FLETCHING = 9;
    public static final int FISHING = 10;
    public static final int FIREMAKING = 11;
    public static final int CRAFTING = 12;
    public static final int SMITHING = 13;
    public static final int MINING = 14;
    public static final int HERBLORE = 15;
    public static final int AGILITY = 16;
    public static final int THIEVING = 17;
    public static final int SLAYER = 18;
    public static final int FARMING = 19;
    public static final int RUNECRAFTING = 20;
    public static final int HUNTER = 21;
    public static final int CONSTRUCTION = 22;
    // 23 ... sailing meme skill or unreleased w.e

    public static final String[] SKILL_NAMES = {
        "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking", "Woodcutting", "Fletching",
        "Fishing", "Firemaking", "Crafting", "Smithing", "Mining", "Herblore", "Agility", "Thieving", "Slayer",
        "Farming", "Runecrafting", "Hunter", "Construction"
    };

    public static final String[] SKILL_INDEFINITES = {
        "an", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "a", "an", "a", "a", "a", "a", "a", "a"
    };

    public double totalXp() {
        double xp = 0.0D;
        for (int i = 0; i < SKILL_COUNT; i++) // 23 kills yo .. skill.length is 24!
            xp += xp()[i];
        return xp;
    }

    private boolean attackLevelBoosted() {
        return level(Skills.ATTACK) > xpLevel(Skills.ATTACK);
    }

    private boolean defenceLevelBoosted() {
        return level(Skills.DEFENCE) > xpLevel(Skills.DEFENCE);
    }

    private boolean strengthLevelBoosted() {
        return level(Skills.STRENGTH) > xpLevel(Skills.STRENGTH);
    }

    private boolean rangeLevelBoosted() {
        return level(Skills.RANGED) > xpLevel(Skills.RANGED);
    }

    private boolean magicLevelBoosted() {
        return level(Skills.MAGIC) > xpLevel(Skills.MAGIC);
    }

    public boolean combatStatsBoosted() {
        return attackLevelBoosted() || defenceLevelBoosted() || strengthLevelBoosted() || rangeLevelBoosted() || magicLevelBoosted();
    }

    /**
     * Checks if the button that was clicked is used for setting a skill to a
     * desired level.
     *
     * @param button The button that was clicked.
     * @return True if a skill should be set, false otherwise.
     */
    public boolean pressedSkill(int button) {
        Skill skill = Skill.fromButton(button);

        if (skill != null) {
            boolean isCombatMaxed = player.getAttribOr(AttributeKey.COMBAT_MAXED, false);
            //Players can set there combat stats to 99, except for trained accounts they have to be maxed first.
            if (!player.getPlayerRights().isAdministrator(player) && (player.getGameMode() == GameMode.TRAINED_ACCOUNT && !isCombatMaxed && skill.canSetLevel())) {
                player.message("As a trained account you have to max out your combat stats first.");
                return false;
            }

            if (GameServer.properties().debugMode && player.getPlayerRights().isDeveloper(player)) {
                player.message("Clicked skill: " + skill.toString());
            }

            if (!skill.canSetLevel() && !player.getPlayerRights().isAdministrator(player)) {
                return true;
            }
            if ((isCombatMaxed) || player.getPlayerRights().isAdministrator(player)) {
                if (CombatFactory.inCombat(player) && !player.getPlayerRights().isAdministrator(player)) {
                    player.message("You can't change your levels during combat.");
                    return true;
                }
                player.getInterfaceManager().close();
                player.setAmountScript("Enter a desired Level:", new InputScript() {

                    @Override
                    public boolean handle(Object value) {
                        int level = (int) value;
                        if (level <= 0 || level > 99) {
                            player.message("Invalid syntax. Please enter a level in the range of 1-99.");
                            return false;
                        }
                        player.getSkills().clickSkillToChangeLevel(skill.getId(), (int) level);
                        return false;
                    }
                });
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Sets a skill to the desired level.
     *
     * @param skill
     * @param level
     */
    public void clickSkillToChangeLevel(int skill, int level) {
        //Make sure they are in a safe area
        if ((!player.tile().inArea(Tile.EDGEVILE_HOME_AREA) || WildernessArea.inWild(player)) && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You can only set levels at home.");
            return;
        }

        //make sure they aren't wearing any items which arent allowed to be worn at that level.
        for (Item item : player.getEquipment().getItems()) {
            if (item == null) {
                continue;
            }

            final Map<Integer, Integer> requiredLevels = World.getWorld().equipmentInfo().requirementsFor(item.getId());

            if (requiredLevels != null) {
                final Integer requiredLevel = requiredLevels.get(skill);
                if (requiredLevel != null) {
                    player.message("<col=FF0000>You don't have the level requirements to wear: %s.", World.getWorld().definitions().get(ItemDefinition.class, item.getId()).name);
                    return;
                }
            }
        }

        if (skill == HITPOINTS) {
            if (level < 10) {
                player.message("Hitpoints must be set to at least level 10.");
                return;
            }
        }

        //Set skill level
        player.getSkills().setXp(skill, Skills.levelToXp(level));
        player.getSkills().update();
        player.getSkills().recalculateCombat();

        if (skill == PRAYER) {
            player.getPacketSender().sendConfig(708, Prayers.canUse(player, DefaultPrayerData.PRESERVE,false) ? 1 : 0);
            player.getPacketSender().sendConfig(710, Prayers.canUse(player, DefaultPrayerData.RIGOUR,false) ? 1 : 0);
            player.getPacketSender().sendConfig(712, Prayers.canUse(player, DefaultPrayerData.AUGURY,false) ? 1 : 0);
        }

        //Update weapon tab to send combat level etc.
        player.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
        Prayers.closeAllPrayers(player);
        BountyHunter.unassign(player);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    /**
     * Starts the {@link Skillable} skill.
     * @param skill
     */
    public void startSkillable(Skillable skill) {
        //Stop previous skills..
        stopSkillable();

        //Close interfaces..
        player.getInterfaceManager().close();

        //Check if we have the requirements to start this skill..
        if (!skill.hasRequirements(player)) {
            return;
        }

        //Start the skill..
        player.setSkillable(Optional.of(skill));
        skill.start(player);
    }

    /**
     * Stops the player's current skill, if they have one active.
     */
    public void stopSkillable() {
        //Stop any previous skill..
        player.getSkillable().ifPresent(e-> e.cancel(player));
        player.setSkillable(Optional.empty());
    }

    /**
     * Gets the max level for said skill.
     *
     * @param skill The skill to get max level for.
     * @return The skill's maximum level.
     */
    public int getMaxLevel(int skill) {
        return xpLevel(skill);
    }

    /**
     * Gets the max level for said skill.
     *
     * @param skill The skill to get max level for.
     * @return The skill's maximum level.
     */
    public int getMaxLevel(Skill skill) {
        return xpLevel(skill.getId());
    }

    public boolean check(int skill, int lvlReq) {
        return level(skill) >= lvlReq;
    }

    /**
     * Checks the fixed (not boosted) level.
     */
    public boolean checkFixed(int skill, int levelReq, String action) {
        if (xpLevel(skill) < levelReq) {
            player.message("You need " + Skills.SKILL_NAMES[skill] + " level of " + levelReq + " or higher to " + action + ".");
            return false;
        }
        return true;
    }

    public boolean check(int skill, int levelReq, String action) {
        if(!check(skill, levelReq)) {
            player.message("You need " + Skills.SKILL_NAMES[skill] + " level of " + levelReq + " or higher to " + action + ".");
            return false;
        }
        return true;
    }

    public boolean check(int skill, int lvlReq, int itemId, String action) {
        if(!check(skill, lvlReq)) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.ITEM_STATEMENT, new Item(itemId), "", "You need " + Skills.SKILL_NAMES[skill] + " level of " + lvlReq + " or higher to " + action + ".");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if(isPhase(0)) {
                        stop();
                    }
                }
            });
            return false;
        }
        return true;
    }

    public boolean check(int skill, int lvlReq, int itemId1, int itemId2, String action) {
        if(!check(skill, lvlReq)) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.DOUBLE_ITEM_STATEMENT,new Item(itemId1), new Item(itemId2), "You need " + Skills.SKILL_NAMES[skill] + " level of " + lvlReq + " or higher to " + action + ".");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if(isPhase(0)) {
                        stop();
                    }
                }
            });
            return false;
        }
        return true;
    }
}
