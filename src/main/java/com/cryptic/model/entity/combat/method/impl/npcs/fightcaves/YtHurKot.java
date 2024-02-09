package com.cryptic.model.entity.combat.method.impl.npcs.fightcaves;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

/**
 * @author Origin | December, 23, 2020, 14:35
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class YtHurKot extends NPC {

    private TzTokJad jad;

    protected YtHurKot(int id, Tile tile, TzTokJad jad) {
        super(id, tile);
        this.jad = jad;
        respawns(false);
        getCombatInfo().aggressive = false;
        walkRadius(5);
        putAttrib(AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE, 100);
    }

    @Override
    public void combatSequence() {

        if (jad == null) {
            return;
        }

        if (dead()) {
            return;
        }

        long lastTime = System.currentTimeMillis() - (long) getAttribOr(AttributeKey.LAST_WAS_ATTACKED_TIME, 0L);
        if (lastTime < 6000L) {
            getCombat().reset();//Last attack was 6 seconds ago, reset combat
        }

        if (CombatFactory.lastAttacker(this) != null || getCombat().getTarget() != null) {
            return;
        }

        if (getInteractingEntity() != jad) {
            setEntityInteraction(jad);
        }

        if (this.tile.isWithinDistance(this, jad,1)) {
            jad.graphic(444);
            if (jad.hp() < jad.maxHp())
                jad.setHitpoints(jad.hp() + 1);
        } else {
            getRouteFinder().routeAbsolute(jad.tile().x, jad.tile().y);
        }
    }

    @Override
    public void die() {
        if (jad != null) {
            jad.removeHealer(this);
        }
        jad = null;
        super.die();
    }


}
