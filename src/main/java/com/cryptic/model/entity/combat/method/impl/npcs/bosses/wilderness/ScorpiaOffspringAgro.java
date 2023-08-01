package com.cryptic.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;

/**
 * @author Patrick van Elderen | February, 24, 2021, 19:03
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ScorpiaOffspringAgro implements AggressionCheck {

    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        return Poison.poisoned(victim);
    }
}
