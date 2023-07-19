package com.aelous.model.content.raids.theatre.boss.maiden.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.maiden.blood.BloodSpawn;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.aelous.model.content.raids.theatre.boss.maiden.utils.MaidenUtils.*;

public class MaidenProcess extends NPC {
    private final Player player;
    private BloodSpawn orb;
    private final List<Player> players = new ArrayList<>();
    @Getter @Setter private int randomBlood = 0;
    private int intervalCount = 0;
    private int attackInterval = 10;

    public MaidenProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.setSize(6);
        this.spawnDirection(Direction.EAST.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
    }

    public void sequenceTornadoAndBlood() {
        if (Utils.sequenceRandomInterval(randomBlood, 7, 14)) {
            throwBlood();
            this.setRandomBlood(0);
            return;
        }
        randomBlood++;
        this.face(player);
        Chain.noCtx().runFn(1, () -> this.face(null));
        this.animate(8092);
        int tileDist = this.tile().distance(player.tile());
        int duration = (80 + -5 + (8 * tileDist));
        Projectile p = new Projectile(this, player, 1577, 80, duration, 0, 0, 0, 6, 8);
        final int delay = this.executeProjectile(p);
        Hit hit = Hit.builder(this, player, CombatFactory.calcDamageFromType(this, player, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
    }

    public void throwBlood() {
        var tile = player.tile().copy();
        this.face(player);
        this.animate(8091);
        Chain.noCtx().runFn(1, () -> this.face(null));
        var tileDist = this.tile().distance(tile);
        int duration = (68 + 25 + (10 * tileDist));
        Projectile p = new Projectile(this, tile, 2002, 68, duration, 95, 0, 20, 5, 10);
        p.send(this, tile);
        World.getWorld().tileGraphic(1579, tile, 0, p.getSpeed());
        orb = new BloodSpawn(10821, new Tile(p.getEnd().getX(), p.getEnd().getY()), player);
        Chain.noCtx().runFn(16, orb::spawn);
    }

    public void spawnNylocas() {
      
    }

    public void heal() {
        Iterator<Integer> iterator = orb.damage.iterator();
        while (iterator.hasNext()) {
            var d = iterator.next();
            this.healHit(this, d);
            iterator.remove();
        }
    }


    @Override
    public void postSequence() {
        if (this.dead()) {
            return;
        }

        if (IGNORED.contains(player.tile()) || (!MAIDEN_AREA.contains(player.tile()) && IGNORED.contains(player.tile()))) {
            return;
        }

        if (MAIDEN_AREA.contains(player.tile()) && !IGNORED.contains(player.tile())) {
            if (!players.contains(player)) {
                players.add(player);
            }
        } else {
            players.remove(player);
        }

        if (orb != null) {
            heal();
        }

        intervalCount++;
        attackInterval--;
        if (intervalCount >= 10 && attackInterval <= 0 && !this.dead()) {
            sequenceTornadoAndBlood();
            intervalCount = 0;
            attackInterval = 10;
        }
    }

    @Override
    public void die() {
        if (orb != null) {
            orb.clear();
        }
        Chain.noCtx().runFn(1, () -> {
            this.animate(8094);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        });
    }
}
