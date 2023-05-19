package com.aelous.model.entity.combat.magic.impl;

import com.aelous.model.content.mechanics.MultiwayCombat;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.autocasting.Autocasting;
import com.aelous.model.entity.combat.skull.SkullType;
import com.aelous.model.entity.combat.skull.Skulling;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 *
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

    public void whenSpellCast(Entity cast, Entity castOn) {
        if (spellRadius() == 0) {
            return;
        }

        int delay = (int) (2 + Math.floor((1 + cast.tile().getManHattanDist(cast.tile(), castOn.tile())) / 3D));

        castOn.putAttrib(AttributeKey.LAST_DAMAGER, cast);
        castOn.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
        castOn.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);
        cast.putAttrib(AttributeKey.LAST_ATTACK_TIME, System.currentTimeMillis());
        cast.putAttrib(AttributeKey.LAST_TARGET, castOn);
        cast.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);

        ArrayList<Entity> targets = new ArrayList<>();

        Iterator<? extends Entity> it = null;
        if (cast.isPlayer() && castOn.isPlayer()) {
            it = cast.getLocalPlayers().iterator();
        } else if (cast.isPlayer() && castOn.isNpc()) {
            it = cast.getLocalNpcs().iterator();
        } else if (cast.isNpc() && castOn.isNpc()) {
            it = World.getWorld().getNpcs().iterator();
        } else if (cast.isNpc() && castOn.isPlayer()) {
            it = World.getWorld().getPlayers().iterator();
        }

        if (it != null) {
            while (it.hasNext()) {
                Entity next = it.next();

                if (next == null) {
                    continue;
                }

                if (!(next.tile().isWithinDistance(castOn.tile(), spellRadius()) && next.hp() > 0 && next.hp() > 0)) {
                    continue;
                }

                if (next.isNpc()) {
                    NPC n = (NPC) next;
                    if (castOn == n) {
                        continue;
                    }

                    if (n.getCombatInfo() != null && n.getCombatInfo().unattackable) {
                        continue;
                    }

                    if (!MultiwayCombat.includes(n)) {
                        continue;
                    }

                    if (n.id() == 7710 || n.id() == 7709) {
                        continue;
                    }

                    if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, n)) {
                        cast.getCombat().reset();
                        continue;
                    }
                    targets.add(n);
                } else {
                    Player p = (Player) next;
                    if (castOn == p) {
                        continue;
                    }

                    if (p.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA, -1) == 0 || !WildernessArea.inAttackableArea(p) || !MultiwayCombat.includes(p)) {
                        continue;
                    }

                    if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, p)) {
                        cast.getCombat().reset();
                        continue;
                    }
                    targets.add(p);
                }
            }
        }

        for (Entity target : targets) {
            Hit hit = castOn.hit(cast, CombatFactory.calcDamageFromType(cast, target, CombatType.MAGIC), delay, CombatType.MAGIC);
            if (cast.isPlayer() && target.isPlayer() && WildernessArea.inWilderness(target.tile())) {
                Skulling.skull(cast.getAsPlayer(), target.getAsPlayer(), SkullType.WHITE_SKULL);
            }
            spellEffect(cast, target, hit);
        }
    }


    @Override
    public List<Item> equipmentRequired(Player player) {
        return List.of();
    }

    @Override
    public final void finishCast(Entity cast, Entity castOn, boolean accurate, int damage) {
    }

    /**
     * The effect this spell has on the target.
     *
     * @param cast   the entity casting this spell.
     * @param castOn the person being hit by this spell.
     * @param hit
     */
    public abstract void spellEffect(Entity cast, Entity castOn, Hit hit);

    /**
     * The radius of this spell, only comes in effect when the victim is hit in
     * a multicombat area.
     *
     * @return how far from the target this spell can hit when targeting
     * multiple entities.
     */
    public abstract int spellRadius();
}
