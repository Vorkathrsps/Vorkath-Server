package com.aelous.model.map.object.dwarf_cannon;

import com.aelous.model.content.mechanics.MultiwayCombat;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.object.OwnedObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Color;
import com.google.common.base.Stopwatch;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author Patrick van Elderen | April, 16, 2021, 13:39
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class DwarfCannon extends OwnedObject {

    public static final String IDENTIFIER = "dwarfCannon";
    public static final int CANNON_BALL = 2;
    public static final int BASE = 6, STAND = 8, BARRELS = 10, FURNACE = 12;
    public static final int[] CANNON_PARTS = {BASE, STAND, BARRELS, FURNACE};
    public static final int[] CANNON_OBJECTS = {7, 8, 9, 6};
    public static final int SETUP_ANIM = 827;
    private static int MAX_AMMO = 30;
    private static final int CANNON_RANGE = 8;
    private static final int MAX_HIT = 30;
    private static final int DECAY_TIME = 20;
    private static final int BROKEN_TIME = 25;

    private Stopwatch decayTimer = Stopwatch.createUnstarted();

    public Stopwatch getDecayTimer() {
        return decayTimer;
    }

    private int ammo;

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    private CannonStage stage;

    public CannonStage getStage() {
        return stage;
    }

    public void setStage(CannonStage stage, boolean changeId) {
        this.stage = stage;
        if (changeId) setId(stage.getObjectId());
    }

    private CannonDirection cannonDirection = CannonDirection.NORTH;

    public CannonDirection getCannonDirection() {
        return cannonDirection;
    }

    public void setCannonDirection(CannonDirection cannonDirection) {
        this.cannonDirection = cannonDirection;
    }

    public static final Area[] AREA_RESTRICTIONS = {
        new Area(3036, 3478, 3144, 3524, -1), // edgevile
        new Area(1600, 9984, 1727, 10111, -1), // catacomes of kourend
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
        setCannonDirection(CannonDirection.NORTH);
    }

    @Override
    public void tick() {
        if (decayTimer == null) {
            decayTimer = Stopwatch.createUnstarted();
        }
        if (cannonDirection == null) {
            cannonDirection = CannonDirection.NORTH;
        }
        checkDecayTimer();
        rotate();
    }

    public void fill() {
        if (getOwner().isPlayer()) {
            Player player = getOwner().getAsPlayer();
            MAX_AMMO = player.getMemberRights().isExtremeMemberOrGreater(player) ? 50 : MAX_AMMO;
        }
        if (getAmmo() < MAX_AMMO && getOwner().inventory().count(CANNON_BALL) > 0) {
            int needed = MAX_AMMO - getAmmo();
            int available = getOwner().inventory().count(CANNON_BALL);

            if (needed > available) needed = available;

            if (needed > 0) {
                getOwner().inventory().remove(CANNON_BALL, needed);
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
    }

    public void pickup() {
        int spaces = 4;
        if (getAmmo() > 0) {
            spaces += getOwner().inventory().count(CANNON_BALL) > 0 ? 0 : 1;
        }
        if (getOwner().inventory().getFreeSlots() > spaces) {
            IntStream.of(getStage().getParts())
                    .mapToObj(Item::new)
                    .forEach(getOwner().inventory()::add);
            if (getAmmo() > 0) getOwner().inventory().add(CANNON_BALL, getAmmo());
            getOwner().animate(SETUP_ANIM);
            destroy();
            getOwner().message("You pick up the cannon.");
        } else {
            getOwner().message("You don't have enough inventory space to do that.");
        }
    }

    private static Tile getCorrectedTile(Tile pos) {
        return pos.copy().transform(1, 1, 0);
    }

    private void rotate() {
        boolean ownerOnline = getOwnerOpt().isPresent();
        Optional<NPC> target = Optional.empty();
        if (ownerOnline && getStage().equals(CannonStage.FIRING)) {
            if (!MultiwayCombat.includes(getOwner())
                    && Objects.nonNull(getOwner().getCombat().getTarget())) {
                Entity combatTarget = getOwner().getCombat().getTarget();
                if (combatTarget.isNpc()) {
                    target = Optional.ofNullable(combatTarget.getAsNpc());
                    if (target.isPresent()) {
                        if (!cannonDirection.validArea(
                                tile(),
                                target.get()
                                        .tile()
                                        .copy()
                                        .center(
                                                target.get()
                                                        .getSize()))) { // this changes the tile..
                            target = Optional.empty();
                        }
                    }
                }
            } else {
                target =
                        World.getWorld()
                                .getNpcs()
                                .nonNullStream()
                                .filter(
                                        npc ->
                                                npc.tile()
                                                        .isWithinDistance(
                                                                getCorrectedTile(tile()),
                                                                CANNON_RANGE))
                                .filter(npc -> npc.def().combatlevel > 0 && npc.hp() > 0)
                                .filter(npc -> !npc.dead())
                                .filter(npc -> !npc.def().isPet)
                                .filter(
                                        npc ->
                                                cannonDirection.validArea(
                                                        getCorrectedTile(tile()).transform(1, 1, 0),
                                                        npc.tile()))
                                .filter(
                                        npc ->
                                                ProjectileRoute.allow(
                                                        getCorrectedTile(tile()).getX(),
                                                        getCorrectedTile(tile()).getY(),
                                                        getCorrectedTile(tile()).getZ(),
                                                        1,
                                                        npc.tile().getX(),
                                                        npc.tile().getY(),
                                                        npc.getSize()))
                                .findAny();
            }
            if (tile().inArea(new Area(2240, 4672, 2303, 4735, -1))) { // king black dragon
                getOwner().message("Your cannon has been destroyed for placing it in this area.");
                destroy();
                getOwner().putAttrib(AttributeKey.LOST_CANNON, true);
                return;
            }
        }

        if (ownerOnline && getStage().equals(CannonStage.FIRING)) {
            animate(cannonDirection.getAnimationId());
            cannonDirection = cannonDirection.next();
        } else if (getStage().equals(CannonStage.FURNACE)
                && getAmmo() <= 0
                && getCannonDirection() != CannonDirection.NORTH) {
            animate(cannonDirection.getAnimationId());
            cannonDirection = cannonDirection.next();
        }

       /* target.ifPresent(
                npc -> {
                    Player owner = getOwnerOpt().get();
                    Projectile cannonBall =
                            new Projectile(
                                    getCorrectedTile(tile()),
                                    npc.tile(),
                                    npc.getProjectileLockonIndex(),
                                    53,
                                    85,
                                    35,
                                    20,
                                    20,
                                    0,
                                    0,
                                    64);
                    cannonBall.sendProjectile();
                    var tileDist = owner.tile().transform(3, 3, 0).distance(npc.tile());
                    var delay = Math.max(1, (20 + (tileDist * 12)) / 30);

                    npc.hit(owner, World.getWorld().random(MAX_HIT), delay);
                    setAmmo(getAmmo() - 1);
                    if (getAmmo() <= 0) {
                        owner.message("Your cannon is out of ammo!");
                        setStage(CannonStage.FURNACE, true);
                    }
                });*/
    }

    public void checkDecayTimer() {
        if (needsDecaying() && !getStage().equals(CannonStage.BROKEN)) {
            getOwnerOpt()
                    .ifPresent(
                            player -> player.message("<col=ff0000>Your cannon has broken.</col>"));
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
                                GroundItemHandler.createGroundItem(
                                        new GroundItem(
                                                new Item(CANNON_BALL, getAmmo()),
                                                player.tile(),
                                                player));
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

    public CannonStage incrementSetupStage() {
        setStage(this.stage.next(), true);
        return stage;
    }

    public boolean isValidSpot() {
        int[][] area = World.getWorld().clipAround(tile, 2);

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

        if (ObjectManager.objWithTypeExists(
                        10, new Tile(player.tile().x, player.tile().y, player.tile().level))
                || ObjectManager.objWithTypeExists(
                        11, new Tile(player.tile().x, player.tile().y, player.tile().level))) {
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
        // TODO can't place a cannon in the inferno
        // TODO can't place a cannon in your house

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
