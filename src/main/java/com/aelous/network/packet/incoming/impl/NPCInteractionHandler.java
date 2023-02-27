package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.content.achievements.Achievements;
import com.aelous.model.content.achievements.AchievementsManager;
import com.aelous.model.content.areas.edgevile.BobBarter;
import com.aelous.model.content.areas.edgevile.dialogue.GeneralStoreDialogue;
import com.aelous.model.content.areas.edgevile.dialogue.SurgeonGeneralTafaniDialogue;
import com.aelous.model.content.areas.home.TwiggyOKorn;
import com.aelous.model.content.areas.wilderness.dialogue.MandrithDialogue;
import com.aelous.model.content.areas.wilderness.dialogue.PilesDialogue;
import com.aelous.model.content.areas.zeah.woodcutting_guild.dialogue.*;
import com.aelous.model.content.bank_pin.BankTeller;
import com.aelous.model.content.bank_pin.dialogue.BankTellerDialogue;
import com.aelous.model.content.items.RockCake;
import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.content.skill.impl.fishing.Fishing;
import com.aelous.model.content.skill.impl.hunter.HuntingExpert;
import com.aelous.model.content.skill.impl.hunter.Impling;
import com.aelous.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.npc.NPC;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Tuple;
import com.aelous.utility.timers.TimerKey;

import java.lang.ref.WeakReference;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Ynneh | 14/04/2022 - 00:39
 * <https://github.com/drhenny>
 */
public class NPCInteractionHandler implements PacketListener {

    private static final int ATTACK_OPTION = 72, OPTION_1 = 155, OPTION_2 = 17, OPTION_3 = 21, OPTION_4 = 18;

    @Override
    public void handleMessage(Player player, Packet packet) throws Exception {

        int opcode = packet.getOpcode();

        int index = -1, option = -1;

        if (opcode == ATTACK_OPTION) {
            index = packet.readShortA();
            option = 0;
        }
        if (opcode == OPTION_1 || opcode == OPTION_4) {
            index = packet.readLEShort();
            option = opcode == OPTION_1 ? 1 : 4;
        }

        if (opcode == OPTION_2) {
            index = packet.readLEShortA();
            option = 2;
        }

        if (opcode == OPTION_3) {
            index = packet.readShort();
            option = 3;
        }

        if (option == -1)
            return;

        if (index < 0 || index > World.getWorld().getNpcs().size())
            return;

        NPC npc = World.getWorld().getNpcs().get(index);

        if (npc == null)
            return;

        if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
            player.getBankPin().openIfNot();
            return;
        }

        if (player.askForAccountPin()) {
            player.sendAccountPinMessage();
            return;
        }

        if (player.locked() || player.dead() || npc.dead() || npc.cantInteract())
            return;

        player.stopActions(false);

