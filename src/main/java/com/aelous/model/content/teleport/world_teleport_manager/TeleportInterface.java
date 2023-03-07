package com.aelous.model.content.teleport.world_teleport_manager;

import com.aelous.model.content.areas.edgevile.dialogue.SkillingAreaHuntingExpertDialogue;
import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.skill.impl.slayer.SlayerConstants;
import com.aelous.model.content.teleport.TeleportType;
import com.aelous.model.content.teleport.Teleports;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

import static com.aelous.model.content.teleport.world_teleport_manager.TeleportData.*;

/**
 * @author Patrick van Elderen | February, 20, 2021, 21:08 <--- DOGSHIT
 * @author Ynneh (re-wrote this dogshit)
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TeleportInterface {

    private final Player player;

    public TeleportInterface(Player player) {
        this.player = player;
    }

    private static final int FAVORITES_TAB = 1;
    private static final int RECENT_TAB = 2;

    public boolean loadedRecents, loadedFavorites;

    public List<TeleportData> recent_teleports = Lists.newArrayList();

    public static void teleportRecent(Player player) {
        if (player.recentTeleport == null) {
            player.getPacketSender().sendMessage("You have no history of a previous teleport for your session.");
            return;
        }
        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
            return;
        }
        Teleports.basicTeleport(player, player.recentTeleport);
    }

    public static void handleSelectPageIndex(Player player, int button) {
        int pageIndex = button - 29055 + 1;
        player.setCurrentTabIndex(pageIndex);
        updateTitle(player, pageIndex == 1 ? "Favorites" : pageIndex == 2 ? "Recent" : pageIndex == 3 ? "PvP" : pageIndex == 4 ? "PvM" : pageIndex == 5 ? "Bossing" : pageIndex == 6 ? "Minigames" : "Other");
    }

    private static void updateTitle(Player player, String string) {
        player.getPacketSender().sendString(29078, "World Teleports - " + string);
    }

    private final TeleportData[] VALUES = TeleportData.values();

    public Optional<TeleportData> findTeleport(int tabIndex, int teleportIndex) {
        int index = 0;
        for (TeleportData teleport : VALUES) {
            if (teleport.tabIndex == tabIndex) {
                if (index == teleportIndex) {
                    return Optional.of(teleport);
                }
                index++;
            }
        }
        return Optional.empty();
    }

    public static Tile getLocation(TeleportData value) {
        return value.tile;
    }

    // this method doesn't really need an explanation
    public boolean handleButton(int id, int optionIndex) {

        if (id >= 29055 && id <= 29061) {
            handleSelectPageIndex(player, id);
            return true;
        }

        if (id >= 29095 && id <= 29125) {

            int index = id - 29095;

//            /System.err.println("optionIndex="+optionIndex+" index="+index);

            int currentTab = player.getCurrentTabIndex();

            if (optionIndex == 0) {
                if (currentTab == RECENT_TAB && index < player.getRecentTeleports().size()) {
                    TeleportData recent = player.getRecentTeleports().get(index);
                    teleport(recent);
                    return true;
                }
                if (currentTab == FAVORITES_TAB && index < player.getFavorites().size()) {
                    player.getFavorites().stream().forEach(f -> System.out.println(f.teleportName));
                    TeleportData favorite = player.getFavorites().get(index);
                    teleport(favorite);
                    return true;
                }

                List<TeleportData> listForTab = Lists.newArrayList();

                Arrays.stream(values()).filter(tp -> tp.tabIndex == player.getCurrentTabIndex()).forEach(tp -> listForTab.add(tp));

                System.out.println("buttonId="+id+" index="+index+" option="+optionIndex+" currentIndex="+player.getCurrentTabIndex());

                if (listForTab.size() == 0) {
                    player.getPacketSender().sendMessage("Error loading list.. no teleports found for category.");
                    return true;
                }
                if (index > listForTab.size()) {
                    player.getPacketSender().sendMessage("Error teleporting to destination no teleports found for id="+index);
                    return true;
                }
                teleport(listForTab.get(index));
                return true;
            }
            if (optionIndex == 1) {
                if (player.getFavorites().size() >= 20) {
                    player.message("You can only have 20 favourite teleports.");
                    return false;
                }
                TeleportData teleport = VALUES[index];
                Optional<TeleportData> possibleTeleport = findTeleport(player.getCurrentTabIndex(), index);
                if (possibleTeleport.isPresent()) {
                    teleport = possibleTeleport.get();
                }
                if (player.getFavorites().contains(teleport)) {
                    player.getPacketSender().sendMessage("You already have this teleport saved in your favorites list.");
                    return false;
                }
                if (currentTab == RECENT_TAB && index < player.getRecentTeleports().size()) {
                    TeleportData recent = player.getRecentTeleports().get(index);
                    teleport = recent;
                }
                player.getFavorites().add(teleport);
                player.getPacketSender().addFavoriteTeleport(teleport);
                player.getPacketSender().sendMessage("Adding " + teleport.teleportName + " to your favorites list.");
            } else if (optionIndex == 2) {
                TeleportData data = player.getFavorites().get(index);
                player.getPacketSender().sendMessage("Removing " + data.teleportName + " from your favorite teleports.");
                player.getPacketSender().removeFavorite(data);
                player.getFavorites().remove(index);
            }
            return true;
        }
        return false;
    }

    public void takePaymentAndTeleport(TeleportData teleportData) {
        player.costBMAction(teleportData.paymentAmount(), "This teleport costs " + teleportData.paymentAmount() + " BM would you like to proceed?", () -> {
            if (teleportData.dangerous() && !Teleports.pkTeleportOk(player, teleportData.tile)) {
                return;
            }

            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                return;
            }
            beginTeleportAfterChecks(player, teleportData, null);
        });
    }

    public void teleport(TeleportData chosen) { // instead of making a method that gets the enum index via iterating, this is a way better option.

        if (chosen.tile == null && Arrays.asList(WORLD_BOSS, GWD, FISHING_AREAS, MINING_AREAS, WOODCUTTING_AREAS, HUNTER_AREAS).stream().noneMatch(tp -> tp == chosen)) {
            player.message("This teleport doesn't exist. Please contact a staff member.2");
            return;
        }

        if (!handleCustomTeleport(chosen)) {

            if (chosen.dangerous()) {
                handleDangerousTeleport(chosen);
                return;
            }
            if (chosen.paymentAmount() > 0) {
                takePaymentAndTeleport(chosen);
                return;
            }
            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC))
                return;

            beginTeleportAfterChecks(player, chosen, null);
        }
    }

    private void handleDangerousTeleport(TeleportData chosen) {

        if (!Teleports.pkTeleportOk(player, chosen.tile))
            return;

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, "This location is dangerous, would you like to proceed?");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if (chosen.paymentAmount() > 0) {
                            takePaymentAndTeleport(chosen);
                        } else {
                            if (chosen.dangerous() && !Teleports.pkTeleportOk(player, chosen.tile)) {
                                stop();
                                return;
                            }

                            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                stop();
                                return;
                            }
                            beginTeleportAfterChecks(player, chosen, null);
                        }
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    public static void open(Player player) {
        if (!player.getTeleportInterface().loadedFavorites) {
            preloadFavorites(player);
        }
        if (!player.getTeleportInterface().loadedRecents) {
            preloadRecent(player);
        }
        int pageIndex = 1;
        updateTitle(player, (pageIndex == 1 ? "Favorites" : pageIndex == 2 ? "Recent" : pageIndex == 3 ? "PvP" : pageIndex == 4 ? "PvM" : pageIndex == 5 ? "Bossing" : pageIndex == 6 ? "Minigames" : "Other"));
        player.getInterfaceManager().open(29050);
    }

    private static void preloadFavorites(Player player) {
        player.getTeleportInterface().loadedFavorites = true;
        player.getPacketSender().clearFavorites();
        player.getFavorites().stream().filter(f -> f != null).forEach(f -> player.getPacketSender().addFavoriteTeleport(f));
    }

    private static void preloadRecent(Player player) {
        player.getTeleportInterface().loadedRecents = true;
        player.getPacketSender().resetRecentTeleports();
        player.getRecentTeleports().stream().filter(f -> f != null).forEach(f -> player.getPacketSender().updateRecentTeleport(f));
    }

    public boolean handleCustomTeleport(TeleportData chosen) {
        if (chosen == TeleportData.BARROWS) {
            Chain.bound(null).runFn(2, () -> {
                GroundItemHandler.createGroundItem(new GroundItem(new Item(952, 1), new Tile(3565, 3305), player));
            });
            return false;
        }

        if (chosen == TeleportData.REV_CAVES) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.STATEMENT, "This location is dangerous, would you like to proceed?");
                    setPhase(0);
                }

                @Override
                protected void next() {
                    if (isPhase(0)) {
                        send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                        setPhase(1);
                    }
                }

                @Override
                protected void select(int option) {
                    if (isPhase(1)) {
                        if (option == 1) {
                            if (chosen.dangerous() && !Teleports.pkTeleportOk(player, chosen.tile)) {
                                stop();
                                return;
                            }
                            if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                                stop();
                                return;
                            }
                            beginTeleportAfterChecks(player, chosen, player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.REVENANT_TELEPORT) ? new Tile(3244, 10145, 0) : chosen.tile);
                        } else if (option == 2) {
                            stop();
                        }
                    }
                }
            });
            return true;
        }

        if (chosen == WORLD_BOSS) {
            if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WORLD_BOSS_TELEPORT)) {
                player.message("You do not meet the requirements to use this teleport.");
                return true;
            }
            if (WildernessBossEvent.getINSTANCE().getActiveNpc().isPresent() && WildernessBossEvent.currentSpawnPos != null) {
                Tile tile = WildernessBossEvent.currentSpawnPos;
                if (chosen.dangerous() && !Teleports.pkTeleportOk(player, tile)) {
                    return true;
                }
                if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                    return true;
                }
                beginTeleportAfterChecks(player, chosen, null);
            } else {
                player.message("The world boss recently died and will respawn shortly.");
            }
            return true;
        }
        if (chosen == GWD) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Armadyl", "Bandos", "Saradomin", "Zamorak");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (option == 1) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2841, 5291, 2));
                    } else if (option == 2) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2860, 5354, 2));
                    } else if (option == 3) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2911, 5267, 0));
                    } else if (option == 4) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2925, 5336, 2));
                    }
                }
            });
            //Don't handle tp code.
            return true;
        }

        if (chosen == FISHING_AREAS) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Catherby Fishing Area", "Fishing Guild", "Nevermind");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (option == 1) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2835, 3433));
                    } else if (option == 2) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2594, 3415));
                    } else if (option == 3) {
                        stop();
                    }
                }
            });
            //Don't handle tp code.
            return true;
        }

        if (chosen == MINING_AREAS) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Essence Mining", "Varrock Mining Area", "Desert Mining Area", "Mining Guild", "Nevermind");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (option == 1) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2911, 4830));
                    } else if (option == 2) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(3284, 3365));
                    } else if (option == 3) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(3300, 3300));
                    } else if (option == 4) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(3050, 9762));
                    } else if (option == 5) {
                        stop();
                    }
                }
            });
            //Don't handle tp code.
            return true;
        }

        if (chosen == WOODCUTTING_AREAS) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Camelot Trees", "Woodcutting Guild", "Nevermind");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (option == 1) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(2726, 3473));
                    } else if (option == 2) {
                        if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        beginTeleportAfterChecks(player, chosen, new Tile(1663, 3506));
                    } else if (option == 3) {
                        stop();
                    }
                }
            });
            //Don't handle tp code.
            return true;
        }

        if (chosen == HUNTER_AREAS) {
            player.getDialogueManager().start(new SkillingAreaHuntingExpertDialogue());
            //Don't handle tp code.
            return true;
        }
        return false;
    }

    public static void beginTeleportAfterChecks(Player player, TeleportData data, Tile toDest) {
        if (toDest != null) {
            Teleports.basicTeleport(player, toDest);
            if (data != null) {
                addTpHistory(player, data);
            }
            return;
        }

        if (data == null)
            return;

        addTpHistory(player, data);

    }

    /**
     * Sends recent teleports to client.
     *
     * @param player
     * @param data
     */
    private static void addTpHistory(Player player, TeleportData data) {
        player.recentTeleport = data.tile;

        Teleports.basicTeleport(player, data.tile);

        if (player.getRecentTeleports().contains(data))
            return;

        if (player.getRecentTeleports().size() >= 24) {
            player.getRecentTeleports().remove(0);
            player.getRecentTeleports().add(0, data);
        } else
            player.getRecentTeleports().add(data);

        player.getPacketSender().updateRecentTeleport(data);
    }

    public void sendRecentTeleportOnLogin() {
        /** Remove before adding (clear the list) **/
        player.getPacketSender().resetRecentTeleports();
        /** Sends whole list.. maybe add for sessions only? **/
        player.getRecentTeleports().stream().filter(r -> r != null).forEach(r -> player.getPacketSender().updateRecentTeleport(r));
    }

}
