package com.cryptic.model.content.skill.impl.woodcutting;

import com.cryptic.cache.definitions.identifiers.ObjectIdentifiers;
import com.cryptic.model.action.Action;
import com.cryptic.model.action.policy.WalkablePolicy;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.areas.zeah.woodcutting_guild.WoodcuttingGuild;
import com.cryptic.model.content.skill.impl.firemaking.LogLighting;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Optional;
import java.util.Random;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * Created by Bart on 8/28/2015.
 *
 * @author PVE
 * @Since augustus 29, 2020
 */
public class Woodcutting extends PacketInteraction {

    public enum Tree {
        REGULAR(ItemIdentifiers.LOGS, "logs", 1, 55, 5.0, 75, true, 15000),
        ACHEY(ACHEY_TREE_LOGS, "achey logs", 1, 55, 7.0, 75, true, 14000),
        OAK(OAK_LOGS, "oak logs", 15, 95, 7.0, 15, false, 14000),
        WILLOW(WILLOW_LOGS, "willow logs", 30, 140, 9.0, 10, false, 13000),
        TEAK(TEAK_LOGS, "teak logs", 35, 140, 10.0, 10, false, 12000),
        JUNIPER(JUNIPER_LOGS, "juniper logs", 42, 150, 20.0, 30, false, 12000),
        MAPLE(MAPLE_LOGS, "maple logs", 45, 180, 20.0, 60, false, 11000),
        MAHOGANY(MAHOGANY_LOGS, "mahogany logs", 50, 200, 25.0, 80, false, 11000),
        YEW(YEW_LOGS, "yew logs", 60, 225, 35.0, 100, false, 8000),
        MAGIC(MAGIC_LOGS, "magic logs", 75, 375, 45.0, 100, false, 5000),
        REDWOOD(REDWOOD_LOGS, "redwood logs", 90, 460, 60.0, 200, false, 4500),
        ENTTRUNK(-1, "ent trunk", -1, 250, 0.0, 0, false, 0); // Used for algo only

        private final int logs;
        private final String treeName;
        private final int level;
        private final int difficulty;
        private final double xp;
        private final int respawnTime;
        private final boolean single;
        private final int petOdds;

        Tree(int logs, String treeName, int level, int difficulty, double xp, int respawnTime, boolean single, int petOdds) {
            this.logs = logs;
            this.treeName = treeName;
            this.level = level;
            this.difficulty = difficulty;
            this.xp = xp;
            this.respawnTime = respawnTime;
            this.single = single;
            this.petOdds = petOdds;
        }
    }

    public enum Hatchet {
        BRONZE(ItemIdentifiers.BRONZE_AXE, 13, 879, 1),
        IRON(IRON_AXE, 15, 877, 1),
        STEEL(STEEL_AXE, 18, 875, 6),
        BLACK(BLACK_AXE, 21, 873, 11),
        MITHRIL(MITHRIL_AXE, 26, 871, 21),
        ADAMANT(ADAMANT_AXE, 30, 869, 31),
        RUNE(RUNE_AXE, 35, 867, 41),
        DRAGON(DRAGON_AXE, 42, 2846, 61),
        THIRD_AGE(_3RD_AGE_AXE, 42, 7264, 61),
        INFERNAL(INFERNAL_AXE, 45, 2117, 61);

        public final int id;
        public final int points;
        public final int anim;
        public final int level;

        Hatchet(int id, int points, int anim, int level) {
            this.id = id;
            this.points = points;
            this.anim = anim;
            this.level = level;
        }

        public static final ImmutableSet<Hatchet> VALUES = ImmutableSortedSet.copyOf(values()).descendingSet();
    }

    static double experienceMultiplier = 15;

    public static int chance(int level, Tree type, Hatchet axe) {
        double points = ((level - type.level) + 1 + axe.points);
        double denom = type.difficulty;
        return (int) (Math.min(0.95, points / denom) * 100);
    }

