package com.cryptic.model.content.skill.impl.thieving;

import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.inter.dialogue.DialogueManager;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.utility.ItemIdentifiers.COINS_995;

/**
 * @author Origin | April, 21, 2021, 11:44
 */
public class Stalls extends PacketInteraction {

    public enum Stall {

        //Normal stalls
        CRAFTING_STALL(1, 3, 16, 49000, "crafting stall",
            new int[][]
                {
                    {4874, 4797},
                    {6166, 6984},
                    {630, 634}
                }),
        BAKERS_STALL(5, 3, 16, 48000, "bakers stall",
            new int[][]
                {
                    {11730, 634},
                    {6945, 6984}
                }),
        SILK_STALL(20, 3, 24, 47000, "silk stall",
            new int[][]
                {
                    {11729, 634},
                    {629, 634}
                }),
        FUR_STALL(35, 3, 36, 43000, "fur stall",
            new int[][]
                {
                    {11732, 634},
                    {4278, 634}
                }),
        SILVER_STALL(50, 3, 54, 40000, "silver stall",
            new int[][]
                {
                    {11734, 634},
                    {628, 634}
                }),
        SPICE_STALL(65, 3, 81, 30000, "spice stall",
            new int[][]
                {
                    {6572, 6573},
                    {11733, 634},
                    {20348, 20349}
                }),
        GEM_STALL(75, 3, 160, 20000, "gem stall",
            new int[][]
                {
                    {6162, 6984},
                    {11731, 634},
                    {631, 634}
                }),
        MONKEY_GENERAL_STALL(5, 2, 16, 49000, "general stall",
            new int[][]
                {
                    {4876, 4797},
                }),
        MAGIC_STALL(65, 2, 100, 12000, "magic stall",
            new int[][]
                {
                    {4877, 4797},
                }),
        SCIMITAR_STALL(65, 2, 160, 1000, "scimitar stall",
            new int[][]
                {
                    {4878, 4797},
                });

        public final int levelReq, respawnTime, petOdds;
        public final int[][] objIDs;
        public final double experience;
        public final String name;

        Stall(int levelReq, int respawnTime, double experience, int petOdds, String name, int[][] objIDs) {
            this.levelReq = levelReq;
            this.respawnTime = respawnTime * 1000 / 600;
            this.experience = experience;
            this.petOdds = petOdds;
            this.name = name;
            this.objIDs = objIDs;
        }
    }

    private void attempt(Player player, Stall stall, GameObject object, int replacementID) {
        if (!player.getSkills().check(Skills.THIEVING, stall.levelReq, "steal from the " + stall.name))
            return;

        if (player.inventory().isFull()) {
            DialogueManager.sendStatement(player, "Your inventory is too full to hold any more.");
            return;
        }

        player.message("You attempt to steal from the " + stall.name + "...");
        player.lock();
        player.animate(832);

        Chain.bound(player).runFn(1, () -> player.animate(832)).then(1, () -> {
            replaceStall(stall, object, replacementID, player);
            var coins = 0;

            if (stall == Stall.CRAFTING_STALL) {
                coins = World.getWorld().random(25, 50);
                AchievementsManager.activate(player, Achievements.THIEF_I, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.BAKERS_STALL) {
                coins = World.getWorld().random(50, 75);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.SILK_STALL) {
                coins = World.getWorld().random(50, 85);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.FUR_STALL) {
                coins = World.getWorld().random(50, 95);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.SILVER_STALL) {
                coins = World.getWorld().random(75, 100);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.MONKEY_GENERAL_STALL) {
                coins = World.getWorld().random(75, 100);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.MAGIC_STALL) {
                coins = World.getWorld().random(100, 125);
                AchievementsManager.activate(player, Achievements.THIEF_III, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.SCIMITAR_STALL) {
                coins = World.getWorld().random(125, 150);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
                player.getTaskMasterManager().increase(Tasks.STEAL_FROM_SCIMITAR_STALL);
            } else if (stall == Stall.SPICE_STALL) {
                coins = World.getWorld().random(150, 175);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.GEM_STALL) {
                coins = World.getWorld().random(175, 200);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            }

            var thievingBoostPerk = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.MORE_BM_THIEVING);

            if (thievingBoostPerk) {
                coins *= 10.0 / 100;
            }

            player.inventory().add(new Item(COINS_995, coins), true);

            if (Utils.percentageChance(5)) {
                player.hit(null, Utils.random(3));
                player.stun(3);
            }

            player.getSkills().addXp(Skills.THIEVING, stall.experience);
            player.unlock();
        });
    }

    private void replaceStall(Stall stall, GameObject object, int replacementID, Player player) {
        var replacement = new GameObject(replacementID, object.tile(), object.getType(), object.getRotation());
        ObjectManager.replace(object, replacement, stall.respawnTime);
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1 || option == 2) {
            for (Stall stall : Stall.values()) {
                for (int[] id : stall.objIDs) {
                    if (object.getId() == id[0]) {
                        attempt(player, stall, object, id[1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
