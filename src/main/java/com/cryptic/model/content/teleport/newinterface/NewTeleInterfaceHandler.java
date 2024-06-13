package com.cryptic.model.content.teleport.newinterface;

import com.cryptic.model.content.areas.edgevile.dialogue.SkillingAreaHuntingExpertDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import org.apache.commons.lang3.Range;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NewTeleInterfaceHandler extends PacketInteraction {

    public static final Range<Integer> TELEPORT_BUTTONS = Range.between(88101, 88101 + 30);
    public static final Range<Integer> FAVORITE_BUTTONS = Range.between((88101 + 30 + 30 + 30), (88101 + 30 + 30 + 30) + 30);

    @Override
    public boolean handleButtonInteraction(Player player, int button) {
        if (TELEPORT_BUTTONS.contains(button)) {
            List<SpecificTeleport> thespecificteleport = player.getnewteleInterface().thespecificteleport.stream().filter(t -> t.button == button).collect(Collectors.toList());
            if (thespecificteleport.getFirst().description.toLowerCase().contains("dangerous")) {
                player.getnewteleInterface().confirmDangerousTeleport(thespecificteleport.getFirst());
            } else if (Objects.requireNonNull(thespecificteleport).getFirst().text.equals("GodWars Bosses")) {
                player.getnewteleInterface().gwdOptions();
            } else if (thespecificteleport.getFirst().text.equals("Fishing Areas")) {
                player.getnewteleInterface().fishingAreas();
            } else if (thespecificteleport.getFirst().text.equals("Mining Areas")) {
                player.getnewteleInterface().miningAreas();
            } else if (thespecificteleport.getFirst().text.equals("Rooftop Agility Areas")) {
                player.getnewteleInterface().roofTopAreas();
            } else if (thespecificteleport.getFirst().text.equals("Runecrafting Areas")) {
                player.getnewteleInterface().runecraftingAreas();
            } else if (thespecificteleport.getFirst().text.equals("Woodcutting Areas")) {
                player.getnewteleInterface().woodcuttingAreas();
            } else if (thespecificteleport.getFirst().text.equals("Hunter Areas")) {
                player.getDialogueManager().start(new SkillingAreaHuntingExpertDialogue());
            } else if (thespecificteleport.getFirst().text.equals("Wilerness Event Boss")) {
                player.getnewteleInterface().wildernessEvent();
            } else {
                player.getnewteleInterface().confirmdialog(thespecificteleport.getFirst());
            }
        }

        if (FAVORITE_BUTTONS.contains(button)) {

            List<SpecificTeleport> thespecificteleport = player.getnewteleInterface().thespecificteleport.stream().filter(t -> t.favoritebutton == button).toList();

            if (player.getnewfavs().stream().anyMatch(b -> b.text.equalsIgnoreCase(thespecificteleport.getFirst().text))) {
                List<SpecificTeleport> toremove = player.getnewfavs().stream().filter(b -> b.text.equalsIgnoreCase(thespecificteleport.getFirst().text)).toList();
                SpecificTeleport specifictoremove = toremove.getFirst();
                player.getnewfavs().remove(specifictoremove);
                player.getPacketSender().sendChangeSprite(button, (byte) 0);
                if (player.getnewteleInterface().category == 0)
                    player.getnewteleInterface().drawInterface(88005);
                return false;
            }

            SpecificTeleport telewereadding = thespecificteleport.getFirst();
            player.getnewfavs().add(new SpecificTeleport(telewereadding.button, telewereadding.tile, telewereadding.text, telewereadding.description, true, telewereadding.favoritebutton));
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
