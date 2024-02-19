package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.core.task.Task;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.totems.NorthEastTotemCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.totems.NorthWestTotemCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.totems.SouthEastTotemCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.totems.SouthWestTotemCombat;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance.NightmareInstance;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.state.AshihamaPhase;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.state.AshihamaState;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import static com.cryptic.model.entity.attributes.AttributeKey.NIGHTMARE_CURSE;
import static com.cryptic.model.entity.attributes.AttributeKey.NO_MOVEMENT_NIGHTMARE;

public class Nightmare extends CommonCombatMethod { //TODO increase max hit based on wrong protection prayer
    AtomicInteger cursedCount = new AtomicInteger();
    AtomicBoolean cursed = new AtomicBoolean(false);
    AtomicInteger sleepWalkerDamage = new AtomicInteger(5);
    List<Tile> tiles = new ArrayList<>();
    @Getter
    List<NPC> sleepWalkers = new ArrayList<>();
    int attackCount = 0;
    @Getter
    @Setter
    int nightmareHitpoints = 2400;
    @Getter
    @Setter
    boolean spawningHusks = false;
    @Getter
    @Setter
    AshihamaState ashihamaState = AshihamaState.SHIELD;
    @Getter
    @Setter
    AshihamaPhase ashihamaPhase = AshihamaPhase.ONE;
    CommonCombatMethod[] totemCombatMethods = {new SouthWestTotemCombat(), new NorthWestTotemCombat(), new SouthEastTotemCombat(), new NorthEastTotemCombat()};
    public static Area[] QUADS = new Area[]{new Area(3863, 9951, 3872, 9961, 3), new Area(3872, 9951, 3881, 9961, 3), new Area(3863, 9941, 3872, 9951, 3), new Area(3872, 9941, 3881, 9951, 3)};
    private static final int ASHIHAMA_TOTEM_INTERFACE_ID = 75000;
    private static final int TOTEM_TRANSFORMATION_OFFSET = 1;
    private static final int[] UNCHARGED_TOTEMS = {9434, 9440, 9437, 9443};
    private static final int[] CHARGED_TOTEMS = {9436, 9442, 9439, 9445};
    private static final int[] PROGRESS_BARS = {75004, 75006, 75008, 75010};
    @Override
    public void process(Entity entity, Entity target) {
        if (!(entity instanceof NPC nightmare) || !(target instanceof Player player)) {
            return;
        }

        if (player.getInstancedArea() == null) {
            return;
        }

        for (var t : player.getNightmareInstance().getPlayers()) {
            if (t == player) {
                if (!player.getNightmareInstance().getHusks().isEmpty()) {
                    BooleanSupplier isEmpty = () -> t.getNightmareInstance().getHusks().isEmpty();
                    t.waitUntil(isEmpty, () -> t.clearAttrib(NO_MOVEMENT_NIGHTMARE));
                }
            }
        }


        AshihamaState currentState = this.getAshihamaState();
        AtomicInteger delay = new AtomicInteger();
        BooleanSupplier middleTile = () -> nightmare.tile().equals(3870, 9949);

        var totems = player.getNightmareInstance().getTotems();

        if (currentState == AshihamaState.TOTEM && totems.isEmpty()) {
            this.setAshihamaState(AshihamaState.NO_SHIELD);
            this.setAshihamaPhase(AshihamaPhase.TWO);

            player.getInterfaceManager().removeOverlay();

            nightmare.getCombat().setTarget(null);

            Arrays.stream(CHARGED_TOTEMS).forEach(id -> player.getInstancedArea().getNpcs().stream().filter(totem -> totem.id() == id).forEach(totem -> {
                int tileDist = totem.tile().getChevDistance(nightmare.tile());
                int duration = (30 + 62 + (10 * tileDist));
                Projectile p = new Projectile(totem, nightmare, 1768, 30, duration, 137, 76, 2, 1, 10);
                delay.set(totem.executeProjectile(p));

                Chain.noCtx().runFn(p.getSpeed() / 30 + 1, () -> {
                    totem.transmog(id - 2, false);
                    totem.setCombatInfo(World.getWorld().combatInfo(id - 2));
                    totem.setHitpoints(totem.maxHp());
                    totem.noRetaliation(true);
                    totem.getCombat().setAutoRetaliate(false);
                }).then(1, () -> {
                    nightmare.hit(totem, 800, delay.get(), CombatType.MAGIC);
                    nightmare.graphic(1769, GraphicHeight.HIGH, p.getSpeed());
                });
            }));

            Chain.noCtx().runFn(1, () -> {
                nightmare.lock();
                nightmare.animate(8607);
            }).then(1, () -> {
                nightmare.hidden(true);
                nightmare.teleport(3870, 9949, player.getInstancedArea().getzLevel() + 3);
                Direction direction = Direction.SOUTH;
                nightmare.setPositionToFace(new Tile(3870, 9948).center(5).tileToDir(direction));
            }).waitUntil(1, middleTile, () -> {
                nightmare.transmog(9431, false);
                nightmare.setCombatInfo(World.getWorld().combatInfo(9431));
                nightmare.setHitpoints(nightmare.maxHp());
                nightmare.setCombatMethod(null);
                nightmare.hidden(false);
                nightmare.animate(8610);
            }).then(1, () -> {
                spawnSleepWalker(nightmare, player);
            }).waitUntil(1, sleepWalkers::isEmpty, () -> {
                this.setAshihamaState(AshihamaState.SHIELD);
                nightmare.animate(8604);
                Hit hit = Hit.builder(nightmare, target, sleepWalkerDamage.get(), 2, CombatType.MAGIC).setAccurate(true);
                hit.submit();
                player.graphic(1782, GraphicHeight.LOW, 4);
                sleepWalkerDamage.getAndSet(0);
            }).then(9, () -> {
                nightmare.unlock();
                nightmare.transmog(9425, false);
                nightmare.setCombatInfo(World.getWorld().combatInfo(9425));
                nightmare.setHitpoints(nightmare.maxHp());
                nightmare.setCombatMethod(this);
                nightmare.getCombat().setTarget(player);
            });
        }
    }

