package com.cryptic.model.content.skill.impl.mining;

import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.daily_tasks.DailyTasks;
import com.cryptic.model.content.skill.perks.SkillingSets;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;

import java.util.*;
import java.util.stream.Stream;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.ROCKS_11390;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.ROCKS_11391;

public class Mining extends PacketInteraction {
    private static final int geode_multiplier = 50;
    public static final Set<Integer> GEMS = new HashSet<>(Arrays.asList(
        ItemIdentifiers.UNCUT_SAPPHIRE,
        ItemIdentifiers.UNCUT_EMERALD,
        ItemIdentifiers.UNCUT_RUBY,
        ItemIdentifiers.UNCUT_DIAMOND,
        ItemIdentifiers.UNCUT_OPAL
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

    static int[] star = new int[]{41020, 41021, 41223, 41224, 41225, 41226, 41228, 41229};

    private static void mine(Player player, Ore rockType, int replId) {
        GameObject obj = player.getAttribOr(AttributeKey.INTERACTION_OBJECT, null); //TODO add jail ore back
        Optional<Pickaxe> pick = Mining.findPickaxe(player);

        if ((int) player.getAttribOr(AttributeKey.JAILED, 0) == 1) {
            player.message("You don't need any more ores to escape.");
            return;
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

        var delay = pick.get().getDelay();

        player.repeatingTask(delay, mine -> {
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
                if (Utils.rollDie(rockType.geode_chance, 1)) {
                    player.getInventory().addOrDrop(new Item(Utils.randomElement(GEODES), 1));
                    player.message(Color.BLUE.wrap("The rock broke, inside you find a geode!"));
                }

                if ((rockType != Ore.COAL_ORE && rockType != Ore.GEM_ROCK) && pick.get() == Pickaxe.INFERNAL && Utils.random(2) == 0) {
                    player.graphic(580, GraphicHeight.HIGH, 0);
                    addBar(player, rockType);
                    return;
                }

                if ((Utils.rollDie(calculateGemOdds(player), 1) && rockType != Ore.GEM_ROCK)) {
                    int gem = Utils.randomElement(GEMS);
                    player.getInventory().add(new Item(gem));
                    player.message(Color.BLUE.wrap("You manage to find gems in the rock you were mining."));
                } else {
                    if (player.hasAttrib(AttributeKey.INFERNAL_SMITH)) {
                        switch (rockType) {
                            case COPPER_ORE, TIN_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.BRONZE_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case IRON_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.IRON_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case SILVER_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.SILVER_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case COAL_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.STEEL_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case GOLD_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.GOLD_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case MITHRIL_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.MITHRIL_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case ADAMANT_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.ADAMANTITE_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            case RUNE_ORE -> {
                                player.getInventory().add(new Item(ItemIdentifiers.RUNITE_BAR));
                                player.skills().addXp(Skills.SMITHING, rockType.experience);
                            }
                            default -> player.getInventory().add(new Item(rockType.item));
                        }
                    } else if (player.hasAttrib(AttributeKey.REMOTE_STORAGE)) {
                        player.getBank().add(new Item(rockType.item));
                    } else {
                        Item ore = new Item(rockType.item);
                        isSetCrashedStarBonus(player, ore);
                        player.getInventory().add(new Item(rockType.item));
                    }
                    player.message("You manage to mine some " + rockType.name + ".");
                }

                double experience = rockType.experience;
                if (!player.hasAttrib(AttributeKey.INFERNAL_SMITH)) {
                    experience = isSetExperienceBonus(player, experience);
                    rollForPet(player, rockType);
                    player.getSkills().addXp(Skills.MINING, experience);
                }

                DailyTasks.check(player, DailyTasks.MINING, rockType.name);

                switch (rockType) {
                    case COPPER_ORE -> AchievementsManager.activate(player, Achievements.MINING_I, 1);
                    case COAL_ORE -> AchievementsManager.activate(player, Achievements.MINING_II, 1);
                    case ADAMANT_ORE -> AchievementsManager.activate(player, Achievements.MINING_III, 1);
                    case RUNE_ORE -> AchievementsManager.activate(player, Achievements.MINING_IV, 1);
                }

                if (rockType == Ore.RUNE_ORE) {
                    player.getTaskMasterManager().increase(Tasks.MINE_RUNITE_ORE);
                }

                if (rockType == Ore.GEM_ROCK) {
                    Item gem = new Item(Utils.randomElement(GEMS));
                    gem = isNoted(player, gem);
                    rockType.setItem(gem.getId());
                }

            }
        }).then(1, () -> player.animate(Animation.DEFAULT_RESET_ANIMATION));

    }

    private static void isSetCrashedStarBonus(Player player, Item ore) {
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.MINING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    ore.setAmount(ore.getAmount() * 2);
                }
            }
        }
    }

    private static Item isNoted(Player player, Item gem) {
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.MINING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    gem = gem.note();
                }
            }
        }
        return gem;
    }

    private static void rollForPet(Player player, Ore ore) {
        double chance = ore.pet_chance;
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.MINING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    chance *= 0.85D;
                    break;
                }
            }
        }
        if (Utils.rollDie((int) chance, 1)) {
            player.getInventory().add(new Item(ItemIdentifiers.ROCK_GOLEM));
            World.getWorld().sendWorldMessage("<img=2010> " + Color.BURNTORANGE.wrap("<shad=0>" + player.getUsername() + " has received a Rock Golem Pet!" + "</shad>"));
        }
    }

    private static double isSetExperienceBonus(Player player, double experience) {
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.MINING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    experience *= set.experienceBoost;
                    break;
                }
            }
        }
        return experience;
    }

    private static void addBar(Player player, Ore rock) {
        switch (rock) {
            case COPPER_ORE, TIN_ORE -> {
                player.inventory().add(new Item(2349));
                player.getSkills().addXp(Skills.SMITHING, 2.5);
            }
            case IRON_ORE -> {
                player.inventory().add(new Item(2351));
                player.getSkills().addXp(Skills.SMITHING, 5.0);
            }
            case SILVER_ORE -> {
                player.inventory().add(new Item(2355));
                player.getSkills().addXp(Skills.SMITHING, 5.5);
            }
            case GOLD_ORE -> {
                player.inventory().add(new Item(2357));
                player.getSkills().addXp(Skills.SMITHING, 9.0);
            }
            case MITHRIL_ORE -> {
                player.inventory().add(new Item(2359));
                player.getSkills().addXp(Skills.SMITHING, 12.0);
            }
            case ADAMANT_ORE -> {
                player.inventory().add(new Item(2361));
                player.getSkills().addXp(Skills.SMITHING, 15.0);
            }
            case RUNE_ORE -> {
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
