package com.cryptic.model.map.object.dwarf_cannon;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.content.mechanics.MultiwayCombat;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.items.ground.GroundItemHandler;
import com.cryptic.model.map.object.ObjectManager;
import com.cryptic.model.map.object.OwnedObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.RSPolygon;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import com.google.common.base.Stopwatch;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Origin | April, 16, 2021, 13:39
 */
@Getter
public class DwarfCannon extends OwnedObject {

    public static final String IDENTIFIER = "dwarfCannon";
    public static final int[] cannon_balls = new int[]{2, ItemIdentifiers.GRANITE_CANNONBALL};
    public static final int BASE = 6, STAND = 8, BARRELS = 10, FURNACE = 12;
    public static final int[] CANNON_PARTS = {BASE, STAND, BARRELS, FURNACE};
    public static final int[] CANNON_OBJECTS = {7, 8, 9, 6};
    public static final int SETUP_ANIM = 827;
    private static int MAX_AMMO = 30;
    private static final int CANNON_RANGE = 9;
    private static final int DECAY_TIME = 20;
    private static final int BROKEN_TIME = 25;

    private Stopwatch decayTimer = Stopwatch.createUnstarted();

    @Setter
    private int ammo;

    private CannonStage stage;

    private MulticannonDirection direction;
    private final transient Map<MulticannonDirection, RSPolygon> polygons;

    public void setStage(CannonStage stage, boolean changeId) {
        this.stage = stage;
        if (changeId) setId(stage.getObjectId());
    }

    public static final Area[] AREA_RESTRICTIONS = {
        new Area(3036, 3478, 3144, 3524, -1), // edgevile
        //new Area(1600, 9984, 1727, 10111, -1), // catacomes of kourend
        new Area(1728, 5312, 1791, 5375, -1), // ancient cavern
        new Area(3281, 3158, 3304, 3178, -1), // alkarid palace
        new Area(2368, 3072, 2431, 3135, -1), // castle wars
        new Area(2950, 9800, 3071, 9855, -1), // dwarven mine
        new Area(2994, 9698, 3071, 9799, -1), // dwarven mine
        new Area(3008, 6016, 3071, 6079, -1), // zalcano
        new Area(3405, 3579, 3452, 3530, -1), // slayer tower
        new Area(3229, 10151, 3257, 10187, -1), // revenant caves
        new Area(3245, 10136, 3259, 10154, -1), // revenant caves
        new Area(2838, 3534, 2876, 3556, -1), // warriors guild
        new Area(2432, 10112, 2559, 10175, -1), // waterbirth dungeon
        new Area(2240, 9984, 2303, 10047, -1), // kraken cove
        new Area(3200, 10304, 3263, 10367, -1), // scorpia
        new Area(3520, 9664, 3583, 9727, -1), // barrows crypt
        new Area(1990, 3526, 2112, 3648, -1), // Home
        new Area(2628, 2627, 2680, 2683, -1), // Pest control
        new Area(1247, 10144, 1411, 10296, -1), // Karluum dungeon
        new Area(3326, 3202, 3392, 3266, -1), // Duel arena
        new Area(3349, 3267, 3392, 3325, -1), // Duel arena
        new Area(3642, 3204, 3683, 3234, -1), // Ver sinhaza
    };

    public DwarfCannon(Player owner, int id) {
        super(owner, IDENTIFIER, id, owner.tile(), 10, 0);
        this.stage = CannonStage.BASE;
        direction = MulticannonDirection.NORTH;
        polygons = MulticannonDirection.create(getCorrectedTile(this.tile()));
    }

    @Override
    public void tick() {
        if (decayTimer == null) {
            decayTimer = Stopwatch.createUnstarted();
        }
        if (direction == null) {
            direction = MulticannonDirection.NORTH;
        }
        checkDecayTimer();
        computeTarget();
    }

    public void fill() {
        if (getOwner().isPlayer()) {
            Player player = getOwner().getAsPlayer();
            MAX_AMMO = player.getMemberRights().isExtremeMemberOrGreater(player) ? 50 : MAX_AMMO;
        }
        for (var balls : cannon_balls) {
            if (getAmmo() < MAX_AMMO && getOwner().inventory().count(balls) > 0) {
                int needed = MAX_AMMO - getAmmo();
                int available = getOwner().inventory().count(balls);

                if (needed > available) needed = available;

                if (needed > 0) {
                    getOwner().inventory().remove(balls, needed);
                    getOwner()
                        .message(
                            "You load the cannon with "
                                + (needed == 1 ? "one" : needed)
                                + " cannonball"
                                + ((needed > 1) ? "s." : "."));
                    setAmmo(getAmmo() + needed);
                }

                setStage(CannonStage.FIRING, false);
            }
            break;
        }
    }

