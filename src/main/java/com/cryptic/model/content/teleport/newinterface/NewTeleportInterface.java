package com.cryptic.model.content.teleport.newinterface;

import com.cryptic.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.model.map.position.Tile;

import java.util.*;
import java.util.stream.Collectors;

public class NewTeleportInterface {


    private final Player player;

    public NewTeleportInterface(Player player) {
        this.player = player;
    }


    public static final int FAVORITES_TAB = 0;
    public static final int TRAINING = 1;
    static final int SLAYING = 2;
    public static final int BOSSING = 3;
    public static final int SKILLING = 4;
    public static final int MINIGAMES = 5;
    public static final int WILDERNESS = 6;
    public static final int CITIES = 7;
    public static final int MISCELLANEOUS = 8;

    private final NewTeleData[] VALUES = NewTeleData.values();

    public ArrayList<SpecificTeleport> thespecificteleport = new ArrayList<>();


    public void confirmdialog(SpecificTeleport thespecificteleport) {

        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start (Object...parameters){
                send(DialogueType.OPTION, "Confirm teleport to "+thespecificteleport.text+" ?", "Yes", "Cancel");
                setPhase(0);
            }

            @Override
            protected void select ( int option){
                if (option == 1) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        return;
                    }
                    Teleports.basicTeleport(player, thespecificteleport.tile);
                    stop();
                } else if (option == 2) {
                    stop();
                }
            }
        });
    }

    public void confirmDangerousTeleport(SpecificTeleport thespecificteleport) {
        Tile teleportLocation = thespecificteleport.tile;
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.STATEMENT, "This location is dangerous, would you like to proceed?");
                setPhase(0);
            }

            @Override
            protected void next() {
                if (isPhase(0)) {
                    send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes.", "No.");
                    setPhase(1);
                }
            }

            @Override
            protected void select(int option) {
                if (isPhase(1)) {
                    if (option == 1) {
                        if(thespecificteleport != null && !Teleports.pkTeleportOk(player, teleportLocation)) {
                            stop();
                            return;
                        }

                        if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                            stop();
                            return;
                        }
                        Teleports.basicTeleport(player, teleportLocation);
                    } else if (option == 2) {
                        stop();
                    }
                }
            }
        });
    }

    public void gwdOptions() {
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.GODWARS_ENTRY)) {
            player.message("You need to unlock this teleport first.");
            return;
        }
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Armadyl", "Bandos", "Saradomin", "Zamorak");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2841, 5291, 2));
                } else if (option == 2) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2860, 5354, 2));
                } else if (option == 3) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2911, 5267, 0));
                } else if (option == 4) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2925, 5336, 2));
                }
            }
        });
    }

    public void fishingAreas() {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Catherby Fishing Area", "Fishing Guild", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2835,3433));
                } else if (option == 2) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2594, 3415));
                } else if (option == 3) {
                    stop();
                }
            }
        });
    }

    public void miningAreas() {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Essence Mining", "Varrock Mining Area", "Desert Mining Area", "Mining Guild", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2911, 4830));
                } else if (option == 2) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(3284,3365));
                } else if (option == 3) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(3300,3300));
                } else if (option == 4) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(3050, 9762));
                } else if (option == 5) {
                    stop();
                }
            }
        });
    }

    public void woodcuttingAreas() {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Camelot Trees", "Woodcutting Guild", "Nevermind");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(2726, 3473));
                } else if (option == 2) {
                    if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                        stop();
                        return;
                    }
                    Teleports.basicTeleport(player, new Tile(1663, 3506));
                } else if (option == 3) {
                    stop();
                }
            }
        });
    }

    public void wildernessEvent() {
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.WORLD_BOSS_TELEPORT)) {
            player.message("You do not meet the requirements to use this teleport.");
            return;
        }
        if (WildernessBossEvent.getINSTANCE().getActiveNpc().isPresent() && WildernessBossEvent.currentSpawnPos != null) {
            Tile tile = WildernessBossEvent.currentSpawnPos;
            if(!Teleports.pkTeleportOk(player, tile)) {
                return;
            }
            if (!Teleports.canTeleport(player,true, TeleportType.GENERIC)) {
                return;
            }
            Teleports.basicTeleport(player, tile);
        } else {
            player.message("The world boss recently died and will respawn shortly.");
        }
    }

    int teleportbutton = 88101;
    int textbutton = 88101 + 30 ;
    int descriptionbutton = 88101 + 30 + 30;
    int favoritebutton = 88101 + 30 + 30 + 30;
    public void hidetheindividualteleports() {
        thespecificteleport.clear();
        for(int i = 0 ; i < 30; i ++){
            player.getPacketSender().sendInterfaceComponentMoval(-900, 0, teleportbutton + i);
            player.getPacketSender().sendInterfaceComponentMoval(-900, 0, textbutton+i);
            player.getPacketSender().sendInterfaceComponentMoval(-900, 0, descriptionbutton+i);
            player.getPacketSender().sendInterfaceComponentMoval(-900, 0, favoritebutton+i);

        }
    }

    int category = 0;
    public void displaythecategories(List<NewTeleData> listofthespecificteleports){
        category = listofthespecificteleports.get(0).category;
        for(int i = 0 ; i < listofthespecificteleports.size(); i++){
            NewTeleData thespecificteleportdata = listofthespecificteleports.get(i);
            boolean favorited = false;
            for(SpecificTeleport tele : player.getnewfavs()){
                if(thespecificteleportdata.text.equalsIgnoreCase(tele.text))
                    favorited =true;
            }
            thespecificteleport.add(new SpecificTeleport(teleportbutton + i, thespecificteleportdata.tile,thespecificteleportdata.text, thespecificteleportdata.description, favorited, favoritebutton+i));
        }

    }
    public List<NewTeleData> getalltasksbasedoncategory(int category) {
        List<NewTeleData> tasks = Arrays.stream(VALUES).filter(task -> task.category == category).collect(Collectors.toList());

        return tasks;
    }

    public void open(){

        player.message("sendfavorites##");
        drawInterface(88005);

        player.getInterfaceManager().open(88000);
    }

    public void drawInterface(int button){
        hidetheindividualteleports();

        List<NewTeleData> specificcategory;
        List<SpecificTeleport> allthefavoriteteleportsyouhavesaved;


        if (button == 88005) {
            allthefavoriteteleportsyouhavesaved = player.getnewfavs();
            displaythefavorites(allthefavoriteteleportsyouhavesaved);
            //    player.getnewteleInterface().displayFavorites(allthefavoriteteleportsyouhavesaved);
        } else {
            specificcategory = player.getnewteleInterface().getalltasksbasedoncategory(button-88005);
            displaythecategories(specificcategory);
        }
        for(int i = 0 ; i < thespecificteleport.size(); i ++){
            player.getPacketSender().sendInterfaceComponentMoval(0, 0, teleportbutton + i);
            player.getPacketSender().sendInterfaceComponentMoval(0, 0, textbutton+i);
            player.getPacketSender().sendInterfaceComponentMoval(0, 0, descriptionbutton+i);
            player.getPacketSender().sendInterfaceComponentMoval(0, 0, favoritebutton+i);

        }
        for(int i = 0 ; i < thespecificteleport.size(); i ++){
            SpecificTeleport data = thespecificteleport.get(i);
            player.getPacketSender().sendString(textbutton+i, data.text);
            player.getPacketSender().sendString(descriptionbutton+i, data.description);
            boolean favorited = data.favorited ? true : false;
            player.getPacketSender().sendChangeSprite(data.favoritebutton, favorited ? (byte) 1 : (byte) 0 );
        }

        player.getPacketSender().sendScrollbarHeight(88050, thespecificteleport.size() * 37);
    }


    //all it has to do is populate the array
    public void displaythefavorites(List<SpecificTeleport> listoffavorites){
        category = FAVORITES_TAB;
        for(int i = 0 ; i < listoffavorites.size(); i++){
            SpecificTeleport thespecificteleportdata = listoffavorites.get(i);
            boolean favorited = thespecificteleportdata.favorited ? true : false;
            thespecificteleport.add(new SpecificTeleport(teleportbutton + i, thespecificteleportdata.tile,thespecificteleportdata.text, thespecificteleportdata.description, favorited, favoritebutton+i));
        }



    }
}
