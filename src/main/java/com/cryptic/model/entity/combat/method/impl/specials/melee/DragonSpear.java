package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.content.duel.DuelRule;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.MovementQueue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.routes.TargetRoute;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

import java.util.function.BooleanSupplier;

/**
 * The dragon spear has a special attack, Shove, which it shares with the Zamorakian spear and hasta. It pushes an opponent back and stuns them for three seconds,
 * consuming 25% of the player's special attack energy.
 * <p>
 * The effects of this special are non-stackable, meaning that players cannot use the spear's special attack on a target who is already stunned.
 * In addition to this, players are given a one tick (0.6 seconds) period of immunity after a stun wears off in which they cannot be stunned again.
 * Despite this, it is popular in player killing as the brief stun causes all incoming damage to be ignored until it dissipates, after which all damage taken is applied at once.
 * <p>
 * In addition, it cannot be used against large monsters, such as giants, because they are too big to push back. If a player tries this, the special attack does not occur
 * and no special attack energy is consumed. A message appears in the chat box, reading, "That creature is too large to knock back!"
 * A general way to tell if the spear can use the "Shove" special attack is to determine how much space the target monster takes up.
 * If it takes up more than one space, it cannot be shoved. For example, a player can shove an ice warrior, which takes up one space, but not an ice giant, which takes up four.
 */
public class DragonSpear extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity attacker, Entity victim) {
        if (attacker.isPlayer() && victim.isNpc()) {
            var playerAttacker = (Player) attacker;
            if (victim.getSize() > 1) {
                playerAttacker.message("You can't spear this monster.");
                return false;
            }
        }
        if (attacker.isPlayer() && victim.isPlayer()) {
            var player = (Player) attacker;
            if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MOVEMENT.ordinal()]) {
                player.message("This weapon's special attack cannot be used in this duel.");
                return false;
            }
            if (victim.stunned()) {
                player.message("They're already stunned!");
                return false;
            }
        }

        if (victim.stunned() || attacker.hasAttrib(AttributeKey.SPEARING)) return false;

        attacker.animate(1064);
        attacker.graphic(253, GraphicHeight.HIGH, 0);

        attacker.putAttrib(AttributeKey.SPEARING, true);
        Chain.noCtx().runFn(7, () -> attacker.clearAttrib(AttributeKey.SPEARING));
        attacker.getCombat().reset();
        attacker.face(victim);

        victim.getTimers().extendOrRegister(TimerKey.COMBAT_LOGOUT, 16);
        victim.stopActions(true);
        victim.getCombat().reset();

        Tile tile = legalTileDirection(victim, attacker);
        victim.stun(5);
        victim.step(tile.getX(), tile.getY(), MovementQueue.StepType.FORCED_WALK);
        CombatSpecial.drain(attacker, CombatSpecial.DRAGON_SPEAR.getDrainAmount());
        return true;

    }

    Tile legalTileDirection(Entity victim, Entity attacker) {
        Tile victimTile = victim.tile();
        Tile attackerTile = attacker.tile();
        boolean attackOnXAxis = Math.abs(victimTile.getX() - attackerTile.getX()) > Math.abs(victimTile.getY() - attackerTile.getY());
        boolean attackOnYAxis = !attackOnXAxis;
        int deltaX = 0;
        int deltaY = 0;
        if (attackOnXAxis)
            deltaX = victimTile.getX() < attackerTile.getX() ? -1 : 1;
        if (attackOnYAxis)
            deltaY = victimTile.getY() < attackerTile.getY() ? -1 : 1;
        if (!victim.getMovementQueue().canWalkNoLogicCheck(deltaX, deltaY)) {
            if (attackOnXAxis) deltaX = victimTile.getX() < attackerTile.getX() ? 1 : -1;
            if (attackOnYAxis) deltaY = victimTile.getY() < attackerTile.getY() ? 1 : -1;
        }
        return new Tile(deltaX, deltaY);
    }


    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
