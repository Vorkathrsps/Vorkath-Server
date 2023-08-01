package com.cryptic.model.content.teleport.newinterface;

import com.cryptic.model.content.areas.edgevile.dialogue.SkillingAreaHuntingExpertDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import org.apache.commons.lang3.Range;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NewTeleInterfaceHandler extends PacketInteraction {

    public static final Range<Integer> TELEPORT_BUTTONS = Range.between(88101, 88101+30);
    public static final Range<Integer> FAVORITE_BUTTONS = Range.between((88101+30+30+30), (88101+30+30+30)+30);

    @Override
    public boolean handleButtonInteraction(Player player, int button) {

        if (TELEPORT_BUTTONS.contains(button)) {
            List<SpecificTeleport> thespecificteleport = player.getnewteleInterface().thespecificteleport.stream().filter(t -> t.button == button).collect(Collectors.toList());
            if (thespecificteleport.get(0).description.toLowerCase().contains("dangerous")) {
                player.getnewteleInterface().confirmDangerousTeleport(thespecificteleport.get(0));
            } else if (Objects.requireNonNull(thespecificteleport).get(0).text.equals("GodWars Bosses")) {
                player.getnewteleInterface().gwdOptions();
            } else if (thespecificteleport.get(0).text.equals("Fishing Areas")) {
                player.getnewteleInterface().fishingAreas();
            } else if (thespecificteleport.get(0).text.equals("Mining Areas")) {
                player.getnewteleInterface().miningAreas();
            } else if (thespecificteleport.get(0).text.equals("Woodcutting Areas")) {
                player.getnewteleInterface().woodcuttingAreas();
            } else if (thespecificteleport.get(0).text.equals("Hunter Areas")) {
                player.getDialogueManager().start(new SkillingAreaHuntingExpertDialogue());
            } else if (thespecificteleport.get(0).text.equals("Wilerness Event Boss")) {
                player.getnewteleInterface().wildernessEvent();
            } else {
                player.getnewteleInterface().confirmdialog(thespecificteleport.get(0));
            }
        }

        if (FAVORITE_BUTTONS.contains(button)) {

            List<SpecificTeleport> thespecificteleport = player.getnewteleInterface().thespecificteleport.stream().filter(t -> t.favoritebutton == button).collect(Collectors.toList());

            if(player.getnewfavs().stream().anyMatch(b -> b.text.equalsIgnoreCase(thespecificteleport.get(0).text))){
                List<SpecificTeleport> toremove = player.getnewfavs().stream().filter(b -> b.text.equalsIgnoreCase(thespecificteleport.get(0).text)).collect(Collectors.toList());
                SpecificTeleport specifictoremove = toremove.get(0);
                player.getnewfavs().remove(specifictoremove);
                player.getPacketSender().sendChangeSprite(button, (byte) 0);
                if(player.getnewteleInterface().category == 0)
                    player.getnewteleInterface().drawInterface(88005);
                return false;
            }

            SpecificTeleport telewereadding = thespecificteleport.get(0);
            player.getnewfavs().add(new SpecificTeleport(telewereadding.button,telewereadding.tile,telewereadding.text,telewereadding.description,true,telewereadding.favoritebutton));
            player.getPacketSender().sendChangeSprite(button, (byte) 1);
            return true;
        }
        //categories
        switch (button) {//favorite
            case 88005, 88006, 88007, 88008, 88009, 88010, 88011, 88012, 88013 ->
                player.getnewteleInterface().drawInterface(button);
        }

        return false;
    }

}
