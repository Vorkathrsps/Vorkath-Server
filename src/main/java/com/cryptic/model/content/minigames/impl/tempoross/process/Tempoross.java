package com.cryptic.model.content.minigames.impl.tempoross.process;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.World;
import com.cryptic.model.content.minigames.impl.tempoross.skilling.interaction.SpiritPoolsInteraciton;
import com.cryptic.model.content.minigames.impl.tempoross.state.TemporossState;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;

public class Tempoross extends NPC {
    Player player;
    @Getter @Setter public static boolean activatePools;
    @Setter public static TemporossState state;

    Tile face = new Tile(3038, 2977);
    public Tempoross(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        face = face.transform(this.getSize() / 2, this.getSize() / 2);
        this.setFaceTile(face);
        setActivatePools(true);
    }
    public void hit() {
        Iterator<Integer> iterator = SpiritPoolsInteraciton.damage.iterator();
        while (iterator.hasNext()) {
            var d = iterator.next();
            this.hit(player, d);
            iterator.remove();
        }
    }
    @Override
    public void postSequence() {
        if (SpiritPoolsInteraciton.isInteracting() && this.getId() == NpcIdentifiers.TEMPOROSS_10574) {
            this.hit();
        }
    }
    @Override
    public void die() {
        if (this.getId() == NpcIdentifiers.TEMPOROSS_10574) {
            setActivatePools(false);
            setState(TemporossState.ONE);
            this.transmog(NpcIdentifiers.TEMPOROSS_10572);
            this.spawnDirection(Direction.WEST.toInteger());
        } else if (this.getId() == NpcIdentifiers.TEMPOROSS_10572 && state.equals(TemporossState.ONE)) {
            setActivatePools(true);
            setState(TemporossState.TWO);
            this.transmog(NpcIdentifiers.TEMPOROSS_10574);
            this.spawnDirection(Direction.WEST.toInteger());
        } else if (this.getId() == NpcIdentifiers.TEMPOROSS_10574 && state.equals(TemporossState.TWO)) {
            setActivatePools(false);
            setState(TemporossState.THREE);
            this.transmog(NpcIdentifiers.TEMPOROSS_10575);
            this.spawnDirection(Direction.WEST.toInteger());
        } else if (this.getId() == NpcIdentifiers.TEMPOROSS_10575 && state.equals(TemporossState.THREE)) {
            World.getWorld().unregisterNpc(this);
        }
    }
}
