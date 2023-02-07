package com.aelous.model.entity.npc.bots;

import com.aelous.model.content.consumables.FoodConsumable;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCDeath;
import com.aelous.model.entity.npc.bots.impl.*;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.SecondsTimer;
import com.aelous.utility.Stopwatch;
import com.aelous.utility.timers.TimerKey;

/**
 * Represents an NPC Bot.
 *
 * @author Professor Oak
 */
public abstract class NPCBotHandler {

    //These should be public instead of private simply because we need the bot_id in CombatFactory to determine if we need to skull.
    /**
     * The id of the Rune main bot npc.
     */
    public static final int RUNE_MAIN_BOT_ID = 13004;

    /**
     * The id of the archer bot npc.
     */
    public static final int ARCHER_BOT_ID = 13006;

    public static final int PURE_ARCHER_BOT_ID = 13008;

    public static final int PURE_BOT_ID = 13000;

    public static final int F2P_BOT_ID = 13002;

    /**
     * Assigns a bot handler to specified {@link NPC}.
     */
    public static void assignBotHandler(NPC npc) {
        switch (npc.id()) {
            case RUNE_MAIN_BOT_ID -> npc.setBotHandler(new RuneMainBot(npc));
            case ARCHER_BOT_ID -> npc.setBotHandler(new ArcherBot(npc));
            case PURE_BOT_ID -> npc.setBotHandler(new PureBot(npc));
            case PURE_ARCHER_BOT_ID -> npc.setBotHandler(new PureArcherBot(npc));
            case F2P_BOT_ID -> npc.setBotHandler(new F2pBot(npc));
        }

        //If they haven't been given a combat method yet and they're a bot,
        //Simply use the their bot handler's choice of method.
        if (npc.getCombatMethod() == null) {//the name is null cuz cant find a def
            if (npc.getBotHandler() != null) {
                npc.setCombatMethod(npc.getBotHandler().getMethod());
            }
        }
    }

    /**
     * The npc, owner of this instance.
     */
    public NPC npc;

    /**
     * Constructs a new npc bot.
     * @param npc        The bot's npc id.
     */
    public NPCBotHandler(NPC npc) {
        this.npc = npc;
        this.eatDelay = new Stopwatch();
        this.vengeanceDelay = new SecondsTimer();
    }

    /**
     * Processes this bot.
     */
    public abstract void process();

    /**
     * Handles what happens when the bot
     * dies.
     */
    public abstract void onDeath(Player killer);

    /**
     * Gets the bot's combat method.
     */
    public abstract CombatMethod getMethod();

    /**
     * The amount of times we have eaten food.
     */
    private int eatCounter;

    /**
     * The delay for eating food.
     * Makes sure food isn't consumed too quick.
     */
    private final Stopwatch eatDelay;

    /**
     * The delay for casting vengeance
     * Makes sure vengeance is only cast every 30 seconds.
     */
    private final SecondsTimer vengeanceDelay;

    /**
     * Resets all attributes.
     */
    public void reset() {
        //Reset our attributes
        eatCounter = 0;
        npc.setSpecialAttackPercentage(100);
        npc.setSpecialActivated(false);
        NPCDeath.deathReset(npc);
        //Reset hitpoints
        npc.setHitpoints(npc.maxHp());
    }

    /**
     * Eats the specified {@link FoodConsumable.Food}.
     * @param food            The food to eat.
     * @param minDelayMs    The minimum delay between each eat in ms.
     */
    public void eat(FoodConsumable.Food food, int minDelayMs) {
        //Make sure delay has finished..
        if (eatDelay.elapsed(minDelayMs)) {
            int heal = food.getHeal();
            int currentHp = npc.hp();
            int maxHp = npc.maxHp();

            //Heal us..
            npc.setHitpoints(Math.min(currentHp + heal, maxHp));

            //Increase attack delay..
            npc.getTimers().extendOrRegister(TimerKey.COMBAT_ATTACK, 2);

            //Perform eat animation..
            npc.animate(829);

            //Increase counter..
            eatCounter++;

            //Reset the eat delay..
            eatDelay.reset();
        }
    }

