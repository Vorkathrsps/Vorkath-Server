package com.cryptic.model.content.skill.impl.firemaking;

import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.content.skill.perks.SkillingSets;
import com.cryptic.model.content.tasks.impl.Tasks;
import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.TimedObjectSpawnTask;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.utility.ItemIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.FIRE_26185;

/**
 * @author PVE
 * @Since augustus 29, 2020
 */
public class LogLighting {

    public enum LightableLog {
        LOGS(1511, 40.0, 1, 21, 200),
        ACHEY(2862, 40.0, 1, 21, 200),
        OAK(1521, 60.0, 15, 35, 233),
        WILLOW(1519, 90.0, 30, 50, 284),
        TEAK(6333, 105.0, 35, 55, 316),
        MAPLE(1517, 135.0, 45, 65, 350),
        MAHOGANY(6332, 157.5, 50, 70, 400),
        YEW(1515, 202.5, 60, 80, 500),
        MAGIC(1513, 303.8, 75, 95, 550),
        REDWOOD(19669, 350.0, 90, 99, 600);

        public int id;
        public double xp;
        public int req;
        public int barb_req;
        public int lifetime;

        LightableLog(int id, double xp, int req, int barb_req, int lifetime) {
            this.id = id;
            this.xp = xp;
            this.req = req;
            this.barb_req = barb_req;
            this.lifetime = lifetime;
        }

        // Die roll  /100
        public int lightChance(Player player, boolean catching) {
            int points = 40;
            int diff = Math.min(6, player.getSkills().levels()[Skills.FIREMAKING] - req); // 6 points max
            return Math.min(100, points + diff * (catching ? 15 : 10));
        }

        public static LightableLog logForId(int id) {
            for (LightableLog log : LightableLog.values()) {
                if (log.id == id)
                    return log;
            }
            return null;
        }
    }

    public enum LightingAnimation {
        TRAININGBOW(9705, 6713),
        SHORTBOW(841, 6714),
        LONGBOW(839, 6714),
        OAK_SHORTBOW(843, 6715),
        OAK_LONGBOW(845, 6715),
        WILLOW_SHORTBOW(849, 6716),
        WILLOW_LONGBOW(847, 6716),
        MAPLE_SHORTBOW(853, 6717),
        MAPLE_LONGBOW(851, 6717),
        YEW_SHORTBOW(857, 6718),
        YEW_LONGBOW(855, 6718),
        MAGIC_SHORTBOW(861, 6719),
        MAGIC_LONGBOW(859, 6719),
        SEERCULL(6724, 6720);

        private final int item;
        private final int anim;

        LightingAnimation(int item, int anim) {
            this.item = item;
            this.anim = anim;
        }
    }

    static double experienceMultiplier = 15;

