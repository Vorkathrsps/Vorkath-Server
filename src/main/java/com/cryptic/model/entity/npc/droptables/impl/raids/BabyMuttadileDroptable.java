package com.cryptic.model.entity.npc.droptables.impl.raids;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.npcs.raids.cox.Muttadile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import java.lang.ref.WeakReference;

import static com.cryptic.model.entity.attributes.AttributeKey.MUTTADILE_HEAL_COUNT;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.MUTTADILE_7563;

/**
 * @Author Origin
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
                mommaMuttadile.setCombatInfo(World.getWorld().combatInfo(MUTTADILE_7563));
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