        if (opcode == ATTACK_OPTION) {

            if (npc.combatInfo() == null) {
                player.message("Error processing combat definitions! forId=" + npc.getId() + ". Report this to a developer!");
                return;
            }

            Tuple<Integer, Player> ownerLink = npc.getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1, null));
            if (ownerLink.first() != null && ownerLink.first() >= 0 && ownerLink.first() != player.getIndex()) {
                player.message("They don't seem interested in fighting you.");
                player.getCombat().reset();
                return;
            }
            npc.getMovementQueue().setBlockMovement(true);
            player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);
            player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(npc));
            player.getCombat().attack(npc);
            npc.getMovementQueue().setBlockMovement(false);
            return;
        }


        player.setPositionToFace(npc.tile());
        player.debugMessage("NPCDebug=" + option + " Id=" + npc.id() + " name=" + npc.getMobName() + " Pos=" + npc.tile().toString());
        player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(npc));
        player.putAttrib(AttributeKey.INTERACTION_OPTION, option);
        player.setEntityInteraction(npc);
        npc.getMovementQueue().setBlockMovement(true);
        int size = npc.getSize();
        Runnable bankerAction = BankTeller.bankerDialogue(player, npc);
        if (bankerAction != null) {
            size++;
        }

        int finalOption = option;
        TargetRoute.set(player, npc, () -> {
            if (bankerAction != null) {
                bankerAction.run();
                return;
            }
            player.setInteractingNpcId(npc.id());
            handleInteraction(player, npc, finalOption);
            npc.getMovementQueue().setBlockMovement(false);
        });
    }

    private void handleInteraction(Player player, NPC npc, int option) {

        /** Controller overrides **/
        if (PacketInteractionManager.checkNpcInteraction(player, npc, option))
            return;

        if (player.getController() != null && player.getController().handleNpcOption(player, npc, option))
            return;

        if (option == 1) {
            handleOptionOne(player, npc);
            return;
        }

        if (option == 2) {

            if (Fishing.onNpcOption2(player, npc)) {
                return;
            }

            if (Fishing.onNpcOption1(player, npc)) {
                return;
            }

            if (npc.def().name.equalsIgnoreCase("banker")) {
                player.getBank().open();
                return;
            }

            switch (npc.id()) {
                case TWIGGY_OKORN -> {
                    if (AchievementsManager.isCompleted(player, Achievements.COMPLETIONIST)) {
                        if (player.inventory().getFreeSlots() < 2) {
                            player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE, 1));
                            player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_HOOD, 1));
                        } else {
                            player.message("You need at least 2 free slots.");
                        }
                    } else {
                        player.message("You haven't completed all of the achievements yet.");
                    }
                    //World.getWorld().shop(41).open(player);
                }

                case SUROK_MAGIS -> TeleportInterface.teleportRecent(player);

                case GUNDAI -> player.getBank().open();

                case MAKEOVER_MAGE_1307 -> player.getInterfaceManager().open(61380);


                case BOB_BARTER_HERBS -> player.getDialogueManager().start(new BobBarter());
                case THE_COLLECTOR -> {
                    npc.setPositionToFace(player.tile());
                    player.inventory().addOrDrop(new Item(ItemIdentifiers.COLLECTION_LOG, 1));
                }
                case FORESTER_7238 -> player.getDialogueManager().start(new ForesterD());
                case SURGEON_GENERAL_TAFANI -> {
                    npc.setPositionToFace(player.tile());
                    player.performGraphic(new Graphic(683));
                    player.message("<col=" + Color.BLUE.getColorValue() + ">You have restored your hitpoints, run energy and prayer.");
                    player.message("<col=" + Color.HOTPINK.getColorValue() + ">You've also been cured of poison and venom.");
                    player.hp(Math.max(player.skills().level(Skills.HITPOINTS), player.skills().xpLevel(Skills.HITPOINTS)), 20); //Set hitpoints to 100%
                    player.skills().replenishSkill(5, player.skills().xpLevel(5)); //Set the players prayer level to full
                    player.skills().replenishStatsToNorm();
                    player.setRunningEnergy(100.0, true);
                    Poison.cure(player);
                    Venom.cure(2, player, false);
                    if (player.getMemberRights().isEliteMemberOrGreater(player)) {
                        if (player.getTimers().has(TimerKey.RECHARGE_SPECIAL_ATTACK)) {
                            player.message("Special attack energy can only be restored every couple of minutes.");
                        } else {
                            player.setSpecialAttackPercentage(100);
                            player.setSpecialActivated(false);
                            CombatSpecial.updateBar(player);
                            player.getTimers().register(TimerKey.RECHARGE_SPECIAL_ATTACK, 150); //Set the value of the timer.
                            player.message("<col=" + Color.HOTPINK.getColorValue() + ">You have restored your special attack.");
                        }
                    }
                }
                case SHOP_KEEPER, SHOP_ASSISTANT_2818 -> {
                    npc.setPositionToFace(player.tile());
                    World.getWorld().shop(1).open(player);
                }
            }
            return;
        }

        if (option == 3) {

            if (npc.id() == BOB_BARTER_HERBS) {
                player.getDialogueManager().start(new BobBarter());
                return;
            }

            if (npc.id() == TWIGGY_OKORN) {
                if (AchievementsManager.isCompleted(player, Achievements.COMPLETIONIST)) {
                    if (player.inventory().getFreeSlots() < 2) {
                        player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_CAPE, 1));
                        player.inventory().add(new Item(ItemIdentifiers.ACHIEVEMENT_DIARY_HOOD, 1));
                    } else {
                        player.message("You need at least 2 free slots.");
                    }
                } else {
                    player.message("You haven't completed all of the achievements yet.");
                }
                return;
            }

            if (npc.id() == GUNDAI) {
                TradingPost.open(player);
                return;
            }
            return;
        }
        if (option == 4) {

            return;
        }
    }

    private void handleOptionOne(Player player, NPC npc) {

        /** Controllers **/
        if (player.getController() != null && player.getController().handleNpcOption(player, npc, 1)) {
            return;
        }
        if (Impling.onNpcOption1(player, npc)) {
            return;
        }

        if (Fishing.onNpcOption1(player, npc)) {
            return;
        }

        if (RockCake.onNpcOption1(player, npc)) {
            return;
        }

        if (HuntingExpert.onNpcOption1(player, npc)) {
            return;
        }

        if (npc.def().name.equalsIgnoreCase("banker")) {
            player.getDialogueManager().start(new BankTellerDialogue(), npc);
            return;
        }

        switch (npc.id()) {

            case BOB_BARTER_HERBS:
                player.getDialogueManager().start(new BobBarter());
                break;

            case MURFET:
                player.getDialogueManager().start(new MurfetD());
                break;

            case GUILDMASTER_LARS:
                player.getDialogueManager().start(new LarsD());
                break;

            case KAI:
                player.getDialogueManager().start(new KaiD());
                break;

            case BERRY_7235:
                player.getDialogueManager().start(new BerryD());
                break;

            case THE_COLLECTOR:
                npc.setPositionToFace(player.tile());
                player.inventory().addOrDrop(new Item(ItemIdentifiers.COLLECTION_LOG, 1));
                break;
            case TWIGGY_OKORN:
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new TwiggyOKorn());
                break;

            case SUROK_MAGIS:
                npc.setPositionToFace(player.tile());
                TeleportInterface.open(player);
                break;

            case SHOP_KEEPER:
            case SHOP_ASSISTANT_2818:
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new GeneralStoreDialogue());
                break;

            case MAKEOVER_MAGE:
            case MAKEOVER_MAGE_1307:
                npc.setPositionToFace(player.tile());
                player.getInterfaceManager().close();
                player.getInterfaceManager().open(3559);
                break;

            case SURGEON_GENERAL_TAFANI:
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new SurgeonGeneralTafaniDialogue());
                break;

            case MANDRITH:
                if (npc.tile().equals(3183, 3945, 0)) {
                    player.getDialogueManager().start(new MandrithDialogue());
                    return;
                }
                npc.setPositionToFace(player.tile());
                break;

            case PILES:
                player.getDialogueManager().start(new PilesDialogue());
                break;

            default:
                player.message("Nothing interesting happens.");
                break;
        }
    }
}