    @Override
    public void preDefend(Hit hit) {
        NPC nightmare = (NPC) entity;
        Player player = (Player) target;

        if (player == null || nightmare == null) {
            return;
        }

        if (this.getAshihamaState().equals(AshihamaState.SHIELD)) {
            hit.setHitMark(HitMark.SHIELD_HITSPLAT);
        } else {
            hit.setHitMark(HitMark.REGULAR);
        }

        if (this.getAshihamaState().equals(AshihamaState.TOTEM)) {
            hit.block();
        }

        if (attackCount >= Utils.random(4, 6)) {
            var randomTarget = Utils.randomElement(player.getNightmareInstance().getPlayers());
            nightmare.face(randomTarget);
            nightmare.getCombat().setTarget(randomTarget);
        }

    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        var nightmare = (NPC) entity;

        if (!withinDistance(8)) {
            return false;
        }

        if (nightmare == null || target == null) {
            return false;
        }

        if (cursed != null && cursed.get()) {
            cursedCount.getAndIncrement();
        }

        attackCount++;

        if (attackCount >= Utils.random(7, 12)) {
            attackCount = 0;
            sequenceSpecialAttack(nightmare, target);
        } else {
            sequenceNormalCombat(nightmare, target);
        }

        return true;
    }

    void sequenceSpecialAttack(NPC nightmare, Entity target) {
        Random rand = new Random();
        int randomValue = rand.nextInt(4);
        Player player = (Player) target;
        var husks = player.getNightmareInstance().getHusks();

        nightmare.face(target);

        switch (randomValue) {
            case 1 -> graspingClaws(nightmare, player);
            case 2 -> {
                if (!this.getAshihamaPhase().equals(AshihamaPhase.ONE)) {
                    return;
                }
                if (husks.isEmpty()) {
                    spawnHusk(nightmare, player);
                }
            }
            case 3 -> {
                if (!this.getAshihamaPhase().equals(AshihamaPhase.TWO)) {
                    return;
                }
                curse();
            }
        }
    }

    void sequenceNormalCombat(NPC nightmare, Entity target) {
        Random rand = new Random();
        int randomValue = rand.nextInt(3);

        nightmare.face(target);

        switch (randomValue) {
            case 0 -> meleeAttack(nightmare, (Player) target);
            case 1 -> rangeAttack(nightmare, target);
            case 2 -> magicAttack(nightmare, target);
            default -> {
            }
        }
    }

    private void curse() {
        Chain.noCtx().cancelWhen(() -> cursedCount.get() >= 5).repeatingTask(1, curseCount -> {
            cursedCount.getAndIncrement();
            cursed.getAndSet(true);

            if (cursedCount.get() == 5) {
                target.clearAttrib(AttributeKey.NIGHTMARE_CURSE);
                if (!target.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                    int hintId = Prayers.getPrayerHeadIcon(target);
                    for (var prayerIndex : Prayers.PROTECTION_PRAYERS) {
                        if (target.getPrayerActive()[prayerIndex]) {
                            target.setHeadHint(hintId);
                        }
                    }
                }
            }
        });

        Task.runOnceTask(1, apply -> {
            if (cursed.get()) {
                target.putAttrib(NIGHTMARE_CURSE, true);
            }

            if (target.hasAttrib(AttributeKey.NIGHTMARE_CURSE)) {
                int hintId = Prayers.getPrayerHeadIcon(target);
                for (var prayerIndex : Prayers.PROTECTION_PRAYERS) {
                    if (target.getPrayerActive()[prayerIndex]) {
                        target.setHeadHint(hintId);
                    }
                }
            }
        });
    }

