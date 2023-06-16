package com.aelous.model.entity.combat.method.impl.npcs.verzik;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.FaceDirection;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.MapObjects;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;

import static com.aelous.model.entity.attributes.AttributeKey.MINION_LIST;

public class VerzikPillar extends CommonCombatMethod {
    @Override
    public void init(NPC npc) {
        npc.noRetaliation(true);
        npc.getCombat().setAutoRetaliate(false);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        return false;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 0;
    }


    @Override
    public void onDeath(Player killer, NPC npc) {
        var boss = npc.<NPC>getAttribOr(AttributeKey.BOSS_OWNER, null);
        if (boss != null) {
            var pillars = boss.<ArrayList<NPC>>getAttribOr(MINION_LIST, null);
            pillars.removeIf(n -> n == npc);
            MapObjects.get(32687, npc.tile()).ifPresent(pillar -> {
                pillar.setId(32688); // falling
                Chain.noCtx().delay(2, () -> {
                    for (Player player : npc.closePlayers(1)) { // fall damage after pillar has fallen
                        player.hit(npc, 10);
                    }
                    pillar.setId(32689); // fallen object
                    for (Player player : npc.closePlayers()) {
                        if (pillar.bounds().contains(player.tile())) {
                            // overlapping, cant move, need to be forced moved
                            Direction direction = Direction.of(player.tile().x - npc.tile().x, player.tile().y - npc.tile().y);
                            FaceDirection face = FaceDirection.forTargetTile(npc.tile(), player.tile());
                            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x() , direction.y()), 30, 60, 1157, face.direction);
                            player.setForceMovement(forceMovement);
                        }
                    }
                });
            });

        }
    }
}