    public void pickup() {
        int spaces = 4;
        if (getAmmo() > 0) {
            for (var balls : cannon_balls) {
                spaces += getOwner().inventory().count(balls) > 0 ? 0 : 1;
                break;
            }
        }
        if (getOwner().inventory().getFreeSlots() > spaces) {
            IntStream.of(getStage().getParts())
                .mapToObj(Item::new)
                .forEach(getOwner().inventory()::add);
            if (getAmmo() > 0) {
                for (var balls : cannon_balls) {
                    getOwner().inventory().add(balls, getAmmo());
                    break;
                }
            }
            getOwner().animate(SETUP_ANIM);
            destroy();
            getOwner().message("You pick up the cannon.");
        } else {
            getOwner().message("You don't have enough inventory space to do that.");
        }
    }

    private static Tile getCorrectedTile(Tile pos) {
        return pos.clone().transform(1, 1, 0);
    }

    private void computeTarget() {
        boolean ownerOnline = getOwnerOpt().isPresent();
        if (!ownerOnline) return;
        Optional<NPC> target = Optional.empty();
        if (getStage().equals(CannonStage.FIRING)) {
            rotate();
            if (!MultiwayCombat.includes(getOwner()) && Objects.nonNull(getOwner().getCombat().getTarget())) {
                Entity combatTarget = getOwner().getCombat().getTarget();
                if (combatTarget.isNpc()) {
                    target = Optional.ofNullable(combatTarget.getAsNpc());
                    if (target.isPresent()) {
                        if (!canAttack(target.get())) {
                            target = Optional.empty();
                        }
                    }
                }
            }

            List<NPC> potentialTargets = populatePotentialTargets();

            if (potentialTargets.isEmpty()) return;

            for (var t : potentialTargets) {
                if (t == null) continue;
                if (t.dead()) continue;
                if (canAttack(t)) {
                    target = Optional.of(t);
                    break;
                }
            }

            target.ifPresent(npc -> getOwnerOpt().ifPresent(owner -> {
                if (canAttack(npc)) {
                    var center = getCorrectedTile(this.tile());
                    var distance = center.getManhattanDistance(npc.getCentrePosition());
                    var duration = (41 + -5 + (5 * distance));
                    Projectile p1 = new Projectile(center, npc.getCentrePosition(), 53, 0, duration, 40, 30, 3, this.getSize(), 64, 5);
                    p1.send(center, npc.getCentrePosition());
                    final int delay = (int) (p1.getSpeed() / 30D);
                    var hit = new Hit(owner, npc, delay + 1, CombatType.RANGED);
                    hit.checkAccuracy(false).submit().postDamage(h1 -> {
                        h1.setDamage(Utils.random(1, owner.getCombat().getMaximumRangedDamage()));
                        if (h1.getDamage() > 30) h1.setDamage(30);
                        getOwner().getSkills().addXp(Skills.RANGED, h1.getDamage());
                        setAmmo(getAmmo() - 1);
                        if (getAmmo() <= 0) {
                            owner.message("Your cannon is out of ammo!");
                            setStage(CannonStage.FURNACE, true);
                        }
                    });
                }
            }));
        }
    }

    private boolean canAttack(final NPC npc) {
        final Player player = this.getOwner();
        if (player == null) return false;
        return polygons.get(direction).contains(npc.getCentrePosition());
    }

    private boolean isProjectileClipped(final Tile cannonTile) {
        return ProjectileRoute.isProjectileClipped(null, null, cannonTile, new Tile(cannonTile.getX() + 2, cannonTile.getY(), cannonTile.getZ()), true) || ProjectileRoute.isProjectileClipped(null, null, cannonTile, new Tile(cannonTile.getX(), cannonTile.getY() + 2, cannonTile.getZ()), true) || ProjectileRoute.isProjectileClipped(null, null, cannonTile, new Tile(cannonTile.getX() + 2, cannonTile.getY() + 2, cannonTile.getZ()), true) || ProjectileRoute.isProjectileClipped(null, null, cannonTile, new Tile(cannonTile.getX() + 1, cannonTile.getY() + 2, cannonTile.getZ()), true) || ProjectileRoute.isProjectileClipped(null, null, cannonTile, new Tile(cannonTile.getX() + 2, cannonTile.getY() + 1, cannonTile.getZ()), true);
    }

    private ArrayList<NPC> populatePotentialTargets() {
        ArrayList<NPC> potentialTargets = new ArrayList<>(); // Initialize the ArrayList
        var cached = NpcDefinition.cached;
        for (var n : getOwner().closeNpcs(16)) {
            if (n == null) continue;
            if (n.getZ() != getOwner().getZ()) continue;
            if (!n.tile().isViewableFrom(getOwner().tile())) continue;
            if (n.getCombatInfo() == null) continue;
            if (isProjectileClipped(getCorrectedTile(this.tile()))) continue;
            if (!n.tile().isWithinDistance(getCorrectedTile(this.tile()), CANNON_RANGE)) continue;
            var def = cached.get(n.getId());
            if (def.isPet || !def.isInteractable || def.actions[1] == null) continue;
            if (!MultiwayCombat.includes(n.tile()) && n.getCombat().getTarget() != this.getOwner()) continue;
            potentialTargets.add(n);
        }
        return potentialTargets;
    }