    private void graspingClaws(NPC nightmare, Entity target) {
        Player player = (Player) target;

        if (nightmare == null || target == null) {
            return;
        }

        Set<Tile> usedTiles = new HashSet<>();

        nightmare.animate(8598);

        int numTiles = 50;

        NightmareInstance.room().transformArea(0, 0, 0, 0, player.getNightmareInstance().getzLevel() + 3).forEachPos(pos ->
            Arrays.stream(QUADS).forEach(t -> {
                if (pos.inArea(t) && World.getWorld().clipAt(pos) == 0) {
                    tiles.add(pos);
                }
            }));

        Collections.shuffle(tiles);

        numTiles = Math.min(numTiles, tiles.size());

        for (var member : player.getNightmareInstance().getPlayers()) {

            if (member == null) {
                return;
            }

            if (!member.getNightmareInstance().getHusks().isEmpty()) {
                tiles.remove(player.tile());
            }

            for (int i = 0; i < numTiles; i++) {
                Tile t = tiles.get(i);
                if (!usedTiles.contains(t)) {
                    World.getWorld().tileGraphic(1767, t, 0, 20);
                    usedTiles.add(t);
                }
            }

            Chain.noCtx().runFn(6, () -> {
                for (var t : usedTiles) {
                    if (member.tile().equals(t.getX(), t.getY(), t.getZ())) {
                        member.hit(nightmare, World.getWorld().random(50));
                    }
                }
            }).then(1, () -> {
                tiles.clear();
                usedTiles.clear();
            });

        }
    }

    private void spawnSleepWalker(NPC nightmare, Entity target) {
        var player = (Player) target;

        if (nightmare == null || target == null) {
            return;
        }

        var numberOfWalkers = player.getNightmareInstance().getPlayers().size() + 1;

        if (numberOfWalkers > 24) {
            numberOfWalkers = 24;
        }

        NightmareInstance.room().transformArea(0, 0, 0, 0, player.getNightmareInstance().getzLevel() + 3).forEachPos(pos ->
            Arrays.stream(QUADS).forEach(t -> {
                if (pos.inArea(t) && World.getWorld().clipAt(pos) == 0) {
                    tiles.add(pos);
                }
            }));

        Collections.shuffle(tiles);

        NPC sleep_walker;

        for (int index = 0; index < numberOfWalkers; index++) {
            Tile t = tiles.get(index);
            sleep_walker = new NPC(9446, new Tile(t.getX(), t.getY(), player.getInstancedArea().getzLevel() + 3));
            createSleepWalker(sleep_walker, nightmare, target);
        }
    }

    private void createSleepWalker(NPC sleepWalker, NPC nightmare, Entity target) {
        var player = (Player) target;

        if (sleepWalker == null || nightmare == null) {
            return;
        }

        sleepWalkers.add(sleepWalker);

        if (player.getInstancedArea() != null) {
            player.getInstancedArea().addNpc(sleepWalker);
        }

        World.getWorld().registerNpc(sleepWalker);
        World.getWorld().definitions().get(NpcDefinition.class, 9446);
        sleepWalker.respawns(false);
        sleepWalker.setIgnoreOccupiedTiles(true);
        sleepWalker.face(nightmare);

        Chain.noCtx().runFn(8, () -> sleepWalker.getMovementQueue().step(nightmare.getAbsX(), nightmare.getAbsY(), MovementQueue.StepType.REGULAR));

        BooleanSupplier removeFromList = sleepWalker::dead;
        BooleanSupplier removeAddDamage = () -> sleepWalker.tile().inSqRadius(nightmare.tile().transform(nightmare.getSize() / 2, nightmare.getSize() / 2), 2);

        BooleanSupplier conditions = () -> removeFromList.getAsBoolean() || removeAddDamage.getAsBoolean();

        sleepWalker.waitUntil(conditions, () -> {
            sleepWalker.die();
            tiles.remove(sleepWalker.tile());
            sleepWalkers.remove(sleepWalker);
            if (removeAddDamage.getAsBoolean()) {
                sleepWalkerDamage.getAndAdd(5);
            }
        });

    }

