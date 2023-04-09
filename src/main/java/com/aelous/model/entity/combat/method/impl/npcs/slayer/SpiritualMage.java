package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Patrick van Elderen | January, 08, 2021, 08:48
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SpiritualMage extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        // Attack the player
        entity.animate(entity.attackAnimation());
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        target.hit(entity, hit, CombatType.MAGIC).checkAccuracy().submit();

        // Does NOT splash when miss!
        if(target instanceof Player) {
            Player playerTarget = (Player) target;
            NPC npc = (NPC) entity;
            if (hit > 0) {
                playerTarget.performGraphic(get_graphic(npc.id())); // Cannot protect from this.
            } else {
                playerTarget.performGraphic(new Graphic(85, GraphicHeight.HIGH,0)); // Cannot protect from this.
            }
        }
        return true;
    }

    private Graphic get_graphic(int npc) {
        return switch (npc) {
            case SPIRITUAL_MAGE_3161, BATTLE_MAGE -> new Graphic(78, GraphicHeight.LOW, 0);
            case SPIRITUAL_MAGE, SARADOMIN_PRIEST, BATTLE_MAGE_1611 -> new Graphic(76, GraphicHeight.HIGH, 0);
            default -> new Graphic(77, GraphicHeight.HIGH, 0);
        };
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 7;
    }
}
