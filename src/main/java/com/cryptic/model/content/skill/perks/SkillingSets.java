package com.cryptic.model.content.skill.perks;

import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.container.equipment.Equipment;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public enum SkillingSets {

    GRACEFUL_OUTFIT(new int[]{11850, 11852, 11854, 11856, 11858, 11860}, Skill.AGILITY, 1.025, 2.0),
    FARMERS_OUTFIT(new int[]{13640, 13642, 13643, 13644, 13646}, Skill.FARMING, 1.025, 1.0),
    PYROMANCER_OUTFIT(new int[]{20704, 20706, 20708, 20710}, Skill.FIREMAKING, 1.025, 2.5),
    ANGLER_OUTFIT(new int[]{13258, 13259, 13260, 13261}, Skill.FISHING, 1.025, 2.0),
    PROSPECTOR_OUTFIT(new int[]{12013, 12014, 12015, 12016}, Skill.MINING, 1.025, 2.0),
    ZEALOT_OUTFIT(new int[]{25434, 25436, 25438, 25440}, Skill.PRAYER, 1.025, 2.5),
    RAIMENTS_OF_THE_EYE_OUTFIT(new int[]{26850, 26852, 26854, 26856}, Skill.RUNECRAFTING, 1.025, 1.0),
    ROGUES_OUTFIT(new int[]{5553, 5554, 5555, 5556, 5557}, Skill.THIEVING, 1.025, 2.5),
    FORESTRY_OUTFIT(new int[]{28169, 28171, 28173, 28175}, Skill.WOODCUTTING, 1.025, 2.0);

    @Getter public final Skill skillType;
    @Getter public final int[] set;
    @Getter public final double experienceBoost, chanceIncrease;
    @Getter public static final SkillingSets[] VALUES = SkillingSets.values();
    SkillingSets(int[] set, Skill skillType, double experienceBoost, double chanceIncrease) {
        this.set = set;
        this.skillType = skillType;
        this.experienceBoost = experienceBoost;
        this.chanceIncrease = chanceIncrease;
    }
}