    private void rotate() {
        final int dir = this.direction.ordinal() + 1;
        if (getOwnerOpt().isPresent() && getStage().equals(CannonStage.FIRING)) {
            animate(direction.getAnimation().getId());
            this.direction = MulticannonDirection.values[dir == 8 ? 0 : dir];
        } else if (getStage().equals(CannonStage.FURNACE) && getAmmo() <= 0 && direction != MulticannonDirection.NORTH) {
            animate(direction.getAnimation().getId());
            this.direction = MulticannonDirection.values[dir == 8 ? 0 : dir];
        }
    }

    public void checkDecayTimer() {
        if (needsDecaying() && !getStage().equals(CannonStage.BROKEN)) {
            getOwnerOpt().ifPresent(player -> player.message("<col=ff0000>Your cannon has broken.</col>"));
            setStage(CannonStage.BROKEN, true);
        }
        if (needsDestroyed()) {
            getOwnerOpt()
                .ifPresent(
                    player -> {
                        player.message(
                            "<col=ff0000>Your cannon has decayed. Speak to Drunken"
                                + " dwarf to get a new one!</col>");
                        player.putAttrib(AttributeKey.LOST_CANNON, true);
                        for (var balls : cannon_balls) {
                            GroundItemHandler.createGroundItem(
                                new GroundItem(
                                    new Item(balls, getAmmo()),
                                    player.tile(),
                                    player));
                            break;
                        }
                        setAmmo(0);
                        destroy();
                    });
        }
    }

    public boolean needsDecaying() {
        return decayTimer.elapsed(TimeUnit.MINUTES) > DECAY_TIME
            && !getStage().equals(CannonStage.BROKEN);
    }

    public boolean needsDestroyed() {
        return decayTimer.elapsed(TimeUnit.MINUTES) > BROKEN_TIME
            && getStage().equals(CannonStage.BROKEN);
    }

    public void incrementSetupStage() {
        setStage(this.stage.next(), true);
    }

    public boolean isValidSpot() {
        int[][] area = World.getWorld().clipAround(tile(), 2);

        for (int[] array : area) {
            for (int value : array) {
                if (value != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean handleAreaRestriction() {
        if (getOwnerOpt().isEmpty()) {
            return false;
        }

        Player player = getOwnerOpt().get();

        if (RegionManager.zarosBlock(player.tile())) {
            player.message("You can't place a cannon here.");
            return false;
        }

        if (player.getRaids() != null && player.getRaids().raiding(player)) {
            player.message("You can't place a cannon in raids.");
            return false;
        }

        if (player.tile().region() == 9551) {
            player.message("You can't place a cannon in Fight Caves.");
            return false;
        }

        if (player.tile().region() == 10536) {
            player.message("You cannot place your cannon at Void Island.");
            return false;
        }

        if (player.tile().inArea(new Area(2944, 4736, 3135, 4927, 0))) {
            player.message(
                "That horrible slime on the ground makes this area unsuitable for a cannon.");
            return false;
        }
        if (player.tile().inArea(new Area(2999, 3501, 3034, 3523, 0))) {
            player.message(
                "It is not permitted to set up a cannon this close to the Dwarf Black Guard.");
            return false;
        }
        if (player.tile().inArea(new Area(2688, 9984, 2815, 10047, 0))) {
            player.message("The humid air in these tunnels won't do your cannon any good!");
            return false;
        }
        if (player.tile().inArea(new Area(3138, 3468, 3189, 3516, 0))) {
            player.message(
                "The Grand Exchange staff prefer not to have heavy artillery operated around"
                    + " their premises.");
            return false;
        }
        if (player.tile().inArea(new Area(3136, 4544, 3199, 4671, 0))) {
            player.message(
                "This temple is ancient and would probably collapse if you started firing a"
                    + " cannon.");
            return false;
        }
        if (player.tile().inArea(new Area(1280, 9920, 1343, 9983, 0))) {
            player.message(
                "This temple is ancient and would probably collapse if you started firing a"
                    + " cannon.");
            return false;
        }
        if (player.tile().region() == 9007) {
            player.message("The ground is too damp to support a cannon.");
            return false;
        }
        boolean normal = Stream.of(AREA_RESTRICTIONS).anyMatch(area -> player.tile().inArea(area));
        if (normal) {
            player.message("You can't deploy a cannon here.");
            return false;
        }
        return true;
    }

    public static void onLogin(Player player) {
        var reclaim = player.<Boolean>getAttribOr(AttributeKey.LOST_CANNON, false);
        if (reclaim) {
            player.message(
                Color.RED.wrap(
                    "Your cannon has been destoryed, you can reclaim it from the Drunken"
                        + " Dwarf at home."));
        }
    }

    public boolean hasParts() {
        return IntStream.of(CANNON_PARTS).allMatch(getOwner().inventory()::contains);
    }

    public boolean isPart(int id) {
        return IntStream.of(CANNON_PARTS).anyMatch(part -> part == id);
    }
}
