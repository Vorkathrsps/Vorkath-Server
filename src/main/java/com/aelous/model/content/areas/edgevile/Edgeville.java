package com.aelous.model.content.areas.edgevile;

import com.aelous.GameServer;
import com.aelous.model.content.account.ChangeAccountTypeDialogue;
import com.aelous.model.content.areas.edgevile.dialogue.*;
import com.aelous.model.content.areas.lumbridge.dialogue.Hans;
import com.aelous.model.content.areas.wilderness.content.todays_top_pkers.TopPkers;
import com.aelous.model.content.areas.wilderness.dialogue.ArtifactTraderDialogue;
import com.aelous.model.content.mechanics.MagicalAltarDialogue;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.content.tasks.TaskMasterD;
import com.aelous.model.content.teleport.OrnateJewelleryBox;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.entity.player.*;
import com.aelous.core.task.impl.TickableTask;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.shop.impl.ShopReference;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ALTAR;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 21, 2020 17:06
 */
public class Edgeville extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (option == 1) {
            if(npc.id() == ARMOUR_SALESMAN) {
                player.shopReference = ShopReference.GEAR;
                player.getPacketSender().sendConfig(1126, 1);
                World.getWorld().shop(10).open(player);
                return true;
            }
            if(npc.id() == PERDU) {
                player.getDialogueManager().start(new PerduDialogue());
                return true;
            }
            if(npc.id() == IRON_MAN_TUTOR) {
                player.getDialogueManager().start(new ChangeAccountTypeDialogue());
                return true;
            }
            if (npc.id() == GRAND_EXCHANGE_CLERK || npc.id() == GRAND_EXCHANGE_CLERK_2149) {
                TradingPost.open(player);
                return true;
            }
            if (npc.id() == DRUNKEN_DWARF_2408) {
                player.getDialogueManager().start(new DrunkenDwarfDialogue());
                return true;
            }
            if (npc.id() == VANNAKA) {
                player.getDialogueManager().start(new TaskMasterD());
                return true;
            }
            if (npc.id() == AUBURY) {
                player.getDialogueManager().start(new AuburyDialogue());
                return true;
            }

