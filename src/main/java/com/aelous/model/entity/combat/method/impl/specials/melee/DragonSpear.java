package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.content.duel.DuelRule;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.timers.TimerKey;

/**
 * The dragon spear has a special attack, Shove, which it shares with the Zamorakian spear and hasta. It pushes an opponent back and stuns them for three seconds,
 * consuming 25% of the player's special attack energy.
 *
 * The effects of this special are non-stackable, meaning that players cannot use the spear's special attack on a target who is already stunned.
 * In addition to this, players are given a one tick (0.6 seconds) period of immunity after a stun wears off in which they cannot be stunned again.
 * Despite this, it is popular in player killing as the brief stun causes all incoming damage to be ignored until it dissipates, after which all damage taken is applied at once.
 *
 * In addition, it cannot be used against large monsters, such as giants, because they are too big to push back. If a player tries this, the special attack does not occur
 * and no special attack energy is consumed. A message appears in the chat box, reading, "That creature is too large to knock back!"
 * A general way to tell if the spear can use the "Shove" special attack is to determine how much space the target monster takes up.
 * If it takes up more than one space, it cannot be shoved. For example, a player can shove an ice warrior, which takes up one space, but not an ice giant, which takes up four.
 */
public class DragonSpear extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity attacker, Entity victim) {
        //The special attack can only be used on targets that take up one square.
        if(attacker.isPlayer() && victim.isNpc()) {
            var playerAttacker = (Player) attacker;
            if(victim.getSize() > 1) {
                playerAttacker.message("You can't spear this monster.");
                return false;
            }
        }
        //Player vs Player
        if(attacker.isPlayer() && victim.isPlayer()) {
            var player = (Player) attacker;

            //Start with checks first

            if (player.getDueling().inDuel() && player.getDueling().getRules()[DuelRule.NO_MOVEMENT.ordinal()]) {
                player.message("This weapon's special attack cannot be used in this duel.");
                return false;
            }

            //The effects of this special are non-stackable, meaning that players cannot use the spear's special attack on a target who is already stunned.
            if (victim.stunned()) {
                player.message("They're already stunned!");
                return false;
            }
        }
        attacker.animate(1064);
        attacker.graphic(253, GraphicHeight.HIGH, 0);

        attacker.setPositionToFace(victim.tile());
        attacker.getTimers().register(TimerKey.COMBAT_ATTACK, 2);
        attacker.getCombat().setTarget(victim);


        //Player vs Player
        if(attacker.isPlayer() && victim.isPlayer()) {
            var player = (Player) attacker;

            // Since this weapon doesn't deal damage, manually extend the in-combat timer.
            victim.getTimers().extendOrRegister(TimerKey.COMBAT_LOGOUT, 16);
            victim.getMovementQueue().clear(); // clears any pre-existing movement. spear replaces their movement

            //It pushes an opponent back and stuns them for three seconds.
            Tile targTile = victim.tile().transform(-1, 0, 0);
            boolean legal = victim.getMovementQueue().canWalkNoLogicCheck(-1, 0);
            if (!legal) {
                targTile = victim.tile().transform(1, 0, 0);
                legal = victim.getMovementQueue().canWalkNoLogicCheck(1, 0);
            }

            victim.stun(5);
            if (legal)
                victim.getMovementQueue().interpolate(targTile, MovementQueue.StepType.FORCED_WALK);
        }
        CombatSpecial.drain(attacker, CombatSpecial.DRAGON_SPEAR.getDrainAmount());
        return true;

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
