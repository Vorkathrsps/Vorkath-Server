package com.cryptic.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.cryptic.core.task.Task;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.chainedwork.Chain;

import java.util.function.BooleanSupplier;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.VORKATH_8061;

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
        BooleanSupplier waitForTicks = () -> ticks == 1;
        npc.waitUntil(waitForTicks, () -> {
            Chain.noCtx().runFn(1, () -> {
                player.animate(POKE_ANIMATION);
                player.message("You poke the dragon..");
            }).then(2, () -> {
                npc.animate(WAKE_ANIMATION);
            }).then(9, () -> {
                npc.transmog(VORKATH_8061, false);
                npc.setPositionToFace(player.tile());
                npc.setCombatInfo(World.getWorld().combatInfo(npc.id()));
                npc.setHitpoints(npc.getCombatInfo().stats.hitpoints);
                npc.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(player.getIndex(), player));
                npc.getMovementQueue().setBlockMovement(true);
                npc.setCombatMethod(new Vorkath());
                npc.getCombat().attack(player);
            });
        });
    }
}