            if (npc.id() == WIZARD_MIZGOG) {
                player.getDialogueManager().start(new WizardMizgogDialogue());
                return true;
            }
            if (npc.id() == HUNTING_EXPERT) {
                player.getDialogueManager().start(new SkillingAreaHuntingExpertDialogue());
                return true;
            }
            if (npc.id() == HANS) {
                player.getDialogueManager().start(new Hans());
                return true;
            }
            if (npc.id() == EMBLEM_TRADER) {
                player.getDialogueManager().start(new ArtifactTraderDialogue());
                return true;
            }
        } else if (option == 2) {
            if(npc.id() == TRADE_REFEREE) {
                World.getWorld().shop(8).open(player);
                return true;
            }
            if (npc.id() == EMBLEM_TRADER) {
                World.getWorld().shop(17).open(player);
                return true;
            }
            if(npc.id() == IRON_MAN_TUTOR) {
                World.getWorld().shop(16).open(player);
                return true;
            }
            if (npc.id() == VANNAKA) {
                player.getTaskMasterManager().open();
                return true;
            }
            if (npc.id() == HANS) {
                player.getDialogueManager().start(new Hans());
                return true;
            }
            if (npc.id() == AUBURY) {
                World.getWorld().shop(23).open(player);
                return true;
            }
            if (npc.id() == 315) {
                World.getWorld().shop(17).open(player);
                var targetPoints = player.<Integer>getAttribOr(AttributeKey.TARGET_POINTS,0);
                player.message(Color.RED.wrap("You currently have "+ Utils.formatNumber(targetPoints)+" target points."));
                return true;
            }
        } else if (option == 3) {
            if(npc.id() == EMERALD_BENEDICT) {
                World.getWorld().shop(48).open(player);
                return true;
            }
            if(npc.id() == IRON_MAN_TUTOR) {
                World.getWorld().shop(49).open(player);
                return true;
            }
            if (npc.id() == AUBURY) {
                npc.forceChat("Seventhior Distine Molenko!");
                player.graphic(110, GraphicHeight.HIGH, 100);
                player.lockNoDamage();
                Chain.bound(player).runFn(3, () -> {
                    player.teleport(new Tile(2911, 4830, 0));
                    player.unlock();
                });
                return true;
            }
        } else if (option == 4) {
            if(npc.id() == IRON_MAN_TUTOR) {
                World.getWorld().shop(47).open(player);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == SUPPLIES) {
/*            if (option == 1) {
                World.getWorld().shop(4).open(player);
                player.getPacketSender().sendConfig(1125, 1);
                player.getPacketSender().sendConfig(1126, 0);
                player.getPacketSender().sendConfig(1127, 0);
                return true;
            }*/
            if (option == 1) {
                World.getWorld().shop(6).open(player);
                return true;
            }
            if (option == 2) {
                World.getWorld().shop(43).open(player);
                player.getPacketSender().sendConfig(1125, 1);
                player.getPacketSender().sendConfig(1126, 0);
                player.getPacketSender().sendConfig(1127, 0);
                return true;
            }
        }
        if (obj.getId() == ALTAR_6552) {
            if (option == 1) {
                player.getDialogueManager().start(new MagicalAltarDialogue());
            }
            return true;
        }

        if (option == 1) {
            if(obj.getId() == ELVEN_LAMP_36492) {
                Teleports.basicTeleport(player, new Tile(3328, 4751));
                return true;
            }

            if(obj.getId() == PORTAL_26646) {
                Teleports.basicTeleport(player, new Tile(3087, 3492));
                return true;
            }

            if(obj.getId() == 34752) {
                Teleports.basicTeleport(player, GameServer.properties().defaultTile);
                return true;
            }

            if (obj.getId() == GREATER_TELEPORT_FOCUS) {
                TeleportInterface.open(player);
                return true;
            }

            if (obj.getId() == CHAOS_ALTAR_411) {
                if (player.getSkills().level(Skills.PRAYER) < player.getSkills().xpLevel(Skills.PRAYER)) {
                    player.animate(new Animation(645));
                    player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5));
                    player.message("You recharge your Prayer points.");
                } else {
                    player.message("You already have full prayer points.");
                }
                return true;
            }

            if (obj.getId() == 29150) {
                player.switchSpellBook(MagicSpellbook.NORMAL);
                return true;
            }

            if (obj.getId() == LEVER_26761) {
                player.setPositionToFace(obj.tile());

                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                player.lockNoDamage();
                GameObject spawned = new GameObject(88, obj.tile(), obj.getType(), obj.getRotation());
                player.runFn(1, () -> {
                    player.animate(2140);
                    player.message("You pull the lever...");
                }).then(1, () -> {
                    ObjectManager.addObj(spawned);
                    ObjectManager.replace(obj, spawned, 5);
                }).then(1, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    player.teleport(3154, 3924);
                    player.animate(-1);
                    player.unlock();
                    player.message("...And teleport into the wilderness.");
                });
                return true;
            }

            if (obj.getId() == TRAPDOOR_1579) {
                GameObject open = new GameObject(1581, obj.tile(), obj.getType(), obj.getRotation());
                ObjectManager.replaceWith(obj, open);
                return true;
            }

            final Tile crossDitch = new Tile(0, player.tile().getY() < 3522 ? 3 : -3);

            if (obj.getId() == WILDERNESS_DITCH) {
                player.runFn(1, () -> {
                    int diffX = 0, diffY = 0;
                    if (obj.getRotation() == 0 || obj.getRotation() == 2) {
                        if (player.getAbsY() == 3520) {
                            diffY = 3;
                        } else {
                            diffY -= 3;
                        }
                    } else {
                        if (player.getAbsX() == 2995) {
                            diffX = 3;
                        } else {
                            diffX = -3;
                        }
                    }
                    player.lock();
                    ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(diffX, diffY), 30, 60, 6132, crossDitch.getY() == 3 ? 0 : 2);
                    player.setForceMovement(forceMovement);
                }).then(2, player::unlock);

                if (obj.getId() == ALTAR) {
                    if (player.getSkills().level(Skills.PRAYER) < player.getSkills().xpLevel(Skills.PRAYER)) {
                        player.animate(new Animation(645));
                        player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5));
                        player.message("You recharge your Prayer points.");
                    } else {
                        player.message("You already have full prayer points.");
                    }
                    return true;
                }
            }

            if (obj.getId() == DOOR_1536) {
                player.message("You feel like it wouldn't be wise to do that...");
                return true;
            }

            if (obj.getId() == ORNATE_REJUVENATION_POOL) {
                Chain.bound(null).name("RejuvenationPoolTask").runFn(1, () -> player.animate(7305)).then(2, () -> {
                    player.lock();
                    player.message("<col=" + Color.BLUE.getColorValue() + ">You have restored your hitpoints, run energy and prayer.");
                    player.message("<col=" + Color.HOTPINK.getColorValue() + ">You've also been cured of poison and venom.");
                    player.getSkills().resetStats();
                    int increase = player.getEquipment().hpIncrease();
                    player.hp(Math.max(increase > 0 ? player.getSkills().level(Skills.HITPOINTS) + increase : player.getSkills().level(Skills.HITPOINTS), player.getSkills().xpLevel(Skills.HITPOINTS)), 39); //Set hitpoints to 100%
                    player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5)); //Set the players prayer level to fullplayer.putAttrib(AttributeKey.RUN_ENERGY, 100.0);
                    player.setRunningEnergy(100.0, true);
                    Poison.cure(player);
                    Venom.cure(2, player);

                    if (player.tile().region() != 13386) {
                        player.message(Color.RED.tag() + "When being a member your special attack will also regenerate.");
                        if (player.getMemberRights().isRegularMemberOrGreater(player)) {
                            if (player.getTimers().has(TimerKey.RECHARGE_SPECIAL_ATTACK)) {
                                player.message("Special attack energy can be restored in " + player.getTimers().asMinutesAndSecondsLeft(TimerKey.RECHARGE_SPECIAL_ATTACK) + ".");
                            } else {
                                player.restoreSpecialAttack(100);
                                player.setSpecialActivated(false);
                                CombatSpecial.updateBar(player);
                                int time = 0;
                                if (player.getMemberRights().isRegularMemberOrGreater(player))
                                    time = 300;//3 minutes
                                if (player.getMemberRights().isSuperMemberOrGreater(player))
                                    time = 100;//1 minute
                                if (player.getMemberRights().isEliteMemberOrGreater(player))
                                    time = 0;//always
                                player.getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK, time); //Set the value of the timer.
                                player.message("<col=" + Color.HOTPINK.getColorValue() + ">You have restored your special attack.");
                            }
                        }
                    } else {
                        player.restoreSpecialAttack(100);
                        player.setSpecialActivated(false);
                        CombatSpecial.updateBar(player);
                        player.message("<col=" + Color.HOTPINK.getColorValue() + ">You have restored your special attack.");
                    }
                    player.unlock();
                });
                return true;
            }
        } else if (option == 2) {
            if (obj.getId() == SCOREBOARD) {
                TopPkers.SINGLETON.openLeaderboard(player);
                return true;
            }

            if (obj.getId() == DWARVEN_MACHINERY) {
                World.getWorld().shop(15).open(player);
                return true;
            }

            if (obj.getId() == ORNATE_JEWELLERY_BOX) {
                OrnateJewelleryBox.open(player);
                return true;
            }

            if (obj.getId() == TRAPDOOR_1581) {
                GameObject open = new GameObject(1581, obj.tile(), obj.getType(), obj.getRotation());
                GameObject close = new GameObject(1579, obj.tile(), obj.getType(), obj.getRotation());
                ObjectManager.replaceWith(open, close);
                return true;
            }

            if (obj.getId() == 29150) {
                player.switchSpellBook(MagicSpellbook.ANCIENT);
                return true;
            }
        } else if (option == 3) {

            if (obj.getId() == 29150) {
                player.switchSpellBook(MagicSpellbook.LUNAR);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        return false;
    }

}
