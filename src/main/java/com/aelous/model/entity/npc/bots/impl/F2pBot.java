package com.aelous.model.entity.npc.bots.impl;

import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.CombatMethod;
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
public class F2pBot extends NPCBotHandler implements CombatMethod {

    /**
     * The default npc.
     */
    private static final int DEFAULT_BOT_ID = NPCBotHandler.F2P_BOT_ID;
    /**
     * The npc which has
     * a special attack weapon equipped.
     */
    private static final int SPEC_BOT_ID = NPCBotHandler.F2P_BOT_ID + 1;

    //This should be true for "perfect" switching always switching back after one attack, false if this is not what we want for the functionality of 2h switching. This could be named PERFECT_SWITCHING or USE_PERFECT_SWITCHING instead.
    private static final boolean USE_SPEC_HITS_TICKS = true;

    private long switchedWeaponTime;

    //Technically this could be named spec hits or spec ticks, it's basically the same thing.
    private long specHits;

    public F2pBot(NPC npc) {
        super(npc);
        npc.clearAttrib(AttributeKey.VENGEANCE_ACTIVE);
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
            //Eat whenever we need to.
            if (npc.hp() > 0) {
                if (npc.hp() < 14 + Utils.getRandom(10)) {
                    if (getEatCounter() < 28) {
                        super.eat(FoodConsumable.Food.SWORDFISH, 1100);
                    }
                }
            } else {
                npc.forceChat("Gf");
            }

            //Start of range 2h switching logic.
            //old comment: The range 2h switching logic can be improved (it's possible to get multiple hits in a row with 2h if player's HP stays under 31, but this is not likely).
            //new comment: Since it checks for specHits, the bot actually has "perfect" switching.
            //Activate it randomly and if they're in distance and not switched..
            if (inDistance && opponent.hp() <= 31 && (switchedWeaponTime > 0 && (System.currentTimeMillis() - switchedWeaponTime) < 2000)) {
                //Since this is range 2h, we should turn on spec quite frequently if they are under hp..
                if (Utils.getRandom(5) == 1) {
                    //System.out.println("Turning on spec");
                    npc.setSpecialActivated(true);
                }
            }

            //Randomly turn it off before hitting, or check that it isn't used twice in a row..
            if (!inDistance || Utils.getRandom(8) == 1 || (npc.isSpecialActivated() && (System.currentTimeMillis() - switchedWeaponTime) > 2000) || opponent.hp() > 31 || (USE_SPEC_HITS_TICKS && specHits > 3)) {
                //System.out.println("Turning off spec");
                npc.setSpecialActivated(false);
            }
            //End of range 2h switching logic.

            //Update npc depending on the special attack state
            if (!npc.isSpecialActivated()) {
                transform(DEFAULT_BOT_ID);
                switchedWeaponTime = System.currentTimeMillis();
                specHits = 0;
            } else {
                transform(SPEC_BOT_ID);
                specHits++;
                switchedWeaponTime = System.currentTimeMillis();
            }
        }
        else {

            //Reset weapon
            transform(DEFAULT_BOT_ID);

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
    public CombatMethod getMethod() {
        return this;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();
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
