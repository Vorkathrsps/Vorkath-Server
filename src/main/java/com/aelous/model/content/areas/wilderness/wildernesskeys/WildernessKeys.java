package com.aelous.model.content.areas.wilderness.wildernesskeys;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.ground.GroundItem;
import com.aelous.model.items.ground.GroundItemHandler;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Origin
 * @Date: 5/17/2023
 */
public class WildernessKeys {
    @Setter @Getter Player player;
    @Setter @Getter NPC npc;
    @Getter List<Player> targetList = new ArrayList<>();
    public WildernessKeys(Player player, NPC npc) {
        this.player = player;
        this.npc = npc;
    }
    public void rollForWildernessKey(NPC npc) {
        boolean hasKey = player.getAttribOr(AttributeKey.WILDERNESS_KEY, false);
        if (!hasKey) {
            if (Utils.securedRandomChance(0.15F)) {
                Item item = new Item(298, 1);
                GroundItem groundItem = new GroundItem(item, npc.tile(), player);
                GroundItemHandler.createGroundItem(groundItem);
                player.message(Color.RED.wrap("<img=2010>You've received a wilderness key drop!"));
            }
        }
    }
    public boolean digToSpawnNpc(Item item) {
        Tile digTile = new Tile(3028, 3915, 0);
        if (hasSpawnedNpc()) {
            player.message(Color.RED.wrap("You can only spawn one of the wilderness key npcs."));
            return false;
        }
        if (!Objects.equals(player.tile(), digTile) && player.tile().inSqRadius(digTile, 8)) {
            if (!hasSpawnedNpc()) {
                player.getPacketSender().sendPositionalHint(digTile, 2);
                player.message(Color.BLUE.wrap("Use your spade to dig on the marked tile."));
            }
            return false;
        }
        if (item.getId() == ItemIdentifiers.SPADE) {
            if (player.getClickDelay().elapsed(30000)) {
                if (player.tile().equals(3028, 3915, 0) && player.getInventory().contains(ItemIdentifiers.KEY_298)) {
                    player.getPacketSender().sendEntityHintRemoval(true);
                    this.setNpc(new NPC(NpcIdentifiers.JUDGE_OF_YAMA_10938, new Tile(player.tile().getX(), player.tile().getY(), player.tile().getZ())));
                    this.setPlayer(this.player);
                    targetList.add(player);
                    player.getInventory().remove(ItemIdentifiers.KEY_298);
                    player.putAttrib(AttributeKey.SPAWNED_LINKED_NPC, true);
                    player.getCombat().setTarget(npc);
                    player.getPacketSender().sendEntityHint(npc);
                    World.getWorld().registerNpc(npc);
                    npc.putAttrib(AttributeKey.NPC_LINKED_TO_PLAYER, player);
                    npc.respawns(false);
                    npc.getCombat().setTarget(player);
                    npc.face(player);
                    return true;
                }
            }
        }
        return false;
    }
    public void onDeath() {
        npc.clearAttrib(AttributeKey.NPC_LINKED_TO_PLAYER);
        player.clearAttrib(AttributeKey.SPAWNED_LINKED_NPC);
        World.getWorld().unregisterNpc(npc);
        player.getPacketSender().sendEntityHintRemoval(true);
        targetList.clear();
    }
    public boolean hasSpawnedNpc() {
        return player.hasAttrib(AttributeKey.SPAWNED_LINKED_NPC);
    }
    public boolean isNpcLinked() {
        return npc.hasAttrib(AttributeKey.SPAWNED_LINKED_NPC);
    }
}