    public static boolean onItemOnItem(Player player, Item use, Item with) {
        for (LightableLog log : LightableLog.values()) {
            if ((use.getId() == TINDERBOX || with.getId() == TINDERBOX) && (use.getId() == log.id || with.getId() == log.id)) {
                Item logItem = new Item(log.id);

                if (player.getDueling().inDuel()) {
                    player.message("You can't light a fire in here.");
                    return true;
                }
                // Check level requirement
                if (player.getSkills().levels()[Skills.FIREMAKING] < log.req) {
                    String itemname = logItem.definition(World.getWorld()).name.toLowerCase();
                    player.message("You need a Firemaking level of " + log.req + " to burn " + itemname + ".");
                    return true;
                }

                // Check tile
                if (ObjectManager.objWithTypeExists(10, new Tile(player.tile().x, player.tile().y, player.tile().level))
                    || ObjectManager.objWithTypeExists(11, new Tile(player.tile().x, player.tile().y, player.tile().level))
                    || (World.getWorld().floorAt(player.tile()) & 0x4) != 0) {
                    player.message("You can't light a fire here.");
                    return true;
                }

                Tile targTile = player.tile().transform(-1, 0, 0);

                boolean legal = player.getMovementQueue().canWalk(-1, 0);
                if (!legal) {
                    targTile = player.tile().transform(1, 0, 0);
                    legal = player.getMovementQueue().canWalk(1, 0);
                    if (!legal) {
                        return true; // No valid move to go!
                    }
                }

                boolean catchFire = player.getTimers().has(TimerKey.FIRE_CATCHING);

                Tile finalTargTile = targTile;
                GroundItem spawnedItem = new GroundItem(logItem, player.tile(), player);
                GroundItemHandler.createGroundItem(spawnedItem);
                player.inventory().remove(logItem, true);
                boolean fastmode = catchFire && Utils.random(100) <= log.lightChance(player, true);

                Chain.bound(player).runFn(fastmode ? 2 : 1, () -> {
                    if (fastmode) {
                        player.message("The fire catches and the logs begin to burn.");
                        burnComplete(player, log, finalTargTile, spawnedItem);
                    } else {
                        player.animate(733);
                        player.message("You attempt to light the logs.");

                        Chain.bound(player).runFn(3, () -> {

                        }).then(() ->
                            Chain.bound(player).waitUntil(4, () -> {
                                player.animate(733);
                                return Utils.random(100) <= log.lightChance(player, true);
                            }, () -> {
                                burnComplete(player, log, finalTargTile, spawnedItem);
                            }));
                    }
                });
                return true;
            }
        }

        for (LightableLog log : LightableLog.values()) {
            for (LightingAnimation bows : LightingAnimation.values()) {
                if ((use.getId() == bows.item || with.getId() == bows.item) && (use.getId() == log.id || with.getId() == log.id)) {
                    Item logItem = new Item(log.id);

                    if (player.getDueling().inDuel()) {
                        player.message("You can't light a fire in here.");
                        return true;
                    }
                    // Check level requirement
                    if (player.getSkills().levels()[Skills.FIREMAKING] < log.req) {
                        String itemname = logItem.definition(World.getWorld()).name.toLowerCase();
                        player.message("You need a Firemaking level of " + log.req + " to burn " + itemname + ".");
                        return true;
                    }

                    // Check tile
                    if (ObjectManager.objWithTypeExists(10, new Tile(player.tile().x, player.tile().y, player.tile().level))
                        || ObjectManager.objWithTypeExists(11, new Tile(player.tile().x, player.tile().y, player.tile().level))
                        || (World.getWorld().floorAt(player.tile()) & 0x4) != 0) {
                        player.message("You can't light a fire here.");
                        return true;
                    }

                    Tile targTile = player.tile().transform(-1, 0, 0);

                    boolean legal = player.getMovementQueue().canWalk(-1, 0);
                    if (!legal) {
                        targTile = player.tile().transform(1, 0, 0);
                        legal = player.getMovementQueue().canWalk(1, 0);
                        if (!legal) {
                            return true; // No valid move to go!
                        }
                    }

                    boolean catchFire = player.getTimers().has(TimerKey.FIRE_CATCHING);

                    Tile finalTargTile = targTile;
                    GroundItem spawnedItem = new GroundItem(logItem, player.tile(), player);
                    GroundItemHandler.createGroundItem(spawnedItem);
                    player.inventory().remove(logItem, true);
                    boolean fastmode = catchFire && Utils.random(100) <= log.lightChance(player, true);

                    Chain.bound(player).runFn(fastmode ? 2 : 1, () -> {
                        if (fastmode) {
                            player.message("The fire catches and the logs begin to burn.");
                            burnComplete(player, log, finalTargTile, spawnedItem);
                        } else {
                            player.animate(733);
                            player.message("You attempt to light the logs.");

                            Chain.bound(player).runFn(3, () -> {
                                // empty
                            }).then(() ->
                                Chain.bound(player).waitUntil(4, () -> {
                                    player.animate(733);
                                    return Utils.random(100) <= log.lightChance(player, true);
                                }, () -> {
                                    burnComplete(player, log, finalTargTile, spawnedItem);
                                }));
                        }
                    });
                    return true;
                }
            }
        }
        return false;
    }

