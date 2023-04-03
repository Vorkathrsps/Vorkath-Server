package com.aelous.model.content.areas.home;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

//import static net.aelous.util.CustomItemIdentifiers.LARRANS_KEY_TIER_III;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 23, 2021, 10:49
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class HomeArea extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if(object.getId() == 14398) {
                if(object.tile().equals(2037,3621,0)) {
                    player.lockDelayDamage();
                    Chain.bound(player).name("FaladorTightrope1Task").runFn(1, () -> {
                        player.looks().render(763, 762, 762, 762, 762, 762, -1);
                        player.agilityWalk(false);
                        player.stepAbs(2053, 3621, MovementQueue.StepType.FORCED_WALK);
                    }).waitForTile(new Tile(2053, 3621), () -> {
                        player.agilityWalk(true);
                        player.looks().resetRender();
                        player.getSkills().addXp(Skills.AGILITY, 17.0);
                        player.unlock();
                    });
                    return true;
                }
                player.lockDelayDamage();
                Chain.bound(player).name("FaladorTightrope1Task").runFn(1, () -> {
                    player.looks().render(763, 762, 762, 762, 762, 762, -1);
                    player.agilityWalk(false);
                    player.stepAbs(2037, 3621, MovementQueue.StepType.FORCED_WALK);
                }).waitForTile(new Tile(2037, 3621), () -> {
                    player.agilityWalk(true);
                    player.looks().resetRender();
                    player.getSkills().addXp(Skills.AGILITY, 17.0);
                    player.unlock();
                });
                return true;
            }
            if(object.getId() == 23311) {
                TeleportInterface.open(player);
                return true;
            }
            if (object.getId() == STAIRCASE_25801) {
                Ladders.ladderDown(player, new Tile(2021, 3567, 0), true);
                return true;
            }
            if (object.getId() == STAIRCASE_25935) {
                Ladders.ladderUp(player, new Tile(2021, 3567, 1), true);
                return true;
            }
        }
        if(option == 2) {
            if(object.getId() == 13641) {
                TeleportInterface.teleportRecent(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            /**
             * IronMan Shops
             */
            if (npc.id() == AUBURY_11434) {
                World.getWorld().shop(5004).open(player);
            }
            if (npc.id() == SHOP_KEEPER_2884) {
                World.getWorld().shop(5005).open(player);
            }
            /**
             * End
             */
            if(npc.id() == GERRANT_2891) {
                World.getWorld().shop(46).open(player);
                return true;
            }
            if(npc.id() == GRAND_EXCHANGE_CLERK) {
                TradingPost.open(player);
                return true;
            }
            if(npc.id() == GUNNJORN) {
                World.getWorld().shop(33).open(player);
                return true;
            }
            if(npc.id() == RADIGAD_PONFIT) {
                World.getWorld().shop(36).open(player);
                return true;
            }
            if(npc.id() == SHOP_ASSISTANT_2820) {
                World.getWorld().shop(1).open(player);
                return true;
            }
        }
        if(option == 2) {
            if(npc.id() == GERRANT_2891) {
                World.getWorld().shop(46).open(player);
                return true;
            }
            if(npc.id() == GUNNJORN) {
                World.getWorld().shop(32).open(player);
                return true;
            }
            if(npc.id() == RADIGAD_PONFIT) {
                World.getWorld().shop(35).open(player);
                return true;
            }
            if(npc.id() == GRUM_2889) {
                World.getWorld().shop(38).open(player);
                return true;
            }
            if(npc.id() == JATIX) {
                World.getWorld().shop(40).open(player);
                return true;
            }
        }
        if(option == 3) {
            if(npc.id() == GUNNJORN) {
                World.getWorld().shop(34).open(player);
                return true;
            }
            if(npc.id() == RADIGAD_PONFIT) {
                World.getWorld().shop(37).open(player);
                return true;
            }
        }
        if(option == 4) {

        }
        return false;
    }
}
