package com.cryptic.model.content.title.req.impl.other;

import com.cryptic.model.content.title.req.TitleRequirement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skill;

/**
 * Created by Kaleem on 25/03/2018.
 */
public class MasteryNonCombat extends TitleRequirement {

    public MasteryNonCombat() {
        super("Reach level 99 in 1 <br>non combat skill");
    }

    @Override
    public boolean satisfies(Player player) {
        return Skill.ALL.stream().filter(skill -> !skill.isCombatSkill()).map(player.getSkills()::getMaxLevel).anyMatch(skill -> skill >= 99);
    }

}