    private static void burnComplete(Player player, LightableLog log, Tile finalTargTile, GroundItem spawnedItem) {
        player.message("The fire catches and the logs begin to burn.");

        // Remove the logs
        GroundItemHandler.sendRemoveGroundItem(spawnedItem);

        // Set our three tick timer to catch the fire (like on RS :))
        player.getTimers().register(TimerKey.FIRE_CATCHING, 3);

        // Spawn a fire that dies after a minute and walk away
        GameObject fire = makeFire(player, log.lifetime);
        player.animate(-1);
        player.getMovementQueue().interpolate(finalTargTile, MovementQueue.StepType.FORCED_WALK);
        player.lockMoveDamageOk();
        Chain.bound(player).runFn(1, () -> {
            player.unlock();
            if (log == LightableLog.MAGIC) player.getTaskMasterManager().increase(Tasks.BURN_MAGIC_LOGS);
            double chance = getChance(player);
            if (Utils.rollDie((int) chance, 1)) {
                player.inventory().add(new Item(PHOENIX));
            }
            double experience = getExperience(player, log);
            player.getSkills().addXp(Skills.FIREMAKING, experience);
            AchievementsManager.activate(player, Achievements.FIREMAKING_I, 1);
            AchievementsManager.activate(player, Achievements.FIREMAKING_II, 1);
            AchievementsManager.activate(player, Achievements.FIREMAKING_III, 1);
            AchievementsManager.activate(player, Achievements.FIREMAKING_IV, 1);
        });
    }