    private void spawnHusk(NPC nightmare, Entity target) {
        Player player = (Player) target;

        for (var t : player.getNightmareInstance().getPlayers()) {
            var husks = t.getNightmareInstance().getHusks();

            if (nightmare == null) {
                return;
            }

            if (husks.size() == 2) {
                return;
            }

            if (this.isSpawningHusks()) {
                return;
            }

            this.setSpawningHusks(true);

            nightmare.animate(8600);

            Tile t1 = t.tile().transform(0, 1).copy();
            Tile t2 = t.tile().transform(0, -1).copy();

            var distanceTo = nightmare.tile().distance(t.tile());

            int duration = (80 + 15 + (10 * distanceTo));

            Projectile projectileOne = new Projectile(nightmare, t1.transform(0, 1), 1781, 80, duration, 90, 0, 0, 4, 10);
            projectileOne.send(nightmare, t1);
            Projectile projectileTwo = new Projectile(nightmare, t2.transform(0, -1), 1781, 80, duration, 90, 0, 0, 4, 10);
            projectileTwo.send(nightmare, t2);

            t.putAttrib(AttributeKey.NO_MOVEMENT_NIGHTMARE, true);

            Chain.noCtx().runFn(projectileOne.getSpeed() / 30 + 1, () -> {
                createHusk(new NPC(9454, new Tile(t.tile().getX(), t.tile().getY() + 1, t.tile().getZ())), t);
                createHusk(new NPC(9454, new Tile(t.tile().getX(), t.tile().getY() - 1, t.tile().getZ())), t);
            }).then(1, () -> this.setSpawningHusks(false));
        }
    }

    private void createHusk(NPC husk, Entity target) {
        Player player = (Player) target;
        var husks = player.getNightmareInstance().getHusks();

        husks.add(husk);

        if (target.getInstancedArea() != null) {
            target.getInstancedArea().addNpc(husk);
        }

        World.getWorld().registerNpc(husk);

        World.getWorld().definitions().get(NpcDefinition.class, 9454);

        husk.respawns(false);

        husk.setIgnoreOccupiedTiles(true);

        husk.getCombat().setTarget(target);

        husk.face(target);

        husk.animate(8567);

        BooleanSupplier removeFromList = husk::dead;

        husk.waitUntil(removeFromList, () -> husks.remove(husk));

    }

    private void magicAttack(NPC nightmare, Entity target) {
        Player player = (Player) target;

        nightmare.animate(8595);

        for (var t : player.getNightmareInstance().getPlayers()) {

            if (t == null) {
                return;
            }

            var tileDist = nightmare.tile().getChevDistance(t.tile());

            int duration = (80 + -15 + (10 * tileDist));


            Projectile p = new Projectile(nightmare, t, 1764, 80, duration, 90, 30, 0, 5, 10);

            int delay = nightmare.executeProjectile(p);

            Hit hit = Hit.builder(nightmare, t, CombatFactory.calcDamageFromType(nightmare, t, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);

            if (Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MAGIC)) {
                var damage = hit.getDamage();
                hit.setDamage((int) (damage * 1.20));
                hit.submit();
            } else {
                hit.submit();
            }

            t.graphic(1765, GraphicHeight.HIGH_3, p.getSpeed());

        }
    }

    private void rangeAttack(NPC nightmare, Entity target) {
        Player player = (Player) target;

        if (target == null) {
            return;
        }

        nightmare.animate(8596);

        for (var t : player.getNightmareInstance().getPlayers()) {

            if (t == null) {
                return;
            }

            int tileDist = nightmare.tile().getChevDistance(t.tile());

            int duration = (90 + 15 + (5 * tileDist));

            Projectile p = new Projectile(nightmare, t, 1766, 90, duration, 90, 30, 0, 5, 10);

            final int delay = nightmare.executeProjectile(p);

            Hit hit = t.hit(nightmare, CombatFactory.calcDamageFromType(nightmare, t, CombatType.MAGIC), delay, CombatType.MAGIC);

            if (Prayers.usingPrayer(t, Prayers.PROTECT_FROM_MISSILES)) {
                var damage = hit.getDamage();
                hit.setDamage((int) (damage * 1.20));
                hit.submit();
            } else {
                hit.submit();
            }
        }
    }

