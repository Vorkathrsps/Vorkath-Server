package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import lombok.Getter;
import lombok.Setter;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

public final class MagicAccuracy implements AbstractAccuracy {

    @Getter @Setter public int modifier;
    @Getter final Entity attacker;
    @Getter final Entity defender;
    CombatType combatType;

    public MagicAccuracy(final Entity attacker, final Entity defender, CombatType combatType) {
        this.attacker = attacker;
        this.defender = defender;
        this.combatType = combatType;
    }

    @Override
    public Entity attacker() {
        return this.attacker;
    }

    @Override
    public Entity defender() {
        return this.defender;
    }

    @Override
    public CombatType getCombatType() {
        return this.combatType;
    }

    @Override
    public int modifier() {
        return this.modifier;
    }

    @Override
    public int getEquipmentBonusForAttacker() {
        return this.attacker instanceof Player ? this.attacker.getBonuses().totalBonuses(this.attacker, World.getWorld().equipmentInfo()).mage : this.attacker.getAsNpc().getCombatInfo().getBonuses().getMagic();
    }

    @Override
    public int getEquipmentBonusForDefender() {
        if (this.defender instanceof Player player) return player.getBonuses().totalBonuses(player, World.getWorld().equipmentInfo()).getMagedef();
        else if (this.defender instanceof NPC npc) return npc.getCombatInfo().getBonuses().getMagicdefence();
        else return 0;
    }

    @Override
    public int getOffensiveSkillLevelAttacker() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.attacker.getSkills().level(Skills.MAGIC);
    }

    @Override
    public int getDefensiveSKillLevelDefender() {
        return this.defender instanceof NPC npc && npc.getCombatInfo() != null ? npc.getCombatInfo().getStats().magic : this.defender.getSkills().level(Skills.MAGIC);
    }

    @Override
    public double getPrayerBonusAttacker() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player player) {
            if (Prayers.usingPrayer(player, MYSTIC_WILL)) prayerBonus *= 1.05D; // 5% magic level boost
            else if (Prayers.usingPrayer(player, MYSTIC_LORE)) prayerBonus *= 1.10D; // 10% magic level boost
            else if (Prayers.usingPrayer(player, MYSTIC_MIGHT)) prayerBonus *= 1.15D; // 15% magic level boost
            else if (Prayers.usingPrayer(player, AUGURY)) prayerBonus *= 1.25D; // 25% magic level boost
        }

        System.out.println(prayerBonus);
        return prayerBonus;
    }

    @Override
    public double getPrayerBonusDefender() {
        double prayerBonus = 1D;
        if (this.defender instanceof Player) {
            if (Prayers.usingPrayer(this.defender, AUGURY)) prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }

    @Override
    public int getOffensiveStyleBonus() {
        var style = this.attacker().getCombat().getFightType().getStyle();
        return style.equals(FightStyle.ACCURATE) ? 3 : 0;
    }

    @Override
    public int getDefensiveStyleBonus() {
        var style = this.defender().getCombat().getFightType().getStyle();
        return style.equals(FightStyle.DEFENSIVE) ? 3 : style.equals(FightStyle.CONTROLLED) ? 1 : 0;
    }

    @Override
    public int getDefenceRoll() {
        double prayer = this.getPrayerBonusDefender();
        int defenceLevel = (int) Math.floor(this.getDefensiveSKillLevelDefender() * prayer);
        int bonus = this.getEquipmentBonusForDefender();
        int effectiveDefence = 0;
        int effectiveMagic = this.getOffensiveSkillLevelAttacker();
        if (this.defender() instanceof NPC) effectiveDefence = defenceLevel + 9;
        else if (this.defender() instanceof Player) {
            bonus += this.getDefensiveStyleBonus();
            effectiveMagic *= 0.7D;
            effectiveMagic += defenceLevel;
            effectiveMagic *= 0.3D;
            effectiveMagic += 8;
            effectiveDefence += effectiveMagic;
        }
        return effectiveDefence * (bonus + 64);
    }

}
