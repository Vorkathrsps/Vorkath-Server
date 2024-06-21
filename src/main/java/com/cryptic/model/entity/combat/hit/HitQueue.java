package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a "hitqueue", processing pending hits aswell as pending damage.
 *
 * @author Shadowrs|Jak refactored to fix design flaws.
 * @author V1 Professor Oak.
 */
public class HitQueue {

    private static final Logger logger = LogManager.getLogger(HitQueue.class);
    private final List<Hit> hits = new ArrayList<>();

    public void clear() {
        hits.clear();
    }

    public void process(Entity entity) {
        if (entity.stunned()) {
            return;
        }
        if (entity.dead() || (entity.locked() && !entity.isDelayDamageLocked() && !entity.isDamageOkLocked() && !entity.isLogoutOkLocked() && !entity.isMoveLockedDamageOk())) {
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

        if (entity.isDelayDamageLocked() || entity.isLogoutOkLocked() || hits.isEmpty()) {
            return;
        }

        for (final Hit hit : Lists.newArrayList(hits)) {
            try {
                if (hit != null) {
                    if (hit.getTarget() == null || hit.getTarget().isNullifyDamageLock()) {
                        hit.toremove = true;
                        continue;
                    }

                    if (hit.getAttacker() != null) {
                        if (hit.getAttacker().dead() && !hit.reflected) {
                            hit.invalidate();
                            continue;
                        }
                    }

                    if (hit.getTarget().dead() && !hit.reflected) { //noting here just incase something fks up
                        hit.invalidate();
                        continue;
                    }

                    if (hit.isLocked()) {
                        hit.toremove = true;
                        continue;
                    }

                    if (hit.isInvalidated()) {
                        hit.toremove = true;
                        continue;
                    }

                    final int delay = hit.decrementAndGetDelay();
                    if (delay <= 0) {
                        hit.applyBeforeRemove();
                        CombatFactory.executeHit(hit);
                        hit.toremove = true;
                        if (shouldShowSplat(hit))
                            hit.showSplat = true;
                    }
                }
            } catch (RuntimeException e) {
                hit.toremove = true;
                logger.error("{}: RTE in hits - hopefully this stack helps pinpoint the cause: {}", entity.getMobName(), hit, e);
                throw e;
            }
        }
        List<Hit> toShow = hits.stream().filter(e -> e.showSplat).collect(Collectors.toList());
        hits.removeIf(o -> o.toremove);
        if (toShow.isEmpty()) return;
        for (Hit hit : toShow) hit.update();
        toShow.clear();
    }

    /**
     * don't DISPLAY damage if the attack was a magic splash by a player. (0 blue hitsplat)
     */
    private boolean shouldShowSplat(Hit hit) {
        boolean magic_splash = hit.getCombatType() == CombatType.MAGIC && !hit.isAccurate() && !hit.forceShowSplashWhenMissMagic;
        // only hide 0 magic dmg hitplat in PVP, example npcs can splash and it will show a 0 hitsplat (like kraken)
        if (hit.getDamage() == 0 && hit.isAccurate() && hit.getCombatType() == CombatType.MAGIC) {
            return hit.getAttacker().isPlayer();
        } else {
            return !(magic_splash && hit.getAttacker().isPlayer());
        }
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
