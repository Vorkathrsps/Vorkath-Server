package com.aelous.model.entity.combat.hit;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.AccuracyFormula;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracyNpc;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracyNpc;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.method.CombatMethod;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.PlayerStatus;

import java.security.SecureRandom;
import java.util.function.Consumer;

/**
 * Represents a pending hit.
 *
 * @author Professor Oak
 */
public class Hit {

    public boolean toremove;
    public boolean showSplat;
    /**
     * if its a veng/recoil ring type of hit, in this case. this stops infinite loops of vengeance hits/recoil ring
     * repeating on reflected damage.
     */
    public boolean reflected;

    public boolean forceShowSplashWhenMissMagic;

    public Hit forceShowSplashWhenMissMagic() {
        forceShowSplashWhenMissMagic = true;
        return this;
    }

    /**
     * The attacker instance.
     */
    private final Entity attacker;

    /**
     * The victim instance.
     */
    private final Entity target;

    public CombatSpell spell;
    public SplatType splatType;

    public Splat splat;

    /**
     * The total damage this hit will deal
     **/
    private int damage;

    public int getDelay() {
        return this.delay;
    }

    /**
     * The delay of this hit
     **/
    private int delay;

    /**
     * Check accuracy of the hit?
     **/
    private boolean checkAccuracy;

    /**
     * Was the hit accurate?
     **/
    private boolean accurate;

    /**
     * Cache the combat type
     */
    private CombatType combatType;


    /**
     * Damage Src Finder
     * @return
     */
    public Entity getDamageSource() {
        return this.attacker;
    }

    /**
     * Adjusts the hit delay with the characters update index (PID).
     */
    private void adjustDelay() {

        if (attacker.isNpc() || target.isNpc() || attacker.pidOrderIndex == -1) {
            return;
        }

       // if (damageType == DamageType.DWARF_MULTICANNON || damageType == DamageType.VENOM || damageType == DamageType.POISON) {
        //    return;
       // }

        if (attacker.pidOrderIndex <= target.pidOrderIndex) {
            delay -= 1;
        }

        if (delay < 1 && combatType != CombatType.MELEE) {
            delay = 1;
        }
    }

    /**
     * Constructs a QueueableHit with a total of {hitCountToGenerate} hits.
     **/
    public Hit(Entity attacker, Entity target, CombatMethod method, boolean checkAccuracy, int delay, int damage) {
        this.attacker = attacker;
        this.target = target;
        if (method instanceof CommonCombatMethod) {
            CommonCombatMethod commonCombatMethod = (CommonCombatMethod) method;
            combatType = commonCombatMethod.styleOf();
        }
        this.checkAccuracy = checkAccuracy;
        this.damage = damage;
        applyAccuracyToMiss();
        this.delay = delay;
        this.adjustDelay();
        this.splatType = damage < 1 ? SplatType.BLOCK_HITSPLAT : SplatType.HITSPLAT;
        }

    public Hit builder(Entity attacker, Entity target, int damage, int delay) {
        return builder(attacker, target, damage, delay, this.combatType);
    }

    public static Hit builder(Entity attacker, Entity target, int damage, int delay, CombatType type) {
        Hit hit = new Hit(attacker, target, null, false, delay, damage);
        hit.delay = delay;
        hit.combatType = type;
        return hit;
    }

