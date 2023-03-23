package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.Task;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Tuple;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VORKATH_8061;

public class WakeUpVorkath extends Task {

    private static final Animation POKE_ANIMATION = new Animation(827, Priority.HIGH);
    private static final Animation WAKE_ANIMATION = new Animation(7950, Priority.HIGH);

    private final Player player;
    private int ticks;

    public WakeUpVorkath(Player player, int ticks) {
        super("WakeUpVorkathTask", 1, player, true);
        this.player = player;
        this.ticks = ticks;
    }

    @Override
    protected void execute() {
        if (player == null || player.dead()) {
            this.stop();
            return;
        }

        NPC npc = player.getInstancedArea().getNpcs().get(0);
        if (npc == null) {
            this.stop();
            return;
        }

        ticks++;
        npc.getCombat().reset();
        if (ticks == 1) {
            player.animate(POKE_ANIMATION);
            player.message("You poke the dragon..");
        } else if (ticks == 2) {
            npc.animate(WAKE_ANIMATION);
            npc.setPositionToFace(player.tile());
        } else if (ticks == 9) {
            //Remove sleeping vorkath from the world
            npc.transmog(8061);
            npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
            Chain.bound(null).name("VorkathWakeTask").runFn(3, () -> {
                npc.getMovementQueue().setBlockMovement(true);
                npc.setCombatMethod(new Vorkath());
                npc.getCombat().attack(player);
            });
            this.stop();
        }
    }
}