    public static Optional<Hatchet> findAxe(Player player) {
        if (player.getEquipment().hasWeapon()) {
            Optional<Hatchet> result = Hatchet.VALUES.stream().filter(it -> player.getEquipment().contains(it.id) && player.getSkills().levels()[Skills.WOODCUTTING] >= it.level).findFirst();

            if (result.isPresent()) {
                return result;
            }
        }
        return Hatchet.VALUES.stream().filter(def -> player.inventory().contains(def.id) && player.getSkills().levels()[Skills.WOODCUTTING] >= def.level).findAny();
    }

    public static void cut(Player player, Tree tree, int trunkObjectId) {
        Hatchet axe = findAxe(player).orElse(null);

        //Does our player have an axe?
        if (axe == null) {
            player.message("You do not have an axe which you have the Woodcutting level to use.");
            return;
        }

        //Does our player have the required woodcutting level?
        if (player.getSkills().levels()[Skills.WOODCUTTING] < tree.level) {
            player.message("You need a Woodcutting level of " + tree.level + " to chop down this tree.");
            return;
        }

        //Does out player have enough inventory space?
        if (player.inventory().isFull()) {
            player.message("Your inventory is too full to hold any more logs.");
            return;
        }

        player.message("You swing your axe at the tree.");

        if (player.getAnimation() != null) {
            player.animate(65535);
            player.stopActions(false);
        }
        player.animate(axe.anim);
        player.action.execute(cut(player, axe, tree, trunkObjectId), true);
    }

