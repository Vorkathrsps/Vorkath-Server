package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.Ashihama;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.npc.Totem;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class NightmareInstance {
    @Getter Player owner;
    @Getter List<Player> players;
    @Getter NightmareArea nightmareArea;
    @Getter @Setter boolean joinable = true;
    public static Area room() {
        return new Area(Tile.regionToTile(15515).getX(), Tile.regionToTile(15515).getY(), Tile.regionToTile(15515).getX() + 63, Tile.regionToTile(15515).getY() + 63);
    }
    public NightmareInstance(Player owner, List<Player> players, NightmareArea nightmareArea) {
        this.owner = owner;
        this.players = players;
        this.nightmareArea = nightmareArea;
    }

    public void join(Player member) {
        if (owner == null) {
            return;
        }

        if (!this.isJoinable()) {
            return;
        }

        member.setInstance(owner.getInstancedArea());
        member.teleport(new Tile(3872, 9958, owner.getInstancedArea().getzLevel() + 3));
        addPlayerToList(member);
    }

    public void build(Player player) {
        if (!this.isJoinable()) {
            return;
        }

        NPC nightmare = new NPC(9432, new Tile(3870, 9949, this.getNightmareArea().getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, nightmare.id());
        nightmare.setInstance(this.getNightmareArea());

        NPC topRightTotem = new NPC(9443, new Tile(3879, 9958, this.getNightmareArea().getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, topRightTotem.id());
        topRightTotem.setInstance(this.getNightmareArea());

        NPC bottomRightTotem = new NPC(9437, new Tile(3879, 9942, this.getNightmareArea().getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, bottomRightTotem.id());
        bottomRightTotem.setInstance(this.getNightmareArea());

        NPC topLeftTotem = new NPC(9440, new Tile(3863, 9958, this.getNightmareArea().getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, topLeftTotem.id());
        topLeftTotem.setInstance(this.getNightmareArea());

        NPC bottomLeftTotem = new NPC(9434, new Tile(3863, 9942, this.getNightmareArea().getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, bottomLeftTotem.id());
        bottomLeftTotem.setInstance(this.getNightmareArea());

        if (this.isJoinable()) {
            setPlayerInstance(player);
            player.face(nightmare);
        }

        Chain.noCtx().runFn(30, () -> {
            this.setJoinable(false);
            nightmare.animate(8611);
        }).then(8, () -> {
            nightmare.animate(-1);
            nightmare.transmog(9425);
            nightmare.setCombatInfo(World.getWorld().combatInfo(9430));
            nightmare.setHitpoints(nightmare.maxHp());
            nightmare.setCombatMethod(new Ashihama());
            this.getPlayers().stream().findAny().ifPresent(p -> nightmare.getCombat().setTarget(p));
        });
    }

    private void setPlayerInstance(Player player) {
        addPlayerToList(player);
        player.setInstance(this.getNightmareArea());
        player.teleport(new Tile(3872, 9958, this.getNightmareArea().getzLevel() + 3));
    }

    void addPlayerToList(Player player) {
        players.add(player);
    }

}
