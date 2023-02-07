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

    private ArrayList<Entity> targets;

    public void whenSpellCast(Entity cast, Entity castOn) {

        // The spell doesn't support multiple targets or we aren't in a
        // multicombat zone, so do nothing.
        if (spellRadius() == 0) {
            return;
        }

        int delay = (int) (1D + Math.floor(1 + cast.tile().getChevDistance(castOn.tile()) / 3D));
        delay = (int) Math.min(Math.max(1.0 , delay), 5.0);

        int[] AUTOCAST_RESET_STAFFS = {ItemIdentifiers.STAFF_OF_THE_DEAD, ItemIdentifiers.STAFF_OF_LIGHT, ItemIdentifiers.TOXIC_STAFF_OF_THE_DEAD, ItemIdentifiers.TOXIC_STAFF_UNCHARGED,
        ItemIdentifiers.VOLATILE_NIGHTMARE_STAFF, ItemIdentifiers.ELDRITCH_NIGHTMARE_STAFF, ItemIdentifiers.NIGHTMARE_STAFF, ItemIdentifiers.UNCHARGED_TOXIC_TRIDENT, ItemIdentifiers.UNCHARGED_TOXIC_TRIDENT_E, ItemIdentifiers.TRIDENT_OF_THE_SEAS, ItemIdentifiers.TRIDENT_OF_THE_SWAMP
        , ItemIdentifiers.TRIDENT_OF_THE_SWAMP_E, ItemIdentifiers.TRIDENT_OF_THE_SEAS_E, ItemIdentifiers.TRIDENT_OF_THE_SEAS_FULL, ItemIdentifiers.SANGUINESTI_STAFF, ItemIdentifiers.SANGUINESTI_STAFF_UNCHARGED};

        if (cast.getAsPlayer().getEquipment().containsAny(AUTOCAST_RESET_STAFFS)) {
                if (cast.getCombat().getAutoCastSpell() != null) {
                    Autocasting.setAutocast((Player) cast, null);
                    cast.getAsPlayer().getPacketSender().sendAutocastId(-1).sendConfig(108, 0).setDefensiveAutocastState(0);
                    cast.getAsPlayer().stopActions(true);
                }
        }

        // Flag the target as under attack at this moment to factor in delayed combat styles.
        castOn.putAttrib(AttributeKey.LAST_DAMAGER, cast);
        castOn.putAttrib(AttributeKey.LAST_WAS_ATTACKED_TIME, System.currentTimeMillis());
        castOn.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);
        cast.putAttrib(AttributeKey.LAST_ATTACK_TIME, System.currentTimeMillis());
        cast.putAttrib(AttributeKey.LAST_TARGET, castOn);
        cast.getTimers().register(TimerKey.COMBAT_LOGOUT, 16);

        targets = new ArrayList<>();

        // We passed the checks, so now we do multiple target stuff.
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

        for (Iterator<? extends Entity> $it = it; $it.hasNext(); ) {
            Entity next = $it.next();

            if (next == null) {
                continue;
            }

            if (!(next.tile().isWithinDistance(castOn.tile(), spellRadius()) && next.hp() > 0 && next.hp() > 0)) {
                continue;
            }

            if (next.isNpc()) {
                NPC n = (NPC) next;
                //if(n.isPet()) {
                  //  continue;
               // }
                if (castOn == n) // we're already done damage for the primary target, don't do even more
                    continue;

                if (n.combatInfo() != null && n.combatInfo().unattackable) {
                    continue;
                }

                if (!MultiwayCombat.includes(n)) {
                    //not in the multi area and we were, don't carry over.
                    continue;
                }

                //Inferno checks
                if (n.id() == 7710 || n.id() == 7709) {
                    continue;
                }

                if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, n)) {
                    cast.getCombat().reset();//Can't attack, reset combat
                    continue;
                }
                // the list of potential targets
                targets.add(n);
            } else {
                Player p = (Player) next;
                if (castOn == p) // we're already done damage for the primary target, don't do even more
                    continue;
                if (p.<Integer>getAttribOr(AttributeKey.MULTIWAY_AREA,-1) == 0 || !WildernessArea.inAttackableArea(p) || !MultiwayCombat.includes(p)) {
                    //not in the multi area and we were, don't carry over.
                    continue;
                }
                if (!CombatFactory.canAttack(cast, CombatFactory.MAGIC_COMBAT, p)) {
                    cast.getCombat().reset();//Can't attack, reset combat
                    continue;
                }
                // the list of potential targets
                targets.add(p);
            }
        }

        for (Entity target : targets) {

            // dmg is calcd inside hit

            Hit hit = castOn.hit(cast, CombatFactory.calcDamageFromType(cast, castOn, CombatType.MAGIC), delay, CombatType.MAGIC);


            //Hit hit = Hit.builder(cast, target, CombatFactory.calcDamageFromType(cast, target, CombatType.MAGIC));
            if (cast.isPlayer() && target.isPlayer()) { // Check if the player should be skulled for making this attack..
                Player attacker = cast.getAsPlayer();
                Player playerTarget = target.getAsPlayer();
                if (WildernessArea.inWilderness(playerTarget.tile())) {
                    Skulling.skull(attacker, playerTarget, SkullType.WHITE_SKULL);
                }
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
     *  @param cast   the entity casting this spell.
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
