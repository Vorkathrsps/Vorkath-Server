package com.aelous.model.content.skill.impl.cooking;

import com.aelous.model.action.Action;
import com.aelous.model.action.policy.WalkablePolicy;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 15, 2020
 */
public class JugOfWine {

    private static final int JUG_OF_WINE = 1993;
    private static final int JUG = 1935;
    private static final int GRAPES = 1987;
    private static final int JUG_OF_WATER = 1937;

    public static boolean onItemOnItem(Player player, Item use, Item with) {
        if ((use.getId() == JUG_OF_WATER || with.getId() == JUG_OF_WATER) && (use.getId() == GRAPES || with.getId() == GRAPES)) {
            player.action.execute(combine(player, player.inventory().count(JUG_OF_WATER)), true);
            return true;
        }
        return false;
    }

    private static Action<Player> combine(Player player, int amount) {
        return new Action<Player>(player, 2, true) {
            int iterations = 0;

            @Override
            public void execute() {
                ++iterations;

                if (iterations == amount) {
                    stop();
                    return;
                } else if (iterations > 28) {
                    stop();
                    return;
                }

                if (!(player.inventory().containsAll(new Item(JUG_OF_WATER), new Item(GRAPES)))) {
                    stop();
                }

                player.inventory().remove(new Item(JUG_OF_WATER));
                player.inventory().remove(new Item(GRAPES));
                player.inventory().add(new Item(JUG_OF_WINE));

                player.message("You squeeze the grapes into the jug. The wine begins to ferment.");
                player.getSkills().addXp(Skills.COOKING, 200.0);
            }

            @Override
            public String getName() {
                return "JugOfWine";
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

    public static boolean onItemOption2(Player player) {
        if(player.inventory().contains(JUG_OF_WATER)) {
            player.inventory().remove(new Item(JUG_OF_WATER, 1));
            player.inventory().add(new Item(JUG));
            player.message("You empty the contents of the jug on the floor.");
            return true;
        }
        return false;
    }
}
