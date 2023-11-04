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
import com.cryptic.utility.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
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
    public boolean prayerIgnored = false;

    public Hit forceShowSplashWhenMissMagic() {
        forceShowSplashWhenMissMagic = true;
        return this;
    }

    private static final Logger logger = LogManager.getLogger(Hit.class);

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
    public Hit adjustDelayToPID() {
        if (attacker instanceof NPC) {
            return this;
        }

        if (this.isPidIgnored()) {
            this.delay = -1;
        }

        if (this.attacker != null && this.attacker.getCombat().getTarget() != null && this.attacker.pidOrderIndex != -1) {
            if (this.attacker.getCombat().getTarget() != null && this.attacker.pidOrderIndex <= this.attacker.getCombat().getTarget().pidOrderIndex) {
                this.delay--;
                if (this.delay < 1 && this.attacker.getCombat().getCombatType() != CombatType.MELEE)
                    this.delay = 1;
            }
        }

        return this;
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

    public Hit(Entity attacker, Entity target, CombatMethod method, int delay, int damage) {
        if (method instanceof CommonCombatMethod commonCombatMethod) {
            combatType = commonCombatMethod.styleOf();
        }
        this.attacker = attacker;
        this.target = target;
        this.delay = delay;
        this.damage = damage;
        this.hitMark = damage > 0 ? HitMark.DEFAULT : HitMark.MISSED;
    }

    public Hit(Entity attacker, Entity target, int delay, boolean checkAccuracy, CombatType combatType, CombatMethod method) {
        this.attacker = attacker;
        this.target = target;
        this.delay = delay;
        this.checkAccuracy = checkAccuracy;
        this.combatType = combatType;
        if (this.combatType == null && method instanceof CommonCombatMethod commonCombatMethod) this.combatType = commonCombatMethod.styleOf();
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

    @Getter public boolean pidIgnored;

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

    public Hit rollAccuracyAndDamage() {
        if (attacker == null || target == null || hitMark == HitMark.HEALED) return null;
        MagicAccuracy magicAccuracy = new MagicAccuracy(attacker, target, combatType);
        RangeAccuracy rangeAccuracy = new RangeAccuracy(attacker, target, combatType);
        MeleeAccuracy meleeAccuracy = new MeleeAccuracy(attacker, target, combatType);
        if (target instanceof NPC npc) {
            if (npc.getCombatInfo() == null) {
                logger.warn("Missing combat information for {} {} {}", npc, npc.getMobName(), npc.id());
                return null;
            }
            if (npc.isCombatDummy()) {
                checkAccuracy = false;
                accurate = true;
            }
        }

        if (checkAccuracy && combatType != null && !(target.isNpc() && target.npc().getCombatInfo() == null) && !(attacker.isNpc() && attacker.npc().getCombatInfo() == null)) {
            var chance = Utils.THREAD_LOCAL_RANDOM.get().nextDouble();
            switch (combatType) {
                case MAGIC -> accurate = magicAccuracy.successful(chance);
                case RANGED -> accurate = rangeAccuracy.successful(chance);
                case MELEE -> accurate = meleeAccuracy.successful(chance);
            }
        }
        final int alwaysHitDamage = getTarget() != attacker ? attacker.getAttribOr(AttributeKey.ALWAYS_HIT, 0) : 0;
        final boolean alwaysHitActive = alwaysHitDamage > 0;
        final boolean oneHitActive = attacker.getAttribOr(AttributeKey.ONE_HIT_MOB, false);
        if (alwaysHitActive || oneHitActive) accurate = true;
        if (oneHitActive) this.damage = target.hp();
        if (alwaysHitActive) this.damage = alwaysHitDamage;
        if (!checkAccuracy) accurate = true;
        if (!accurate) this.damage = 0;
        else this.damage = CombatFactory.calcDamageFromType(attacker, target, combatType);
        if (this.damage == 0) this.hitMark = HitMark.MISSED;
        else this.hitMark = HitMark.DEFAULT;
        return this;
    }

    public CombatType getCombatType() {
        return combatType;
    }

    public Hit submit() {
        if (target != null && !isLocked() && !isInvalidated()) {
            CombatFactory.addPendingHit(this);
            return this;
        }
        return null;
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
    public Consumer<Hit> mutate;

    public Hit postDamage(Consumer<Hit> postDamage) {
        this.postDamage = postDamage;
        return this;
    }

    public Hit conditions(Consumer<Hit> mutate) {
        this.mutate = mutate;
        return this;
    }

    public void playerSync() {
        if (target == null) return;
        if (target.nextHits.size() >= 4)
            return;
        target.nextHits.add(this);
        target.getUpdateFlag().flag(Flag.FIRST_SPLAT);
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

