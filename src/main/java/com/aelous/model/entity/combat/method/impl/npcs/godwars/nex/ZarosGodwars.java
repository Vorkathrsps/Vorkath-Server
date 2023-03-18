package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.GameServer;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.ScalarLootTable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.aelous.model.content.collection_logs.LogType.BOSSES;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * An utility class for the Zaros part of GWD
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 13, 2022
 */
public class ZarosGodwars {

    public static Nex nex;
    public static NPC fumus;
    public static NPC umbra;
    public static NPC cruor;
    public static NPC glacies;

    public static Optional<GameObject> ancientBarrierPurple = MapObjects.get(42967, new Tile(2909, 5202, 0));
    public static GameObject redBarrierPurple = null;

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
    }

    public static void startEvent() {
        if (nex != null && !nex.isRegistered()) {
            clear();
        }
        if (nex == null) {
            NEX_EVENT_ACTIVE = true;
            Nex nex = new Nex(NEX, new Tile(2924, 5202, 0));
            nex.lockMovement();
            ZarosGodwars.nex = nex;
            Chain.bound(null).then(GameServer.properties().production ? 20 : 5, () -> {
                nex.spawn(false);
            }).thenCancellable(1, () -> {
                nex.forceChat("AT LAST!");
                nex.animate(9182);
            }).thenCancellable(3, () -> {
                NPC fumus = new NPC(FUMUS, new Tile(2913, 5215, 0)).spawn(false);
                fumus.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
                fumus.putAttrib(AttributeKey.BARRIER_BROKEN,false);
                ZarosGodwars.fumus = fumus;
                fumus.setPositionToFace(nex.tile());
                nex.setPositionToFace(fumus.tile());
                nex.forceChat("Fumus!");
                nex.animate(9189);
                Projectile projectile = new Projectile(ZarosGodwars.fumus, nex, 2010, 30, 80, 18, 18, 0);
                projectile.sendProjectile();
            }).thenCancellable(3, () -> {
                NPC umbra = new NPC(UMBRA, new Tile(2937, 5215, 0)).spawn(false);
                umbra.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
                umbra.putAttrib(AttributeKey.BARRIER_BROKEN,false);
                ZarosGodwars.umbra = umbra;
                umbra.setPositionToFace(nex.tile());
                nex.setPositionToFace(umbra.tile());
                nex.forceChat("Umbra!");
                nex.animate(9189);
                Projectile projectile = new Projectile(ZarosGodwars.umbra, nex, 2010, 30, 80, 18, 18, 0);
                projectile.sendProjectile();
            }).thenCancellable(3, () -> {
                NPC cruor = new NPC(CRUOR, new Tile(2937, 5191, 0)).spawn(false);
                cruor.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
                cruor.putAttrib(AttributeKey.BARRIER_BROKEN,false);
                ZarosGodwars.cruor = cruor;
                cruor.setPositionToFace(nex.tile());
                nex.setPositionToFace(cruor.tile());
                nex.forceChat("Cruor!");
                nex.animate(9189);
                Projectile projectile = new Projectile(ZarosGodwars.cruor, nex, 2010, 30, 80, 18, 18, 0);
                projectile.sendProjectile();
            }).thenCancellable(3, () -> {
                NPC glacies = new NPC(GLACIES, new Tile(2913, 5191, 0)).spawn(false);
                glacies.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT,true);
                glacies.putAttrib(AttributeKey.BARRIER_BROKEN,false);
                ZarosGodwars.glacies = glacies;
                glacies.setPositionToFace(nex.tile());
                nex.setPositionToFace(glacies.tile());
                nex.forceChat("Glacies!");
                nex.animate(9189);
                Projectile projectile = new Projectile(ZarosGodwars.glacies, nex, 2010, 30, 80, 18, 18, 0);
                projectile.sendProjectile();
            }).thenCancellable(3, () -> {
                nex.forceChat("Fill my soul with smoke!");
                Projectile projectile = new Projectile(ZarosGodwars.glacies, nex, 2010, 30, 80, 18, 18, 0);
                projectile.sendProjectile();
            }).thenCancellable(2, () -> {
                nex.setPositionToFace(null);
                nex.cantInteract(false);
                nex.unlock();
                Entity target = Utils.randomElement(nex.getCombatMethod().getPossibleTargets(nex));
                if(target != null) {
                    nex.getCombat().setTarget(target);
                    nex.getCombat().attack(target);
                }
            });
        }

    }

}