    private static double getChance(Player player) {
        double chance = 2000;
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.FIREMAKING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    chance *= 0.85D;
                    break;
                }
            }
        }
        return chance;
    }

    private static double getExperience(Player player, LightableLog log) {
        double experience = log.xp;
        for (var set : SkillingSets.VALUES) {
            if (set.getSkillType().equals(Skill.FIREMAKING)) {
                if (player.getEquipment().containsAll(set.getSet())) {
                    experience *= set.experienceBoost;
                    break;
                }
            }
        }
        return experience;
    }

    public static void onInvitemOnGrounditem(Player player, Item item) {
        if (item.getId() == TINDERBOX) {
            GroundItem spawnedItem = player.getAttribOr(AttributeKey.INTERACTED_GROUNDITEM, null);

            LightableLog log = null;
            for (LightableLog log1 : LightableLog.values()) {
                if (spawnedItem.getItem().getId() == log1.id) {
                    log = log1;
                }
            }
            if (log == null) return;// wrong item on gitem

            if (player.getDueling().inDuel()) {
                player.message("You can't light a fire in here.");
                return;
            }
            // Check level requirement
            if (player.getSkills().levels()[Skills.FIREMAKING] < log.req) {
                String itemname = spawnedItem.getItem().definition(World.getWorld()).name.toLowerCase();
                player.message("You need a Firemaking level of " + log.req + " to burn " + itemname + ".");
                return;
            }

            // Check tile
            if (ObjectManager.objWithTypeExists(10, new Tile(player.tile().x, player.tile().y, player.tile().level))
                || ObjectManager.objWithTypeExists(11, new Tile(player.tile().x, player.tile().y, player.tile().level))
                || (World.getWorld().floorAt(player.tile()) & 0x4) != 0) {
                player.message("You can't light a fire here.");
                return;
            }

            Tile targTile = player.tile().transform(-1, 0, 0);

            boolean legal = player.getMovementQueue().canWalk(-1, 0);
            if (!legal) {
                targTile = player.tile().transform(1, 0, 0);
                legal = player.getMovementQueue().canWalk(1, 0);
                if (!legal) {
                    return; // No valid move to go!
                }
            }

            boolean catchFire = player.getTimers().has(TimerKey.FIRE_CATCHING);

            LightableLog finalLog = log;
            Tile finalTargTile = targTile;
            LightableLog finalLog1 = log;
            boolean fastmode = catchFire && Utils.random(100) <= log.lightChance(player, true);
            Chain.bound(player).runFn(fastmode ? 2 : 1, () -> {
                if (fastmode) { // Success
                    // msg after removing log
                    if (!GroundItemHandler.sendRemoveGroundItem(spawnedItem)) { // Invalid!
                        return;
                    }

                    burnComplete(player, finalLog1, finalTargTile, spawnedItem);
                } else {
                    player.animate(733);
                    player.message("You attempt to light the logs.");

                    Chain.bound(player).runFn(3, () -> {
                        // empty
                    }).then(() -> Chain.bound(player).waitUntil(4, () -> Utils.random(100) <= finalLog.lightChance(player, true), () -> {
                        // Remove the logs
                        if (!GroundItemHandler.sendRemoveGroundItem(spawnedItem)) { // Invalid!
                            return;
                        }

                        burnComplete(player, finalLog1, finalTargTile, spawnedItem);
                    }));
                }
            });
        }
    }

    public static void onGroundItemOption2(Player player, Item item) {
        for (LightableLog log : LightableLog.values()) {
            if (item.getId() == log.id) {
                Item logItem = new Item(log.id);
                GroundItem logs = player.getAttribOr(AttributeKey.INTERACTED_GROUNDITEM, null);

                if (getLighter(player) == -1) {
                    player.message("You do not have a suitable lighting method, and the logs won't light themselves.");
                    return;
                }

                if (player.getDueling().inDuel()) {
                    player.message("You can't light a fire in here.");
                    return;
                }
                // Check level requirement
                if (player.getSkills().levels()[Skills.FIREMAKING] < log.req) {
                    String itemname = logItem.definition(World.getWorld()).name.toLowerCase();
                    player.message("You need a Firemaking level of " + log.req + " to burn " + itemname + ".");
                    return;
                }

                // Check tile
                if (ObjectManager.objWithTypeExists(10, new Tile(player.tile().x, player.tile().y, player.tile().level))
                    || ObjectManager.objWithTypeExists(11, new Tile(player.tile().x, player.tile().y, player.tile().level))
                    || (World.getWorld().floorAt(player.tile()) & 0x4) != 0) {
                    player.message("You can't light a fire here.");
                    return;
                }

                Tile targTile = player.tile().transform(-1, 0, 0);

                boolean legal = player.getMovementQueue().canWalk(-1, 0);
                if (!legal) {
                    targTile = player.tile().transform(1, 0, 0);
                    legal = player.getMovementQueue().canWalk(1, 0);
                    if (!legal) {
                        return; // No valid move to go!
                    }
                }

                boolean catchFire = player.getTimers().has(TimerKey.FIRE_CATCHING);

                Tile finalTargTile = targTile;
                boolean fastmode = catchFire && Utils.random(100) <= log.lightChance(player, true);
                Chain.bound(player).runFn(fastmode ? 2 : 1, () -> {
                    if (fastmode) { // Success
                        player.message("The fire catches and the logs begin to burn.");
                        burnComplete(player, log, finalTargTile, logs);
                    } else {
                        player.animate(getLighter(player));
                        player.message("You attempt to light the logs.");

                        Chain.bound(player).runFn(3, () -> {
                            // empty
                        }).then(() -> Chain.bound(player).waitUntil(4, () -> Utils.random(100) <= log.lightChance(player, true), () -> {
                            burnComplete(player, log, finalTargTile, logs);
                        }));
                    }
                });
            }
        }
    }

    private static GameObject makeFire(Player player, int life) {
        GameObject obj = new GameObject(FIRE_26185, player.tile(), 10, 0);
        TaskManager.submit(new TimedObjectSpawnTask(obj, life + Utils.random(15), () -> GroundItemHandler.createGroundItem(new GroundItem(new Item(ItemIdentifiers.ASHES), obj.tile(), player))));
        return obj;
    }

    private static int getLighter(Player player) {
        for (LightingAnimation lighter : LightingAnimation.values()) {
            if (player.inventory().contains(lighter.item)) {
                return lighter.anim;
            }
        }
        return -1;
    }

}
