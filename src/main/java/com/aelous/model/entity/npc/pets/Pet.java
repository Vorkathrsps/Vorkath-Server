package com.aelous.model.entity.npc.pets;

import com.aelous.annotate.Init;
import com.aelous.model.World;
import com.aelous.model.content.skill.impl.mining.Pickaxe;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Optional;

/**
 * @Author: Origin
 * @Date: 7/7/2023
 */
@Getter
public class Pet extends PacketInteraction {
    @Nonnull private final Player player;
    @Nullable private NPC pet;
    Animation ANIMATION = new Animation(827);

    public Pet(@Nonnull final Player player) {
        this.player = player;
    }

    public boolean inventoryContainsItem(@Nonnull final Item item) {
        PetDefinitions petDefinitions = PetDefinitions.getPetByItem(item.getId());
        return petDefinitions != null && player.inventory().contains(petDefinitions.getItem());
    }

    public void clearSpawnedPet() {
        if (player.getPet() != null && player.getPet().getPet() != null) {
            Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getItemByPet(player.getAttribOr(AttributeKey.LAST_PET_ID, -1)));
            petDefinitions.ifPresent(definitions -> player.getInventory().add(definitions.getItem()));
            World.getWorld().unregisterNpc(player.getPet().getPet());
        }
    }

    public boolean dropPet(@Nonnull final Item item) {
        Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getPetByItem(item.getId()));
        if (petDefinitions.isPresent()) {
            player.animate(ANIMATION);
            if (player.getPet().getPet() != null) {
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
        if (player.getPet().getPet() == null) {
            return;
        }
        Chain.noCtxRepeat().repeatingTask(1, t -> {
            if (player.getPet().getPet() == null) {
                t.stop();
                return;
            }
            if (player.isRegistered() && player.getPet().getPet().isRegistered()) {
                if (player.dead()) {
                    return;
                }
                if (!player.getPet().getPet().tile().isWithinDistance(player.tile(), 8)) {
                    player.getPet().getPet().teleport(player.getAbsX(), player.getAbsY(), player.getZ());
                    return;
                }
                player.getPet().getPet().faceEntity(player);
                int[] thisTick = {-1, -1};
                DumbRoute.step(player.getPet().getPet(), player, 1);
                thisTick[0] = player.getPet().getPet().getRouteFinder().routeEntity.finishX;
                thisTick[1] = player.getPet().getPet().getRouteFinder().routeEntity.finishY;
                player.getPet().getPet().getMovement().reset();
                DumbRoute.step(player.getPet().getPet(), thisTick[0], thisTick[1]);
            } else {
                player.getPet().getPet().remove();
                t.stop();
            }
        });
    }


    public void spawnOnLogin() {
        Optional<PetDefinitions> petDefinitions = Optional.ofNullable(PetDefinitions.getItemByPet(player.getAttribOr(AttributeKey.LAST_PET_ID, -1)));
        if (petDefinitions.isPresent()) {
            if (player.<Integer>getAttribOr(AttributeKey.LAST_PET_ID, -1) == petDefinitions.get().getNpc()) {
                dropPet(Item.of(petDefinitions.get().getItem()));
            }
        }
    }

    public void removeOnLogout() {
        if (player.getPet().getPet() != null) {
            clearSpawnedPet();
        }
    }

}

