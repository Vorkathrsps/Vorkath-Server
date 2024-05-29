package com.cryptic.model.entity.player;

import com.cryptic.GameServer;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.areas.edgevile.Mac;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.content.skill.Skillable;
import com.cryptic.model.content.skill.perks.SkillingSets;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.prayer.default_prayer.DefaultPrayerData;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.rights.MemberRights;
import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;
import lombok.Getter;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Bart Pelle on 8/23/2014.
 */
public class Skills {

    public static final int SKILL_COUNT = 23;
    private static final int[] XP_TABLE = new int[100];
    public static boolean USE_EXPERIMENTAL_PERFORMANCE = false;
    @Getter
    public double[] xps = new double[SKILL_COUNT];
    @Getter
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

    public void restoreLevels(double[] xp, int[] levels) {
        this.xps = xp;
        this.levels = levels;
    }


    private double expModifiers(int skill) {
       /* switch(skill) {
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
        }*/
        return 1.0;
    }

    public void update() {
        update(false);
    }

    public void update(boolean ignore) {
        if (!ignore) {
            for (int skill = 0; skill < SKILL_COUNT; skill++) {
                //Send the skill
                player.getPacketSender().updateSkill(skill, levels[skill], (int) xps[skill]);
            }

            updatePrayerText();
            recalculateCombat();

            //Update prayer orb
            player.getPacketSender().sendString(4012, "" + levels[PRAYER] + "");
            player.getPacketSender().sendString(4013, "" + xpToLevel((int) xps[PRAYER]) + "");

            //Update hp orb
            player.getPacketSender().sendString(4016, "" + levels[HITPOINTS] + "");
            player.getPacketSender().sendString(4017, "" + xpToLevel((int) xps[HITPOINTS]) + "");

            //Send total level
            player.getPacketSender().sendString(10121, "" + totalLevel());
        }
    }

