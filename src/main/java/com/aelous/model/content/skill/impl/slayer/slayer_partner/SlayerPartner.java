package com.aelous.model.content.skill.impl.slayer.slayer_partner;

import com.aelous.GameEngine;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.InputScript;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.Optional;

import static com.aelous.model.entity.player.QuestTab.InfoTab.SLAYER_TASK;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.THORODIN_5526;

/**
 * @author PVE
 * @Since juli 22, 2020
 */
public class SlayerPartner {

    private static void task(Player player, int task, int amount) {
        player.putAttrib(AttributeKey.SLAYER_TASK_ID, task);
        player.putAttrib(AttributeKey.SLAYER_TASK_AMT, amount);

        SlayerCreature creature = SlayerCreature.lookup(player.slayerTaskId());
        if(creature != null) {
            player.message("Ok, great. Your new task is to kill " + amount + " " + Slayer.taskName(creature.uid) + ".");
            player.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(player));
        }
    }

    /**
     * Upon login always set the exact same task as your partner has.
     */
    public static void onLogin(Player player) {
        String partnerName = player.getAttribOr(AttributeKey.SLAYER_PARTNER, "None");

        //Don't do anything if we do not have a slayer partner.
        if (partnerName.equalsIgnoreCase("None")) {
            return;
        }

        Optional<Player> partner = World.getWorld().getPlayerByName(partnerName);
        if(partner.isEmpty()) {
            return;
        }

        boolean online = partner.get().isRegistered();

        int task = partner.get().getAttribOr(AttributeKey.SLAYER_TASK_ID,0);
        int amt = partner.get().getAttribOr(AttributeKey.SLAYER_TASK_AMT,0);
        if(online) {
            //Partner has a task
            if (task > 0 && amt > 0) {
                //new Task
                task(player, task, amt);
            } else {
                //Reset old slayer task
                task(player,0,0);
            }
        } else {
            //Offline check
            Player offlinePartner = new Player();
            offlinePartner.setUsername(Utils.formatText(partnerName.substring(0, 1).toUpperCase() + partnerName.substring(1)));

            GameEngine.getInstance().submitLowPriority(() -> {
                try {
                    if (PlayerSave.loadOfflineWithoutPassword(offlinePartner)) {
                        GameEngine.getInstance().addSyncTask(() -> {
                            int offlineTask = offlinePartner.getAttribOr(AttributeKey.SLAYER_TASK_ID,0);
                            int offlineAmt = offlinePartner.getAttribOr(AttributeKey.SLAYER_TASK_AMT,0);
                            //Partner has a task
                            if (offlineTask > 0 && offlineAmt > 0) {
                                //new Task
                                task(player, offlineTask, offlineAmt);
                            } else {
                                //Reset old slayer task
                                task(player,0,0);
                            }
                        });
                    } else {
                        System.err.println("Something went wrong setting a new slayer task.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Gives the same slayer rewards to the slayer partner.
     */
    public static void reward(Player player, NPC npc) {
        String slayerPartner = player.getAttribOr(AttributeKey.SLAYER_PARTNER, "None");
        Optional<Player> partner = World.getWorld().getPlayerByName(slayerPartner);

        //Partner is online so we can reward him to
        partner.ifPresent(value -> Slayer.reward(value, npc));
    }

    /**
     * Slayer gem 'partner' option.
     */
    public static void partnerOption(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Invite slayer partner", "Reset slayer partner", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(1)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                    setPhase(2);
                } else if (isPhase(3)) {
                    String previousPartner = player.getAttribOr(AttributeKey.SLAYER_PARTNER, "None");
                    Optional<Player> partner = World.getWorld().getPlayerByName(previousPartner);

                    //If partner is online reset partner otherwise reset offline
                    if (partner.isPresent()) {
                        partner.get().putAttrib(AttributeKey.SLAYER_PARTNER, "None");
                    } else {
                        Player plr2 = new Player();
                        plr2.setUsername(Utils.formatText(previousPartner.substring(0, 1).toUpperCase() + previousPartner.substring(1)));
                        GameEngine.getInstance().submitLowPriority(() -> {
                            try {
                                if (PlayerSave.loadOfflineWithoutPassword(plr2)) {
                                    GameEngine.getInstance().addSyncTask(() -> {
                                        plr2.putAttrib(AttributeKey.SLAYER_PARTNER, "None");
                                        //Delay saving the account by 4 ticks.
                                        Chain.bound(null).name("SlayerPartnerSaveTask").runFn(4, () -> GameEngine.getInstance().submitLowPriority(() -> {
                                            PlayerSave.save(plr2);
                                            player.message("You are no longer slayer partners with " + previousPartner.substring(0, 1).toUpperCase() + previousPartner.substring(1) + ".");
                                        }));
                                    });
                                } else {
                                    System.err.println("Something went wrong resetting offline slayer partners.");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                    //Reset for player leaving party
                    player.putAttrib(AttributeKey.SLAYER_PARTNER, "None");
                    stop();
                } else if (isPhase(4)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Invite slayer partner", "Reset slayer partner", "Nevermind");
                    setPhase(0);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        stop();
                        player.setAmountScript("Who would you like to be your slayer partner?", new InputScript() {

                            @Override
                            public boolean handle(Object value) {
                                String input = (String) value;
                                Optional<Player> partner = World.getWorld().getPlayerByName(input);
                                //Check if player is online
                                if (partner.isPresent()) {
                                    invite(player, partner.get(), true);
                                } else {
                                    DialogueManager.sendStatement(player, input + " is not online.");
                                }
                                return true;
                            }
                        });
                    } else if (option == 2) {
                        String slayerPartner = player.getAttribOr(AttributeKey.SLAYER_PARTNER, "None");
                        boolean hasSlayerParter = !slayerPartner.equalsIgnoreCase("None");
                        if (hasSlayerParter) {
                            send(DialogueType.STATEMENT, "Would you like to reset your slayer partner?");
                            setPhase(1);
                        } else {
                            send(DialogueType.STATEMENT, "You currently have no slayer partner.");
                            setPhase(4);
                        }
                    } else if (option == 3) {
                        stop();
                    }
                } else if (isPhase(2)) {
                    if (option == 1) {
                        send(DialogueType.STATEMENT, "You have reset your slayer partner.");
                        setPhase(3);
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    /**
     * Sends out an slayer partner invitation
     */
    public static void invite(Player requester, Player requestee, boolean fromDistance) {
        //Can't invite players who are already in an interface interaction.
        if (requestee.getInterfaceManager().getMain() != -1) {
            requester.message("That player is busy.");
            return;
        }

        //player has to be in interaction radius
        if (!requester.tile().inSqRadius(requestee.tile(), fromDistance ? 16 : 1)) {
            return;
        }

        //We can't invite someone without a slayer task.
        if (requester.slayerTaskAmount() <= 0) {
            DialogueManager.sendStatement(requester, "I should get a slayer task first.");
            return;
        }

        Chain.bound(requester).runFn(1, () -> DialogueManager.sendStatement(requester, "Sending request to " + requestee.getUsername() + "...")).then(4, () -> {
            //You can't invite someone who already has a partner
            String slayerPartner = requestee.getAttribOr(AttributeKey.SLAYER_PARTNER, "None");
            if (slayerPartner.equalsIgnoreCase(requester.getUsername())) {
                DialogueManager.sendStatement(requester, requestee.getUsername() + " is already your slayer partner.");
            } else if (!slayerPartner.equalsIgnoreCase("None")) {
                DialogueManager.sendStatement(requester, requestee.getUsername() + " is the slayer partner of " + slayerPartner + ".");
            }
        });

        requestee.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, requester.getUsername() + " invited you as slayer partner", "Accept.", "Decline.");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(1)) {
                    stop();
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(0)) {
                    if (option == 1) {
                        stop();
                        requestee.message(requester.getUsername() + " is now your slayer partner until you leave the party.");
                        DialogueManager.sendStatement(requester, requestee.getUsername() + " accepted your request.");
                        requestee.putAttrib(AttributeKey.SLAYER_PARTNER, requester.getUsername());
                        requester.putAttrib(AttributeKey.SLAYER_PARTNER, requestee.getUsername());

                        //Copy task
                        requestee.putAttrib(AttributeKey.SLAYER_TASK_ID, requester.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
                        requestee.putAttrib(AttributeKey.SLAYER_TASK_AMT, requester.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0));
                        requestee.getPacketSender().sendString(SLAYER_TASK.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_TASK.childId).fetchLineData(requestee));
                        Slayer.displayCurrentAssignment(requestee);

                        SlayerCreature task = SlayerCreature.lookup(requestee.slayerTaskId());
                        int num = requestee.slayerTaskAmount();

                        requestee.getDialogueManager().start(new Dialogue() {
                            @Override
                            protected void start(Object... parameters) {
                                send(DialogueType.NPC_STATEMENT,THORODIN_5526, Expression.ANXIOUS, "Excellent, you're doing great. Your new task is to kill", "" + num + " " + Slayer.taskName(task.uid) + ".");
                            }
                        });
                        setPhase(1);
                    } else if (option == 2) {
                        stop();
                        DialogueManager.sendStatement(requester, requestee.getUsername() + " declined your request.");
                    }
                }
            }
        });
    }
}
