package com.aelous.model.content.skill.impl.mining;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.aelous.model.action.impl.UnwalkableAction;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * Created by Bart on 8/28/2015.
 * <p>
 * Handles the mining skill, as well as a few utility functions to add mining-like features elsewhere in the game.
 */
public class Mining extends PacketInteraction {

    public enum Rock {
        CLAY(434, "clay", 1, 15, 5.0, 3, 4160),
        COPPER(436, "copper", 1, 35, 17.5, 5, 4160),
        TIN(438, "tin", 1, 35, 17.5, 5, 4160),
        IRON(440, "iron", 15, 75, 35.0, 10, 4160),
        SILVER(442, "silver", 20, 85, 40.0, 20, 4160),
        COAL(453, "coal", 30, 110, 50.0, 30, 2000),
        GOLD(444, "gold", 40, 180, 65.0, 40, 2000),
        MITHRIL(447, "mithril", 55, 210, 80.0, 60, 1500),
        LOVAKITE(13356, "lovakite", 65, 210, 10.0, 40, 1300),
        ADAMANT(449, "adamant", 70, 310, 95.0, 300, 1300),
        RUNE(451, "rune", 85, 380, 125.0, 1500, 1000),
        JAIL_BLURITE(668, "blurite", 1, 320, 0.0, 3, 1000000);

        public final int ore;
        public final String rockName;
        public final int level;
        public final int difficulty;
        public final double xp;
        public final int respawnTime;
        public final int petOdds;

        Rock(int ore, String rockName, int level, int difficulty, double xp, int respawnTime, int petOdds) {
            this.ore = ore;
            this.rockName = rockName;
            this.level = level;
            this.difficulty = difficulty;
            this.xp = xp;
            this.respawnTime = respawnTime;
            this.petOdds = petOdds;
        }
    }

    public enum Pickaxe {
        BRONZE(1265, 5, 625, 1),
        IRON(1267, 9, 626, 1),
        STEEL(1269, 14, 627, 6),
        BLACK(12297, 21, 3866, 11),
        MITHRIL(1273, 26, 629, 21),
        ADAMANT(1271, 30, 628, 31),
        RUNE(1275, 36, 624, 41),
        GILDED(23276, 36, 624, 41),
        DRAGON(11920, 42, 7139, 61),
        THIRD_AGE(20014, 42, 7283, 61),
        DRAGON_OR(12797, 42, 642, 61),
        INFERNAL(13243, 42, 4482, 61);

        public int id;
        public int points;
        public int anim;
        public int level;

        Pickaxe(int id, int points, int anim, int level) {
            this.id = id;
            this.points = points;
            this.anim = anim;
            this.level = level;
        }

        public static final ImmutableSet<Pickaxe> VALUES = ImmutableSortedSet.copyOf(values()).descendingSet();
    }

    public static int chance(int level, Rock type, Pickaxe pick) {
        double points = ((level - type.level) + 1 + pick.points);
        double denom = type.difficulty;
        return (int) (Math.min(0.95, points / denom) * 100);
    }

    public static Optional<Pickaxe> findPickaxe(Player player) {
        if (player.getEquipment().hasWeapon()) {
            Optional<Pickaxe> pickaxe = Pickaxe.VALUES.stream().filter(it -> player.getEquipment().contains(it.id) && player.getSkills().levels()[Skills.MINING] >= it.level).findFirst();
            if (pickaxe.isPresent()) {
                return pickaxe;
            }

        }
        return Pickaxe.VALUES.stream().filter(def -> player.inventory().contains(def.id) && player.getSkills().levels()[Skills.MINING] >= def.level).findAny();
    }

    private static void interact(Player player, Rock rockType, int replId) {
        int option = player.getAttribOr(AttributeKey.INTERACTION_OPTION, 0);
        if (option == 1) {
            mine(player, rockType, replId);
        } else {
            prospect(player, rockType);
        }
    }

    private static void mine(Player player, Rock rockType, int replId) {
        Rock rock = rockType;
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null);
        Optional<Mining.Pickaxe> pick = Mining.findPickaxe(player);

        // Any rocks mined in the jail are converted to bluite. This is the ore required to leave the jail.
        // If it was coal, people could mine coal before they are even jailed and instantly escape!
        if ((int) player.getAttribOr(AttributeKey.JAILED, 0) == 1) {
            rock = Rock.JAIL_BLURITE;

            // Expensive code but stops retards stockpiling ores.
            if (player.getBank().count(Rock.JAIL_BLURITE.ore) + player.inventory().count(Rock.JAIL_BLURITE.ore) >= (int) player.getAttribOr(AttributeKey.JAIL_ORES_TO_ESCAPE, 0)) {
                player.message("You don't need any more ores to escape.");
                return;
            }
        }

        // Is the inventory full?
        if (player.inventory().isFull()) {
            DialogueManager.sendStatement(player, "Your inventory is too full to hold any more " + rock.rockName + ".");
            return;
        }

