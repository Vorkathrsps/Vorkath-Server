package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentInfo;
import lombok.Getter;
import lombok.Setter;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

public final class MagicAccuracy {

    @Getter
    @Setter
    public float modifier;
    @Getter
    Entity attacker;
    @Getter
    Entity defender;
    CombatType combatType;
    PreDamageEffectHandler handler = new PreDamageEffectHandler(new EquipmentDamageEffect());
    public double attackRoll = 0;
    public double defenceRoll = 0;
    @Getter
    public double chance = 0;

    public MagicAccuracy(Entity attacker, Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    public boolean successful(double selectedChance) {
        this.attackRoll = getAttackRoll();
        this.defenceRoll = getDefenceRoll();
        if (this.attackRoll > this.defenceRoll)
            this.chance = 1D - (this.defenceRoll + 2D) / (2D * (this.attackRoll + 1D));
        else this.chance = this.attackRoll / (2D * (this.defenceRoll + 1D));
        if (Hit.isDebugAccuracy()) this.attacker.message("[Magic] Chance To Hit: [" + String.format("%.2f%%", this.chance * 100) + "]");
        return this.chance > selectedChance;
    }

    private int getEquipmentBonusAttacker() {
        return this.attacker instanceof Player ? EquipmentInfo.totalBonuses(this.attacker, World.getWorld().equipmentInfo()).mage : this.attacker.getAsNpc().getCombatInfo().getBonuses().getMagic();
    }

    private int getMagicLevelAttacker() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.attacker.getSkills().level(Skills.MAGIC);
    }

    private int getMagicLevelDefender() {
        return this.defender instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.defender.getSkills().level(Skills.MAGIC);
    }

    private double getPrayerBonus() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, MYSTIC_WILL)) prayerBonus *= 1.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(this.attacker, MYSTIC_LORE)) prayerBonus *= 1.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(this.attacker, MYSTIC_MIGHT)) prayerBonus *= 1.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(this.attacker, AUGURY)) prayerBonus *= 1.25D; // 25% magic level boost
        }
        return prayerBonus;
    }

    private double getPrayerBonusDefender() {
        double prayerBonus = 1D;
        if (this.defender instanceof Player) {
            if (Prayers.usingPrayer(this.defender, AUGURY)) prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    public int getAttackRoll() {
        int magicLevel = getMagicLevelAttacker();
        int effectiveLevel = getEffectiveLevel(magicLevel);
        int attackBonus = getEquipmentBonusAttacker();
        var roll = effectiveLevel * (attackBonus + 64);
        float modification = this.modifier;
        if (modification > 0) roll *= modification;
        return roll;
    }

    private int getEffectiveLevel(int magicLevel) {
        int effectiveLevel = (int) (magicLevel * getPrayerBonus() + 8);
        if (this.attacker instanceof Player a) {
            this.handler.triggerMagicAccuracyModificationAttacker(a, this.combatType, this);
            if (a.getCombatSpecial() != null && a.isSpecialActivated()) {
                double specialMultiplier = a.getCombatSpecial().getAccuracyMultiplier();
                effectiveLevel *= specialMultiplier;
            }
        } else {
            effectiveLevel = magicLevel + 9;
        }
        return effectiveLevel;
    }

    private int getEquipmentBonusDefender() {
        if (this.defender instanceof Player player)
            return EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo()).getMagedef();
        else if (this.defender instanceof NPC npc) return npc.getCombatInfo().getBonuses().getMagicdefence();
        return 0;
    }

    public int getDefenceRoll() {
        int magicLevel = getMagicLevelDefender();
        int defenceBonus = getEquipmentBonusDefender();
        int effectiveLevel = 0;
        if (this.defender instanceof Player player) {
            magicLevel *= getPrayerBonusDefender();
            int defenceLevel = player.getSkills().level(Skill.DEFENCE.getId());
            switch (player.getCombat().getFightType().getStyle()) {
                case DEFENSIVE -> defenceLevel += 3;
                case CONTROLLED -> defenceLevel += 1;
            }
            effectiveLevel = (int) (0.7D * magicLevel + 0.3D * defenceLevel + 8);
        } else if (this.defender instanceof NPC) {
            effectiveLevel = magicLevel + 9;
        }
        return effectiveLevel * (defenceBonus + 64);
    }

}
