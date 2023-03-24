package com.aelous.model.entity.player;

import com.aelous.GameServer;
import com.aelous.model.content.DropsDisplay;
import com.aelous.model.content.achievements.AchievementWidget;
import com.aelous.model.content.areas.wilderness.content.EloRating;
import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.collection_logs.LogType;
import com.aelous.model.content.items_kept_on_death.ItemsKeptOnDeath;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Ali 20.10.2017
 */
public final class QuestTab {

    private static final Logger logger = LogManager.getLogger(QuestTab.class);

    private QuestTab() {
    }

    public enum InfoTab {

        TIME(12658) {
            @Override
            public String fetchLineData(Player player) {
                return "Time: <col=ffffff> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM hh:mm a"));
            }
        },

        UPTIME(12659) {
            @Override
            public String fetchLineData(Player player) {
                return "Uptime: <col=ffffff>" + QuestTab.fetchUpTime();
            }
        },

        WORLD_BOSS_SPAWN(12660) {
            @Override
            public String fetchLineData(Player player) {
                return "Next World Boss: <col=ffffff>" + WildernessBossEvent.getINSTANCE().timeTill(false);
            }
        },

        GAME_MODE(12666) {
            @Override
            public String fetchLineData(Player player) {
                return "Game Mode: <col=ffffff>" + Utils.gameModeToString(player);
            }
        },

        PLAY_TIME(12667) {
            @Override
            public String fetchLineData(Player player) {
                return "Play Time: <col=ffffff>" + Utils.convertSecondsToShortDuration(player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L), false);
            }
        },

        REGISTERED_ON(12668) {
            @Override
            public String fetchLineData(Player player) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                return "Registered On: <col=ffffff>" + simpleDateFormat.format(player.getCreationDate());
            }
        },

        MEMBER_RANK(12669) {
            @Override
            public String fetchLineData(Player player) {
                return "Member Rank: <col=ffffff>" + player.getMemberRights().getName();
            }
        },

        TOTAL_DONATED(12670) {
            @Override
            public String fetchLineData(Player player) {
                double totalAmountPaid = player.getAttribOr(AttributeKey.TOTAL_PAYMENT_AMOUNT, 0D);
                return "Total Donated: <col=ffffff>" + totalAmountPaid + "0$";
            }
        },

        VOTE_POINTS(12671) {
            @Override
            public String fetchLineData(Player player) {
                int votePoints = player.getAttribOr(AttributeKey.VOTE_POINS, 0);
                return "Vote Points: <col=ffffff>" + votePoints;
            }
        },

        BOSS_POINTS(12672) {
            @Override
            public String fetchLineData(Player player) {
                int bossPoints = player.getAttribOr(AttributeKey.BOSS_POINTS, 0);
                return "Boss Points: <col=ffffff>" + Utils.formatNumber(bossPoints);
            }
        },

        REFERRALS(12673) {
            @Override
            public String fetchLineData(Player player) {
                int referralCount = player.getAttribOr(AttributeKey.REFERRALS_COUNT, 0);
                return "Referrals: <col=ffffff>" + Utils.formatNumber(referralCount);
            }
        },

        PLAYERS_PKING(12676) {
            @Override
            public String fetchLineData(Player player) {
                return "Players in wild: <col=ffffff>" + World.getWorld().getPlayersInWild();
            }
        },

        ELO_RATING(12677) {
            @Override
            public String fetchLineData(Player player) {
                int rating = player.getAttribOr(AttributeKey.ELO_RATING, EloRating.DEFAULT_ELO_RATING);
                return "Elo Rating: <col=ffffff>" + Utils.formatNumber(rating);
            }
        },

        KILLS(12678) {
            @Override
            public String fetchLineData(Player player) {
                int kills = player.getAttribOr(AttributeKey.PLAYER_KILLS, 0);
                return "Player kills: <col=ffffff>" + Utils.formatNumber(kills);
            }
        },

        DEATHS(12679) {
            @Override
            public String fetchLineData(Player player) {
                int deaths = player.getAttribOr(AttributeKey.PLAYER_DEATHS, 0);
                return "Player deaths: <col=ffffff>" + Utils.formatNumber(deaths);
            }
        },

        KD_RATIO(12680) {
            @Override
            public String fetchLineData(Player player) {
                return "K/D Ratio: <col=ffffff>" + player.getKillDeathRatio();
            }
        },

        TARGETS_KILLED(12681) {
            @Override
            public String fetchLineData(Player player) {
                int kills = player.getAttribOr(AttributeKey.TARGET_KILLS, 0);
                return "Targets Killed: <col=ffffff>" + Utils.formatNumber(kills);
            }
        },

        CURRENT_KILLSTREAK(12682) {
            @Override
            public String fetchLineData(Player player) {
                int killstreak = player.getAttribOr(AttributeKey.KILLSTREAK, 0);
                return "Killstreak: <col=ffffff>" + Utils.formatNumber(killstreak);
            }
        },

        KILLSTREAK_RECORD(12684) {
            @Override
            public String fetchLineData(Player player) {
                int record = player.getAttribOr(AttributeKey.KILLSTREAK_RECORD, 0);
                return "Highest killstreak: <col=ffffff>" + Utils.formatNumber(record);
            }
        },

        TARGET_KILLS(12683) {
            @Override
            public String fetchLineData(Player player) {
                int wildernessStreak = player.getAttribOr(AttributeKey.WILDERNESS_KILLSTREAK, 0);
                return "Wilderness streak: <col=ffffff>" + Utils.formatNumber(wildernessStreak);
            }
        },

        TARGET_POINTS(12685) {
            @Override
            public String fetchLineData(Player player) {
                int targetPoints = player.getAttribOr(AttributeKey.TARGET_POINTS, 0);
                return "Target points: <col=ffffff>" + Utils.formatNumber(targetPoints);
            }
        },

        RISKED_WEALTH(12686) {
            @Override
            public String fetchLineData(Player player) {
                long risked = ItemsKeptOnDeath.getLostItemsValue();
                return "Risked wealth: <col=ffffff>" + Utils.formatNumber(risked) + " BM";
            }
        },

        SLAYER_TASK(12690) {
            @Override
            public String fetchLineData(Player player) {
                String name = Slayer.taskName(player.getAttribOr(AttributeKey.SLAYER_TASK_ID, 0));
                int num = player.getAttribOr(AttributeKey.SLAYER_TASK_AMT, 0);
                if (num == 0) {
                    return "Task: <col=ffffff> None";
                } else {
                    return "Task: <col=ffffff> " + num + " x " + name;
                }
            }
        },

        TASK_STREAK(12691) {
            @Override
            public String fetchLineData(Player player) {
                int num = player.getAttribOr(AttributeKey.SLAYER_TASK_SPREE, 0);
                return "Task Streak: <col=ffffff> " + Utils.formatNumber(num);
            }
        },

        TASKS_COMPLETED(12692) {
            @Override
            public String fetchLineData(Player player) {
                int num = player.getAttribOr(AttributeKey.COMPLETED_SLAYER_TASKS, 0);
                return "Tasks Completed: <col=ffffff> " + Utils.formatNumber(num);
            }
        },

        SLAYER_KEYS_RECEIVED(12693) {
            @Override
            public String fetchLineData(Player player) {
                var keys = player.<Integer>getAttribOr(AttributeKey.SLAYER_KEYS_RECEIVED, 0);
                return "Slayer Keys Received: <col=ffffff>" + Utils.formatNumber(keys);
            }
        },


        SLAYER_POINTS(12694) {
            @Override
            public String fetchLineData(Player player) {
                int rewardPoints = player.getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
                return "Slayer Points: <col=ffffff>" + Utils.formatNumber(rewardPoints);
            }
        },

        PLAYERS_ONLINE(12700) {
            @Override
            public String fetchLineData(Player player) {
                int count = World.getWorld().getPlayers().size();
                return "Players Online: <col=ffffff>" + count;
            }
        };

        public final int childId;

        InfoTab(int childId) {
            this.childId = childId;
        }

        public abstract String fetchLineData(final Player player);

        public static final Map<Integer, InfoTab> INFO_TAB;

        static {
            INFO_TAB = new HashMap<>();

            for (InfoTab info : InfoTab.values()) {
                INFO_TAB.put(info.childId, info);
            }
        }
    }

    public static boolean onButton(Player player, int button) {
        switch (button) {
            case 12661 -> {
                player.getPacketSender().sendURL("https://aelous.net/");
                player.message("Opening https://aelous.net/ in your web browser...");
            }

            case 12662 -> {
                player.getPacketSender().sendURL("https://discord.gg/tJsWHM6FRr");
                player.message("Opening discord in your web browser...");
                return true;
            }

            case 12663 -> {
                player.getPacketSender().sendURL("https://aelous.net/store/");
                player.message("Opening https://aelous.net/store/ in your web browser...");
                return true;
            }

            case 12652 -> {
                AchievementWidget.openEasyJournal(player);
                return true;
            }

            case 12752 -> player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "View Monster Drops.", "Open Collection Log.", "Open Boss Kill Log.", "Nevermind.");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            DropsDisplay.start(player);
                            stop();
                        } else if (option == 2) {
                            player.getCollectionLog().open(LogType.BOSSES);
                            stop();
                        } else if (option == 3) {
                            player.getBossKillLog().openLog();
                            stop();
                        } else if (option == 4) {
                            stop();
                        }
                    }
                }
            });

            case 12754 -> {
                QuestTab.refreshInfoTab(player);
                return true;
            }
        }
        return false;
    }

    public static void refreshInfoTab(final Player player) {
        InfoTab.INFO_TAB.forEach((childId, lineInfo) -> {
            player.getPacketSender().sendString(childId, lineInfo.fetchLineData(player));
        });
    }

    public static String fetchUpTime() {
        final long upTime = System.currentTimeMillis() - GameServer.startTime;
        return String.format("%d hrs, %d mins",
            TimeUnit.MILLISECONDS.toHours(upTime),
            TimeUnit.MILLISECONDS.toMinutes(upTime) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(upTime)));
    }

}