    public void updatePrayerText() {
        int currentLevel = levels[PRAYER];
        int maxLevel = xpToLevel((int) xps[PRAYER]);
        player.getPacketSender().sendString(687, Color.ORANGE.tag() + currentLevel + "/" + maxLevel);
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
        this.makeDirty(skill, false);
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
        if (player.getGameMode() == GameMode.TRAINED_ACCOUNT) {
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
        player.getPacketSender().sendString(4012, "" + levels[PRAYER] + "");
        player.getPacketSender().sendString(4013, "" + xpToLevel((int) xps[PRAYER]) + "");

        //Update hp orb
        player.getPacketSender().sendString(4016, "" + levels[HITPOINTS] + "");
        player.getPacketSender().sendString(4017, "" + xpToLevel((int) xps[HITPOINTS]) + "");

        if (!ignore) {
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
        this.setXp(skill, amt, false);
    }

    public void setXp(int skill, double amt, boolean ignore) {
        xps[skill] = Math.min(200000000, amt);
        int newLevel = xpToLevel((int) xps[skill]);
        levels[skill] = newLevel;

        if (!ignore) {
            recalculateCombat();
            makeDirty(skill);
        }
    }

    public void setLevel(int skill, int lvtemp) {
        this.setLevel(skill, lvtemp, false);
    }

    public void setLevel(int skill, int lvtemp, boolean ignore) {
        levels[skill] = lvtemp;
        if (!ignore) {
            makeDirty(skill);
        }
    }

    public boolean addXp(int skill, double amt) {
        boolean isCombatExperience = skill == ATTACK || skill == STRENGTH || skill == DEFENCE || skill == HITPOINTS || skill == MAGIC || skill == RANGED;
        for (GameMode mode : GameMode.values()) {
            if (mode.equals(player.getGameMode())) {
                double setBonus = SkillingSets.check(player, skill);
                amt *= setBonus;
                if (player.getTimers().has(TimerKey.DOUBLE_EXPERIENCE)) amt *= 2.0D;
                if (isCombatExperience) return addExperience(skill, amt, mode.combatXp, true);
                return addExperience(skill, amt, mode.multiplier, true);
            }
        }
        return false;
    }

    public boolean addExperience(int skill, double amount, double multiplier, boolean counter) {
        WeakReference<? extends Entity> weakEntityRef = player.getAttribOr(AttributeKey.TARGET, new WeakReference<>(null));
        Entity target = weakEntityRef.get();
        boolean isCombatExperience = skill == ATTACK || skill == STRENGTH || skill == DEFENCE || skill == HITPOINTS || skill == MAGIC || skill == RANGED;
        boolean isExperienceLocked = player.getAttribOr(AttributeKey.XP_LOCKED, false);
        boolean inWilderness = WildernessArea.inWilderness(player.tile());
        boolean isNpc = target instanceof NPC;
        boolean isMember = player.getMemberRights().isRegularMemberOrGreater(player);

        if (isCombatExperience && isNpc) {
            NPC npc = (NPC) target;
            if (npc.isCombatDummy()) {
                player.getPacketSender().sendFakeXPDrop(skill, amount * multiplier);
                return false;
            }
            if (!npc.isDamageOkLocked() && npc.hidden() || npc.locked()) {
                return false;
            }
        }

        if (this.getTotalExperience() >= 2_600_000_000L) {
            player.getPacketSender().sendFakeXPDrop(skill, amount * multiplier);
            return false;
        }

        if (isExperienceLocked) {
            player.getPacketSender().sendFakeXPDrop(skill, amount * multiplier);
            return false;
        }

        if (isMember && target instanceof NPC) {
            double memberMultiplier = getDonatorRankMultiplier(player.getMemberRights());
            amount *= memberMultiplier;
        }

        if (multiplier >= 1.0) {
            amount *= multiplier;
        }

        if (inWilderness) {
            amount *= 1.05;
        }

        if (skill == AGILITY) {
            amount = isAgilitySetBonus(amount);
        }

        rollSkillingSet(skill);
        rollAntiqueLamp();

        int oldLevel = xpToLevel((int) xps[skill]);
        xps[skill] = Math.min(200000000, xps[skill] + amount);
        int newLevel = xpToLevel((int) xps[skill]);

        if (newLevel > oldLevel) {
            levels[skill] += newLevel - oldLevel;
            player.graphic(199, GraphicHeight.HIGH, 0);
        }

        if (oldLevel != newLevel) {
            int levelDifference = newLevel - oldLevel;
            if (levelDifference == 1) {
                player.message("Congratulations, you just advanced %s %s level.", SKILL_INDEFINITES[skill], SKILL_NAMES[skill]);
            } else {
                player.message("Congratulations, you just advanced %d %s levels.", levelDifference, SKILL_NAMES[skill]);
            }
            if (newLevel == 99) {
                player.graphic(1388, GraphicHeight.HIGH, 0);
                player.message(Color.ORANGE_RED.tag() + "Congratulations on achieving level 99 in " + SKILL_NAMES[skill] + "!");
                player.message(Color.ORANGE_RED.tag() + "You may now purchase a skillcape from Mac who can be found at home.");
                World.getWorld().sendWorldMessage("<lsprite=2017> <col=" + Color.HOTPINK.getColorValue() + ">" + player.getUsername() + "</col> has just achieved level 99 in " + Color.BLUE.tag() + "" + SKILL_NAMES[skill] + "</col> on a " + Color.BLUE.tag() + " " + Utils.gameModeToString(player) + "</col>!");
            }
            if (totalLevel() >= Mac.TOTAL_LEVEL_FOR_MAXED) {
                World.getWorld().sendWorldMessage("<lsprite=2017> <col=" + Color.HOTPINK.getColorValue() + ">" + player.getUsername() + "</col> has just maxed out on a " + Color.BLUE.tag() + " " + Utils.gameModeToString(player) + "</col>!");
            }

            sendLeveledUpDialogue(skill, newLevel);
            recalculateCombat();
        }

        player.getPacketSender().sendXPDrop(skill, amount, counter);
        player.getPacketSender().sendString(10121, "" + totalLevel());
        makeDirty(skill);
        update();

        return oldLevel != newLevel;
    }

    private void sendLeveledUpDialogue(int skill, int newLevel) {
        if (skill == FARMING) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    sendItemStatement(new Item(5340), "", "Congratulations! You've just advanced Farming level!", "You have reached level " + newLevel + "!");
                    setPhase(0);
                }
            });
        } else if (skill == HUNTER) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... options) {
                    sendItemStatement(new Item(9951), "", "Congratulations! You've just advanced Hunter level!", "You have reached level " + newLevel + "!");
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

    private void rollSkillingSet(int skill) {
        var definition = ItemDefinition.cached;
        for (SkillingSets set : SkillingSets.getVALUES()) {
            if (set.getSkillType().getId() == skill) {
                if (Utils.rollDie(1000, 1)) {
                    final int itemID = Utils.randomElement(set.getSet());
                    final Item item = new Item(itemID);
                    final String name = definition.get(itemID).name;
                    player.getInventory().addOrBank(item);
                    player.message(Color.PURPLE.wrap("<shad=0>You have recieved a ") + Color.PURPLE.wrap(name + "!</shad>"));
                    break;
                }
            }
        }
    }

    private void rollAntiqueLamp() {
        int chance = 500;
        switch (player.getMemberRights()) {
            case RUBY_MEMBER, SAPPHIRE_MEMBER -> chance = 450;
            case DIAMOND_MEMBER, EMERALD_MEMBER -> chance = 400;
            case DRAGONSTONE_MEMBER, ONYX_MEMBER -> chance = 350;
            case ZENYTE_MEMBER -> chance = 300;
        }
        if (Utils.rollDie(chance, 1)) {
            player.getInventory().addOrDrop(new Item(ItemIdentifiers.ANTIQUE_LAMP_28800));
        }
    }

    private double isAgilitySetBonus(double amount) {
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().getId() == AGILITY) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    amount *= set.experienceBoost;
                    System.out.println("yeuh");
                    break;
                }
            }
        }
        return amount;
    }

    private double getDonatorRankMultiplier(MemberRights memberRights) {
        return switch (memberRights) {
            case RUBY_MEMBER -> 1.05;
            case SAPPHIRE_MEMBER -> 1.08;
            case EMERALD_MEMBER -> 1.10;
            case DIAMOND_MEMBER -> 1.12;
            case DRAGONSTONE_MEMBER -> 1.15;
            case ONYX_MEMBER -> 1.18;
            case ZENYTE_MEMBER -> 1.20;
            default -> 1.0;
        };
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

    int getLevelFor(Skill skill) {
        return player.getSkills().levels[skill.getId()];
    }

    public void alterSkillsArray(Player player, int changeFrom, int changeTo) {
        var experienceChange = Skills.levelToXp(changeTo);
        player.getSkills().setXp(changeFrom, experienceChange);
        player.getSkills().update();
        player.getSkills().recalculateCombat();
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

        if (!player.hasAttrib(AttributeKey.EXAGGERATION_BOOST)) {
            for (int i = 0; i < SKILL_COUNT; i++) {
                if (i == 5) continue;
                if (levels[i] < xpLevel(i)) {
                    levels[i]++;
                    update(i);
                } else if (levels[i] > xpLevel(i)) {
                    levels[i]--;
                    update(i);
                }
            }
        } else {
            for (int i = 0; i < SKILL_COUNT; i++) {
                if (i >= 6) break;
                if (levels[i] < xpLevel(i)) {
                    levels[i]++;
                    update(i);
                } else if (levels[i] > xpLevel(i)) {
                    levels[i]--;
                    update(i);
                }
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
        return Arrays.stream(xps).mapToLong(e -> (long) e).sum();
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

            if (GameServer.properties().debugMode && player.getPlayerRights().isCommunityManager(player)) {
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
        if ((!player.tile().inArea(Tile.EDGEVILE_HOME_AREA) || WildernessArea.isInWilderness(player)) && !player.getPlayerRights().isAdministrator(player)) {
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
            player.getPacketSender().sendConfig(708, Prayers.canUse(player, DefaultPrayerData.PRESERVE, false) ? 1 : 0);
            player.getPacketSender().sendConfig(710, Prayers.canUse(player, DefaultPrayerData.RIGOUR, false) ? 1 : 0);
            player.getPacketSender().sendConfig(712, Prayers.canUse(player, DefaultPrayerData.AUGURY, false) ? 1 : 0);
        }

        //Update weapon tab to send combat level etc.
        player.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
        Prayers.closeAllPrayers(player);
        BountyHunter.unassign(player);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    /**
     * Starts the {@link Skillable} skill.
     *
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
        player.getSkillable().ifPresent(e -> e.cancel(player));
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
        if (!check(skill, levelReq)) {
            player.message("You need " + Skills.SKILL_NAMES[skill] + " level of " + levelReq + " or higher to " + action + ".");
            return false;
        }
        return true;
    }

    public boolean check(int skill, int lvlReq, int itemId, String action) {
        if (!check(skill, lvlReq)) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    sendItemStatement(new Item(itemId), "", "You need " + Skills.SKILL_NAMES[skill] + " level of " + lvlReq + " or higher to " + action + ".");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
            return false;
        }
        return true;
    }

    public boolean check(int skill, int lvlReq, int itemId1, int itemId2, String action) {
        if (!check(skill, lvlReq)) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    sendItemStatement(new Item(itemId1), new Item(itemId2), "You need " + Skills.SKILL_NAMES[skill] + " level of " + lvlReq + " or higher to " + action + ".");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        stop();
                    }
                }
            });
            return false;
        }
        return true;
    }
}
