package com.cryptic.model.entity.combat.formula.accuracy;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.combat.weapon.FightStyle;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.EquipmentBonuses;
import lombok.Getter;
import lombok.Setter;

import static com.cryptic.model.entity.combat.prayer.default_prayer.Prayers.*;

public class MeleeAccuracy implements AbstractAccuracy {

    @Getter @Setter public int modifier;
    @Getter @Setter Entity attacker, defender;
    CombatType combatType;
    public MeleeAccuracy(Entity attacker, Entity defender, CombatType combatType) {
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
        int bonus = 0;
        if (this.attacker instanceof Player) {
            EquipmentBonuses attackerBonus = this.attacker.getBonuses().totalBonuses(this.attacker, World.getWorld().equipmentInfo());
            final AttackType type = this.attacker.getCombat().getFightType().getAttackType();
            switch (type) {
                case STAB -> bonus = attackerBonus.getStab();
                case CRUSH -> bonus = attackerBonus.getCrush();
                case SLASH -> bonus = attackerBonus.getSlash();
            }
        } else if (this.attacker instanceof NPC npc) {
            bonus = npc.getCombatInfo().getBonuses().getAttack();
        }
        return bonus;
    }
    @Override
    public int getEquipmentBonusForDefender() {
        int bonus = 0;
        AttackType type;
        if (this.defender instanceof NPC npc) {
            var stats = npc.getCombatInfo().bonuses;
            type = this.attacker instanceof Player player ? player.getCombat().getAttackType() : npc.getCombat().getAttackType();
            if (type != null) {
                if (npc.getCombatInfo() != null) {
                    if (npc.getCombatInfo().stats != null) {
                        switch (type) {
                            case STAB -> bonus = stats.stabdefence;
                            case CRUSH -> bonus = stats.crushdefence;
                            case SLASH -> bonus = stats.slashdefence;
                        }
                    }
                }
            }
        } else if (this.defender instanceof Player) {
            var stats = this.defender.getBonuses().totalBonuses(this.defender, World.getWorld().equipmentInfo());
            type = this.defender.getCombat().getAttackType();
            if (type != null) {
                switch (type) {
                    case STAB -> bonus = stats.stabdef;
                    case CRUSH -> bonus = stats.crushdef;
                    case SLASH -> bonus = stats.slashdef;
                }
            }
        }
        return bonus;
    }
    @Override
    public int getOffensiveSkillLevelAttacker() {
        return this.attacker instanceof NPC npc && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().attack : this.attacker.getSkills().level(Skills.ATTACK);
    }
    @Override
    public int getDefensiveSKillLevelDefender() {
        return this.defender instanceof NPC npc && npc.getCombatInfo().stats != null ? npc.getCombatInfo().getStats().defence : this.defender.getSkills().level(Skills.DEFENCE);
    }
    @Override
    public double getPrayerBonusAttacker() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.attacker, CLARITY_OF_THOUGHT)) prayerBonus *= 1.05D;
            else if (Prayers.usingPrayer(this.attacker, IMPROVED_REFLEXES)) prayerBonus *= 1.10D;
            else if (Prayers.usingPrayer(this.attacker, INCREDIBLE_REFLEXES)) prayerBonus *= 1.15D;
            else if (Prayers.usingPrayer(this.attacker, CHIVALRY)) prayerBonus *= 1.15D;
            else if (Prayers.usingPrayer(this.attacker, PIETY)) prayerBonus *= 1.20D;
        }
        return prayerBonus;
    }
    @Override
    public double getPrayerBonusDefender() {
        double prayerBonus = 1D;
        if (this.attacker instanceof Player) {
            if (Prayers.usingPrayer(this.defender, THICK_SKIN)) prayerBonus *= 1.05D;
            else if (Prayers.usingPrayer(this.defender, ROCK_SKIN)) prayerBonus *= 1.10D;
            else if (Prayers.usingPrayer(this.defender, STEEL_SKIN)) prayerBonus *= 1.15D;
            else if (Prayers.usingPrayer(this.defender, CHIVALRY)) prayerBonus *= 1.20D;
            else if (Prayers.usingPrayer(this.defender, PIETY)) prayerBonus *= 1.25D;
        }
        return prayerBonus;
    }
    @Override
    public int getOffensiveStyleBonus() {
        var style = this.attacker().getCombat().getFightType().getStyle();
        return style.equals(FightStyle.ACCURATE) ? 3 : style.equals(FightStyle.CONTROLLED) ? 1 : 0;
    }

    @Override
    public int getDefensiveStyleBonus() {
        var style = this.defender().getCombat().getFightType().getStyle();
        return style.equals(FightStyle.DEFENSIVE) ? 3 : style.equals(FightStyle.CONTROLLED) ? 1 : 0;
    }

}
