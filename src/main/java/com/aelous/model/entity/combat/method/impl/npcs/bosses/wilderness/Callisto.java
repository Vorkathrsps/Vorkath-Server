package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

/**
 * Handles Callisto's combat.
 *
 * @author PVE, Oak did a shitty job. Parts taken from OSS.
 */
public class Callisto extends CommonCombatMethod {

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;//Should be one because melee bear
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //All attacks are melee
       // if (CombatFactory.canReach(npc, CombatFactory.MELEE_COMBAT, target)) {
            // At all times, callisto can initiate the heal.
          //  if (Utils.rollDie(18, 1)) {
         //       prepareHeal(npc);
         //   }


            // Determine if we do a special hit, or a regular hit.
           // if (Utils.rollDie(18, 1)) {
           //     fury(npc, target);
           // } else if (Utils.rollDie(6, 1) && !npc.<Boolean>getAttribOr(AttributeKey.CALLISTO_ROAR, false)) {
                roar((NPC) entity, target);
           // } else {
            //    target.hit(npc, CombatFactory.calcDamageFromType(npc, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
           //     npc.animate(npc.attackAnimation());
        return true;
            }
       // }
  //  }

    /**
     * Callisto unleashes a shockwave against his target. When this happens, a game message will appear saying that he has used the ability against you,
     * just like Vet'ion's earthquake and Venenatis' web attack. This attack has no cooldown and can hit up to 60 in one attack. Callisto will use this ability much more often
     * the further you are from him. The projectile of this attack is similar to Wind Wave.
     */
    private void fury(NPC npc, Entity target) {
        npc.animate(4925);
        new Projectile(npc, target, 395, 40, 60, 31, 43, 0).sendProjectile();

        Chain.bound(null).name("CallistoFuryTask").runFn(2, () -> {
            target.hit(npc, Utils.random(30), CombatType.MELEE).checkAccuracy().submit();
            ((Player)target).message("Callisto's fury sends an almighty shockwave through you.");
            target.stun(4);
            target.graphic(245, GraphicHeight.HIGH, 0);
        });
    }

    /**
     * A blast will appear under Callisto. Even though the game states that Callisto will prepare to heal himself based on the damage dealt to him,
     * he actually heals himself during this time for a small amount.
     */
    private void prepareHeal(NPC npc) {
        npc.graphic(157);
        npc.putAttrib(AttributeKey.CALLISTO_DMG_HEAL, true);
        npc.heal(Utils.random(3, 10));
    }

    /**
     * A small white projectile flies from the player straight to Callisto. When this happens, you will be knocked back several spaces from your current location,
     * and a game message will appear stating "Callisto's roar knocks you backwards.", dealing 3 damage.
     */
    private void roar(NPC npc, Entity target) {
        npc.putAttrib(AttributeKey.CALLISTO_ROAR, true);
        if (target.isPlayer()) {
            Direction direction = Direction.of(target.tile().x - npc.tile().x, target.tile().y - npc.tile().y);

            Tile tile = target.tile().transform(direction.x() * 3, direction.y() * 3);

            FaceDirection face = FaceDirection.forTargetTile(npc.tile(), target.tile());

            int[][] area = World.getWorld().clipAround(tile, 3);

            for (int[] array : area) {
                for (int value : array) {
                    if (value != 0) {
                        npc.clearAttrib(AttributeKey.CALLISTO_ROAR);
                        return;
                    }
                }
            }
            ((Player)target).message(Color.RED.wrap("Callisto's roar throws you backwards."));
            ForceMovement forceMovement = new ForceMovement(target.tile(), new Tile(direction.x() * 3, direction.y() * 3), 30, 60, 1157, face.direction);
            target.setForceMovement(forceMovement);
            target.graphic(245, GraphicHeight.HIGH, 60);
            Chain.bound(null).name("CallistoRoarTask").runFn(3, () -> {
                target.stun(2);
                target.hit(npc, 3, CombatType.MELEE).checkAccuracy().submit();
            });
        }
        npc.clearAttrib(AttributeKey.CALLISTO_ROAR);
    }

}