        if (pick.isEmpty()) {
            DialogueManager.sendStatement(player, "You need a pickaxe to mine this rock.", "You do not have a pickaxe which " + "you have the Mining level to use.");
            return;
        }

        if (player.getSkills().levels()[Skills.MINING] < rock.level && (int) player.getAttribOr(AttributeKey.JAILED, 0) == 0) {
            DialogueManager.sendStatement(player, "You need a Mining level of " + rock.level + " to mine this rock.");
            return;
        }
        player.animate(pick.get().anim);
        Chain.bound(player).runFn(1, () -> player.message("You swing your pick at the rock."));

        int gemOdds = player.getEquipment().containsAny(1706, 1708, 1710, 1712, 11976, 11978) ? 3 : 1;

        Rock finalRock = rock;
        player.action.execute(new UnwalkableAction(player, 4) {
            @Override
            protected void execute() {
                // check obj
                if (!ObjectManager.objWithTypeExists(10, obj.tile()) && !ObjectManager.objWithTypeExists(11, obj.tile())) {
                    player.animate(-1);
                    stop();
                    return;
                }

                // Roll for an uncut
                if (Utils.rollDie(256, gemOdds)) {
                    giveGem(player);
                }

                var odds = Math.max(1, Mining.chance(player.getSkills().levels()[Skills.MINING], finalRock, pick.get()));

                //For jailing we want to boost the rates of mining otherwise some players will literally be there for days.
                if (player.jailed()) {
                    odds = Utils.random(90, 95);
                }

                if (player.getAttribOr(AttributeKey.DEBUG_MESSAGES, false)) {
                    player.debug("Mining chance: " + odds + ".");
                }

                if (Utils.random(100) <= odds) {
                    player.message("You manage to mine some " + finalRock.rockName + ".");


                    // Infernal pickaxe logic
                    if (finalRock != Rock.COAL && pick.get() == Pickaxe.INFERNAL && Utils.random(2) == 0) {
                        player.graphic(580, GraphicHeight.HIGH, 0);
                        addBar(player, finalRock);
                    } else {
                        player.inventory().add(new Item(finalRock.ore));
                        switch (finalRock) {
                            case COPPER -> AchievementsManager.activate(player, Achievements.MINING_I, 1);
                            case COAL -> AchievementsManager.activate(player, Achievements.MINING_II, 1);
                            case ADAMANT -> AchievementsManager.activate(player, Achievements.MINING_III, 1);
                            case RUNE -> AchievementsManager.activate(player, Achievements.MINING_IV, 1);
                        }
                    }

                    if(finalRock == Rock.RUNE) {
                        player.getTaskMasterManager().increase(Tasks.MINE_RUNITE_ORE);
                    }

                    player.animate(-1); // does -1 a same time it does the real an want me to open OSS no oke ye lol

                    // The Prospector blessing reduces rock respawn timers by half.
                    int rockTime = finalRock.respawnTime;

                    // High-level rocks on PvP are faster. Nobody waits on a PvP server.
                    rockTime = finalRock == Rock.RUNE ? 200 : // 2 minutes
                        finalRock == Rock.ADAMANT ? 50 : // 30 seconds
                            finalRock == Rock.MITHRIL ? 25 : // 15 seconds
                                rockTime;

                    GameObject original = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                    GameObject spawned = new GameObject(replId, obj.tile(), obj.getType(), obj.getRotation());

                    ObjectManager.replace(original, spawned, Math.max(1, rockTime - 1));

                    if (!player.jailed()) {
                        player.getSkills().addXp(Skills.MINING, finalRock.xp);
                    }

                    //Caskets Money, money, money..
                    if (Utils.rollDie(20, 1)) {
                        player.inventory().addOrDrop(new Item(7956, 1));
                        player.message("The rock broke, inside you find a casket!");
                    }

                    stop();
                    return;
                }
                player.animate(pick.get().anim);
            }
        });
    }

    /**
     * Attempts to roll for a gem. Chances to get a gem are 1/256, or 3/256 when wearing a charged glory amulet.
     */
    private static void giveGem(Player player) {
        int roll = Utils.random(9);
        switch (roll) {
            case 0 -> {
                player.message("You just found a Diamond!");
                player.inventory().add(new Item(1617));
            }
            case 1, 2 -> {
                player.message("You just found a Ruby!");
                player.inventory().add(new Item(1619));
            }
            case 3, 4, 5 -> {
                player.message("You just found an Emerald!");
                player.inventory().add(new Item(1621));
            }
            case 6, 7, 8, 9 -> {
                player.message("You just found a Sapphire!");
                player.inventory().add(new Item(1623));
            }
        }
    }

    private static void prospect(Player player, Rock rock) {
        player.stopActions(true);
        player.message("You examine the rock for ores...");
        Chain.bound(player).runFn(4, () -> {
            player.message("This rock contains " + rock.rockName + ".");
            player.stopActions(true);
        });
    }

    private static void addBar(Player player, Mining.Rock rock) {
        boolean success = false;
        switch (rock) {
            case COPPER, TIN -> {
                player.inventory().add(new Item(2349));
                player.getSkills().addXp(Skills.SMITHING, 2.5);
                success = true;
            }
            case IRON -> {
                player.inventory().add(new Item(2351));
                player.getSkills().addXp(Skills.SMITHING, 5.0);
                success = true;
            }
            case SILVER -> {
                player.inventory().add(new Item(2355));
                player.getSkills().addXp(Skills.SMITHING, 5.5);
                success = true;
            }
            case GOLD -> {
                player.inventory().add(new Item(2357));
                player.getSkills().addXp(Skills.SMITHING, 9.0);
                success = true;
            }
            case MITHRIL -> {
                player.inventory().add(new Item(2359));
                player.getSkills().addXp(Skills.SMITHING, 12.0);
                success = true;
            }
            case ADAMANT -> {
                player.inventory().add(new Item(2361));
                player.getSkills().addXp(Skills.SMITHING, 15.0);
                success = true;
            }
            case RUNE -> {
                player.inventory().add(new Item(2363));
                player.getSkills().addXp(Skills.SMITHING, 20.0);
                success = true;
            }
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        Mining.Rock type = Mining.Rock.GOLD;
        DecimalFormat format = new DecimalFormat("###.##");

        PrintStream out = new PrintStream(System.getProperty("user.home")+"\\Desktop\\mining_" + type.toString().toLowerCase() + ".csv");
        out.print("lvl");
        for (Mining.Pickaxe h : Mining.Pickaxe.values())
            out.print("," + h);
        out.println();

        for (int i = 1; i < 99; i++) {
            out.print(i);
            for (Mining.Pickaxe h : Mining.Pickaxe.values()) {
                out.print("," + format.format(Mining.chance(i, type, h)) + "%");
            }
            out.println();
        }

        out.flush();
        out.close();
    }

    private static final class RegisterableRock {

        int obj;
        Mining.Rock type;
        int empty;

        RegisterableRock(int obj, Mining.Rock type, int empty) {
            this.obj = obj;
            this.type = type;
            this.empty = empty;
        }

        RegisterableRock(int obj, Mining.Rock type) {
            this.obj = obj;
            this.type = type;
            this.empty = ROCKS_11390;
        }
    }

    private static final List<RegisterableRock> rocks;

    static {
        rocks = Arrays.asList(
            new RegisterableRock(ROCKS_11362, Mining.Rock.CLAY),
            new RegisterableRock(ROCKS_11161, Mining.Rock.COPPER, ROCKS_11391),
            new RegisterableRock(ROCKS_10943, Mining.Rock.COPPER),
            new RegisterableRock(ROCKS_11364, Mining.Rock.IRON),
            new RegisterableRock(ROCKS_11365, Mining.Rock.IRON, ROCKS_11391),
            new RegisterableRock(ROCKS_11360, Mining.Rock.TIN),
            new RegisterableRock(ROCKS_11361, Mining.Rock.TIN, ROCKS_11391),
            new RegisterableRock(ROCKS_11366, Mining.Rock.COAL, ROCKS_11391),
            new RegisterableRock(ROCKS_11367, Mining.Rock.COAL),
            new RegisterableRock(ROCKS_11368, Mining.Rock.SILVER),
            new RegisterableRock(ROCKS_11369, Mining.Rock.SILVER, ROCKS_11391),
            new RegisterableRock(ROCKS_11370, Mining.Rock.GOLD),
            new RegisterableRock(ROCKS_11371, Mining.Rock.GOLD, ROCKS_11391),
            new RegisterableRock(ROCKS_11372, Mining.Rock.MITHRIL),
            new RegisterableRock(ROCKS_11373, Mining.Rock.MITHRIL, ROCKS_11391),
            new RegisterableRock(ROCKS_28596, Mining.Rock.LOVAKITE),
            new RegisterableRock(ROCKS_28597, Mining.Rock.LOVAKITE, ROCKS_11391),
            new RegisterableRock(ROCKS_11374, Mining.Rock.ADAMANT),
            new RegisterableRock(ROCKS_11375, Mining.Rock.ADAMANT, ROCKS_11391),
            new RegisterableRock(ROCKS_11376, Mining.Rock.RUNE),
            new RegisterableRock(ROCKS_11377, Mining.Rock.RUNE, ROCKS_11391));
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        for (RegisterableRock rock : rocks) {
            if (obj.getId() == rock.obj) {
                Mining.interact(player, rock.type, rock.empty);
                return true;
            }
        }

        if (obj.getId() == ROCKS_11390 || obj.getId() == ROCKS_11391) {
            player.message("There is no ore currently available in this rock.");
            return true;
        }
        return false;
    }

}