    /**
     * Cast vengeances.
     * There's a delay, allowing it to only be cast every 30 seconds.
     */
    public void castVengeance() {

        //Make sure we don't already have vengeance active.
        boolean hasVengeance = npc.getAttribOr(AttributeKey.VENGEANCE_ACTIVE, false);
        if (hasVengeance) {
            return;
        }

        //Make sure delay has finished..
        if (!vengeanceDelay.active()) {

            //Perform veng animation..
            npc.animate(8316);

            //Perform veng graphic..
            npc.performGraphic(new Graphic(726, GraphicHeight.HIGH));

            //Set has vengeance..
            npc.putAttrib(AttributeKey.VENGEANCE_ACTIVE, true);

            //Reset the veng delay..
            vengeanceDelay.start(30);
        }
    }

    /**
     * Attempts to get the bot's current opponent.
     * Either it's the target or it's an attacker.
     * @return        The opponent player.
     */
    public Player getOpponent() {
        Entity p = npc.getCombat().getTarget();
        if (p == null) {
            p = npc.getAttrib(AttributeKey.LAST_DAMAGER);
        }
        if (p != null && p.isPlayer()) {
            return p.getAsPlayer();
        }
        return null;
    }

    public void resetOverheadPrayers(NPC n) {
        Prayers.deactivatePrayer(n, Prayers.PROTECT_FROM_MAGIC);
        Prayers.deactivatePrayer(n, Prayers.PROTECT_FROM_MELEE);
        Prayers.deactivatePrayer(n, Prayers.PROTECT_FROM_MISSILES);
        Prayers.deactivatePrayer(n, Prayers.SMITE);
    }

    /**
     * Gets the overhead prayer which the bot
     * should currently be using, based on the opponent's
     * choices.
     */
    public int getOverheadPrayer(final Player p, final boolean inDistance) {
        int prayer = -1;

        //Check if the enemy isn't in range..
        if (inDistance) {
            //Check if enemy is in range and if they're smiting..
            //If so, we will do the same.
            if (Prayers.usingPrayer(p, Prayers.SMITE)) {
                prayer = Prayers.SMITE;
            }
        }

        //Check if enemy is protecting against our combat type..
        //Or if they're farcasting..
        //If so, we will counter pray.
        if (prayer != Prayers.SMITE) {
            CombatType botType = null;
            if (this instanceof F2pBot)
                botType = CombatType.MELEE;
            if (this instanceof PureBot)
                botType = CombatType.MELEE;
            if (this instanceof RuneMainBot)
                botType = CombatType.MELEE;
            if (this instanceof PureArcherBot)
                botType = CombatType.RANGED;
            if (botType == null)
                System.err.println("unknown bot combat style: "+this);
            int counterPrayer = Prayers.getProtectingPrayer(botType);
            if (Prayers.usingPrayer(p, counterPrayer) || (!inDistance)) {
                //prayer = PrayerHandler.getProtectingPrayer(CombatFactory.getMethod(p).getCombatType());
                //We want their last hit combat type,
                //this protects against manually casting mage when using range/melee.
                //This is more delayed than using getProtectingPrayer (after hit vs before hit),
                //but it's also more realistic like OSRS.

                boolean melee = p.getAttribOr(AttributeKey.LAST_ATTACK_WAS_MELEE, false);
                boolean range = p.getAttribOr(AttributeKey.LAST_ATTACK_WAS_RANGED, false);
                boolean magic = p.getAttribOr(AttributeKey.LAST_ATTACK_WAS_MAGIC, false);
                if (melee) {
                    prayer = Prayers.PROTECT_FROM_MELEE;
                } else if (range) {
                    prayer = Prayers.PROTECT_FROM_MISSILES;
                } else if (magic) {
                    prayer = Prayers.PROTECT_FROM_MAGIC;
                }
            }
        }
        return prayer;
    }

    /**
     * Transforms an npc into a different one.
     * @param id        The new npc id.
     */
    public void transform(int id) {

        //Check if we haven't already transformed..
        if (npc.transmog() == id) {
            return;
        }

        //Set the transformation id.
        npc.transmog(id);
    }

    public int getEatCounter() {
        return eatCounter;
    }

}
