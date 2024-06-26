package com.cryptic.model.entity.combat.method.impl.specials.range;

import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;

/**
 * Morrigan's javelin has a special attack, Phantom Strike, that consumes 50% of the player's special attack energy and applies a damage over time effect to the opponent after the special attack is performed.
 * This special attack has no effect on NPCs.
 *
 * Every 3 ticks (1.8 seconds) after the special attack is performed, the opponent will take an additional 5 hitpoints of damage until the same damage dealt by the special attack has been delivered.
 * When this occurs, the target will receive a message in their chatbox stating "You start to bleed as a result of the javelin strike."
 * Subsequent damage will result in the chatbox stating "You continue to bleed as a result of the javelin strike."
 */
public class MorrigansJavelin extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(806);
        entity.graphic(1621, GraphicHeight.HIGH, 0);

        int tileDist = entity.tile().transform(1, 1).getChevDistance(target.tile());
        int duration = (40 + 11 + (3 * tileDist));
        Projectile p1 = new Projectile(entity, target, 1622, 40, duration, 40, 30, 0, entity.getSize(), 5);
        final int delay = entity.executeProjectile(p1);
        var hit = entity.submitHit(target, delay, this);
        if(target instanceof Player playerTarget) {
            playerTarget.message("You start to bleed as a result of the javelin strike.");
            playerTarget.hit(target, 5, CombatType.RANGED).submit();

            TaskManager.submit(new Task("Phantom Strike Task", 3) {
                final int damage = hit.getDamage();
                int damageDealt = 0;
                @Override
                protected void execute() {
                    if(playerTarget.dead() || !playerTarget.isRegistered()) {
                        this.stop();
                    }

                    if (damage - damageDealt >= 5) {
                        Hit hit = playerTarget.hit(entity, 5,0, CombatType.RANGED).setAccurate(true);
                        hit.submit();
                        playerTarget.message("You continue to bleed as a result of the javelin strike.");
                        damageDealt += 5;
                    } else {
                        int left = damage - damageDealt;
                        Hit hit = playerTarget.hit(entity, 5,0, CombatType.RANGED).setAccurate(true);
                        hit.submit();
                        playerTarget.message("You continue to bleed as a result of the javelin strike.");
                        damageDealt += left;
                    }

                    if (damageDealt >= damage) {
                        this.stop();
                    }
                }
            });
        }
        CombatSpecial.drain(entity, CombatSpecial.MORRIGANS_JAVALIN.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return entity.getCombat().getFightType().getStyle().equals(FightStyle.DEFENSIVE) ? 6 : 4;
    }
}
