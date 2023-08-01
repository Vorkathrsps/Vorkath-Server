package com.cryptic.model.entity.combat.method.impl.npcs.verzik;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.FaceDirection;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.MapObjects;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;

import static com.cryptic.model.entity.attributes.AttributeKey.MINION_LIST;

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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }


    @Override
    public void onDeath(Player killer, NPC npc) {
        var boss = npc.<NPC>getAttribOr(AttributeKey.BOSS_OWNER, null);
        if (boss != null) {
            var pillars = boss.<ArrayList<NPC>>getAttribOr(MINION_LIST, null);
            pillars.removeIf(n -> n == npc);
            MapObjects.get(32687, npc.tile()).ifPresent(pillar -> {
                pillar.setId(32688);
                Chain.noCtx().delay(2, () -> {
                    pillar.setId(32689);
                    for (Player player : npc.closePlayers(1)) { // fall damage after pillar has fallen
                        player.hit(npc, 10);
                    }
                    for (Player player : npc.closePlayers()) {
                        if (pillar.bounds().contains(player.tile())) {
                            player.hit(npc, 10);
                            Direction direction = Direction.of(player.tile().x - npc.tile().x, player.tile().y - npc.tile().y);
                            FaceDirection face = FaceDirection.forTargetTile(npc.tile(), player.tile());
                            ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x() , direction.y()), 30, 60, 1157, face.direction);
                            player.setForceMovement(forceMovement);
                        }
                    }
                }).then(1, () -> pillar.animate(8104)).then(2, pillar::remove);
            });
        }
    }
}
