package com.aelous.model.content.areas.slayer_tower;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 29, 2020 19:45
 */
public class SlayerTower extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            //Basement is task only!
            if (obj.getId() == LADDER_30191) {
                if (player.slayerTaskAmount() > 0) {
                    Ladders.ladderDown(player, new Tile(3412, 9932, 3), true);
                } else {
                    player.message("You can only enter the basement if you have a slayer task.");
                }
                return true;
            }
            if (obj.getId() == LADDER_30192) {
                Ladders.ladderUp(player, new Tile(3417, 3536, 0), true);
                return true;
            }
            if (obj.getId() == STAIRCASE_2118) {
                if (obj.tile().x == 3434 && obj.tile().y == 3537) {
                    Ladders.ladderDown(player, new Tile(3438, player.tile().y, player.tile().level - 1), false);
                }
                return true;
            }
            if (obj.getId() == STAIRCASE) {
                if (obj.tile().x == 3434 && obj.tile().y == 3537) {
                    Ladders.ladderUp(player, new Tile(3433, player.tile().y, player.tile().level + 1), false);
                }
                return true;
            }
            if (obj.getId() == STAIRCASE_2119) {
                if (obj.tile().x == 3413 && obj.tile().y == 3540) {
                    Ladders.ladderUp(player, new Tile(3417, player.tile().y, player.tile().level + 1), false);
                }
                return true;
            }
            if (obj.getId() == STAIRCASE_2120) {
                if (obj.tile().x == 3415 && obj.tile().y == 3540) {
                    Ladders.ladderDown(player, new Tile(3412, player.tile().y, player.tile().level - 1), false);
                }
                return true;
            }
            if (obj.getId() == DOOR_2108 || obj.getId() == DOOR_2111) {
                openDoor();
                return true;
            }
            if (obj.getId() == DOOR_2113 || obj.getId() == DOOR_2112) {
                closeDoor();
                return true;
            }
            if (obj.getId() == SPIKEY_CHAIN) {
                if (obj.tile().x == 3422 && obj.tile().y == 3550) {
                    if (!player.getEquipment().contains(new Item(ItemIdentifiers.NOSE_PEG))) {
                        player.getDialogueManager().start(new Dialogue() {
                            @Override
                            protected void start(Object... parameters) {
                                send(DialogueType.STATEMENT, "A foul stench seems to be seeping down from the floor above...", "it could be dangerous up there without a nosepeg.");
                                setPhase(0);
                            }

                            @Override
                            public void next() {
                                if (getPhase() == 0) {
                                    send(DialogueType.OPTION, "Go up anyway?", "Yes.", "No.");
                                    setPhase(1);
                                }
                            }

                            @Override
                            public void select(int option) {
                                if (option == 1) {
                                    Ladders.ladderUp(player, new Tile(player.tile().x, player.tile().y, player.tile().level + 1), true);
                                    player.getSkills().addXp(Skills.AGILITY, 4.0);
                                    stop();
                                } else if (option == 2) {
                                    player.message("You decide to save your nose, and self, from almost certain death.");
                                    stop();
                                }
                            }
                        });
                    } else {
                        Ladders.ladderUp(player, new Tile(player.tile().x, player.tile().y, player.tile().level + 1), true);
                        player.getSkills().addXp(Skills.AGILITY, 4.0);
                    }
                } else if (obj.tile().x == 3447 && obj.tile().y == 3576 && player.getSkills().level(Skills.AGILITY) >= 71) {
                    Ladders.ladderUp(player, new Tile(player.tile().x, player.tile().y, player.tile().level + 1), true);
                    player.getSkills().addXp(Skills.AGILITY, 4.0);
                } else {
                    player.message("You need an Agility level of 71 to negotiate this obstacle.");
                }
                return true;
            }

            if (obj.getId() == SPIKEY_CHAIN_16538) {
                if (obj.tile().x == 3422 && obj.tile().y == 3550) {
                    Ladders.ladderDown(player, new Tile(player.tile().x, player.tile().y, player.tile().level - 1), true);
                } else if (obj.tile().x == 3447 && obj.tile().y == 3576 && player.getSkills().level(Skills.AGILITY) >= 71) {
                    Ladders.ladderDown(player, new Tile(player.tile().x, player.tile().y, player.tile().level - 1), true);
                } else {
                    player.message("You need an Agility level of 71 to negotiate this obstacle.");
                }
                return true;
            }
        }
        return false;
    }

    private static void openDoor() {
        ObjectManager.removeObj(new GameObject(2111, new Tile(3429, 3535), 0, 1));
        ObjectManager.removeObj(new GameObject(2108, new Tile(3428, 3535), 0, 1));
        ObjectManager.addObj(new GameObject(2113, new Tile(3429, 3536), 0, 2));
        ObjectManager.addObj(new GameObject(2112, new Tile(3428, 3536), 0, 4));
    }

    private static void closeDoor() {
        ObjectManager.addObj(new GameObject(2111, new Tile(3429, 3535), 0, 1));
        ObjectManager.addObj(new GameObject(2108, new Tile(3428, 3535), 0, 1));
        ObjectManager.removeObj(new GameObject(2113, new Tile(3429, 3536), 0, 2));
        ObjectManager.removeObj(new GameObject(2112, new Tile(3428, 3536), 0, 0));
    }
}