    private static boolean collapseTree(Player player, Tree tree, int trunkObjectId) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
        GameObject old = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
        GameObject spawned = new GameObject(trunkObjectId, obj.tile(), obj.getType(), obj.getRotation());
        ObjectManager.replace(old, spawned, tree.respawnTime);
        player.getSkills().addExperience(Skills.WOODCUTTING, tree.xp, experienceMultiplier, true);
        return true;
    }

    static Random random = new Random();
    static int[] birdNest = new int[]{5070, 5071, 5072, 5073, 5074, 5075};

    private static Action<Player> cut(Player player, Hatchet axe, Tree tree, int trunkObjectId) {
        return new Action<>(player, 2, true) {

            @Override
            public void execute() {

                final int level = player.getSkills().levels()[Skills.WOODCUTTING];

                player.animate(axe.anim);

                if (player.getInventory().isFull()) {
                    player.animate(65535);
                    player.looks().resetRender();
                    player.message("Your inventory is too full to hold any more logs.");
                    stop();
                }

                if (Utils.percentageChance((int) 12.5D)) {
                    player.inventory().add(new Item(tree.logs));
                    collapseTree(player, tree, trunkObjectId);
                    stop();
                }

                int modifiedLevel = level;
                if (player.tile().inArea(WoodcuttingGuild.AREA_EAST) || player.tile().inArea(WoodcuttingGuild.AREA_WEST)) {
                    modifiedLevel += 7;
                }


                //var chance = ((modifiedLevel + axe.points) / (tree.level * 2));

                var chance = chance(modifiedLevel, tree, axe);

                double rand = random.nextDouble(1.0, 200.0);

                if (chance > rand) {
                    if (axe == Hatchet.INFERNAL && tree.logs > 0) {
                        LogLighting.LightableLog log = LogLighting.LightableLog.logForId(tree.logs);

                        if (log != null) {
                            player.graphic(580, GraphicHeight.MIDDLE, 0);
                            player.getSkills().addExperience(Skills.FIREMAKING, log.xp / 2, experienceMultiplier, true);
                        }
                    }
                } else {
                    player.getSkills().addExperience(Skills.WOODCUTTING, tree.xp, experienceMultiplier, true);
                    player.inventory().add(new Item(tree.logs));
                }

                //Finding a casket Money, money, money..
                if (Utils.rollDie(100, 1)) {
                    player.inventory().addOrDrop(new Item(Utils.randomElement(birdNest), 1));
                    player.message("You find a bird's nest whilst cutting down the tree.");
                }

                switch (tree) {
                    case REGULAR -> AchievementsManager.activate(player, Achievements.WOODCUTTING_I, 1);
                    case WILLOW -> AchievementsManager.activate(player, Achievements.WOODCUTTING_II, 1);
                    case YEW -> {
                        AchievementsManager.activate(player, Achievements.WOODCUTTING_III, 1);
                        player.getTaskMasterManager().increase(Tasks.CUT_YEW_TREES);
                    }
                    case MAGIC -> {
                        AchievementsManager.activate(player, Achievements.WOODCUTTING_IV, 1);
                        player.getTaskMasterManager().increase(Tasks.CUT_MAGIC_TREES);
                    }
                }
            }

            @Override
            public String getName() {
                return "Woodcutting";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }

            @Override
            public void onStop() {
                super.onStop();
                player.animate(-1);
            }
        }

            ;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == TREE_1278) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1342);
                return true;
            }
            if (obj.getId() == ObjectIdentifiers.TREE) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1342);
                return true;
            }
            if (obj.getId() == EVERGREEN_2091) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1342);
                return true;
            }
            if (obj.getId() == DEAD_TREE_1286) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1351);
                return true;
            }
            if (obj.getId() == DEAD_TREE_1283) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1347);
                return true;
            }
            if (obj.getId() == DEAD_TREE_1383) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1358);
                return true;
            }
            if (obj.getId() == DEAD_TREE_1289) {
                Woodcutting.cut(player, Woodcutting.Tree.REGULAR, TREE_STUMP_1353);
                return true;
            }
            if (obj.getId() == ACHEY_TREE) {
                Woodcutting.cut(player, Woodcutting.Tree.ACHEY, ACHEY_TREE_STUMP);
                return true;
            }
            if (obj.getId() == OAK_10820) {
                Woodcutting.cut(player, Woodcutting.Tree.OAK, TREE_STUMP_1356);
                return true;
            }
            if (obj.getId() == WILLOW) {
                Woodcutting.cut(player, Woodcutting.Tree.WILLOW, TREE_STUMP_9711);
                return true;
            }
            if (obj.getId() == WILLOW_10829) {
                Woodcutting.cut(player, Woodcutting.Tree.WILLOW, TREE_STUMP_9711);
                return true;
            }
            if (obj.getId() == WILLOW_10831) {
                Woodcutting.cut(player, Woodcutting.Tree.WILLOW, TREE_STUMP_9711);
                return true;
            }
            if (obj.getId() == WILLOW_10833) {
                Woodcutting.cut(player, Woodcutting.Tree.WILLOW, TREE_STUMP_9711);
                return true;
            }
            if (obj.getId() == TEAK) {
                Woodcutting.cut(player, Woodcutting.Tree.TEAK, TREE_STUMP_9037);
                return true;
            }
            if (obj.getId() == MATURE_JUNIPER_TREE) {
                Woodcutting.cut(player, Woodcutting.Tree.JUNIPER, STUMP_27500);
                return true;
            }
            if (obj.getId() == MAPLE_TREE_10832) {
                Woodcutting.cut(player, Woodcutting.Tree.MAPLE, TREE_STUMP_9712);
                return true;
            }
            if (obj.getId() == YEW) {
                Woodcutting.cut(player, Woodcutting.Tree.YEW, TREE_STUMP_9714);
                return true;
            }
            if (obj.getId() == MAGIC_TREE_10834) {
                Woodcutting.cut(player, Woodcutting.Tree.MAGIC, TREE_STUMP_9713);
                return true;
            }
            if (obj.getId() == REDWOOD) {
                player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> Woodcutting.cut(player, Tree.REDWOOD, REDWOOD_29669));
                return true;
            }
            if (obj.getId() == REDWOOD_29670) {
                player.waitUntil(1, () -> !player.getMovementQueue().isMoving(), () -> Woodcutting.cut(player, Tree.REDWOOD, REDWOOD_29671));
                return true;
            }
            if (obj.getId() == MAHOGANY) {
                Woodcutting.cut(player, Woodcutting.Tree.MAHOGANY, TREE_STUMP_9035);
                return true;
            }
        }
        return false;
    }

}
