package com.aelous.model.entity.combat.hit;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Represents a "hitqueue", processing pending hits aswell as pending damage.
 *
 * @author Shadowrs|Jak refactored to fix design flaws.
 * @author V1 Professor Oak.
 */
public class HitQueue {

    private static final Logger logger = LogManager.getLogger(HitQueue.class);

    //Our list containing all our incoming hits waiting to be processed.
    private LinkedList<Hit> hits = new LinkedList<>();

    public void clear() {
        hits.clear();
    }

    public void process(Entity entity) {

        if (entity.stunned()) {
            return;
        }

        if (entity.dead() || (entity.locked() && !entity.isDelayDamageLocked() && !entity.isDamageOkLocked() && !entity.isLogoutOkLocked())) {
            hits.clear();
            return;
        }
        if (entity.isPlayer()) {
            Player player = entity.getAsPlayer();

            if (player.getDueling().inDuel() && player.getDueling().getOpponent().dead() && !player.locked()) {
                hits.clear();
                player.lockNoDamage();
                return;
            }
        }

        if (entity.isDelayDamageLocked() || entity.isLogoutOkLocked() || hits.size() == 0) {
            return;
        }

        for (Hit hit : hits) {
            try {
                if (hit == null || hit.getTarget() == null || hit.getAttacker() == null || hit.getTarget().isNullifyDamageLock()) {
                    hit.toremove = true;
                    continue;
                }

                if (hit.invalid()) {
                    hit.toremove = true;
                    continue;
                }

                if (hit.decrementAndGetDelay() <= 0) {
                    CombatFactory.executeHit(hit);
                    hit.toremove = true;
                    if (shouldShowSplat(hit))
                        hit.showSplat = true;
                }
                //Thread.dumpStack();
            } catch (RuntimeException e) {
                hit.toremove = true;
                logger.error(entity.getMobName() + ": RTE in hits - hopefully this stack helps pinpoint the cause: " + hit, e);
                throw e;
            }
        }
        List<Hit> toShow = hits.stream().filter(e -> e.showSplat).collect(Collectors.toList());
        hits.removeIf(o -> o.toremove);
        if (toShow.size() == 0)
            return;
        for (Hit hit : toShow) {
            hit.playerSync();
        }

        toShow.clear();

    }

    /**
     * don't DISPLAY damage if the attack was a magic splash by a player. (0 blue hitsplat)
     */
    private boolean shouldShowSplat(Hit hit) {
        boolean magic_splash = hit.getCombatType() == CombatType.MAGIC && !hit.isAccurate() && !hit.forceShowSplashWhenMissMagic;
        // only hide 0 magic dmg hitplat in PVP, example npcs can splash and it will show a 0 hitsplat (like kraken)
        return !(magic_splash && hit.getAttacker().isPlayer());
    }

    /**
     * Add a pending hit to our queue.
     */
    public void add(Hit c_h) {
        hits.add(c_h);
    }

    public int size() {
        return hits.size();
    }
}
