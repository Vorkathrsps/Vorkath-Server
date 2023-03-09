package com.aelous.model.content.skill.impl.thieving;

import com.aelous.GameServer;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.tasks.impl.Tasks;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.World;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;

/**
 * @author Patrick van Elderen | April, 21, 2021, 11:44
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Stalls extends PacketInteraction {

    public enum Stall {

        //Donator zone stalls
        SPICE_STALL(65, 5, 181.0, 13000, "spice stall", new int[][]{{6572, 6573}, {11733, 634}, {20348, 20349},}),
        GEM_STALL(75, 10, 200.0, 8500, "gem stall", new int[][]{{6162, 6984}, {11731, 634},}),

        //Normal stalls
        CRAFTING_STALL(1, 2, 10.0, 49000, "crafting stall", new int[][]{{4874, 4797}, {6166, 6984},}),
        MONKEY_GENERAL_STALL(5, 2, 20.0, 49000, "general stall", new int[][]{{4876, 4797},}),
        MAGIC_STALL(65, 2, 50, 12000, "magic stall", new int[][]{{4877, 4797},}),
        SCIMITAR_STALL(65, 2, 50.0, 1000, "scimitar stall", new int[][]{{4878, 4797},});

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

        Chain.bound(player).runFn(1, () -> {
            replaceStall(stall, object, replacementID, player);
            var bloodMoney = 0;

            if (stall == Stall.CRAFTING_STALL) {
                bloodMoney = World.getWorld().random(150, 257);
                AchievementsManager.activate(player, Achievements.THIEF_I, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.MONKEY_GENERAL_STALL) {
                bloodMoney = World.getWorld().random(150, 378);
                AchievementsManager.activate(player, Achievements.THIEF_II, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.MAGIC_STALL) {
                bloodMoney = World.getWorld().random(150, 600);
                AchievementsManager.activate(player, Achievements.THIEF_III, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.SCIMITAR_STALL) {
                bloodMoney = World.getWorld().random(150, 1337);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
                player.getTaskMasterManager().increase(Tasks.STEAL_FROM_SCIMITAR_STALL);
            } else if (stall == Stall.SPICE_STALL) {
                bloodMoney = World.getWorld().random(215, 1750);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            } else if (stall == Stall.GEM_STALL) {
                bloodMoney = World.getWorld().random(215, 3000);
                AchievementsManager.activate(player, Achievements.THIEF_IV, 1);
                AchievementsManager.activate(player, Achievements.MASTER_THIEF, 1);
            }

            var thievingBoostPerk = player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.MORE_BM_THIEVING);
            if (thievingBoostPerk) {
                bloodMoney *= 10.0 / 100;
            }


            if (GameServer.properties().pvpMode) {
                player.inventory().add(new Item(BLOOD_MONEY, bloodMoney), true);
            }

            if (Utils.percentageChance(5)) {
                TaskManager.submit(new ForceMovementTask(player, 3, new ForceMovement(player.tile().clone(), new Tile(0, 3), 0, 70, 2)));
                player.animate(3130);
                player.getMovementQueue().clear();
                player.stun(10);
                player.message("A mysterious force knocks you back.");
            }

            player.getSkills().addXp(Skills.THIEVING, stall.experience, true);
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
                for (int[] ids : stall.objIDs) {
                    if (object.getId() == ids[0]) {
                        attempt(player, stall, object, ids[1]);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
