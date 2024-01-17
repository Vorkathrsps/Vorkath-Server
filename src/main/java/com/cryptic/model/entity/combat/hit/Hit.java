package com.cryptic.model.entity.combat.hit;

import com.cryptic.model.World;
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
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.vorkath.Vorkath;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Consumer;

/**
 * Represents a pending hit.
 *
 * @author Professor Oak
 */
public class Hit {
    public boolean toremove;
    public boolean showSplat;
    @Getter
    private HitMark hitMark;
    public boolean reflected;
    public boolean forceShowSplashWhenMissMagic;
    public boolean prayerIgnored = false;
    private static final Logger logger = LogManager.getLogger(Hit.class);
    private Entity attacker;
    private Entity target;
    private int damage;
    @Getter
    private int delay;
    @Getter
    public boolean checkAccuracy;
    private boolean accurate;
    @Getter
    public CombatType combatType;
    @Getter
    @Setter
    public boolean isMaxHit;
    @Getter
    public boolean pidIgnored;

    @Getter
    @Setter
    boolean invalidated = false;

    public Entity getSource() {
        return attacker;
    }

    public Hit setHitMark(HitMark hitMark) {
        this.hitMark = hitMark;
        return this;
    }

    public Hit(Entity attacker, Entity target) {
        this.attacker = attacker;
        this.target = target;
    }

    public Hit(Entity attacker, Entity target, CombatMethod method, boolean checkAccuracy, int delay, int damage, HitMark hitMark) {
        this.attacker = attacker;
        this.target = target;
        if (method instanceof CommonCombatMethod commonCombatMethod) combatType = commonCombatMethod.styleOf();
        this.checkAccuracy = checkAccuracy;
        this.damage = damage;
        this.delay = delay;
        this.hitMark = hitMark;
    }

    public Hit(Entity attacker, Entity target, CombatMethod method, int delay, int damage) {
        if (method instanceof CommonCombatMethod commonCombatMethod) combatType = commonCombatMethod.styleOf();
        this.attacker = attacker;
        this.target = target;
        this.delay = delay;
        this.damage = damage;
        this.hitMark = damage > 0 ? HitMark.DEFAULT : HitMark.MISSED;
    }

    /**
     * (Method) Has Type
     *
     * @param attacker
     * @param target
     * @param delay
     * @param method
     */
    public Hit(Entity attacker, Entity target, int delay, CombatMethod method) {
        this.attacker = attacker;
        this.target = target;
        this.delay = delay;
        if (method instanceof CommonCombatMethod commonCombatMethod) this.combatType = commonCombatMethod.styleOf();
    }

    public Hit(Entity attacker, Entity target, int damage, int delay, CombatType method) {
        this.attacker = attacker;
        this.target = target;
        this.damage = damage;
        this.delay = delay;
        this.combatType = method;
    }

    /**
     * (Method) Typeless
     *
     * @param attacker
     * @param target
     * @param delay
     * @param combatType
     */
    public Hit(Entity attacker, Entity target, int delay, CombatType combatType) {
        this.attacker = attacker;
        this.target = target;
        this.delay = delay;
        this.combatType = combatType;
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

    public Hit setDamage(int damage) {
        this.damage = damage;
        if (this.damage > 0) this.setHitMark(HitMark.DEFAULT);
        return this;
    }

    public void damageModifier(double damageModifier) {
        this.damage += damageModifier;
    }

    public Hit setCombatType(CombatType type) {
        this.combatType = type;
        return this;
    }

    public boolean isLocked() {
        return target.locked() && !target.isDamageOkLocked() && !target.isDelayDamageLocked() && !target.isMoveLockedDamageOk();
    }

    public void invalidate() {
        this.accurate = false;
        this.invalidated = true;
        this.toremove = true;
    }

    public int getMaximumHit() {
        int maxHit = -1;
        if (attacker instanceof Player) {
            if (this.getSource().getCombat().getCombatType() == CombatType.MELEE) maxHit = attacker.getCombat().getMaximumMeleeDamage();
            if (this.getSource().getCombat().getCombatType() == CombatType.RANGED) maxHit = attacker.player().getCombat().getMaximumRangedDamage();
            if (this.getSource().getCombat().getCombatType() == CombatType.MAGIC) maxHit = attacker.getCombat().getMaximumMagicDamage();
        }
        return maxHit;
    }

    public Hit roll() {
        if (attacker == null || target == null || hitMark == HitMark.HEALED) return this;
        MagicAccuracy magicAccuracy = new MagicAccuracy(this.attacker, this.target, this.combatType);
        RangeAccuracy rangeAccuracy = new RangeAccuracy(this.attacker, this.target, this.combatType);
        MeleeAccuracy meleeAccuracy = new MeleeAccuracy(this.attacker, this.target, this.combatType);
        if (this.attacker instanceof Player player)
            CombatFactory.sigils.sigilAccuracyBonus(player, this.target, rangeAccuracy, magicAccuracy, meleeAccuracy);
        if (target instanceof NPC npc) {
            if (npc.getCombatInfo() == null) {
                logger.warn("Missing combat information for {} {} {}", npc, npc.getMobName(), npc.id());
                return null;
            }
            if (npc.isCombatDummy()) this.checkAccuracy = false;
        }
        double chance = World.getWorld().random().nextDouble();
        if (this.checkAccuracy && this.combatType != null && !(target.isNpc() && target.npc().getCombatInfo() == null) && !(attacker.isNpc() && attacker.npc().getCombatInfo() == null)) {
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
        if (!checkAccuracy) this.accurate = true;
        if (!this.accurate) this.damage = 0;
        else this.damage = CombatFactory.calcDamageFromType(attacker, target, combatType);
        if (oneHitActive) this.damage = target.hp();
        if (alwaysHitActive) this.damage = alwaysHitDamage;
        if (!this.accurate && this.damage <= 0) this.setHitMark(HitMark.MISSED);
        else this.setHitMark(HitMark.DEFAULT);
        return this;
    }

    public void addCombatXp(Player player, CombatType style, FightStyle mode, boolean isAccurate, int damage) {
        if (combatType == null) return;
        double hXP = calculateHitpointsExperience(damage);
        double rmXP = calculateRangedOrMeleeXP(damage);
        switch (style) {
            case MELEE -> {
                switch (mode) {
                    case ACCURATE -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.ATTACK, rmXP);
                        }
                    }

                    case AGGRESSIVE -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.STRENGTH, rmXP);
                        }
                    }

