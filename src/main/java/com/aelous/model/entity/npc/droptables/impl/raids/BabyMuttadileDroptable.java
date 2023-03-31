package com.aelous.model.entity.npc.droptables.impl.raids;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.npcs.raids.cox.Muttadile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import java.lang.ref.WeakReference;

import static com.aelous.model.entity.attributes.AttributeKey.MUTTADILE_HEAL_COUNT;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.MUTTADILE_7563;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 30, 2021
 */
public class BabyMuttadileDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);
            party.teamMessage("<col=ef20ff>"+npc.def().name+" has been defeated!");

            NPC mommaMuttadile = party.getMommaMuttadile();
            mommaMuttadile.putAttrib(MUTTADILE_HEAL_COUNT,0);
            mommaMuttadile.getCombat().reset();
            mommaMuttadile.respawns(false);
            mommaMuttadile.lockNoDamage();
            var targ = mommaMuttadile.<WeakReference<Entity>>getAttribOr(AttributeKey.TARGET, new WeakReference<Entity>(null)).get();
            Chain.bound(null).runFn(1, () -> {
                mommaMuttadile.transmog(MUTTADILE_7563);
                mommaMuttadile.getCombatInfo(World.getWorld().combatInfo(MUTTADILE_7563));
                mommaMuttadile.def(World.getWorld().definitions().get(NpcDefinition.class, MUTTADILE_7563));
                mommaMuttadile.setCombatMethod(new Muttadile());
                mommaMuttadile.animate(7423);
                mommaMuttadile.getMovement().reset();
                mommaMuttadile.getMovement().interpolate(new Tile(3311, 5329, mommaMuttadile.tile().level));
            }).then(3, () -> {
                mommaMuttadile.heal(mommaMuttadile.maxHp());
                mommaMuttadile.unlock();
                if (targ != null) {
                    mommaMuttadile.setPositionToFace(targ.tile());
                    mommaMuttadile.getCombat().attack(targ);
                    mommaMuttadile.cloneDamage(mommaMuttadile);
                }
            });
        }
    }
}
