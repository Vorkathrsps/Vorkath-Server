package com.aelous.model.content.skill.impl.herblore;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.World;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.ChatBoxItemDialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;

import java.util.HashMap;
import java.util.Map;

import static com.aelous.utility.ItemIdentifiers.SWAMP_TAR;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public enum HerbTar {

    GUAM(249, 10142, 30.0, 19),
    MARRENTILL(251, 10143, 42.5, 31),
    TARROMIN(253, 10144, 55.0, 39),
    HARRALANDER(255, 10145, 67.5, 44);

    private final int herb;
    private final int result;
    private final int reqLevel;
    private final double exp;
    private final String herbName;

    HerbTar(int herb, int result, double exp, int reqLevel) {
        this.herb = herb;
        this.result = result;
        this.exp = exp;
        this.reqLevel = reqLevel;
        this.herbName = new Item(herb, 1).definition(World.getWorld()).name.toLowerCase();
    }

    private static final Map<Integer, HerbTar> tarsMap = new HashMap<>();

    static {
        for (HerbTar tars : HerbTar.values()) {
            tarsMap.put(tars.herb, tars);
        }
    }

    public static boolean onItemOnItem(Player player, Item use, Item with) {
        if (use.getId() == SWAMP_TAR || with.getId() == SWAMP_TAR) {
            int id = use.getId() == SWAMP_TAR ? with.getId() : use.getId();
            HerbTar tar = tarsMap.get(id);
            if (tar != null) {
                int herbslot = player.inventory().getSlot(tar.herb);
                Item herb = player.inventory().get(herbslot);
                if (herb == null)
                    return false;
                String herbName = herb.name().replace("leaf", "leaves");
                if (!player.inventory().contains(PestleAndMortar.PESTLE_AND_MORTAR)) {
                    player.message("You need a pestle and mortar to mix " + herbName.toLowerCase() + " with swamp tar.");
                } else if (player.getSkills().xpLevel(Skills.HERBLORE) < tar.reqLevel) {
                    player.message("You need a Herblore level of " + tar.reqLevel + " to make " + herbName.split(" ")[0] + " tar.");
                } else {
                    int count = player.inventory().count(tar.herb);
                    if (count >= 1) {
                        ChatBoxItemDialogue.sendInterface(player, 1746, 175, tar.herb);

                        player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
                            @Override
                            public void firstOption(Player player) {
                                player.action.execute(mix(player, new Item(tar.herb), new Item(SWAMP_TAR), tar, 1), true);
                            }

                            @Override
                            public void secondOption(Player player) {
                                player.action.execute(mix(player, new Item(tar.herb), new Item(SWAMP_TAR), tar, 5), true);
                            }

                            @Override
                            public void thirdOption(Player player) {

                                player.setAmountScript("How many would you like to make?", new InputScript() {

                                    @Override
                                    public boolean handle(Object value) {
                                        player.action.execute(mix(player, new Item(tar.herb), new Item(SWAMP_TAR), tar, (int) value), true);
                                        return true;
                                    }
                                });
                            }

                            @Override
                            public void fourthOption(Player player) {
                                player.action.execute(mix(player, new Item(tar.herb), new Item(SWAMP_TAR), tar, 14), true);
                            }
                        };
                    }
                }
                return true;
            }
        }
        return false;
    }

    private static Action<Player> mix(Player player, Item primary, Item secondary, HerbTar herbTar, int amount) {
        return new Action<Player>(player, 2, true) {
            int iterations = 0;

            @Override
            public void execute() {
                ++iterations;

                player.animate(5249);
                player.getSkills().addXp(Skills.HERBLORE, herbTar.exp);
                player.inventory().removeAll(primary, secondary);
                player.inventory().add(new Item(herbTar.result));

                player.message("You mix the " + herbTar.herbName + " into the swamp tar.");

                if (iterations == amount) {
                    stop();
                    return;
                } else if (iterations > 28) {
                    stop();
                    return;
                }

                if (!(player.inventory().containsAll(primary, secondary))) {
                    stop();
                    DialogueManager.sendStatement(player, "<col=369>You have run out of materials.");
                }
            }

            @Override
            public String getName() {
                return "Herblore";
            }

            @Override
            public boolean prioritized() {
                return false;
            }

            @Override
            public WalkablePolicy getWalkablePolicy() {
                return WalkablePolicy.NON_WALKABLE;
            }
        };
    }
}
