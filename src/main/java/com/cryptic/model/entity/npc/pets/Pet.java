package com.cryptic.model.entity.npc.pets;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.utility.Tuple;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Data;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import java.util.ArrayList;

import static com.cryptic.model.entity.attributes.AttributeKey.PLAYER_UID;

/**
 * @Author: Origin
 * @Date: 7/7/2023
 * @Updated: 4/15/24
 */
@Data
public class Pet {

    @Nullable
    public NPC entity;

    public final boolean inventoryContainsItem(final Player player, @Nonnull final Item item) {
        PetDefinitions petDefinitions = PetDefinitions.getPetByItem(item.getId());
        return petDefinitions != null && player.inventory().contains(petDefinitions.getItem());
    }

    public final void pickup(final Player player) {
        if (this.entity != null && this.entity.isRegistered()) {
            player.stopActions(true);
            player.animate(827);
            PetDefinitions definition = PetDefinitions.getItemByPet(this.entity.id());
            if (definition != null) {
                if (!WildernessArea.inWilderness(player.tile())) {
                    player.getInventory().addOrBank(new Item(definition.getItem(), 1));
                    this.invalidate();
                    return;
                }
                if (!player.inventory().isFull()) {
                    player.getInventory().add(new Item(definition.getItem(), 1));
                    this.invalidate();
                }
            }
        }
    }

    public final void onLogout(final Player player) {
        if (this.entity != null && this.entity.isRegistered()) {
            player.animate(827);
            this.addToInventory(player);
            this.invalidate();
        }
    }

    private void addToInventory(final Player player) {
        if (this.entity != null && this.entity.isRegistered()) {
            PetDefinitions definition = PetDefinitions.getItemByPet(this.entity.id());
            if (definition != null) player.getInventory().addOrBank(new Item(definition.item, 1));
        }
    }

    public final void clearSpawnedEntity(final Player player) {
        if (this.entity != null && this.entity.isRegistered()) {
            player.animate(827);
            this.addToInventory(player);
            this.invalidate();
        }
    }

    private void invalidate() {
        if (this.entity != null && this.entity.isRegistered()) {
            this.entity.clearAttrib(AttributeKey.OWNING_PLAYER);
            this.entity.remove();
            this.entity = null;
        }
    }

    public final boolean dropPet(final Player player, @Nonnull final Item item) {
        PetDefinitions definition = PetDefinitions.getPetByItem(item.getId());
        player.stopActions(true);
        if (definition != null) {
            player.animate(827);
            if (this.entity != null && this.entity.isRegistered()) {
                this.clearSpawnedEntity(player);
            }
            if (this.inventoryContainsItem(player, item)) {
                player.getInventory().remove(definition.item);
                this.entity = new NPC(definition.npc, player.tile()).walkRadius(-1);
                player.putAttrib(AttributeKey.LAST_PET_ID, this.entity.id());
                Long uid = player.<Long>getAttribOr(PLAYER_UID, 0L);
                this.entity.putAttrib(AttributeKey.OWNING_PLAYER, new Tuple<>(uid, player));
                this.entity.spawn(false);
                this.follow(player);
                return true;
            }
        }
        return false;
    }

    public final void follow(final Player player) {
        if (this.entity == null) return;
        Chain.noCtxRepeat().repeatingTask(1, t -> {
            if (this.entity == null || !this.entity.isRegistered()) {
                t.stop();
                return;
            }
            if (player.isRegistered()) {
                if (player.dead()) {
                    return;
                }
                if (!this.entity.tile().isWithinDistance(player.tile(), 8)) {
                    this.entity.teleport(player.getAbsX(), player.getAbsY(), player.getZ());
                    return;
                }
                this.entity.faceEntity(player);
                int[] thisTick = {-1, -1};
                DumbRoute.step(entity, player, 1);
                thisTick[0] = entity.getRouteFinder().routeEntity.finishX;
                thisTick[1] = entity.getRouteFinder().routeEntity.finishY;
                this.entity.getMovement().reset();
                DumbRoute.step(entity, thisTick[0], thisTick[1]);
            } else {
                this.entity.remove();
                t.stop();
            }
        });
    }
}

