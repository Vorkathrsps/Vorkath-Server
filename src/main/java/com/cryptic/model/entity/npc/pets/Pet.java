package com.cryptic.model.entity.npc.pets;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * @Author: Origin
 * @Date: 7/7/2023
 */
@Data
public class Pet {
    @Nonnull
    private final Player player;
    @Nullable @Setter public NPC pet;
    Animation ANIMATION = new Animation(827);

    public Pet(@Nonnull final Player player) {
        this.player = player;
    }

    public boolean inventoryContainsItem(@Nonnull final Item item) {
        PetDefinitions petDefinitions = PetDefinitions.getPetByItem(item.getId());
        return petDefinitions != null && player.inventory().contains(petDefinitions.getItem());
    }

    public void pickup() {
        if (player.getPetEntity() != null && player.getPetEntity().getPet() != null) {
            player.animate(ANIMATION);
            Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getItemByPet(player.getAttribOr(AttributeKey.LAST_PET_ID, -1)));
            petDefinitions.ifPresent(definitions -> player.getInventory().addOrBank(new Item(definitions.getItem(), 1)));
            World.getWorld().unregisterNpc(player.getPetEntity().getPet());
        }
    }

    public void clearSpawnedPet() {
        if (player.getPetEntity() != null && player.getPetEntity().getPet() != null) {
            player.animate(ANIMATION);
            World.getWorld().unregisterNpc(player.getPetEntity().getPet());
        }
    }

    public void spawnOnLogin(@Nonnull final Item item) {
        Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getPetByItem(item.getId()));
        if (petDefinitions.isPresent()) {
            player.animate(ANIMATION);
            pet = new NPC(petDefinitions.get().npc, player.tile()).walkRadius(-1);
            player.putAttrib(AttributeKey.LAST_PET_ID, pet.id());
            World.getWorld().registerNpc(pet);
            follow();
        }
    }

    public boolean dropPet(@Nonnull final Item item) {
        Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getPetByItem(item.getId()));
        if (petDefinitions.isPresent()) {
            player.animate(ANIMATION);
            if (player.getPetEntity().getPet() != null) {
                clearSpawnedPet();
            }
            if (this.inventoryContainsItem(item)) {
                player.getInventory().remove(petDefinitions.get().item);
                pet = new NPC(petDefinitions.get().npc, player.tile()).walkRadius(-1);
                player.putAttrib(AttributeKey.LAST_PET_ID, pet.id());
                World.getWorld().registerNpc(pet);
                follow();
            }
            return true;
        }
        return false;
    }

    public void follow() {
        if (player.getPetEntity().getPet() == null) {
            return;
        }
        Chain.noCtxRepeat().repeatingTask(1, t -> {
            if (player.getPetEntity().getPet() == null) {
                t.stop();
                return;
            }
            if (player.isRegistered() && player.getPetEntity().getPet().isRegistered()) {
                if (player.dead()) {
                    return;
                }
                if (!player.getPetEntity().getPet().tile().isWithinDistance(player.tile(), 8)) {
                    player.getPetEntity().getPet().teleport(player.getAbsX(), player.getAbsY(), player.getZ());
                    return;
                }
                player.getPetEntity().getPet().faceEntity(player);
                int[] thisTick = {-1, -1};
                DumbRoute.step(player.getPetEntity().getPet(), player, 1);
                thisTick[0] = player.getPetEntity().getPet().getRouteFinder().routeEntity.finishX;
                thisTick[1] = player.getPetEntity().getPet().getRouteFinder().routeEntity.finishY;
                player.getPetEntity().getPet().getMovement().reset();
                DumbRoute.step(player.getPetEntity().getPet(), thisTick[0], thisTick[1]);
            } else {
                player.getPetEntity().getPet().remove();
                t.stop();
            }
        });
    }


    public void spawnOnLogin() {
        Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getItemByPet(player.getAttribOr(AttributeKey.LAST_PET_ID, -1)));
        if (petDefinitions.isPresent()) {
            if (player.<Integer>getAttribOr(AttributeKey.LAST_PET_ID, -1) == petDefinitions.get().getNpc()) {
                spawnOnLogin(Item.of(petDefinitions.get().getItem()));
            }
        }
    }

}