    public Hit delay(int d) {
        this.delay = Math.max(0, d);
        return this;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public Entity getTarget() {
        return this.target;
    }


    public int decrementAndGetDelay() {
        return --delay;
    }

    public int getDamage() {
        return damage;
    }

    public Hit setAccurate(boolean accurate) {
        this.accurate = accurate;
        return this;
    }

    public boolean isAccurate() {
        return accurate;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Hit damageModifier(double damageModifier) {
        this.damage += damageModifier;
        return this;
    }

    public Hit setCombatType(CombatType type) {
        this.combatType = type;
        return this;
    }

    public boolean invalid() {

        // Locked, and the lock isn't one that allows damage through.
        if (target.locked() && !target.isDamageOkLocked() && !target.isDelayDamageLocked())
            return true;

        return false;
    }

    /**
     * checks alwaysHit attrib and accuracy (depending on combat method+style). sets damage to 0 or maxhp or does no change at all, retaining existing {@link #damage} value set by {@link CombatFactory#calcDamageFromType(Mob, Mob, CombatType)}
     */
    private void applyAccuracyToMiss() {
        if (attacker == null || target == null) {
            return;
        }

        if (target.dead()) {
            //System.out.println(target.getMobName() + " is dead.");
            return;
        }

        if (attacker.isPlayer() && target.isPlayer()) {
            if (target.getAsPlayer().getStatus() == PlayerStatus.TRADING) {
                target.getAsPlayer().getTrading().abortTrading();
            }
        }

        if (splatType == SplatType.NPC_HEALING_HITSPLAT) {
            return;
        }

        if (target.isNpc() && target.getAsNpc().isCombatDummy()) {
            checkAccuracy = false;
        }

        var success = false;

        if (combatType != null) {
            switch (combatType) {
                case MAGIC -> {
                    if (target.isPlayer())
                        success = MagicAccuracy.doesHit(attacker, target, combatType);
                    else {
                        success = MagicAccuracyNpc.doesHit(attacker, target, combatType);
                    }
                }
                case RANGED -> {
                    if (target.isPlayer())
                        success = RangeAccuracy.doesHit(attacker, target, combatType);
                    else {
                        success = RangeAccuracyNpc.doesHit(attacker, target, combatType);
                    }
                }
                case MELEE -> success = AccuracyFormula.doesHit(attacker, target, combatType);

                default -> AccuracyFormula.doesHit(attacker, target, combatType);
            }
        }

        //Was the hit accurate?
        accurate = !checkAccuracy || success;

        int damage;

        final int alwaysHitDamage = attacker.getAttribOr(AttributeKey.ALWAYS_HIT, 0);
        final boolean alwaysHitActive = alwaysHitDamage > 0;
        final boolean oneHitActive = attacker.getAttribOr(AttributeKey.ONE_HIT_MOB, false);

        if (alwaysHitActive || oneHitActive)
            accurate = true;

        if (!accurate) {
            //The hit wasn't accurate. Blocked by defence. Don't do any damage.
            damage = 0;
        } else {
            //The hit was accurate. Getting random damage..
            if (oneHitActive) {
                damage = target.hp();
            } else if (alwaysHitActive) {
                damage = alwaysHitDamage;
            } else {
                //When Nex uses turmoil his hits are boosted by 10%
                //Yup lol ur server isnt even properly configured and shit welp have fun XD
                if (attacker instanceof NPC) {
                    NpcDefinition def = attacker.getAsNpc().def();
                    String name = def.name;
                    if (attacker.isNpc() && name != null && name.equalsIgnoreCase("Nex") && attacker.<Boolean>getAttribOr(AttributeKey.TURMOIL_ACTIVE, false)) {
                        this.damage *= 1.10;
                    }
                }
                // use existing damage
                damage = this.damage;
            }
        }

        //Update total damage
        this.damage = damage;
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public void submit() {
        if (target != null && !invalid()) {
                CombatFactory.addPendingHit(this);
            }
        }

    @Override
    public String toString() {
        return "PendingHit{" +
            "attacker=" + attacker +
            ", target=" + target +
            ", dmg=" + damage +
            ", delay=" + delay +
            ", checkAccuracy=" + checkAccuracy +
            ", accurate=" + accurate +
            ", combatType=" + combatType +
            '}';
    }

    public Hit setIsReflected() {
        reflected = true;
        return this;
    }

    public Hit checkAccuracy() {
        checkAccuracy = true;
        applyAccuracyToMiss();
        return this;
    }

    public Hit spell(CombatSpell spell) {
        this.spell = spell;
        return this;
    }

    public Hit graphic(Graphic graphic) {
        this.target.graphic(graphic.id(), graphic.getHeight(), graphic.delay());
        return this;
    }

    /**
     * called after a hit has been executed and appears visually. will be finalized and damage cannot change.
     */
    public Consumer<Hit> postDamage;

    public Hit postDamage(Consumer<Hit> postDamage) {
        this.postDamage = postDamage;
        return this;
    }

    public void playerSync() {
        if (target == null) return;
        if (target.splats.size() >= 4)
            return;
        target.splats.add(new Splat(getDamage(), splatType));
        target.getUpdateFlag().flag(Flag.FIRST_SPLAT);
    }
}

