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

public class ZamorakianSpear extends CommonCombatMethod {

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
        CombatSpecial.drain(attacker, CombatSpecial.ZAMORAKIAN_SPEAR.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 0;
    }
}
