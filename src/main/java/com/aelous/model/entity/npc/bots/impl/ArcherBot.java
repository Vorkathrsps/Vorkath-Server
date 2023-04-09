package com.aelous.model.entity.npc.bots.impl;

import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
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
 * Represents a simple archer bot.
 * @author Professor Oak
 */
public class ArcherBot extends NPCBotHandler implements CombatMethod {

    private boolean ramboMode;
    private int ramboShots;
    
    public ArcherBot(NPC npc) {
        super(npc);
        npc.putAttrib(AttributeKey.VENGEANCE_ACTIVE, true);
        npc.getCombat().setRangedWeapon(RangedData.RangedWeapon.BALLISTA);
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

                if (npc.hp() < 40 + Utils.getRandom(15)) {

                    if (getEatCounter() < 28) {
                        super.eat(FoodConsumable.Food.SHARK, 1100);
                    }
                }

                //Cast vengeance when ever we can.
                super.castVengeance();

                //Sometimes go nuts
                if (Utils.getRandom(20) == 1) {
                    ramboMode = true;
                }
                if (ramboMode) {

                    npc.forceChat("Raaaaaarrrrgggghhhhhh!");

                    if (ramboShots++ >= 1) {
                        ramboShots = 0;
                        ramboMode = false;
                    }
                }

            } else {
                npc.forceChat("Gg");
            }

        } else {

            //Turn off prayers
            Prayers.closeAllPrayers(npc);

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
        return CombatFactory.RANGED_COMBAT;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED),1, CombatType.RANGED).checkAccuracy();
        hit.submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 7;
    }
}
