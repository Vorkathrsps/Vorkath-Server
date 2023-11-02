package com.cryptic.model.entity.combat.method.impl.npcs.godwars.saradomin;

import com.cryptic.model.entity.attributes.AttributeKey;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zilyana extends CommonCombatMethod {
    private static final Area ENCAMPMENT = new Area(2888, 5257, 2908, 5276);

    public static Area getENCAMPMENT() {
        return ENCAMPMENT;
    }

    private final List<String> QUOTES = Arrays.asList("Death to the enemies of the light!",
        "Slay the evil ones!",
        "Saradomin lend me strength!",
        "By the power of Saradomin!",
        "May Saradomin be my sword!",
        "Good will always triumph!",
        "Forward! Our allies are with us!",
        "Saradomin is with us!",
        "In the name of Saradomin!",
        "Attack! Find the Godsword!");

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return false;

        NPC npc = (NPC) entity;

        if (GwdLogic.isBoss(npc.id())) {
            Map<Entity, Long> last_attacked_map = npc.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
            last_attacked_map.put(target, System.currentTimeMillis());
            npc.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
        }

        if (Utils.rollDie(3, 1)) npc.forceChat(Utils.randomElement(QUOTES));

        if (Utils.rollDice(50)) {
            if (entity.getTimers().left(TimerKey.ZILY_SPEC_COOLDOWN) > 0) return false;
            magic();
        } else {
            melee();
        }
        return true;
    }

    public void melee() {
        entity.animate(6967);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
    }

    public void magic() {
        entity.animate(6970);
        entity.getTimers().extendOrRegister(TimerKey.ZILY_SPEC_COOLDOWN, 7);
        entity.getMovementQueue().clear();
        target.graphic(1221, GraphicHeight.LOW, 30);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }
}
