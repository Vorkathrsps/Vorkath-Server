package com.aelous.model.entity.combat.method.impl.npcs.godwars.saradomin;

import com.aelous.model.entity.attributes.AttributeKey;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zilyana extends CommonCombatMethod {

    public static boolean isMinion(NPC n) {
        return n.id() >= 2206 && n.id() <= 2208;
    }

    private static final Area ENCAMPMENT = new Area(2888, 5257, 2908, 5276);

    public static Area getENCAMPMENT() {
        return ENCAMPMENT;
    }

    private static Entity lastBossDamager = null;

    public static Entity getLastBossDamager() {
        return lastBossDamager;
    }

    public static void setLastBossDamager(Entity lastBossDamager) {
        Zilyana.lastBossDamager = lastBossDamager;
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
        if (entity.isNpc()) {
            NPC npc = (NPC) entity;
            Player player = (Player) target;
            int melee_distance = entity.tile().distance(target.tile());
            boolean canMelee = melee_distance <= 1;
            if (Utils.rollDie(3, 1)) {
                npc.forceChat(Utils.randomElement(QUOTES));
            }

            if (canMelee) {
                entity.animate(6967);
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
                // If we're in melee distance it's actually classed as if the target hit us -- has an effect on auto-retal in gwd!
                if (GwdLogic.isBoss(npc.id())) {
                    Map<Entity, Long> last_attacked_map = npc.getAttribOr(AttributeKey.LAST_ATTACKED_MAP, new HashMap<Entity, Long>());
                    last_attacked_map.put(target, System.currentTimeMillis());
                    npc.putAttrib(AttributeKey.LAST_ATTACKED_MAP, last_attacked_map);
                }
            } else {
                entity.animate(6970);
                npc.getTimers().extendOrRegister(TimerKey.ZILY_SPEC_COOLDOWN, 7);
                npc.getMovementQueue().clear();
                player.graphic(1221, GraphicHeight.LOW, 30);
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
            }
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
