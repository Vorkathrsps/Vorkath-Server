package com.aelous.model.content.skill.impl.mining;

import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.skill.DefaultSkillable;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ROCKS_11390;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ROCKS_11391;

public class Mining extends PacketInteraction {
    private static final int experience_multiplier = 100;
    private static final int geode_multiplier = 50;
    private static final Set<Integer> GEMS = new HashSet<>(Arrays.asList(
        ItemIdentifiers.UNCUT_SAPPHIRE,
        ItemIdentifiers.UNCUT_EMERALD,
        ItemIdentifiers.UNCUT_RUBY,
        ItemIdentifiers.UNCUT_DIAMOND
    ));
    private static final Set<Integer> GEODES = new HashSet<>(Arrays.asList(
        ItemIdentifiers.CLUE_GEODE_BEGINNER,
        ItemIdentifiers.CLUE_GEODE_EASY,
        ItemIdentifiers.CLUE_GEODE_MEDIUM,
        ItemIdentifiers.CLUE_GEODE_HARD,
        ItemIdentifiers.CLUE_GEODE_ELITE
    ));
    private static final Set<Integer> GLORYS = new HashSet<>(Arrays.asList(
        1706, 1708, 1710, 1712, 11976, 11978
    ));

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        for (Ore ore : Ore.values()) {
            if (option == 1) {
                if (ArrayUtils.contains(ore.getId(), object.getId())) {
                    mine(player, ore, ore.replacement_id);
                    return true;
                }
                if (object.getId() == ROCKS_11390 || object.getId() == ROCKS_11391) {
                    player.message("There is no ore currently available in this rock.");
                    return true;
                }
            } else {
                if (ArrayUtils.contains(ore.getId(), object.getId())) {
                    prospect(player, ore);
                    return true;
                }
            }
        }
        return false;
    }

    private static void mine(Player player, Ore rockType, int replId) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null); //TODO add jail ore back
        Optional<Pickaxe> pick = Mining.findPickaxe(player);

        if ((int) player.getAttribOr(AttributeKey.JAILED, 0) == 1) {
            // if (player.getBank().count(Mining.Rock.JAIL_BLURITE.ore) + player.inventory().count(Ore.BLU.ore) >= (int) player.getAttribOr(AttributeKey.JAIL_ORES_TO_ESCAPE, 0)) {
            player.message("You don't need any more ores to escape.");
            return;
            //  }
        }

        if (player.inventory().isFull()) {
            DialogueManager.sendStatement(player, "Your inventory is too full to hold any more " + rockType.name + ".");
            return;
        }

        if (pick.isEmpty()) {
            DialogueManager.sendStatement(player, "You need a pickaxe to mine this rock.", "You do not have a pickaxe which " + "you have the Mining level to use.");
            return;
        }

        if (player.getSkills().levels()[Skills.MINING] < rockType.level_req && (int) player.getAttribOr(AttributeKey.JAILED, 0) == 0) {
            DialogueManager.sendStatement(player, "You need a Mining level of " + rockType.level_req + " to mine this rock.");
            return;
        }

        player.message("You swing your pick at the rock.");
        player.animate(pick.get().anim);


        Chain.bound(player).repeatingTask(pick.get().getDelay(), mine -> {
            if (!ObjectManager.objWithTypeExists(10, obj.tile()) && !ObjectManager.objWithTypeExists(11, obj.tile()) && !ObjectManager.objWithTypeExists(0, obj.tile())) {
                player.animate(-1);
                mine.stop();
                return;
            }

            if (player.getInventory().isFull()) {
                player.animate(Animation.DEFAULT_RESET_ANIMATION);
                mine.stop();
                return;
            }

            player.animate(pick.get().anim);

            var success = SkillingSuccess.success(player.skills().level(Skills.MINING), rockType.level_req, rockType, pick.get());

            if (success) {
                if (Utils.rollDie(20, 1)) {
                    player.inventory().addOrDrop(new Item(7956, 1));
                    player.message("The rock broke, inside you find a casket!");
                }

                if (Utils.rollDie(rockType.geode_chance / geode_multiplier, 1)) {
                    player.getInventory().addOrDrop(new Item(Utils.randomElement(GEODES), 1));
                    player.message("The rock broke, inside you find a geode!");
                }

                if (rockType != Ore.COAL_ROCK && pick.get() == Pickaxe.INFERNAL && Utils.random(2) == 0) {
                    player.graphic(580, GraphicHeight.HIGH, 0);
                    addBar(player, rockType);
                    return;
                }

                if (Utils.rollDie(calculateGemOdds(player), 1)) {
                    Utils.randomElement(GEMS);
                    player.message("You manage to find gems in the rock you were mining.");
                } else {
                    player.getInventory().add(new Item(rockType.item));
                    player.message("You manage to mine some " + rockType.name + ".");
                }

                player.getSkills().addXp(Skills.MINING, rockType.experience * experience_multiplier);

                switch (rockType) {
                    case COPPER_ROCK -> AchievementsManager.activate(player, Achievements.MINING_I, 1);
                    case COAL_ROCK -> AchievementsManager.activate(player, Achievements.MINING_II, 1);
                    case ADAMANT_ROCK -> AchievementsManager.activate(player, Achievements.MINING_III, 1);
                    case RUNE_ROCK -> AchievementsManager.activate(player, Achievements.MINING_IV, 1);
                }

                if (rockType == Ore.RUNE_ROCK) {
                    player.getTaskMasterManager().increase(Tasks.MINE_RUNITE_ORE);
                }

                if (Utils.percentageChance(33)) {
                    player.animate(Animation.DEFAULT_RESET_ANIMATION);
                    GameObject original = new GameObject(obj.getId(), obj.tile(), obj.getType(), obj.getRotation());
                    GameObject spawned = new GameObject(replId, obj.tile(), obj.getType(), obj.getRotation());
                    ObjectManager.replace(original, spawned, Math.max(1, rockType.respawn_time - 1));
                    mine.stop();
                }
            }
        });
    }

    private static void addBar(Player player, Ore rock) {
        switch (rock) {
            case COPPER_ROCK, TIN_ROCK -> {
                player.inventory().add(new Item(2349));
                player.getSkills().addXp(Skills.SMITHING, 2.5);
            }
            case IRON_ROCK -> {
                player.inventory().add(new Item(2351));
                player.getSkills().addXp(Skills.SMITHING, 5.0);
            }
            case SILVER_ROCK -> {
                player.inventory().add(new Item(2355));
                player.getSkills().addXp(Skills.SMITHING, 5.5);
            }
            case GOLD_ROCK -> {
                player.inventory().add(new Item(2357));
                player.getSkills().addXp(Skills.SMITHING, 9.0);
            }
            case MITHRIL -> {
                player.inventory().add(new Item(2359));
                player.getSkills().addXp(Skills.SMITHING, 12.0);
            }
            case ADAMANT_ROCK -> {
                player.inventory().add(new Item(2361));
                player.getSkills().addXp(Skills.SMITHING, 15.0);
            }
            case RUNE_ROCK -> {
                player.inventory().add(new Item(2363));
                player.getSkills().addXp(Skills.SMITHING, 20.0);
            }
        }
    }

    public static Optional<Pickaxe> findPickaxe(Player player) {
        return Stream.concat(
            Pickaxe.VALUES.stream().filter(it -> player.getEquipment().hasAt(EquipSlot.WEAPON, it.id) && player.getSkills().levels()[Skills.MINING] >= it.level),
            Pickaxe.VALUES.stream().filter(def -> player.inventory().contains(def.id) && player.getSkills().levels()[Skills.MINING] >= def.level)).findFirst();
    }

    private static void prospect(Player player, Ore rock) {
        player.stopActions(true);
        player.message("You examine the rock for ores...");
        Chain.bound(player).runFn(4, () -> {
            player.message("This rock contains " + rock.name + ".");
            player.stopActions(true);
        });
    }

    private static int calculateGemOdds(Player player) {
        for (var amulet : GLORYS) {
            if (player.getEquipment().hasAt(EquipSlot.AMULET, amulet)) {
                return 86;
            }
        }
        return 256;
    }
}
