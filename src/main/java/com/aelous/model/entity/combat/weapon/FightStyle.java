package com.aelous.model.entity.combat.weapon;

import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.player.Skills;

/**
 * A collection of constants that each represent a different fighting style.
 *
 * @author lare96
 */
public enum FightStyle {
    ACCURATE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skills.RANGED }
                : new int[] { Skills.ATTACK };
        }
    },
    AGGRESSIVE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skills.RANGED }
                : new int[] { Skills.STRENGTH };
        }
    },
    DEFENSIVE() {
        @Override
        public int[] skill(CombatType type) {
            return type == CombatType.RANGED ? new int[] { Skills.RANGED,
                Skills.DEFENCE } : new int[] { Skills.DEFENCE };
        }
    },
    CONTROLLED() {
        @Override
        public int[] skill(CombatType type) {
            return new int[] { Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE };
        }
    };

    /**
     * Determines the DefaultSkill trained by this fighting style based on the
     * {@link CombatType}.
     *
     * @param type
     *            the combat type to determine the DefaultSkill trained with.
     * @return the DefaultSkill trained by this fighting style.
     */
    public abstract int[] skill(CombatType type);
}