                    case DEFENSIVE -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.DEFENCE, rmXP);
                        }
                    }

                    case CONTROLLED -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.ATTACK, rmXP / 1.33);
                            player.getSkills().addXp(Skills.STRENGTH, rmXP / 1.33);
                            player.getSkills().addXp(Skills.DEFENCE, rmXP / 1.33);
                        }
                    }
                }
            }

            case RANGED -> {
                switch (mode) {
                    case ACCURATE, AGGRESSIVE -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.RANGED, rmXP);
                        }
                    }

                    case DEFENSIVE -> {
                        if (isAccurate && damage > 0) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.RANGED, rmXP / 1.33);
                            player.getSkills().addXp(Skills.DEFENCE, rmXP / 1.33);
                        }
                    }
                }
            }

            case MAGIC -> {
                CombatSpell spell = player.getCombat().getCastSpell() != null ? player.getCombat().getCastSpell() : player.getCombat().getAutoCastSpell() != null ? player.getCombat().getAutoCastSpell() : player.getCombat().getPoweredStaffSpell() != null ? player.getCombat().getPoweredStaffSpell() : null;
                if (spell != null) {
                    int magicXPIndex;
                    int defenceXpIndex;
                    double mXP = 1;
                    double dXP = 1;
                    double mXPMultiplier = player.<Boolean>getAttribOr(AttributeKey.DEFENSIVE_AUTOCAST, false) ? 1.33 : 2.0;
                    for (magicXPIndex = 0; magicXPIndex < damage; magicXPIndex++) {
                        mXP += ((magicXPIndex + 1) * mXPMultiplier);
                    }
                    for (defenceXpIndex = 0; defenceXpIndex < damage; defenceXpIndex++) {
                        dXP += (defenceXpIndex);
                    }
                    double spellBaseXP = spell.baseExperience();
                    double magicXP = mXP + spellBaseXP;
                    double defenceXP = dXP;
                    if (isAccurate && damage > 0) {
                        if (player.<Boolean>getAttribOr(AttributeKey.DEFENSIVE_AUTOCAST, false)) {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.MAGIC, magicXP);
                            player.getSkills().addXp(Skills.DEFENCE, defenceXP);
                        } else {
                            player.getSkills().addXp(Skills.HITPOINTS, hXP);
                            player.getSkills().addXp(Skills.MAGIC, magicXP);
                        }
                        return;
                    }
                    player.getSkills().addXp(Skills.MAGIC, spellBaseXP);
                }
            }
        }
    }

    private double calculateRangedOrMeleeXP(int damage) {
        return Math.max((damage * (damage - 1D) / 2D), 2.0D);
    }

    private double calculateHitpointsExperience(int damage) {
        return Math.max((damage * (damage - 1D) / 2D) * 1.33D, 2.0D);
    }

    public Hit submit() {
        if (((this.target == null && isLocked()) || isInvalidated() || target.isNullifyDamageLock() || target.isNeedsPlacement()))
            return this;
        if (this.target.dead()) return this;
        if (this.attacker instanceof Player && this.target instanceof NPC npc) {
            CombatMethod method = CombatFactory.getMethod(npc);
            if (method instanceof CommonCombatMethod commonCombatMethod) commonCombatMethod.preDefend(this);
            if (npc.getCombatMethod() instanceof Vorkath vorkath) {
                if (vorkath.resistance != null) {
                    switch (vorkath.resistance) {
                        case PARTIAL -> this.setDamage((int) (this.getDamage() * 0.5D));
                        case FULL -> this.block();
                    }
                }
            }
        }
        if (this.target.getAttribOr(AttributeKey.INVULNERABLE, false)) this.accurate = false;
        if (this.damage >= this.getMaximumHit()) this.setMaxHit(true);
        if (this.attacker instanceof Player player) this.addCombatXp(player, this.combatType, player.getCombat().getFightType().getStyle(), this.accurate, this.damage);
        target.getCombat().getHitQueue().add(this);
        return this;
    }

    public Hit setIsReflected() {
        this.reflected = true;
        return this;
    }

    public Hit checkAccuracy(boolean checkAccuracy) {
        this.checkAccuracy = checkAccuracy;
        return this.roll();
    }

    public Consumer<Hit> postDamage;

    public Hit postDamage(Consumer<Hit> postDamage) {
        if (attacker.dead() || target.dead()) return this;
        this.postDamage = postDamage;
        return this;
    }

    public void update() {
        if (target == null) return;
        if (target.nextHits.size() >= 4) return;
        target.nextHits.add(this);
        target.getUpdateFlag().flag(Flag.FIRST_SPLAT);
    }

    public Hit block() {
        this.isMaxHit = false;
        this.accurate = false;
        this.damage = 0;
        return this;
    }

    public Hit ignorePrayer() {
        this.prayerIgnored = true;
        return this;
    }

    public Hit clientDelay(int delay) {
        this.delay = delay;
        return this;
    }

    public int getMark(Entity source, Entity target, Player observer) {
        return hitMark.getObservedType(this, source, target, observer, isMaxHit);
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
}

