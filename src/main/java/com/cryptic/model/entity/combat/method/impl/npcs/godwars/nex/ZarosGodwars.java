package com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.*;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.model.entity.combat.method.impl.npcs.godwars.nex.NexCombat.NEX_AREA;

public class ZarosGodwars {

    public static Nex nex;
    public static NPC fumus;
    public static NPC umbra;
    public static NPC cruor;
    public static NPC glacies;

    public static boolean NEX_EVENT_ACTIVE = false;

    public static void clear() {
        if (nex != null) {
            for (NPC npc : nex.bloodReavers) {
                if(npc == null) {
                    continue;
                }
                npc.remove();
            }
            nex.remove();
            nex = null;
        }
        if (fumus != null) {
            fumus.remove();
            fumus = null;
        }
        if (umbra != null) {
            umbra.remove();
            umbra = null;
        }
        if (cruor != null) {
            cruor.remove();
            cruor = null;
        }
        if (glacies != null) {
            glacies.remove();
            glacies = null;
        }
        new GameObject(42967, new Tile(2909, 5202, 0), 10, 1).spawn(); // spawn purple
    }

    public static void startEvent() {
        if (nex != null) {
            return;
        }
        NEX_EVENT_ACTIVE = true;
        Nex nex = new Nex(NEX, new Tile(2924, 5202, 0));
        var minions = new Object() {
            NPC a, b, c, d;
        };
        nex.lockMovement();
        ZarosGodwars.nex = nex;
        Chain.bound(null).cancelWhen(() -> {
            var empty = World.getWorld().getPlayers().stream().filter(Objects::nonNull).noneMatch(NEX_AREA::contains);
            var stop = false;
            if (ZarosGodwars.nex != nex || nex.dead() || empty) {
                stop = true;
                NPC[] a = new NPC[] {minions.a, minions.b, minions.c, minions.d};
                for (NPC npc : a) {
                    if (npc == null) continue;
                    npc.remove();
                }
                clear();
            }
            return stop;
        }).thenCancellable(GameServer.properties().production ? 20 : 5, () -> {
            nex.spawn(false);
            Arrays.stream(nex.closePlayers()).forEach(p -> {
                HealthHud.open(p, HealthHud.Type.REGULAR,"Nex", 3400);
            });
        }).thenCancellable(1, () -> {
            nex.forceChat("AT LAST!");
            nex.animate(9182);
        }).thenCancellable(3, () -> {
            NPC fumus = new NPC(FUMUS, new Tile(2913, 5215, 0)).spawn(false);
            fumus.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
            fumus.putAttrib(AttributeKey.BARRIER_BROKEN, false);
            minions.a = fumus;
            fumus.setPositionToFace(nex.tile());
            nex.setPositionToFace(fumus.tile());
            nex.forceChat("Fumus!");
            nex.animate(9189);
            int tileDist = minions.a.tile().transform(3, 3).getManhattanDistance(nex.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile p = new Projectile(minions.a, nex, 2010, 30, duration, 18, 18, 0, 1, 5);
            minions.a.executeProjectile(p);
        }).thenCancellable(3, () -> {
            NPC umbra = new NPC(UMBRA, new Tile(2937, 5215, 0)).spawn(false);
            umbra.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
            umbra.putAttrib(AttributeKey.BARRIER_BROKEN,false);
            minions.b = umbra;
            umbra.setPositionToFace(nex.tile());
            nex.setPositionToFace(umbra.tile());
            nex.forceChat("Umbra!");
            nex.animate(9189);
            int tileDist = minions.b.tile().transform(3, 3).getManhattanDistance(nex.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile p = new Projectile(minions.b, nex, 2010, 30, duration, 18, 18, 0, 1, 5);
            minions.b.executeProjectile(p);
        }).thenCancellable(3, () -> {
            NPC cruor = new NPC(CRUOR, new Tile(2937, 5191, 0)).spawn(false);
            cruor.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
            cruor.putAttrib(AttributeKey.BARRIER_BROKEN,false);
            minions.c = cruor;
            cruor.setPositionToFace(nex.tile());
            nex.setPositionToFace(cruor.tile());
            nex.forceChat("Cruor!");
            nex.animate(9189);
            int tileDist = minions.c.tile().transform(3, 3).getManhattanDistance(nex.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile p = new Projectile(minions.c, nex, 2010, 30, duration, 18, 18, 0, 1, 5);
            minions.c.executeProjectile(p);
        }).thenCancellable(3, () -> {
            NPC glacies = new NPC(GLACIES, new Tile(2913, 5191, 0)).spawn(false);
            glacies.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
            glacies.putAttrib(AttributeKey.BARRIER_BROKEN,false);
            minions.d = glacies;
            glacies.setPositionToFace(nex.tile());
            nex.setPositionToFace(glacies.tile());
            nex.forceChat("Glacies!");
            nex.animate(9189);
            int tileDist = minions.d.tile().transform(3, 3).getManhattanDistance(nex.tile().transform(3, 3, 0));
            int duration = (30 + 11 + (3 * tileDist));
            Projectile p = new Projectile(minions.d, nex, 2010, 30, duration, 18, 18, 0, 1, 5);
            minions.d.executeProjectile(p);
        }).thenCancellable(3, () -> {
            nex.forceChat("Fill my soul with smoke!");
        }).thenCancellable(2, () -> {
         //   nex.setPositionToFace(null);
            nex.cantInteract(false);
            nex.unlock();
            Entity target = Utils.randomElement(nex.getCombatMethod().getPossibleTargets(nex));
            if(target != null) {
                nex.getCombat().setTarget(target);
                nex.getCombat().attack(target);
            }
            //Replace purple barrier with red
            new GameObject(42941, new Tile(2909, 5202, 0), 10, 1).spawn(); // spawn red
            ZarosGodwars.fumus = minions.a;
            ZarosGodwars.umbra = minions.b;
            ZarosGodwars.cruor = minions.c;
            ZarosGodwars.glacies = minions.d;
        });
    }

}
