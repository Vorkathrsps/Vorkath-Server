package com.aelous.model.content.packet_actions.interactions.objects;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.TickAndStop;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.utility.chainedwork.Chain;

import java.util.Arrays;

public class Ladders extends PacketInteraction {

    public static void ladderDown(Player player, Tile tile, boolean animate) {
        if (animate)
            player.animate(827);
        Chain.bound(null).runFn(2, () -> {
            player.teleport(tile);
        });
    }

    public static void ladderUp(Player player, Tile tile, boolean animate) {
        if (animate)
            player.animate(828);
        Chain.bound(null).runFn(2, () -> {
            player.teleport(tile);
        });
    }

    private static final int[] ladders = {14735, 14736, 14737, 12964, 12965, 12966, 16683, 16684, 16679, 11794, 11795, 11790, 11792, 11793, 11801, 11802, 16672, 16673, 25938, 25939, 26118, 26119, 2797, 2796, 17122, 2833, 16671, 24303, 2884};

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        //TODO determine if an object is a ladder rather than storing obj ids. Loop objects, check definition name?
        if (Arrays.stream(ladders).anyMatch(b -> b == obj.getId())) {
            boolean animate = !obj.definition().name.toLowerCase().contains("staircase");
            if (option == 1) {
                switch (obj.definition().options[0]) {
                    case "Climb" -> player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Up.", "Down.");
                            setPhase(0);
                        }

                        @Override
                        protected void select(int option) {
                            if (isPhase(0)) {
                                if (option == 1) {
                                    int change = 1;
                                    if (obj.tile().equals(2715, 3472)) { // Camelot agility stairs goes up 2 levels!
                                        change = 2;
                                    }

                                    Tile endPos = new Tile(player.tile().x, player.tile().y, player.tile().level + change);
                                    ladderUp(player, endPos, animate);
                                    stop();
                                } else if (option == 2) {
                                    int change = 1;
                                    if (obj.tile().equals(2715, 3472)) { // Camelot agility stairs goes down 2 levels!
                                        change = 2;
                                    }
                                    ladderDown(player, new Tile(player.tile().x, player.tile().y, player.tile().level - change), animate);
                                    stop();
                                }
                            }
                        }
                    });
                    case "Climb-up" -> {
                        int change = 1;
                        if (obj.tile().equals(2715, 3472)) { // Camelot agility stairs goes up 2 levels!
                            change = 2;
                        }

                        Tile endPos = new Tile(player.tile().x, player.tile().y, player.tile().level + change);
                        ladderUp(player, endPos, animate);
                    }
                    case "Climb-down" -> {
                        int change = 1;
                        if (obj.tile().equals(2715, 3472)) { // Camelot agility stairs goes down 2 levels!
                            change = 2;
                        }
                        ladderDown(player, new Tile(player.tile().x, player.tile().y, player.tile().level - change), animate);
                    }
                }
                return true;
            } else if (option == 2) {
                if ((obj.definition().options[1].equals("Climb-up"))) {
                    int change = 1;

                    Tile endPos = new Tile(player.tile().x, player.tile().y, player.tile().level + change);
                    ladderUp(player, endPos, animate);
                }
                return true;
            } else if (option == 3) {
                if ((obj.definition().options[2].equals("Climb-down"))) {
                    int change = 1;

                    ladderDown(player, new Tile(player.tile().x, player.tile().y, player.tile().level - change), animate);
                }
                return true;
            }
        }
        if (obj.getId() == 30367) {
            //Climb down the ladders
            Tile tile = obj.tile();

            if (player.getSkills().level(Skills.MINING) < 60) {
                DialogueManager.npcChat(player, Expression.HAPPY, NpcIdentifiers.GUARD_6561, "Sorry, but you need level 60 Mining to get in there.");
            } else {
                if (tile.equals(new Tile(3019, 3340))) { // North
                    ladderDown(player, new Tile(3019, 9741), true);
                } else if (tile.equals(new Tile(3019, 3338))) { // South
                    ladderDown(player, new Tile(3019, 9737), true);
                } else if (tile.equals(new Tile(3020, 3339))) { // East
                    ladderDown(player, new Tile(3021, 9739), true);
                } else if (tile.equals(new Tile(3018, 3339))) { // West
                    ladderDown(player, new Tile(3017, 9739), true);
                }
            }
            return true;
        }
        if(obj.getId() == 17384) {
            Tile ladderTile = obj.tile();
            //Back to Taverly black dragons
            if (ladderTile.equals(new Tile(2842, 3424))) {
                ladderDown(player, new Tile(2842, 9825), true);
            }
            return true;
        }
        if(obj.getId() == 17385) {
            // Climb up the ladders
            Tile ladderTile = obj.tile();

            //Mining guild
            if (ladderTile.equals(new Tile(3019, 9740))) { // North
                ladderUp(player, new Tile(3019, 3341), true);
            } else if (ladderTile.equals(new Tile(3019, 9738))) { // South
                ladderUp(player, new Tile(3019, 3337), true);
            } else if (ladderTile.equals(new Tile(3020, 9739))) { // East
                ladderUp(player, new Tile(3021, 3339), true);
            } else if (ladderTile.equals(new Tile(3018, 9739))) { // West
                ladderUp(player, new Tile(3017, 3339), true);
            } else if (obj.tile().equals(new Tile(2884, 9797))) {
                ladderUp(player, new Tile(2884, 3396), true);
            } else if (obj.tile().equals(new Tile(3209, 9616))) {
                ladderUp(player, new Tile(3210, 3216), true);
            } else if (obj.tile().equals(3008, 9550)) { // Asgarnian ice dungeon
                ladderUp(player, new Tile(3009, 3150), true);
            } else if (ladderTile.equals(new Tile(2842, 9824))) { //Black dragon taverly down to up catherby
                ladderUp(player, new Tile(2842, 3425), true);
            } else if (ladderTile.equals(new Tile(3096, 9876))) { //Edgeville ladder
                player.setPositionToFace(obj.tile());
                player.animate(828);
                TaskManager.submit(new TickAndStop(1) {
                    @Override
                    public void executeAndStop() {
                        player.teleport(3096, 3468);
                    }
                });
                player.animate(-1);
                player.setPositionToFace(new Tile(3095, 3468));
            } else if (obj.tile().equals(3005, 10363)) { // Barb
                ladderUp(player, player.tile().transform(0, -6400), true);
            } else if (obj.tile().equals(2547, 9951)) { // Wildy
                ladderUp(player, new Tile(2548, 3551), true);
            } else if (obj.tile().equals(3088, 9971)) {
                ladderUp(player, new Tile(player.tile().x, player.tile().y - 6400), true);
            } else if (obj.tile().equals(2562, 9756)) { // chaos druid tower dungeon to upstairs
                ladderUp(player, new Tile(2563, 3356), true);
            } else if (obj.tile().equals(2632, 9694)) { // ardy manhole dungeon for rat pits
                ladderUp(player, new Tile(2631, 3294), true);
            } else if (ladderTile.equals(3097, 9867)) { //edge ladder up
                ladderUp(player, new Tile(3092, 3489, 0), true);
            } else {
                player.message("This ladder does not seem to lead anywhere... Odd!");
            }
            return true;
        }
        if (obj.getId() == 272) {
            //up to deck
            if (obj.tile().x == 3018 && obj.tile().y == 3957) {
                ladderUp(player, new Tile(3018, 3958, 1), true);
            }
            return true;
        }
        if (obj.getId() == 273) {
            //down to hold
            if (obj.tile().x == 3018 && obj.tile().y == 3957) {
                ladderDown(player, new Tile(3018, 3958, 0), true);
            }
            return true;
        }
        if (obj.getId() == 1581) {
            ladderDown(player, new Tile(3096, 9867, 0), true);
            return true;
        }
        if(obj.getId() == 11867) {
            ladderDown(player, new Tile(3018, 9850), true);// Ladder down @ mines
            return true;
        }

        if(obj.getId() == 17387) {
            ladderUp(player, new Tile(3018, 3450), true);// Ladder up from mines
            return true;
        }

        //Basement is task only!
        if(obj.getId() == 30191) {
            if (player.slayerTaskAmount() > 0) {
                ladderDown(player, new Tile(3412, 9932, 3), true);
            } else {
                player.message("You can only enter the basement if you have a slayer task.");
            }
            return true;
        }
        if(obj.getId() == 30192) {
            ladderUp(player, new Tile(3417, 3536, 0), true);
            return true;
        }
        if(obj.getId() == 2118) {
            if (obj.tile().x == 3434 && obj.tile().y == 3537) {
                ladderDown(player, new Tile(3438, player.tile().y, player.tile().level - 1), false);
            }
            return true;
        }
        if(obj.getId() == 2114) {
            if (obj.tile().x == 3434 && obj.tile().y == 3537) {
                ladderUp(player, new Tile(3433, player.tile().y, player.tile().level + 1), false);
            }
            return true;
        }
        if(obj.getId() == 2119) {
            if (obj.tile().x == 3413 && obj.tile().y == 3540) {
                ladderUp(player, new Tile(3417, player.tile().y, player.tile().level + 1), false);
            }
            return true;
        }
        if(obj.getId() == 2120) {
            if (obj.tile().x == 3415 && obj.tile().y == 3540) {
                ladderDown(player, new Tile(3412, player.tile().y, player.tile().level - 1), false);
            }
            return true;
        }
        return false;
    }
}
