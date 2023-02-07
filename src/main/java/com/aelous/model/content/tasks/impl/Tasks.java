package com.aelous.model.content.tasks.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Patrick van Elderen | April, 08, 2021, 21:53
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum Tasks {

    //#Default
    NONE(true,false,false,0,"", ""),
    //#PVP tasks
    WEAR_TORAGS_TASK(true,false,false,15,"Kill your opponent while wearing<br>Torag's armour.", "- Must have out a Zulrah pet...<br>while completing this task."),
    WEAR_FULL_DH_TASK(true,false,false,25,"Kill your opponent while wearing<br>Dharok's armour.", "- Must kill the player while holding<br>the greataxe."),
    KILL_WITH_20K_BM_RISK(true,false,false,10,"Kill your opponent while risking<br>20k+ BM.", "- You can't use the 'Protect item' prayer."),
    KILL_WITH_DRAGON_SCIMITAR_OR(true,false,false,15,"Kill your opponent while Dragon<br>Scimitar(or) is your main weapon.", ""),
    KILL_WITH_INQUISITORS_MACE(true,false,false,20,"Kill your opponent with an...<br>Inquisitor's mace.", ""),
    KILL_WITHOUT_HEAD_BODY_AND_LEGS(true,false,false,10,"Kill your opponent without...<br>wearing any head, body and...<br>legs protection.", ""),
    KILL_WITH_AN_IMBUED_SLAYER_HELM_EQUIPED(true,false,false,20,"Kill your opponent while...<br>equipping any imbued slayer helm.", ""),
    KILL_WITHOUT_RING_AMULET_AND_GLOVES(true,false,false,10,"Kill your opponent without...<br>equipping any rings, amulets...<br>and gloves.", ""),
    KILL_WEARING_FULL_OBSIDIAN(true,false,false,30,"Kill your opponent while wearing<br>full Obsidian armour.", ""),
    KILL_WITHOUT_BOOSTED_STATS(true,false,false,8,"Kill your opponent without...<br>boosted stats.", ""),

    //#Skilling tasks
    BONES_ON_ALTAR(false,true,false,125,"Pledge 100 bones on any altar.","- None"),
    CRAFT_DEATH_RUNES(false,true,false,1000,"Craft 1000 death runes.","- Level 65 Runecrafting"),
    WILDERNESS_COURSE(false,true,false,35,"Complete 35 laps at the Wilderness agility course.","- Level 50 Agility"),
    MAKE_SUPER_COMBAT_POTIONS(false,true,false,100,"Make 100 super combat potions.","- Level 90 Herblore"),
    STEAL_FROM_SCIMITAR_STALL(false,true,false,150,"Steal from the scimitar stall 150 times.","- Level 65 Thieving"),
    CRAFT_DRAGONSTONES(false,true,false,200,"Craft 200 uncut dragonstone's.","- Level 55 Crafting"),
    MAGIC_SHORTBOW(false,true,false,200,"Make 200 magic bows.","- Level 80 Fletching"),
    COMPLETE_SLAYER_TASKS(false,true,false,10,"Complete 10 slayer tasks.","- None"),
    BLACK_CHINCHOMPAS(false,true,false,100,"Catch 100 black chinchompas.","- Level 73 Hunter"),
    MINE_RUNITE_ORE(false,true,false,100,"Mine 100 Runite ores.","- Level 85 Mining"),
    MAKE_ADAMANT_PLATEBODY(false,true,false,100,"Create 100 Adamant platebodies.","- Level 88 Smithing"),
    CATCH_SHARKS(false,true,false,100,"Catch 100 sharks.","- Level 76 Fishing"),
    COOK_SHARKS(false,true,false,200,"Cook 200 sharks.","- Level 80 Cooking"),
    BURN_MAGIC_LOGS(false,true,false,100,"Burn 100 magic logs.","- Level 75 Firemaking"),
    CUT_MAGIC_TREES(false,true,false,100,"Cut down 100 Magic trees.","- Level 75 Woodcutting"),
    CUT_YEW_TREES(false,true,false,100,"Cut down 100 Yew trees.","- Level 60 Woodcutting"),
    PLANT_TORSTOL_SEED(false,true,false,25,"Plant 25 Torstol seeds.","- Level 85 Farming"),

    //#PVM tasks
    REVENANTS(false,false,true, 250, "Kill 250 Revenants.","- None"),
    DRAGONS(false,false,true, 200,"Kill 200 Dragons.", "- None"),
    CALLISTO(false,false,true,50,"Kill Callisto 50 times.","- None"),
    CERBERUS(false,false,true,40,"Kill Cerberus 40  times.","- None"),
    CHAOS_FANATIC(false,false,true,75,"Kill Chaos Fanatic 75 times.","- None"),
    CORPOREAL_BEAST(false,false,true,35,"Kill the Corporal Beast 35 times.","- None"),
    CRAZY_ARCHAEOLOGIST(false,false,true,75,"Kill the Crazy Archaeologist 75 times.","- None"),
    DEMONIC_GORILLA(false,false,true,50,"Kill 50 Demonic Gorillas.","- None"),
    KING_BLACK_DRAGON(false,false,true,75,"Kill the King Black Dragon 75 times.","- None"),
    KRAKEN(false,false,true,100,"Kill 100 Kraken.","- None"),
    LIZARDMAN_SHAMAN(false,false,true,100,"Kill 100 Lizardman Shamans.","- None"),
    THERMONUCLEAR_SMOKE_DEVIL(false,false,true,100,"Kill 100 Thermonuclear smoke devils.","- None"),
    VENENATIS(false,false,true,40,"Kill Venenatis 40 times.","- None"),
    VETION(false,false,true,40,"Kill Vet'ion 40 times.","- None"),
    SCORPIA(false,false,true,40,"Kill Scorpia 40 times.","- None"),
    CHAOS_ELEMENTAL(false,false,true,40,"Kill 40 Chaos Elementals.","- None"),
    ZULRAH(false,false,true,60,"Kill Zulrah 60 times.","- None"),
    VORKATH(false,false,true,25,"Kill Vorkath 25 times.","- None"),
    WORLD_BOSS(false,false,true,10,"Kill any world boss 10 times.","- None"),
    KALPHITE_QUEEN(false,false,true,50,"Kill the Kalphite Queen 50 times.","- None"),
    DAGANNOTH_KINGS(false,false,true,100,"Kill 100 Dagannoth Kings.","- None"),
    GIANT_MOLE(false,false,true,150,"Kill 150 Giant Moles.","- None"),
    ALCHEMICAL_HYDRA(false,false,true,35,"Kill 35 Alchemical Hydras.","- None"),
    ;

    private final boolean pvpTask;
    private final boolean skillingTask;
    private final boolean pvmTask;
    private final int taskAmount;
    private final String task;
    private final String[] taskRequirements;

    public boolean isPvpTask() {
        return pvpTask;
    }

    public boolean isSkillingTask() {
        return skillingTask;
    }

    public boolean isPvmTask() {
        return pvmTask;
    }

    public int getTaskAmount() {
        return taskAmount;
    }

    public String task() {
        return task;
    }

    public String[] getTaskRequirements() {
        return taskRequirements;
    }

    Tasks(boolean pvpTask, boolean skillingTask, boolean pvmTask, int taskAmount, String task, String... requirements) {
        this.pvpTask = pvpTask;
        this.skillingTask = skillingTask;
        this.pvmTask = pvmTask;
        this.taskAmount = taskAmount;
        this.task = task;
        this.taskRequirements = requirements;
    }

    /**
     * Picks a random PVP task from the Tasks enum.
     */
    public static Tasks randomPVPTask() {
        List<Tasks> tasks = Arrays.stream(Tasks.values()).filter(task -> task != NONE && !task.skillingTask && !task.pvmTask).collect(Collectors.toList());
        Collections.shuffle(tasks);
        return tasks.get(0);
    }

    /**
     * Picks a random Skilling task from the Tasks enum.
     */
    public static Tasks randomSkillingTask() {
        List<Tasks> tasks = Arrays.stream(Tasks.values()).filter(task -> task != NONE && !task.pvpTask && !task.pvmTask).collect(Collectors.toList());
        Collections.shuffle(tasks);
        return tasks.get(0);
    }

    /**
     * Picks a random PVM task from the Tasks enum.
     */
    public static Tasks randomPVMTask() {
        List<Tasks> tasks = Arrays.stream(Tasks.values()).filter(task -> task != NONE && !task.skillingTask && !task.pvpTask).collect(Collectors.toList());
        Collections.shuffle(tasks);
        return tasks.get(0);
    }
}
