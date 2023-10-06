package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.magic.CombatSpell;
import com.cryptic.model.entity.combat.method.CombatMethod;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerStatus;
import lombok.Getter;
import lombok.Setter;
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
    public boolean prayerIgnored;

    public Hit forceShowSplashWhenMissMagic() {
        forceShowSplashWhenMissMagic = true;
        return this;
    }

    /**
     * The attacker instance.
     */
    private Entity attacker;

    /**
     * The victim instance.
     */
    private Entity target;

    public CombatSpell spell;

    public Hit setHitMark(HitMark hitMark) {
        this.hitMark = hitMark;
        return this;
    }

    /**
     * The total damage this hit will deal
     **/
    private int damage;

    /**
     * The delay of this hit
     **/
    @Getter private int delay;

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
     *
     * @return
     */

    public Entity getSource() {
        return attacker;
    }

    /**
     * Adjusts the hit delay with the characters update index (PID).
     */
    private void adjustDelay() {
        if (target != null && target.isNpc()) {
            return;
        }

        if (pidIgnored) {
            delay = -1;
            return;
        }

        if (attacker != null && attacker.pidOrderIndex != -1) {
            if (target != null && attacker.pidOrderIndex <= target.pidOrderIndex) {
                delay -= 1;
            }
        }

        if (delay < 1 && !combatType.isMelee()) {
            delay = 1;
        }
    }
    public Hit(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    /**
     * Constructs a QueueableHit with a total of {hitCountToGenerate} hits.
     **/
    public Hit(Entity attacker, Entity target, CombatMethod method, boolean checkAccuracy, int delay, int damage, HitMark hitMark) {
        this.attacker = attacker;
        this.target = target;
        if (method instanceof CommonCombatMethod commonCombatMethod) {
            combatType = commonCombatMethod.styleOf();
        }
        this.checkAccuracy = checkAccuracy;
        this.damage = damage;
        this.delay = delay;
        this.hitMark = hitMark;
    }

    public Hit builder(Entity attacker, Entity target, int damage, int delay) {
        return builder(attacker, target, damage, delay, this.combatType);
    }

    public static Hit builder(Entity attacker, Entity target, int damage, int delay, CombatType type) {
        Hit hit = new Hit(attacker, target, null, false, delay, damage, damage > 0 ? HitMark.DEFAULT : HitMark.MISSED);
        hit.delay = delay;
        hit.combatType = type;
        return hit;
    }

    public Hit delay(int d) {
        this.delay = Math.min(32767, Math.max(0, d));
        return this;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public Entity getTarget() {
        return this.target;
    }

    public boolean pidIgnored;

    public int decrementAndGetDelay() {
        if (attacker != null && attacker instanceof NPC) {
            return delay--;
        }
        return --delay;
    }

    public final int getDamage() {
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

    @Getter @Setter public boolean isMaxHit;

    public Hit damageModifier(double damageModifier) {
        this.damage += damageModifier;
        return this;
    }

    public Hit setCombatType(CombatType type) {
        this.combatType = type;
        return this;
    }

    public boolean isLocked() {
        return target.locked() && !target.isDamageOkLocked() && !target.isDelayDamageLocked() && !target.isMoveLockedDamageOk();
    }

    @Getter boolean invalidated = false;

    public Hit invalidate() {
        this.accurate = false;
        this.invalidated = true;
        return this;
    }

    public int getMaximumHit() {
        int maxHit = -1;
        if (attacker instanceof Player) {
            if (this.getSource().getCombat().getCombatType() == CombatType.MELEE) {
                maxHit = attacker.getCombat().getMaximumMeleeDamage();
            }
            if (this.getSource().getCombat().getCombatType() == CombatType.RANGED) {
                maxHit = attacker.player().getCombat().getMaximumRangedDamage();
            }
            if (this.getSource().getCombat().getCombatType()  == CombatType.MAGIC) {
                maxHit = attacker.getCombat().getMaximumMagicDamage();
            }
        }
        return maxHit;
    }

    /**
     * checks alwaysHit attrib and accuracy (depending on combat method+style). sets damage to 0 or maxhp or does no change at all, retaining existing {@link #damage} value set by {@link CombatFactory#calcDamageFromType(Entity, Entity, CombatType)}
     */
    public void applyAccuracyToMiss() {
        if (attacker == null || target == null || target.dead()) {
            return;
        }

        if (attacker.isPlayer() && target.isPlayer()) {
            if (target.getAsPlayer().getStatus() == PlayerStatus.TRADING) {
                target.getAsPlayer().getTrading().abortTrading();
            }
        }

        if (hitMark == HitMark.HEALED) {
            return;
        }

        if (target.isNpc() && target.getAsNpc().isCombatDummy()) {
            checkAccuracy = false;
        }

        var success = false;

        if (target.isNpc() && target.npc().getCombatInfo() == null) {
            System.err.println("missing cbinfo for " + target.npc());
        }

        MagicAccuracy magicAccuracy = new MagicAccuracy(attacker, target, combatType);
        RangeAccuracy rangeAccuracy = new RangeAccuracy(attacker, target, combatType);
        MeleeAccuracy meleeAccuracy = new MeleeAccuracy(attacker, target, combatType);

        if (combatType != null && !(target.isNpc() && target.npc().getCombatInfo() == null) && !(attacker.isNpc() && attacker.npc().getCombatInfo() == null)) {
            switch (combatType) {
                case MAGIC -> success = magicAccuracy.doesHit();
                case RANGED -> success = rangeAccuracy.doesHit();
                case MELEE -> success = meleeAccuracy.doesHit();
            }
        }

        accurate = !checkAccuracy || success;

        int damage;

        final int alwaysHitDamage = getTarget() != attacker ? attacker.getAttribOr(AttributeKey.ALWAYS_HIT, 0) : 0;
        final boolean alwaysHitActive = alwaysHitDamage > 0;
        final boolean oneHitActive = attacker.getAttribOr(AttributeKey.ONE_HIT_MOB, false);

        if (alwaysHitActive || oneHitActive)
            accurate = true;

        if (!accurate) {
            damage = 0;
        } else {
            if (oneHitActive) {
                damage = target.hp();
            } else if (alwaysHitActive) {
                damage = alwaysHitDamage;
            } else {
                damage = this.damage;
            }
        }
        this.damage = damage;
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public void submit() {
        if (target != null && !isLocked() && !isInvalidated()) {
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
        if (target.nextHits.size() >= 4)
            return;
        target.nextHits.add(this);
        target.getUpdateFlag().flag(Flag.FIRST_SPLAT);
        adjustDelay();
    }

    public Hit block() {
        accurate = false;
        damage = 0;
        return this;
    }

    public Hit ignorePrayer() {
        prayerIgnored = true;
        return this;
    }

    public Hit clientDelay(int delay) {
        this.delay = delay;
        return this;
    }

    @Getter
    @Setter
    private HitMark hitMark;

    public int getMark(Entity source, Entity target, Player observer) {
        return hitMark.getObservedType(this, source, target, observer, isMaxHit);
    }
}