    void meleeAttack(NPC nightmare, Player target) {
        Entity player = Utils.randomElement(target.getNightmareInstance().getPlayers());

        if (player == null) {
            return;
        }

        nightmare.animate(8594);

        Hit hit = player.hit(nightmare, CombatFactory.calcDamageFromType(nightmare, player, CombatType.MELEE), 3, CombatType.MELEE);

        hit.setAccurate(true);

        if (Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MELEE)) {
            var damage = hit.getDamage();
            hit.setDamage((int) (damage * 1.20));
            hit.submit();
        } else {
            hit.submit();
        }
    }

    @Override
    public void doFollowLogic() {
        if (entity == null || target == null) {
            return;
        }

        if (entity.getHits() == null) {
            return;
        }

        if (entity.getHits().getCombatType() == CombatType.MELEE) {
            if (!withinDistance(1)) {
                follow(1);
            }
        } else {
            follow(2);
        }
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        NPC nightmare = (NPC) this.entity;
        Player player = (Player) this.target;

        var totems = player.getNightmareInstance().getTotems();

        if (this.getAshihamaPhase().equals(AshihamaPhase.ONE)) {

            Map<Integer, CommonCombatMethod> npcCombatMethodMap = new HashMap<>();

            for (int i = 0; i < UNCHARGED_TOTEMS.length; i++) {
                npcCombatMethodMap.put(UNCHARGED_TOTEMS[i], totemCombatMethods[i]);
            }

            player.getInterfaceManager().sendOverlay(ASHIHAMA_TOTEM_INTERFACE_ID);

            for (var id : PROGRESS_BARS) {
                player.getPacketSender().sendProgressBar(id, 0);
            }

            setAshihamaState(AshihamaState.TOTEM);

            nightmare.transmog(9430, false);
            nightmare.setCombatInfo(World.getWorld().combatInfo(9430));
            nightmare.setHitpoints(this.getNightmareHitpoints());
            nightmare.setCombatMethod(this);

            Arrays.stream(UNCHARGED_TOTEMS).boxed().forEach(id -> player.getInstancedArea().getNpcs().stream().filter(totem -> totem.id() == id).findFirst().ifPresent(totem -> {
                totem.transmog(id + TOTEM_TRANSFORMATION_OFFSET, false);

                CommonCombatMethod combatMethod = npcCombatMethodMap.get(id);
                totem.setCombatMethod(combatMethod);

                totem.setCombatInfo(World.getWorld().combatInfo(id + TOTEM_TRANSFORMATION_OFFSET));
                totem.setHitpoints(totem.maxHp());
                totem.noRetaliation(true);
                totem.getCombat().setAutoRetaliate(false);

                if (player.getInstancedArea() != null) {
                    player.getInstancedArea().addNpc(totem);
                }

                totems.add(totem);

                BooleanSupplier isDead = totem::dead;
                totem.waitUntil(isDead, () -> totems.remove(totem));
            }));
        } else if (this.getAshihamaPhase().equals(AshihamaPhase.TWO)) {
            nightmareHitpoints -= 800;

            Map<Integer, CommonCombatMethod> npcCombatMethodMap = new HashMap<>();

            for (int i = 0; i < UNCHARGED_TOTEMS.length; i++) {
                npcCombatMethodMap.put(UNCHARGED_TOTEMS[i], totemCombatMethods[i]);
            }

            player.getInterfaceManager().sendOverlay(ASHIHAMA_TOTEM_INTERFACE_ID);

            for (var id : PROGRESS_BARS) {
                player.getPacketSender().sendProgressBar(id, 0);
            }

            setAshihamaState(AshihamaState.TOTEM);

            nightmare.transmog(9430, false);
            nightmare.setCombatInfo(World.getWorld().combatInfo(9430));
            nightmare.setHitpoints(this.getNightmareHitpoints());
            nightmare.setCombatMethod(this);

            Arrays.stream(UNCHARGED_TOTEMS).boxed().forEach(id -> player.getInstancedArea().getNpcs().stream().filter(totem -> totem.id() == id).findFirst().ifPresent(totem -> {
                totem.transmog(id + TOTEM_TRANSFORMATION_OFFSET, false);

                CommonCombatMethod combatMethod = npcCombatMethodMap.get(id);
                totem.setCombatMethod(combatMethod);

                totem.setCombatInfo(World.getWorld().combatInfo(id + TOTEM_TRANSFORMATION_OFFSET));
                totem.setHitpoints(totem.maxHp());
                totem.noRetaliation(true);
                totem.getCombat().setAutoRetaliate(false);

                if (player.getInstancedArea() != null) {
                    player.getInstancedArea().addNpc(totem);
                }

                totems.add(totem);

                BooleanSupplier isDead = totem::dead;
                totem.waitUntil(isDead, () -> totems.remove(totem));
            }));
        }
        return true;
    }


    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 6;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 5;
    }

}
