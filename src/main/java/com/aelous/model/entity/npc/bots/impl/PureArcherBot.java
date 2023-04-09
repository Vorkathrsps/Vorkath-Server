package com.aelous.model.entity.npc.bots.impl;

import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.bots.NPCBotHandler;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.utility.Utils;

/**
 * @author Patrick van Elderen | June, 17, 2021, 17:13
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class PureArcherBot extends NPCBotHandler implements CombatMethod {
    
    /**
     * The default npc.
     */
    private static final int DEFAULT_BOT_ID = NPCBotHandler.PURE_ARCHER_BOT_ID;
    /**
     * The npc which has
     * a special attack weapon equipped.
     */
    private static final int SPEC_BOT_ID = NPCBotHandler.PURE_ARCHER_BOT_ID + 1;

    public PureArcherBot(NPC npc) {
        super(npc);
        npc.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
        npc.getCombat().setRangedWeapon(RangedData.RangedWeapon.MAGIC_SHORTBOW);
        npc.getCombatInfo().projectile = 15;
    }

    @Override
    public void process() {

        //Check if npc is in combat..
        if (CombatFactory.inCombat(npc)) {

            //Make sure our opponent is valid..
            Player opponent = getOpponent();
            if (opponent == null) {
                return;
            }
            //If the opponent hasn't teleported away (12 is probably a safe distance to consider teled out), reset combat... This can take a few ticks (I believe up to 9). Might need this reset for non-bot NPCs too although probably not.
            if (npc.tile().distance(opponent.tile()) >= 12) {
                //npc.forceChat("Nice tele.");
                //System.out.println("Nice tele.");
                npc.setEntityInteraction(null);
                npc.getCombat().reset();
                npc.getMovementQueue().clear();
                return;
            }
            //Are we in distance to the opponent?
            final boolean inDistance = (npc.tile().distance(opponent.tile()) <= getMethod().getAttackDistance(npc));

            //Activate prayers..
            Prayers.activatePrayer(npc, Prayers.EAGLE_EYE);
            Prayers.activatePrayer(npc, Prayers.STEEL_SKIN);

            //Activate any overheads..
            int overhead = getOverheadPrayer(opponent, inDistance);
            if (overhead != -1) {

                //Activate overhead!
                Prayers.activatePrayer(npc, overhead);

            } else {

                //We shouldn't be using any overhead.
                //Make sure to turn off any headicons.
                if (npc.getPKBotHeadIcon() != -1) {
                    npc.setPKBotHeadIcon(-1);
                }
                resetOverheadPrayers(npc);
            }

            //Eat whenever we need to.
            if (npc.hp() > 0) {

                if (npc.hp() < 30 + Utils.getRandom(10)) {

                    if (getEatCounter() < 28) {
                        super.eat(FoodConsumable.Food.SHARK, 1100);
                    }
                }

                //Sometimes we spec
                //if (inDistance && npc.getSpecialPercentage() > CombatSpecial.DRAGON_DAGGER.getDrainAmount() && opponent.getHitpoints() <= 45) {
                //Since we switch to Ballista, it doesn't actually use spec.
                if (inDistance && opponent.hp() <= 45) {
                    if (Utils.getRandom(10) == 1) {
                        npc.setSpecialActivated(true);
                    }
                }
                //Randomly turn it off..
                if (!inDistance || Utils.getRandom(8) == 1) {
                    //System.out.println("Deactivated spec");
                    npc.setSpecialActivated(false);
                }
                //Update npc depending on the special attack state
                if (!npc.isSpecialActivated()) {
                    //System.out.println("We are not spec.");
                    transform(DEFAULT_BOT_ID);
                    npc.getCombatInfo().maxhit = 21;
                    //Lower attack speed is faster.
                    npc.getCombatInfo().attackspeed = 3;
                    npc.getCombat().setRangedWeapon(RangedData.RangedWeapon.MAGIC_SHORTBOW);
                    npc.getCombatInfo().projectile = 15;
                } else {
                    npc.getCombatInfo().maxhit = 45;
                    //Lower attack speed is faster.
                    npc.getCombatInfo().attackspeed = 5;
                    npc.getCombat().setRangedWeapon(RangedData.RangedWeapon.BALLISTA);
                    npc.getCombatInfo().projectile = 1301;
                    //System.out.println("We are spec.");
                    npc.setSpecialAttackPercentage(npc.getSpecialAttackPercentage() - CombatSpecial.DRAGON_DAGGER.getDrainAmount()); //use dds spec amount (25%) for ballista spec
                    transform(SPEC_BOT_ID);
                }

            } else {
                npc.forceChat("Ggwp");
            }

        } else {

            //Turn off prayers
            Prayers.closeAllPrayers(npc);

            //Reset weapon
            transform(DEFAULT_BOT_ID);
            npc.getCombat().setRangedWeapon(RangedData.RangedWeapon.MAGIC_SHORTBOW);
            //Reset all attributes
            super.reset();
        }
    }

    @Override
    public void onDeath(Player killer) {
        int botKills = killer.getAttribOr(AttributeKey.BOT_KILLS, 0);
        botKills++;
        killer.putAttrib(AttributeKey.BOT_KILLS, botKills);
        GroundItemHandler.createGroundItem(new GroundItem(new Item(13307, World.getWorld().random(25)), killer.tile(),killer));
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }

    @Override
    public CombatMethod getMethod() {
        return CombatFactory.RANGED_COMBAT;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),1, CombatType.RANGED).checkAccuracy();
        hit.submit();
        return true;
    }
}
