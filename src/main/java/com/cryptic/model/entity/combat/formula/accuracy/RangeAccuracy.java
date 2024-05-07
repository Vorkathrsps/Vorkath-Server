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
import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.EAGLE_EYE;

public class RangeAccuracy implements AbstractAccuracy {

    @Getter @Setter public int modifier;
    @Getter @Setter Entity attacker, defender;
    CombatType combatType;
    public RangeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
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
    public double modifier() {
        return this.attacker() instanceof Player player ? player.sigil.processAccuracy(player, this.defender(), this) + modifier : modifier;
    }

    @Override
    public int getEquipmentBonusForAttacker() {
        return this.attacker instanceof Player ? this.attacker.getBonuses().totalBonuses(this.attacker, World.getWorld().equipmentInfo()).getRange() : this.attacker.getAsNpc().getCombatInfo().getBonuses().getRanged();
    }

    @Override
    public int getEquipmentBonusForDefender() {
        return this.defender instanceof Player ? this.defender.getBonuses().totalBonuses(this.defender, World.getWorld().equipmentInfo()).getRangedef() : this.defender.getAsNpc().getCombatInfo().getBonuses().getRangeddefence();
    }

    @Override
    public int getOffensiveSkillLevelAttacker() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo() != null && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().ranged : this.attacker.getSkills().level(Skills.RANGED);
    }

    @Override
    public int getDefensiveSKillLevelDefender() {
        return this.defender instanceof NPC npc && npc.getCombatInfo() != null && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().defence : this.defender.getSkills().level(Skills.DEFENCE);
    }

    @Override
    public double getPrayerBonusAttacker() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, SHARP_EYE)) prayerBonus *= 1.05D; // 5% range level boost
            else if (Prayers.usingPrayer(this.attacker, HAWK_EYE)) prayerBonus *= 1.10D; // 10% range level boost
            else if (Prayers.usingPrayer(this.attacker, EAGLE_EYE)) prayerBonus *= 1.15D; // 15% range level boost
            else if (Prayers.usingPrayer(this.attacker, RIGOUR)) prayerBonus *= 1.20D; // 20% range level boost
        }
        return prayerBonus;
    }

    @Override
    public double getPrayerBonusDefender() {
        double prayerBonus = 1D;
        if (defender instanceof Player) if (Prayers.usingPrayer(attacker, RIGOUR)) prayerBonus *= 1.25D;
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

}
