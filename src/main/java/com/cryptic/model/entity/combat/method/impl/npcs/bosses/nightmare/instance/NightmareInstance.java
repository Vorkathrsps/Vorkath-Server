package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.instance;

import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.model.World;
import com.cryptic.model.content.instance.InstanceConfiguration;
import com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.combat.Nightmare;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Origin
 * @Date: 10/5/2023
 */
public class NightmareInstance extends NightmareArea {
    @Getter Player owner;
    @Getter public List<Player> players;
    @Getter @Setter boolean joinable = true;
    @Getter public List<NPC> husks = new ArrayList<>();
    @Getter public List<NPC> totems = new ArrayList<>();
    public static Area room() {
        return new Area(Tile.regionToTile(15515).getX(), Tile.regionToTile(15515).getY(), Tile.regionToTile(15515).getX() + 63, Tile.regionToTile(15515).getY() + 63);
    }
    public NightmareInstance(Player owner, List<Player> players) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY_NO_RESPAWN, room());
        this.owner = owner;
        this.players = players;
    }

    public NightmareInstance join(Player member) {
        if (owner == null) {
            return null;
        }

        if (!this.isJoinable()) {
            return null;
        }

        member.setInstance(owner.getNightmareInstance());
        member.teleport(new Tile(3872, 9958, owner.getNightmareInstance().getzLevel() + 3));
        addPlayerToList(member);
        return this;
    }

    public NightmareInstance build() {
        NPC nightmare = new NPC(9432, new Tile(3870, 9949, this.getzLevel() + 3)).spawn(false);
        nightmare.noRetaliation(true);
        World.getWorld().definitions().get(NpcDefinition.class, nightmare.id());
        nightmare.setInstance(this);

        NPC topRightTotem = new NPC(9443, new Tile(3879, 9958, this.getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, topRightTotem.id());
        topRightTotem.setInstance(this);

        NPC bottomRightTotem = new NPC(9437, new Tile(3879, 9942, this.getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, bottomRightTotem.id());
        bottomRightTotem.setInstance(this);

        NPC topLeftTotem = new NPC(9440, new Tile(3863, 9958, this.getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, topLeftTotem.id());
        topLeftTotem.setInstance(this);

        NPC bottomLeftTotem = new NPC(9434, new Tile(3863, 9942, this.getzLevel() + 3)).spawn(false);
        World.getWorld().definitions().get(NpcDefinition.class, bottomLeftTotem.id());
        bottomLeftTotem.setInstance(this);

        setInstance();
        owner.face(nightmare);

        Chain.noCtx().runFn(30, () -> {
            this.setJoinable(false);
            nightmare.animate(8611);
        }).then(8, () -> {
            nightmare.animate(-1);
            nightmare.transmog(9425);
            nightmare.setCombatInfo(World.getWorld().combatInfo(9430));
            nightmare.setHitpoints(nightmare.maxHp());
            nightmare.setCombatMethod(new Nightmare());
            this.getPlayers().stream().findAny().ifPresent(p -> nightmare.getCombat().setTarget(p));
        });

        return this;
    }

    private void setInstance() {
        addPlayerToList(owner);
        owner.setInstance(this);
        owner.teleport(new Tile(3872, 9958, this.getzLevel() + 3));
    }

    void addPlayerToList(Player player) {
        players.add(player);
    }

}
