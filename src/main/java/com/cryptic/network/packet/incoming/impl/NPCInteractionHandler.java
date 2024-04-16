package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.areas.edgevile.BobBarter;
import com.cryptic.model.content.areas.edgevile.dialogue.GeneralStoreDialogue;
import com.cryptic.model.content.areas.edgevile.dialogue.SurgeonGeneralTafaniDialogue;
import com.cryptic.model.content.areas.home.TwiggyOKorn;
import com.cryptic.model.content.areas.wilderness.dialogue.MandrithDialogue;
import com.cryptic.model.content.areas.wilderness.dialogue.PilesDialogue;
import com.cryptic.model.content.areas.zeah.woodcutting_guild.dialogue.*;
import com.cryptic.model.content.bank_pin.BankTeller;
import com.cryptic.model.content.bank_pin.dialogue.BankTellerDialogue;
import com.cryptic.model.content.items.RockCake;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.content.skill.impl.fishing.Fishing;
import com.cryptic.model.content.skill.impl.hunter.HuntingExpert;
import com.cryptic.model.content.skill.impl.hunter.Impling;
import com.cryptic.model.content.teleport.world_teleport_manager.TeleportInterface;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.npc.NPC;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.timers.TimerKey;

import java.lang.ref.WeakReference;
import java.util.Objects;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

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
            index = packet.readUnsignedShortA();
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

        if (index < 0)
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

        player.debugMessage("NPCDebug=" + option + " Id=" + npc.id() + " name=" + npc.getMobName() + " Pos=" + npc.tile().toString());

        if (opcode == ATTACK_OPTION) {

            if (npc.getCombatInfo() == null) {
                player.message("Missing combat definition forId=" + npc.getId() + ". Report this to a developer!");
                return;
            }
            Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
            Tuple<Long, Player> ownerLink = npc.getAttribOr(AttributeKey.OWNING_PLAYER, new Tuple<>(-1L, null));
            if (ownerLink.first() != null && ownerLink.first() >= 0 && !Objects.equals(ownerLink.first(), uid)) {
                player.message("They don't seem interested in fighting you.");
                player.getCombat().reset();
                return;
            }

            player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);
            player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(npc));
            player.getCombat().attack(npc);
            return;
        }

        if (player.getCombat().inCombat()) {
            player.getCombat().reset();
        }

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
            npc.setPositionToFace(player.tile());
            player.setInteractingNpcId(npc.id());
            handleInteraction(player, npc, finalOption);
            npc.getMovementQueue().setBlockMovement(false);
        });
    }

    private void handleInteraction(Player player, NPC npc, int option) {

        player.setEntityInteraction(npc);

        if (option == 3) {
            if (player.getPetEntity().getEntity() != null) {
                if (npc.id() == player.getPetEntity().getEntity().getId()) {
                    player.getPetEntity().pickup(player);
                    return;
                }
            }
        }

        /** Controller overrides **/
        if (PacketInteractionManager.checkNpcInteraction(player, npc, option))
            return;

        if (!player.getControllers().isEmpty()) {
            for (Controller controller : player.getControllers()) {
                controller.handleNpcOption(player, npc, option);
                return;
            }
        }
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
                case VOID_KNIGHT -> {
                    int points = player.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
                    World.getWorld().shop(48).open(player);
                    player.message(Color.ORANGE_2.wrap("<img=13><shad=0>You currently have " + points + " Void Island points.</shad>"));
                }
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
                    return;
                }

                case SUROK_MAGIS -> {
                    TeleportInterface.teleportRecent(player);
                    return;
                }

                case GUNDAI -> {
                    player.getBank().open();
                    return;
                }

                case MAKEOVER_MAGE_1307 -> {
                    player.getInterfaceManager().open(61380);
                    return;
                }


                case BOB_BARTER_HERBS -> {
                    player.getDialogueManager().start(new BobBarter());
                    return;
                }
                case THE_COLLECTOR -> {
                    npc.setPositionToFace(player.tile());
                    player.inventory().addOrDrop(new Item(ItemIdentifiers.COLLECTION_LOG, 1));
                    return;
                }
                case FORESTER_7238 -> {
                    player.getDialogueManager().start(new ForesterD());
                    return;
                }
                case SURGEON_GENERAL_TAFANI -> {
                    npc.setPositionToFace(player.tile());
                    player.performGraphic(new Graphic(683));
                    player.message("<col=" + Color.BLUE.getColorValue() + ">You have restored your hitpoints, run energy and prayer.");
                    player.message("<col=" + Color.HOTPINK.getColorValue() + ">You've also been cured of poison and venom.");
                    player.hp(Math.max(player.getSkills().level(Skills.HITPOINTS), player.getSkills().xpLevel(Skills.HITPOINTS)), 20); //Set hitpoints to 100%
                    player.getSkills().replenishSkill(5, player.getSkills().xpLevel(5)); //Set the players prayer level to full
                    player.getSkills().replenishStatsToNorm();
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
                    return;
                }
                case SHOP_KEEPER, SHOP_ASSISTANT_2818 -> {
                    npc.setPositionToFace(player.tile());
                    World.getWorld().shop(1).open(player);
                    return;
                }
            }
        }

        if (option == 3) {
            switch (npc.id()) {
                case BOB_BARTER_HERBS -> player.getDialogueManager().start(new BobBarter());
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
                }
                case GUNDAI -> TradingPost.open(player);
            }
        }
    }

    private void handleOptionOne(Player player, NPC npc) {

        /** Controllers **/
        if (!player.getControllers().isEmpty()) {
            for (Controller controller : player.getControllers()) {
                controller.handleNpcOption(player, npc, 1);
                return;
            }
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

        if (npc.def().name != null) {
            if (npc.def().name.equalsIgnoreCase("banker")) {
                player.getDialogueManager().start(new BankTellerDialogue(), npc);
                return;
            }
        }

        switch (npc.id()) {
            case LORD_TROBIN_ARCEUUS_10962 -> World.getWorld().shop(350).open(player);
            case 2822, 2821 -> World.getWorld().shop(1).open(player);
            case BOB_BARTER_HERBS -> player.getDialogueManager().start(new BobBarter());
            case MURFET -> player.getDialogueManager().start(new MurfetD());
            case GUILDMASTER_LARS -> player.getDialogueManager().start(new LarsD());
            case KAI -> player.getDialogueManager().start(new KaiD());
            case BERRY_7235 -> player.getDialogueManager().start(new BerryD());
            case THE_COLLECTOR -> {
                npc.setPositionToFace(player.tile());
                player.inventory().addOrDrop(new Item(ItemIdentifiers.COLLECTION_LOG, 1));
            }
            case 2980 -> World.getWorld().shop(6).open(player);
            case TWIGGY_OKORN -> {
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new TwiggyOKorn());
            }
            case VOID_KNIGHT -> {
                int points = player.<Integer>getAttribOr(AttributeKey.VOID_ISLAND_POINTS, 0);
                World.getWorld().shop(48).open(player);
                player.message(Color.ORANGE_2.wrap("<img=13><shad=0>You currently have " + points + " Void Island points.</shad>"));
            }
            case SUROK_MAGIS -> {
                npc.setPositionToFace(player.tile());
                TeleportInterface.open(player);
            }
            case SHOP_KEEPER, SHOP_ASSISTANT_2818 -> {
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new GeneralStoreDialogue());
            }
            case MAKEOVER_MAGE, MAKEOVER_MAGE_1307 -> {
                npc.setPositionToFace(player.tile());
                player.getInterfaceManager().close();
                player.getInterfaceManager().open(3559);
            }
            case SURGEON_GENERAL_TAFANI -> {
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new SurgeonGeneralTafaniDialogue());
            }
            case MANDRITH -> {
                if (npc.tile().equals(3183, 3945, 0)) {
                    player.getDialogueManager().start(new MandrithDialogue());
                    return;
                }
                npc.setPositionToFace(player.tile());
            }
            case PILES -> player.getDialogueManager().start(new PilesDialogue());
            default -> player.message("Nothing interesting happens.");
        }
    }
}
