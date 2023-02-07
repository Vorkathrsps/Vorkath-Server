package com.aelous.model.content.skill.impl.herblore;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 18, 2020
 */
public class PestleAndMortar {

    public static int PESTLE_AND_MORTAR = 233;

    private enum CrushableContents {
        UNICORN_HORN(237, 235, "You grind the unicorn horn to dust."),
        CHOCOLATE_BAR(1973, 1975, "You grind the chocolate to dust."),
        KEBBIT_TEETH(10109, 10111, "You grind the kebbit teeth to dust."),
        BIRD_NEST(5075, 6693, "You grind the bird's nest down."),
        DESERT_GOAT_HORN(9735, 9736, "You grind the goat's horn to dust."),
        CHARCOAL(973, 704, "You grind the charcoal to a powder."),
        ASHES(592, 8865, "You grind down the ashes."),
        BLUE_DRAGON_SCALE(243, 241, "You grind the dragon scale to dust."),
        LAVA_SCALE(11992, 11994, ""),
        SUPERIOR_DRAGON_BONES(22124, 21975, "You grind the item.");

        private final int before;
        private final int after;
        private final String message;

        CrushableContents(int before, int after, String message) {
            this.before = before;
            this.after = after;
            this.message = message;
        }

        static Map<Integer, CrushableContents> crushableContentsMap = new HashMap<>();

        static {
            for (CrushableContents crushableContents : CrushableContents.values()) {
                crushableContentsMap.put(crushableContents.before, crushableContents);
            }
        }
    }

    public static boolean onItemOnItem(Player player, Item use, Item with) {
        if (use.getId() == PESTLE_AND_MORTAR || with.getId() == PESTLE_AND_MORTAR) {
            int before = use.getId() == PESTLE_AND_MORTAR ? with.getId() : use.getId();
            CrushableContents c = CrushableContents.crushableContentsMap.get(before);
            if (c != null) {
                if ((use.getId() == c.before && with.getId() == PESTLE_AND_MORTAR) || (use.getId() == PESTLE_AND_MORTAR && with.getId() == c.before)) {
                    int num = player.inventory().count(c.before);

                    TaskManager.submit(player.loopTask = new Task("loop_skill_task_pestle_and_mortar", 2) {
                        private int iterations = 0;

                        @Override
                        protected void execute() {
                            //If the items required aren't in the players inventory, break the while..
                            if (!player.inventory().contains(c.before) || !player.inventory().contains(PESTLE_AND_MORTAR)) {
                                stop();
                            }

                            player.inventory().remove(new Item(c.before));
                            if (c.before != 11992) {
                                player.message(c.message);
                                player.inventory().add(new Item(c.after));
                            } else {
                                int amt = Utils.random(3, 6);

                                player.message("You grind the lava dragon scale into "+amt+" shards.");
                                player.inventory().add(new Item(c.after, amt));
                            }
                            player.animate(364);

                            if (++iterations == num) {
                                stop();
                            }
                        }
                    });
                }
                return  true;
            }
        }
        return false;
    }
}
