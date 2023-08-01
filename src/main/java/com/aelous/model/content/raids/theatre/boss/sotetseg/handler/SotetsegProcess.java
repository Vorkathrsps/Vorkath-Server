package com.aelous.model.content.raids.theatre.boss.sotetseg.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.Theatre;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.content.raids.theatre.stage.RoomState;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.List;

public class SotetsegProcess extends NPC {
    private final List<Player> players = new ArrayList<>();
    Player player;
    Theatre theatre;
    TheatreArea theatreArea;
    int magicAttackCount = 0;
    private int intervalCount = 0;
    private int attackInterval = 5;
    private int randomAttack = 0;
    public static final Area SOTETSEG_AREA = new Area(3272, 4305, 3289, 4334);
    public static final Area IGNORED = new Area( 3277, 4303,3282, 4307);

    public SotetsegProcess(int id, Tile tile, Player player, Theatre theatre, TheatreArea theatreArea) {
        super(id, tile);
        this.player = player;
        this.theatre = theatre;
        this.theatreArea = theatreArea;
        this.setCombatMethod(null);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.getMovementQueue().setBlockMovement(true);
    }

    public void sendRandomMageOrRange() {
        int[] projectileIds = new int[]{1606, 1607};
        var randomProjectile = Utils.randomElement(projectileIds);
        this.animate(8139);
        int tileDist = this.tile().distance(player.tile());
        int duration = (55 + 12 + (10 * tileDist));
        Projectile p = new Projectile(this, player, randomProjectile, 55, duration, 43, 21, 25, 5, 10);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED), delay, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED).checkAccuracy().postDamage(d -> {
            if (randomProjectile == 1606) {
                magicAttackCount++;
            }
            if (randomProjectile == 1606 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MISSILES)) {
                Prayers.closeAllPrayers(player);
                player.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else if (randomProjectile == 1607 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                Prayers.closeAllPrayers(player);
                player.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else {
                if (d.getDamage() == 0) {
                    d.block();
                }
            }
        });
        hit.submit();
    }

    public void sendSpecialMagicAttack() {
        magicAttackCount = 0;
        this.animate(8139);
        int tileDist = this.tile().distance(player.tile());
        int duration = (55 + 25 + (10 * tileDist));
        Projectile p = new Projectile(this, player, 1604, 55, duration, 50, 0, 50, 5, 10);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, CombatType.MAGIC), delay, CombatType.MAGIC).setAccurate(true);
        hit.setDamage(121);
        hit.submit();
        this.graphic(101, GraphicHeight.MIDDLE, p.getSpeed());
    }

    public void sendMeleeAttack() {
        if (!DumbRoute.withinDistance(this, player, 1)) {
            return;
        }
        this.animate(8138);
        player.hit(this, CombatFactory.calcDamageFromType(this, player, CombatType.MELEE), 1);
    }

    @Override
    public void postSequence() {
        super.postSequence();

        if (this.dead()) {
            return;
        }

        if (!insideBounds()) {
            this.face(null);
            this.setPositionToFace(new Tile(Direction.SOUTH.x, Direction.SOUTH.y));
            return;
        }

        if (insideBounds() && !player.dead()) {
            intervalCount++;
            attackInterval--;
            if (intervalCount >= 5 && attackInterval <= 0 && !this.dead()) {

                if (magicAttackCount == 10) {
                    this.sendSpecialMagicAttack();
                    return;
                }

                if (Utils.sequenceRandomInterval(randomAttack, 7, 14) && DumbRoute.withinDistance(this, player, 1)) {
                    this.sendMeleeAttack();
                    return;
                }

                this.sendRandomMageOrRange();

                intervalCount = 0;
                attackInterval = 5;
            }
        }
    }

    @Override
    public void die() {
        players.clear();
        player.setRoomState(RoomState.COMPLETE);
        player.getTheatreParty().onRoomStateChanged(player.getRoomState());
        Chain.noCtx().runFn(1, () -> {
            this.animate(8139);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        });
    }

    protected boolean insideBounds() {
        if (IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) || (!SOTETSEG_AREA.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) && IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()))) {
            return false;
        }

        if (SOTETSEG_AREA.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile()) && !IGNORED.transform(0, 0, 0, 0, theatreArea.getzLevel()).contains(player.tile())) {
            if (!players.contains(player)) {
                players.add(player);
                return true;
            }
        } else {
            players.remove(player);
            return false;
        }
        return true;
    }

}
