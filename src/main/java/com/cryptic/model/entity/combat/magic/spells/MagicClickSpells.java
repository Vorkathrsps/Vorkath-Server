package com.cryptic.model.entity.combat.magic.spells;

import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.combat.method.impl.arceuus.MagicThrall;
import com.cryptic.model.entity.combat.method.impl.arceuus.MeleeThrall;
import com.cryptic.model.entity.combat.method.impl.arceuus.RangeThrall;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.cryptic.model.content.EffectTimer;
import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.content.bountyhunter.BountyHunter;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.timers.TimerKey;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BooleanSupplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * @author Origin | March, 17, 2021, 14:03
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MagicClickSpells {

    @Getter
    public enum MagicSpells {

        TELEPORT_TO_TARGET_NORMAL(new Spell() {
            @Override
            public String name() {
                return "Teleport to target";
            }

            @Override
            public int spellId() {
                return 13674;
            }

            @Override
            public int levelRequired() {
                return 85;
            }

            @Override
            public int baseExperience() {
                return 45;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                boolean spellSack = player.inventory().contains(BLIGHTED_TELEPORT_SPELL_SACK);
                if (spellSack) {
                    return List.of(Item.of(BLIGHTED_TELEPORT_SPELL_SACK, 1));
                }
                return List.of(new Item[]{new Item(LAW_RUNE, 1), new Item(DEATH_RUNE, 3), new Item(CHAOS_RUNE, 3)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                if (player != null) {
                    if (!player.locked()) {
                        Optional<Player> target = BountyHunter.getTargetfor(player);
                        if (target.isPresent()) {
                            boolean targetInMulti = target.get().<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1;
                            if (targetInMulti) {
                                player.confirmDialogue(new String[]{"Are you sure you wish to teleport?", "Your target is inside a multiway area."}, "", "Proceed.", "Nevermind.", () -> {
                                    if (!WildernessArea.inWilderness(target.get().tile())) {
                                        return;
                                    }
                                    Teleports.basicTeleport(player, new Tile(target.get().tile().getX(), target.get().tile().getY() - 1, target.get().tile().level));
                                    itemsRequired(player).forEach(player.inventory()::remove);
                                    player.getClickDelay().reset();
                                });
                                return;
                            }
                            itemsRequired(player).forEach(player.inventory()::remove);
                            Teleports.basicTeleport(player, new Tile(target.get().tile().getX(), target.get().tile().getY() - 1, target.get().tile().level));
                            player.getClickDelay().reset();
                        }
                    }
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.<Boolean>getAttribOr(AttributeKey.BOUNTY_HUNTER_TARGET_TELEPORT_UNLOCKED, false)) {
                    player.message("You have not unlocked this spell yet.");
                    return false;
                }

                if (!WildernessArea.isInWilderness(player)) {
                    player.message("You must be in the Wilderness to use this spell.");
                    return false;
                }

                if (CombatFactory.inCombat(player)) {
                    player.message("You can't cast this spell during combat.");
                    return false;
                }

                Optional<Player> targetFor = BountyHunter.getTargetfor(player);
                if (targetFor.isPresent()) {
                    if (!WildernessArea.inWilderness(targetFor.get().tile())) {
                        player.message("Your target is currently not in the Wilderness.");
                        return false;
                    }

/*                    if(targetFor.get().tile().insideRiskArea()) {
                        player.message("Your target is currently in the riskzone area, you cannot teleport there.");
                        return false;
                    }*/

                    if (targetFor.get().tile().memberCave() && !player.getMemberRights().isSuperMemberOrGreater(player)) {
                        player.message("Your target is currently in the member cave. You cannot teleport there because you are not a member.");
                        return false;
                    }

                    //Prevent players from teleporting to targets that are doing agility obstacles.
                    if (targetFor.get().getMovementQueue().forcedStep()) {
                        player.message("You can't teleport to your target at this time.");
                        return false;
                    }

                    if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                        return false;
                    }

                    if (!player.getClickDelay().elapsed(30000)) {
                        player.message("You have just teleported to your target. There is a 30 second cooldown.");
                        return false;
                    }
                } else {
                    player.message("You currently have no target to teleport to!");
                    return false;
                }
                return super.canCast(player, target, delete);
            }
        }),

        TELEPORT_TO_TARGET_ANCIENT(new Spell() {

            @Override
            public String name() {
                return "Teleport to target";
            }

            @Override
            public int spellId() {
                return 34674;
            }

            @Override
            public int levelRequired() {
                return 85;
            }

            @Override
            public int baseExperience() {
                return 45;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                boolean spellSack = player.inventory().contains(BLIGHTED_TELEPORT_SPELL_SACK);
                if (spellSack) {
                    return List.of(Item.of(BLIGHTED_TELEPORT_SPELL_SACK, 1));
                }
                return List.of(new Item[]{new Item(LAW_RUNE, 1), new Item(DEATH_RUNE, 3), new Item(CHAOS_RUNE, 3)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                // Send message and effect timer to client
                if (player != null) {
                    if (!player.locked()) {
                        Optional<Player> target = BountyHunter.getTargetfor(player);
                        if (target.isPresent()) {
                            boolean targetInMulti = target.get().<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1;
                            if (targetInMulti) {
                                player.confirmDialogue(new String[]{"Are you sure you wish to teleport?", "Your target is inside a multiway area."}, "", "Proceed.", "Nevermind.", () -> {
                                    Teleports.teleportToTarget(player, target.get().tile());
                                    itemsRequired(player).forEach(player.inventory()::remove);
                                    player.getClickDelay().reset();
                                });
                                return;
                            }
                            itemsRequired(player).forEach(player.inventory()::remove);
                            Teleports.teleportToTarget(player, target.get().tile());
                            player.getClickDelay().reset();
                        }
                    }
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.<Boolean>getAttribOr(AttributeKey.BOUNTY_HUNTER_TARGET_TELEPORT_UNLOCKED, false)) {
                    player.message("You have not unlocked this spell yet.");
                    return false;
                }

                if (!WildernessArea.isInWilderness(player)) {
                    player.message("You must be in the Wilderness to use this spell.");
                    return false;
                }

                if (CombatFactory.inCombat(player)) {
                    player.message("You can't cast this spell during combat.");
                    return false;
                }

                Optional<Player> targetFor = BountyHunter.getTargetfor(player);
                if (targetFor.isPresent()) {
                    if (!WildernessArea.inWilderness(targetFor.get().tile())) {
                        player.message("Your target is currently not in the Wilderness.");
                        return false;
                    }

/*                    if(targetFor.get().tile().insideRiskArea()) {
                        player.message("Your target is currently in the riskzone area, you cannot teleport there.");
                        return false;
                    }*/

                    if (targetFor.get().tile().memberCave() && !player.getMemberRights().isRegularMemberOrGreater(player)) {
                        player.message("Your target is currently in the member cave. You cannot teleport there because you are not a member.");
                        return false;
                    }

                    //Prevent players from teleporting to targets that are doing agility obstacles.
                    if (targetFor.get().getMovementQueue().forcedStep()) {
                        player.message("You can't teleport to your target at this time.");
                        return false;
                    }

                    if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                        return false;
                    }

                    if (!player.getClickDelay().elapsed(30000)) {
                        player.message("You have just teleported to your target. There is a 30 second cooldown.");
                        return false;
                    }
                } else {
                    player.message("You currently have no target to teleport to!");
                    return false;
                }
                return super.canCast(player, target, delete);
            }
        }),

        TELEPORT_TO_TARGET_LUNAR(new Spell() {

            @Override
            public String name() {
                return "Teleport to target";
            }

            @Override
            public int spellId() {
                return 30234;
            }

            @Override
            public int levelRequired() {
                return 85;
            }

            @Override
            public int baseExperience() {
                return 45;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                boolean spellSack = player.inventory().contains(BLIGHTED_TELEPORT_SPELL_SACK);
                if (spellSack) {
                    return List.of(Item.of(BLIGHTED_TELEPORT_SPELL_SACK, 1));
                }
                return List.of(new Item[]{new Item(LAW_RUNE, 1), new Item(DEATH_RUNE, 3), new Item(CHAOS_RUNE, 3)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                // Send message and effect timer to client
                if (player != null) {
                    if (!player.locked()) {
                        Optional<Player> target = BountyHunter.getTargetfor(player);
                        if (target.isPresent()) {
                            boolean targetInMulti = target.get().<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 1;
                            if (targetInMulti) {
                                player.confirmDialogue(new String[]{"Are you sure you wish to teleport?", "Your target is inside a multiway area."}, "", "Proceed.", "Nevermind.", () -> {
                                    Teleports.teleportToTarget(player, target.get().tile());
                                    itemsRequired(player).forEach(player.inventory()::remove);
                                    player.getClickDelay().reset();
                                });
                                return;
                            }
                            itemsRequired(player).forEach(player.inventory()::remove);
                            Teleports.teleportToTarget(player, target.get().tile());
                            player.getClickDelay().reset();
                        }
                    }
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.<Boolean>getAttribOr(AttributeKey.BOUNTY_HUNTER_TARGET_TELEPORT_UNLOCKED, false)) {
                    player.message("You have not unlocked this spell yet.");
                    return false;
                }

                if (!WildernessArea.isInWilderness(player)) {
                    player.message("You must be in the Wilderness to use this spell.");
                    return false;
                }

                if (CombatFactory.inCombat(player)) {
                    player.message("You can't cast this spell during combat.");
                    return false;
                }

                Optional<Player> targetFor = BountyHunter.getTargetfor(player);
                if (targetFor.isPresent()) {
                    if (!WildernessArea.inWilderness(targetFor.get().tile())) {
                        player.message("Your target is currently not in the Wilderness.");
                        return false;
                    }

                    if (targetFor.get().tile().insideRiskArea()) {
                        player.message("Your target is currently in the riskzone area, you cannot teleport there.");
                        return false;
                    }

                    if (targetFor.get().tile().memberCave() && !player.getMemberRights().isSuperMemberOrGreater(player)) {
                        player.message("Your target is currently in the member cave. You cannot teleport there because you are not a member.");
                        return false;
                    }

                    //Prevent players from teleporting to targets that are doing agility obstacles.
                    if (targetFor.get().getMovementQueue().forcedStep()) {
                        player.message("You can't teleport to your target at this time.");
                        return false;
                    }

                    if (!Teleports.canTeleport(player, true, TeleportType.GENERIC)) {
                        return false;
                    }

                    if (!player.getClickDelay().elapsed(30000)) {
                        player.message("You have just teleported to your target. There is a 30 second cooldown.");
                        return false;
                    }
                } else {
                    player.message("You currently have no target to teleport to!");
                    return false;
                }
                return super.canCast(player, target, delete);
            }
        }),

        CHARGE(new Spell() {

            @Override
            public String name() {
                return "Charge";
            }

            @Override
            public int spellId() {
                return 1193;
            }

            @Override
            public int levelRequired() {
                return 80;
            }

            @Override
            public int baseExperience() {
                return 180;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                Item shield = player.getEquipment().get(EquipSlot.SHIELD);
                if (shield != null && shield.getId() == TOME_OF_FIRE) {
                    return List.of(new Item[]{new Item(AIR_RUNE, 3), new Item(BLOOD_RUNE, 3)});
                }
                return List.of(new Item[]{new Item(AIR_RUNE, 3), new Item(FIRE_RUNE, 3), new Item(BLOOD_RUNE, 3)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                if (player != null) {
                    player.getTimers().register(TimerKey.CHARGE_SPELL, 200);
                    player.message("You feel charged with magic power.");
                    player.animate(811);
                    player.getSkills().addXp(Skills.MAGIC, this.baseExperience());
                    player.graphic(111, GraphicHeight.HIGH, 3);
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (player.getTimers().has(TimerKey.CHARGE_SPELL)) {
                    player.message("You can't recast that yet, your current Charge is too strong.");
                    return false;
                }
                return super.canCast(player, target, delete);
            }
        }),

        SUMMON_MELEE_THRALL(new Spell() {
            @Override
            public String name() {
                return "Melee Thrall";
            }

            @Override
            public int spellId() {
                return 40338;
            }

            @Override
            public int levelRequired() {
                return 76;
            }

            @Override
            public int baseExperience() {
                return 88;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return List.of();
                }
                return List.of(new Item[]{new Item(FIRE_RUNE, 10), new Item(BLOOD_RUNE, 5), new Item(COSMIC_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                if (!(cast instanceof Player player)) {
                    return;
                }

                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return;
                }

                player.getTimers().register(TimerKey.THRALL_RESPAWN_TIMER, 10);

                List<Tile> tileList = player.tile().area(2, f -> {
                    if (!f.equals(player.tile()) && (player.getCombat().getTarget() == null || !f.equals(player.getCombat().getTarget().tile()))) {
                        f.add(f.getX(), f.getY());
                        return true;
                    }
                    return false;
                });

                Tile selectedTile = null;

                if (!tileList.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(tileList.size());
                    selectedTile = tileList.get(randomIndex);
                }

                for (var t : player.getActiveThrall()) {
                    if (t != null) {
                        t.remove();
                    }
                }

                player.getActiveThrall().clear();

                if (selectedTile != null) {
                    MeleeThrall thrall = new MeleeThrall(10886, selectedTile, true);

                    player.getActiveThrall().add(thrall);
                    player.getCombat().delayAttack(4);
                    player.animate(8973);
                    player.graphic(1875, GraphicHeight.LOW, 0);

                    thrall.setPositionToFace(player.tile());

                    BooleanSupplier inCombatSupplier = () -> CombatFactory.inCombat(player);

                    thrall.waitUntil(inCombatSupplier, () -> {
                        AtomicBoolean npcRemoved = new AtomicBoolean(false);
                        AtomicBoolean canAttack = new AtomicBoolean(false);

                        canAttack.getAndSet(true);

                        Chain.bound(thrall).repeatingTask(4, combatTick -> {
                            if (npcRemoved.get() || player.getActiveThrall().isEmpty()) {
                                combatTick.stop();
                                return;
                            }

                            if (player.getCombat().inCombat()) {
                                canAttack.getAndSet(true);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (!canAttack.get()) {
                                return;
                            }

                            if (player.getCombat().inCombat() && player.getCombat().getTarget().isNpc() || thrall.getCombat().getTarget() == null) {
                                thrall.getCombat().setTarget(player.getCombat().getTarget());
                            }

                            if (player.getCombat().getTarget().isNpc()) {
                                thrall.sendThrallAttack(thrall, player);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (player.dead()) {
                                combatTick.stop();
                                npcRemoved.getAndSet(true);
                                canAttack.getAndSet(false);
                            }
                        });
                    });
                }
            }
        }),

        SUMMON_RANGING_THRALL(new Spell() {
            @Override
            public String name() {
                return "Range Thrall";
            }

            @Override
            public int spellId() {
                return 40337;
            }

            @Override
            public int levelRequired() {
                return 76;
            }

            @Override
            public int baseExperience() {
                return 88;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return List.of();
                }
                return List.of(new Item[]{new Item(FIRE_RUNE, 10), new Item(BLOOD_RUNE, 5), new Item(COSMIC_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                if (!(cast instanceof Player player)) {
                    return;
                }

                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return;
                }

                player.getTimers().register(TimerKey.THRALL_RESPAWN_TIMER, 10);

                List<Tile> tileList = player.tile().area(2, f -> {
                    if (!f.equals(player.tile()) && (player.getCombat().getTarget() == null || !f.equals(player.getCombat().getTarget().tile()))) {
                        f.add(f.getX(), f.getY());
                        return true;
                    }
                    return false;
                });

                Tile selectedTile = null;

                if (!tileList.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(tileList.size());
                    selectedTile = tileList.get(randomIndex);
                }

                for (var t : player.getActiveThrall()) {
                    if (t != null) {
                        t.remove();
                    }
                }

                player.getActiveThrall().clear();

                if (selectedTile != null) {
                    RangeThrall thrall = new RangeThrall(10883, selectedTile, true);

                    player.getActiveThrall().add(thrall);
                    player.getCombat().delayAttack(4);
                    player.animate(8973);
                    player.graphic(1874, GraphicHeight.LOW, 0);

                    thrall.setPositionToFace(player.tile());

                    BooleanSupplier inCombatSupplier = () -> CombatFactory.inCombat(player);

                    thrall.waitUntil(inCombatSupplier, () -> {
                        AtomicBoolean npcRemoved = new AtomicBoolean(false);
                        AtomicBoolean canAttack = new AtomicBoolean(false);

                        canAttack.getAndSet(true);

                        Chain.bound(thrall).repeatingTask(4, combatTick -> {
                            if (npcRemoved.get() || player.getActiveThrall().isEmpty()) {
                                combatTick.stop();
                                return;
                            }

                            if (player.getCombat().inCombat()) {
                                canAttack.getAndSet(true);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (!canAttack.get()) {
                                return;
                            }

                            if (player.getCombat().inCombat() && player.getCombat().getTarget().isNpc() || thrall.getCombat().getTarget() == null) {
                                thrall.getCombat().setTarget(player.getCombat().getTarget());
                            }

                            if (player.getCombat().getTarget().isNpc()) {
                                thrall.sendThrallAttack(thrall, player);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (player.dead()) {
                                combatTick.stop();
                                npcRemoved.getAndSet(true);
                                canAttack.getAndSet(false);
                            }
                        });
                    });
                }
            }
        }),

        SUMMON_MAGIC_THRALL(new Spell() {
            @Override
            public String name() {
                return "Magic Thrall";
            }

            @Override
            public int spellId() {
                return 40336;
            }

            @Override
            public int levelRequired() {
                return 76;
            }

            @Override
            public int baseExperience() {
                return 88;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return List.of();
                }
                return List.of(new Item[]{new Item(FIRE_RUNE, 10), new Item(BLOOD_RUNE, 5), new Item(COSMIC_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                if (!(cast instanceof Player player)) {
                    return;
                }

                if (player.getTimers().left(TimerKey.THRALL_RESPAWN_TIMER) != 0) {
                    return;
                }

                player.getTimers().register(TimerKey.THRALL_RESPAWN_TIMER, 10);

                List<Tile> tileList = player.tile().area(2, f -> {
                    if (!f.equals(player.tile()) && (player.getCombat().getTarget() == null || !f.equals(player.getCombat().getTarget().tile()))) {
                        f.add(f.getX(), f.getY());
                        return true;
                    }
                    return false;
                });

                Tile selectedTile = null;

                if (!tileList.isEmpty()) {
                    Random random = new Random();
                    int randomIndex = random.nextInt(tileList.size());
                    selectedTile = tileList.get(randomIndex);
                }

                for (var t : player.getActiveThrall()) {
                    if (t != null) {
                        t.remove();
                    }
                }

                player.getActiveThrall().clear();

                if (selectedTile != null) {
                    MagicThrall thrall = new MagicThrall(10880, selectedTile, true);

                    player.getActiveThrall().add(thrall);
                    player.getCombat().delayAttack(4);
                    player.animate(8973);
                    player.graphic(1873, GraphicHeight.LOW, 0);

                    thrall.setPositionToFace(player.tile());

                    BooleanSupplier inCombatSupplier = () -> CombatFactory.inCombat(player);

                    thrall.waitUntil(inCombatSupplier, () -> {
                        AtomicBoolean npcRemoved = new AtomicBoolean(false);
                        AtomicBoolean canAttack = new AtomicBoolean(false);

                        canAttack.getAndSet(true);

                        Chain.bound(thrall).repeatingTask(4, combatTick -> {
                            if (npcRemoved.get() || player.getActiveThrall().isEmpty()) {
                                combatTick.stop();
                                return;
                            }

                            if (player.getCombat().inCombat()) {
                                canAttack.getAndSet(true);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (!canAttack.get()) {
                                return;
                            }

                            if (player.getCombat().inCombat() && player.getCombat().getTarget().isNpc() || thrall.getCombat().getTarget() == null) {
                                thrall.getCombat().setTarget(player.getCombat().getTarget());
                            }

                            if (player.getCombat().getTarget().isNpc()) {
                                thrall.sendThrallAttack(thrall, player);
                            } else {
                                canAttack.getAndSet(false);
                                return;
                            }

                            if (player.dead()) {
                                combatTick.stop();
                                npcRemoved.getAndSet(true);
                                canAttack.getAndSet(false);
                            }
                        });
                    });
                }
            }
        }),

        BONES_TO_BANANAS(new Spell() {

            @Override
            public String name() {
                return "Bones to bananas";
            }

            @Override
            public int spellId() {
                return 1159;
            }

            @Override
            public int levelRequired() {
                return 15;
            }

            @Override
            public int baseExperience() {
                return 25;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(NATURE_RUNE),
                    Item.of(WATER_RUNE, 2),
                    Item.of(EARTH_RUNE, 2)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                if (player != null) {
                    int index = 0;
                    for (Item invItem : player.inventory().getValidItems()) {
                        if (invItem.getId() == BONES) {
                            player.inventory().remove(BONES, 1);
                            player.inventory().add(BANANA, 1);
                            index++;
                        }
                    }
                    player.graphic(141, GraphicHeight.HIGH, 0);
                    player.animate(722);
                    player.getSkills().addXp(Skills.MAGIC, this.baseExperience() * index);
                    player.getClickDelay().reset();
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(500)) {
                    return false;
                }
                if (!player.inventory().contains(BONES)) {
                    player.message("You do not have any bones in your inventory.");
                    return false;
                }
                return super.canCast(player, target, delete);
            }
        }),

        ENCHANT_SAPPHIRE(new Spell() {

            @Override
            public String name() {
                return "Lvl-1 Enchant";
            }

            @Override
            public int spellId() {
                return 1155;
            }

            @Override
            public int levelRequired() {
                return 7;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(WATER_RUNE, 1),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_EMERALD(new Spell() {

            @Override
            public String name() {
                return "Lvl-2 Enchant";
            }

            @Override
            public int spellId() {
                return 1165;
            }

            @Override
            public int levelRequired() {
                return 27;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(AIR_RUNE, 3),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_RUBY_TOPAZ(new Spell() {

            @Override
            public String name() {
                return "Lvl-3 Enchant";
            }

            @Override
            public int spellId() {
                return 1176;
            }

            @Override
            public int levelRequired() {
                return 49;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(FIRE_RUNE, 5),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_DIAMOND(new Spell() {

            @Override
            public String name() {
                return "Lvl-4 Enchant";
            }

            @Override
            public int spellId() {
                return 1180;
            }

            @Override
            public int levelRequired() {
                return 57;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(EARTH_RUNE, 10),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_DRAGONSTONE(new Spell() {

            @Override
            public String name() {
                return "Lvl-5 Enchant";
            }

            @Override
            public int spellId() {
                return 1187;
            }

            @Override
            public int levelRequired() {
                return 68;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(WATER_RUNE, 15),
                    Item.of(EARTH_RUNE, 15),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_ONYX(new Spell() {

            @Override
            public String name() {
                return "Lvl-6 Enchant";
            }

            @Override
            public int spellId() {
                return 6003;
            }

            @Override
            public int levelRequired() {
                return 86;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(FIRE_RUNE, 20),
                    Item.of(EARTH_RUNE, 20),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        ENCHANT_ZENYTE(new Spell() {

            @Override
            public String name() {
                return "Lvl-7 Enchant";
            }

            @Override
            public int spellId() {
                return 22674;
            }

            @Override
            public int levelRequired() {
                return 93;
            }

            @Override
            public int baseExperience() {
                return 17;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(BLOOD_RUNE, 20),
                    Item.of(SOUL_RUNE, 20),
                    Item.of(COSMIC_RUNE, 1)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {

            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        LOW_ALCHEMY(new Spell() {

            @Override
            public String name() {
                return "Low alchemy";
            }

            @Override
            public int spellId() {
                return 1162;
            }

            @Override
            public int levelRequired() {
                return 21;
            }

            @Override
            public int baseExperience() {
                return 31;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                Item shield = player.getEquipment().get(EquipSlot.SHIELD);
                if (shield != null && shield.getId() == TOME_OF_FIRE) {
                    return List.of(new Item[]{new Item(NATURE_RUNE, 1)});
                }
                boolean hasExplorerRing = player.getInventory().containsAny(AchievementsManager.EXPLORER_RINGS) || player.getEquipment().containsAny(AchievementsManager.EXPLORER_RINGS);
                return hasExplorerRing ? List.of() : List.of(new Item[]{new Item(FIRE_RUNE, 3), new Item(NATURE_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                if (player != null) {
                    if (player.isPerformingAction()) return;
                    player.setPerformingAction(true);
                    player.getCombat().reset();
                    player.graphic(-1);
                    player.action.clearNonWalkableActions();
                    player.animate(713);
                    player.graphic(113, GraphicHeight.HIGH, 15);
                    player.getSkills().addXp(Skills.MAGIC, this.baseExperience());
                    player.getClickDelay().reset();
                    Chain.noCtx().runFn(1, () -> player.getPacketSender().sendTab(6)).then(1, player::clearPerformingAction);
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(1800)) {
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        TELEKINETIC_GRAB(new Spell() {

            @Override
            public String name() {
                return "Telekinetic grab";
            }

            @Override
            public int spellId() {
                return 1168;
            }

            @Override
            public int levelRequired() {
                return 33;
            }

            @Override
            public int baseExperience() {
                return 43;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(LAW_RUNE),
                    Item.of(AIR_RUNE)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
            }

        }),

        SUPERHEAT_ITEM(new Spell() {

            @Override
            public String name() {
                return "Superheat item";
            }

            @Override
            public int spellId() {
                return 1173;
            }

            @Override
            public int levelRequired() {
                return 43;
            }

            @Override
            public int baseExperience() {
                return 53;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                Item shield = player.getEquipment().get(EquipSlot.SHIELD);
                if (shield != null && player.getEquipment().hasAt(EquipSlot.SHIELD, TOME_OF_FIRE)) {
                    return List.of(new Item[]{new Item(NATURE_RUNE, 1)});
                }
                return List.of(new Item[]{new Item(FIRE_RUNE, 4), new Item(NATURE_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
            }

        }),

        HIGH_ALCHEMY(new Spell() {

            @Override
            public String name() {
                return "High alchemy";
            }

            @Override
            public int spellId() {
                return 1178;
            }

            @Override
            public int levelRequired() {
                return 55;
            }

            @Override
            public int baseExperience() {
                return 65;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                Item shield = player.getEquipment().get(EquipSlot.SHIELD);
                if (shield != null && shield.getId() == TOME_OF_FIRE) {
                    return List.of(new Item[]{new Item(NATURE_RUNE, 1)});
                }
                boolean hasExplorerRing = player.getInventory().contains(EXPLORERS_RING_4) || player.getEquipment().contains(EXPLORERS_RING_4);
                return hasExplorerRing ? List.of() : List.of(new Item[]{new Item(FIRE_RUNE, 5), new Item(NATURE_RUNE, 1)});
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                if (cast instanceof Player player) {
                    if (player.isPerformingAction()) return;
                    player.setPerformingAction(true);
                    player.resetAnimation();
                    player.graphic(-1);
                    player.animate(713);
                    player.graphic(113, GraphicHeight.HIGH, 15);
                    player.getCombat().reset();
                    player.action.clearNonWalkableActions();
                    player.getSkills().addXp(Skills.MAGIC, this.baseExperience());
                    Chain.noCtx().runFn(1, () -> player.getPacketSender().sendTab(6)).then(1, player::clearPerformingAction);
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (player.getClickDelay().elapsed(1400)) {
                    return super.canCast(player, target, delete);
                }
                return false;
            }

        }),

        BONES_TO_PEACHES(new Spell() {

            @Override
            public String name() {
                return "Bones to peaches";
            }

            @Override
            public int spellId() {
                return 15877;
            }

            @Override
            public int levelRequired() {
                return 60;
            }

            @Override
            public int baseExperience() {
                return 65;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(NATURE_RUNE, 2),
                    Item.of(WATER_RUNE, 4),
                    Item.of(EARTH_RUNE, 4)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                final Player player = cast.isPlayer() ? (Player) cast : null;

                if (player != null) {
                    int index = 0;
                    for (Item invItem : player.inventory().getValidItems()) {
                        if (invItem.getId() == BONES) {
                            player.inventory().remove(BONES, 1);
                            player.inventory().add(PEACH, 1);
                            index++;
                        }
                    }
                    player.graphic(141, GraphicHeight.HIGH, 0);
                    player.animate(722);
                    player.getSkills().addXp(Skills.MAGIC, this.baseExperience() * index);
                    player.getClickDelay().reset();
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                if (!player.getClickDelay().elapsed(500)) {
                    return false;
                }
                if (!player.inventory().contains(BONES)) {
                    player.message("You do not have any bones in your inventory.");
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        }),

        BAKE_PIE(new Spell() {

            @Override
            public String name() {
                return "Bake pie";
            }

            @Override
            public int spellId() {
                return 30017;
            }

            @Override
            public int levelRequired() {
                return 65;
            }

            @Override
            public int baseExperience() {
                return 60;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(ASTRAL_RUNE),
                    Item.of(FIRE_RUNE, 5),
                    Item.of(WATER_RUNE, 4)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
            }
        }),

        CURE_PLANT(new Spell() {

            @Override
            public String name() {
                return "Cure plant";
            }

            @Override
            public int spellId() {
                return 30025;
            }

            @Override
            public int levelRequired() {
                return 66;
            }

            @Override
            public int baseExperience() {
                return 60;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(ASTRAL_RUNE, 1),
                    Item.of(EARTH_RUNE, 8)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public boolean deleteRunes() {
                return true;
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                return super.canCast(player, target, delete);
            }

        }),

        VENGEANCE_OTHER(new Spell() {

            @Override
            public String name() {
                return "Vengeance other";
            }

            @Override
            public int spellId() {
                return 30298;
            }

            @Override
            public int levelRequired() {
                return 93;
            }

            @Override
            public int baseExperience() {
                return 108;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                return List.of(
                    Item.of(ASTRAL_RUNE, 3),
                    Item.of(EARTH_RUNE, 10),
                    Item.of(DEATH_RUNE, 2)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
            }

            public boolean canCastOn(Player player, Player target) {
                return super.canCastOn(player, target);
            }
        }),

        VENGEANCE(new Spell() {

            @Override
            public String name() {
                return "Vengeance";
            }

            @Override
            public int spellId() {
                return 30306;
            }

            @Override
            public int levelRequired() {
                return 94;
            }

            @Override
            public int baseExperience() {
                return 112;
            }

            @Override
            public List<Item> itemsRequired(Player player) {
                boolean spellSack = player.inventory().contains(BLIGHTED_VENGEANCE_SACK);
                if (spellSack) {
                    return List.of(Item.of(BLIGHTED_VENGEANCE_SACK, 1));
                }
                return List.of(
                    Item.of(ASTRAL_RUNE, 4),
                    Item.of(EARTH_RUNE, 10),
                    Item.of(DEATH_RUNE, 2)
                );
            }

            @Override
            public List<Item> equipmentRequired(Player player) {
                return List.of();
            }

            @Override
            public boolean deleteRunes() {
                return false;
            }

            @Override
            public void cast(Entity cast, Entity castOn) {
                if (cast instanceof Player player) {
                    if (!player.locked()) {
                        if (!player.getTimers().has(TimerKey.VENGEANCE_COOLDOWN)) {
                            player.getTimers().register(TimerKey.VENGEANCE_COOLDOWN, 50);
                            player.putAttrib(AttributeKey.VENGEANCE_ACTIVE, true);
                            itemsRequired(player).forEach(player.inventory()::remove);
                            player.animate(new Animation(8317, Priority.HIGH));
                            player.performGraphic(new Graphic(726, GraphicHeight.HIGH, 0, com.cryptic.model.entity.masks.impl.graphics.Priority.HIGH));
                            player.getSkills().addXp(Skills.MAGIC, 112);
                            player.getPacketSender().sendEffectTimer(30, EffectTimer.VENGEANCE).sendMessage("You now have Vengeance's effect.");
                        } else {
                            player.message("You can only cast vengeance spells every 30 seconds.");
                        }
                    }
                }
            }

            @Override
            public boolean canCast(Player player, Entity target, boolean delete) {
                boolean hasVengeance = player.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);
                if (player.getDueling().inDuel()) {
                    player.message("You cannot cast vengeance during a duel!");
                    return false;
                }
                if (player.getSkills().level(Skills.DEFENCE) < 40) {
                    player.message("You need 40 Defence to use Vengence.");
                    return false;
                } else if (player.getSkills().level(Skills.MAGIC) < 94) {
                    player.message("Your Magic level is not high enough to use this spell.");
                    return false;
                } else if (hasVengeance) {
                    player.message("You already have Vengeance casted.");
                    return false;
                } else if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MAGIC.ordinal()]) {
                    player.message("Magic is disabled for this duel.");
                    return false;
                }
                return super.canCast(player, target, delete);
            }

        });

        public static final ImmutableSet<MagicSpells> VALUES = Sets.immutableEnumSet(EnumSet.allOf(MagicSpells.class));

        MagicSpells(Spell spell) {
            this.spell = checkNotNull(spell, "spell");
        }

        private final Spell spell;

        public static Optional<MagicSpells> find(final int buttonId) {
            return VALUES.stream().filter(spell -> spell.getSpell().spellId() == buttonId).findFirst();
        }

    }

    /**
     * Handles clickable spellbook spells. Spells cast on other entities will be handles via
     * a different packet.
     *
     * @param player The player
     * @param button The button
     */
    public static boolean handleSpell(Player player, int button) {
        final Optional<MagicSpells> magicSpell = MagicSpells.find(button);

        if (magicSpell.isEmpty()) {
            return false;
        }

        final Spell spell = magicSpell.get().getSpell();

        switch (magicSpell.get()) {
            case TELEPORT_TO_TARGET_NORMAL, TELEPORT_TO_TARGET_ANCIENT, TELEPORT_TO_TARGET_LUNAR, CHARGE, VENGEANCE, SUMMON_MAGIC_THRALL, SUMMON_MELEE_THRALL, SUMMON_RANGING_THRALL -> {
                if (!spell.canCast(player, null, spell.deleteRunes())) {
                    return false;
                }
                player.sendPrivateSound(1651, 0);
                spell.cast(player, null);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public static Optional<MagicSpells> getMagicSpells(int id) {
        return Arrays.stream(MagicSpells.values()).filter(s -> s != null && s.getSpell().spellId() == id).findFirst();
    }

    public static Spell getMagicSpell(int spellId) {
        return getMagicSpells(spellId).map(MagicSpells::getSpell).orElse(null);
    }

    public static void handleSpellOnPlayer(Player player, Player attacked, Spell spell) {
        if (!spell.canCastOn(player, attacked)) {
            return;
        }

        if (!spell.canCast(player, attacked, false)) {
            return;
        }

        spell.deleteRequiredRunes(player, new HashMap<>());
        spell.cast(player, attacked);
    }

    /**
     * Handles the spells on objects.
     *
     * @param player
     * @param object
     * @param tile
     * @param spell_id
     * @return
     */
    public static boolean handleSpellOnObject(Player player, GameObject object, Tile tile, int spell_id) {
        Optional<MagicSpells> spell = getMagicSpells(spell_id);

        if (spell.isEmpty()) {
            return false;
        }

        if (!spell.get().getSpell().canCast(player, null, true)) {
            return false;
        }
        return true;
    }

}
