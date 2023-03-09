package com.aelous.model.content.skill.impl.farming;

import com.aelous.model.content.skill.impl.farming.patch.Allotments;
import com.aelous.model.content.skill.impl.farming.patch.Farmbit;
import com.aelous.model.content.skill.impl.farming.patch.Flowers;
import com.aelous.model.content.skill.impl.farming.patch.Herbs;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Bart on 4/24/2016.
 *
 * Base stub for farming to handle misc. stuff in it.
 */
public class Farming extends PacketInteraction {

    public static final int PLANT_CURE = 6036;
    public static final int SPADE = 952;
    public static final int RAKE = 5341;
    public static final int SEED_DIBBER = 5343;

    private static final int[] WATERING_CANS = { 5333, 5334, 5335, 5336, 5337, 5338, 5339, 5340 };
    private static final int EMPTY_CAN = 5331;

    //TODO
    // Seed lookup map
    public static Set<Integer> ALL_SEEDS = new HashSet<Integer>() {

    };

    // Register all the timers and triggers
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        for (Farmbit fb : Farmbit.ALLOTMENTS) {
            if(object.getId() == fb.obj) {
                if(option == 1) {
                    Allotments.interactAllotment(player, fb);
                }
                return true;
            }
        }

        for (Farmbit fb : Farmbit.HERBS) {
            if(object.getId() == fb.obj) {
                if(option == 1) {
                    Herbs.interactHerb(player, fb);
                }
                return true;
            }
        }

        for (Farmbit fb : Farmbit.FLOWERS) {
            if(object.getId() == fb.obj) {
                if(option == 1) {
                    Flowers.interactFlower(player, fb);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        for (Farmbit fb : Farmbit.ALLOTMENTS) {
            if (object.getId() == fb.obj) {
                Allotments.itemOnAllotment(player, fb);
                return true;
            }
        }
        for (Farmbit fb : Farmbit.HERBS) {
            if (object.getId() == fb.obj) {
                Herbs.itemOnHerbPatch(player, fb);
                return true;
            }
        }
        for (Farmbit fb : Farmbit.FLOWERS) {
            if (object.getId() == fb.obj) {
                Flowers.itemOnFlowerPatch(player, fb);
                return true;
            }
        }
        return false;
    }

    // Register the growth timer TODO
			//r.onTimer(fb.timer) { Flowers.grow(it, fb) }

    public static void rakeWeeds(Player player, Farmbit varbit, String name) {
        if (!player.inventory().contains(5341)) {
            player.message("You need a rake to weed a farming patch.");
            return;
        }

        var value = player.varps().farmbit(varbit);

        // Does this patch need weeding?
        if (value > 2) {
            player.message("This "+name+" doesn't need weeding right now.");
            return;
        }

        //TODO confirm this with Jak
        Chain.bound(null).runFn(1, () -> {
            TaskManager.submit(player.loopTask = new Task("loop_task_weeds", 1) {

                int internalTimer = 3;
                @Override
                protected void execute() {
                    player.animate(2273);

                    if (internalTimer-- == 0) {
                        if (player.varps().farmbit(varbit) < 3) {
                            // Did we succeed?
                            if (World.getWorld().rollDie(255, 100 + player.getSkills().level(Skills.FARMING))) {
                                player.varps().farmbit(varbit, player.varps().farmbit(varbit) + 1);
                                player.getSkills().addXp(Skills.FARMING, 4.0); // Yeah, you get 4 xp... #worth
                                player.inventory().add(new Item(6055), true); // Free weed :)
                                Farming.setTimer(player, varbit.timer,1000);
                                stop();
                            }
                        } else {
                            internalTimer = 3;
                        }
                    }
                }

                @Override
                public void onStop() {
                    player.animate(-1);
                }
            });
        });
    }

    public static void synchRegion(Player player) {
        var region = player.tile().region();
        Arrays.stream(Farmbit.values()).forEach(farmbit -> {
            if (region == farmbit.visibleRegion) {
                player.varps().varp(farmbit.varp, player.<Integer>getAttribOr(farmbit.attrib, 0));
            }
        });
    }

    //decreased farm time by 50%
    public static void addTimer(Player player, TimerKey timerKey, int time) { //time = 1000
        player.getTimers().addOrSet(timerKey,time / 2);
    }

    public static void setTimer(Player player, TimerKey timerKey, int time) { //time = 1000
        player.getTimers().register(timerKey, time / 2);
    }

    public static void synch(Player player, Farmbit farmbit, boolean force) {
        if (force || player.tile().region() == farmbit.visibleRegion) {
            player.varps().varp(farmbit.varp, player.<Integer>getAttribOr(farmbit.attrib, 0));
        }
    }

}
