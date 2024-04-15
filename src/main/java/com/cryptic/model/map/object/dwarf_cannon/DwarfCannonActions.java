package com.cryptic.model.map.object.dwarf_cannon;

import static com.cryptic.model.map.object.dwarf_cannon.DwarfCannon.*;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.cache.definitions.identifiers.ObjectIdentifiers;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;

import java.awt.*;

/**
 * @author Origin | April, 18, 2021, 18:34
 */
public class DwarfCannonActions extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            if (item.getId() == ItemIdentifiers.CANNON_BASE) {

                var region = player.tile().region();
                int[] blocked_regions = new int[]{14131, 9008, 9551};

                if (player.getInstancedArea() != null || player.getTheatreInstance() != null) {
                    player.message(Color.RED.wrap("You cannot use your cannon inside of an instance."));
                    return true;
                }

                if (ArrayUtils.contains(blocked_regions, region)) {
                    player.message(Color.RED.wrap("You cannot use your cannon in this area."));
                    return true;
                }

                var reclaim = player.<Boolean>getAttribOr(AttributeKey.LOST_CANNON, false);
                if (reclaim && !player.getPlayerRights().isAdministrator(player)) {
                    player.message(
                        "You can't deploy this cannon, you have one you need to reclaim.");
                } else {

                    DwarfCannon cannon = new DwarfCannon(player, CANNON_OBJECTS[0]);

                    if (!cannon.hasParts()) {
                        player.message("You don't have all the parts to build your cannon.");
                        return true;
                    }
                    if (!cannon.isValidSpot()) {
                        player.message("There's not enough room to setup your cannon here.");
                        return true;
                    }

                    if (!cannon.handleAreaRestriction()) {
                        return true;
                    }

                    if (World.getWorld().getOwnedObject(player, DwarfCannon.IDENTIFIER) != null) {
                        player.message("You already have a cannon deployed.");
                        return true;
                    }

                    Chain.bound(player)
                        .cancelWhen(
                            () ->
                                cannon.getOwnerOpt().isEmpty()
                                    || cannon.getOwner().dead())
                        .runFn(
                            1,
                            () -> {

                                // wheres spawn obj
                                World.getWorld().registerOwnedObject(cannon);

                                cannon.getDecayTimer().start();

                                cannon.add();
                                player.animate(SETUP_ANIM);
                                player.inventory()
                                    .remove(
                                        CANNON_PARTS[cannon.getStage().ordinal()],
                                        1);
                                player.message("You place down the base.");
                                player.lock();
                            })
                        .then(
                            2,
                            () -> {
                                for (int index = 1; index < 4; index++) {
                                    // add new Tasks @ instantly (0*2=0), 2 4, 6
                                    Chain.bound(player)
                                        .runFn(
                                            index * 2,
                                            () -> {
                                                player.animate(SETUP_ANIM);
                                                cannon.incrementSetupStage();
                                                Item cannonPart =
                                                    new Item(
                                                        CANNON_PARTS[
                                                            cannon.getStage()
                                                                .ordinal()],
                                                        1);
                                                String name =
                                                    cannonPart
                                                        .name()
                                                        .toLowerCase()
                                                        .replace(
                                                            "cannon",
                                                            "")
                                                        .trim();
                                                player.inventory()
                                                    .remove(
                                                        CANNON_PARTS[
                                                            cannon.getStage()
                                                                .ordinal()],
                                                        1);
                                                player.message(
                                                    "You add the "
                                                        + name
                                                        + ".");
                                            });
                                }
                            })
                        .then(
                            6,
                            () -> {
                                cannon.fill();
                                player.unlock();
                            });
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (option == 1) {
            if (object.getId() == ObjectIdentifiers.BROKEN_MULTICANNON) {
                if (object.isOwnedObject() && object.asOwnedObject().isOwner(player)) {
                    DwarfCannon cannon = ((DwarfCannon) object);
                    player.runFn(
                            1,
                            () -> {
                                cannon.getDecayTimer().reset();
                                cannon.getDecayTimer().start();
                                player.animate(3684);
                            })
                        .then(2, () -> cannon.setStage(CannonStage.FIRING, true));
                } else {
                    player.message("Your not the owner of this cannon.");
                }
                return true;
            }
            if (object.getId() == ObjectIdentifiers.DWARF_MULTICANNON) {
                if (object.isOwnedObject() && object.asOwnedObject().isOwner(player)) {
                    ((DwarfCannon) object).fill();
                } else {
                    player.message("Your not the owner of this cannon.");
                }
                return true;
            }
            if (object.getId() == ObjectIdentifiers.CANNON_BASE
                || object.getId() == ObjectIdentifiers.CANNON_STAND
                || object.getId() == ObjectIdentifiers.CANNON_BARRELS) {
                if (object.isOwnedObject() && object.asOwnedObject().isOwner(player)) {
                    ((DwarfCannon) object).pickup();
                } else {
                    player.message("Your not the owner of this cannon.");
                }
                return true;
            }
        }
        if (option == 2) {
            if (object.getId() == ObjectIdentifiers.DWARF_MULTICANNON) {
                if (object.isOwnedObject() && object.asOwnedObject().isOwner(player)) {
                    object.asOwnedObject().getOwner().tile().faceObjectTile(object);
                    ((DwarfCannon) object).pickup();
                } else {
                    player.message("Your not the owner of this cannon.");
                }
                return true;
            }
        }
        if (option == 3) {
            if (object.getId() == ObjectIdentifiers.DWARF_MULTICANNON) {
                if (!object.isOwnedObject()) {
                    return true;
                }
                DwarfCannon cannon = ((DwarfCannon) object);
                for (var balls : cannon_balls) {
                    if (object.asOwnedObject().isOwner(player)) {
                        if (player.inventory().hasFreeSlots(1) || player.inventory().hasCapacity(new Item(balls))) {
                            if (cannon.getAmmo() > 0) {
                                player.inventory().add(balls, cannon.getAmmo());
                                player.message(STR."You unload your cannon and receive Cannonball x \{cannon.getAmmo()}");
                                cannon.setAmmo(0);
                                cannon.setStage(CannonStage.FURNACE, false);
                            }
                        } else {
                            player.message("You don't have enough inventory space to do that.");
                        }
                    } else {
                        player.message("Your not the owner of this cannon.");
                    }
                    break;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject object) {
        for (var balls : cannon_balls) {
            if (item.getId() == balls && object.getId() == BASE) {
                if (object.isOwnedObject() && object.asOwnedObject().isOwner(player)) {
                    ((DwarfCannon) object).fill();
                } else {
                    player.message("Your not the owner of this cannon.");
                }
                return true;
            }
            break;
        }
        return false;
    }
}
