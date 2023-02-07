package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.Attacks;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 16, 2021, 18:41
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Swap {

    public static void performAttack(Party party, int height) {
        //System.out.println("Swap");
        party.setLeftHandAttackTimer(20);
        Chain.bound(null).runFn(1, () -> {
            if (party.isLeftHandDead()) {
                return;
            }
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7359));

            int playersToChoose = 0;
            int graphic = Attacks.WHITE_CIRCLE;
            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                    if (!party.getSwapPlayers().contains(member)) {
                        party.getSwapPlayers().add(member);
                        playersToChoose++;
                        member.graphic(graphic);
                        member.setGraphicSwap(graphic);
                        if (playersToChoose % 2 == 0) {
                            graphic++;
                        }
                        party.setLonePair(false);
                    }
                }
            }

            if (playersToChoose == 1 || playersToChoose == 3 || playersToChoose == 5) {
                party.setSwapTile(Attacks.randomLocation(height));
                party.setGraphicSwap(graphic);
                World.getWorld().tileGraphic(graphic, party.getSwapTile(),0,0);
                party.setLonePair(true);
            }

            for (int index = 0; index < 6; index += 2) {
                if (index == 0 && party.getSwapPlayers().size() >= 2 || index > 0 && party.getSwapPlayers().size() >= (index * 2)) {
                    party.getSwapPlayers().get(index).message("You have been paired with <col=FF0000>" + party.getSwapPlayers().get(index + 1).getUsername() + "</col>! The magical power will enact soon...");
                } else {
                    if (party.getSwapPlayers().size() - 1 >= index)
                        party.getSwapPlayers().get(index).message("The Basilisk had no one to pair you with! The magical power will enact soon...");
                }
            }
            for (int index = 1; index < 6; index += 2) {
                if (index == 1 && party.getSwapPlayers().size() >= 2 || index > 1 && party.getSwapPlayers().size() >= (index * 2)) {
                    party.getSwapPlayers().get(index).message("You have been paired with <col=FF0000>" + party.getSwapPlayers().get(index - 1).getUsername() + "</col>! The magical power will enact soon...");
                } else {
                    if (party.getSwapPlayers().size() - 1 >= index)
                        party.getSwapPlayers().get(index).message("The Basilisk had no one to pair you with! The magical power will enact soon...");
                }
            }
        }).then(3, () -> {
            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                    member.graphic(member.getGraphicSwap());
                }
            }
            if (party.isLonePair())
                World.getWorld().tileGraphic(party.getGraphicSwap(), party.getSwapTile(),0,0);
        }).then(3, () -> {
            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                    member.graphic(member.getGraphicSwap());
                }
            }
            if (party.isLonePair())
                World.getWorld().tileGraphic(party.getGraphicSwap(), party.getSwapTile(),0,0);
        }).then(2, () -> {
            for (int index = 0; index < 6; index += 2) {
                if (index == 0 && party.getSwapPlayers().size() >= 2 || index > 0 && party.getSwapPlayers().size() >= (index * 2)) {
                    Tile firstPlayer = party.getSwapPlayers().get(index).tile();
                    Tile secondPlayer = party.getSwapPlayers().get(index + 1).tile();
                    hitPlayerSwap(party, firstPlayer, secondPlayer, index, true);
                } else {
                    if (party.getSwapPlayers().size() - 1 >= index) {
                        Tile firstPlayer = party.getSwapPlayers().get(index).tile();
                        Tile secondPlayer = party.getSwapTile();
                        hitPlayerSwap(party, firstPlayer, secondPlayer, index, false);
                    }
                }
            }
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7355));
            party.getSwapPlayers().clear();
        });
    }

    public static void hitPlayerSwap(Party party, Tile firstPlayer, Tile secondPlayer, int index, boolean foundPair) {
        if (party.getSwapPlayers().get(index).dead() || party.getSwapPlayers().get(index).skills().level(Skills.HITPOINTS) < 1) {
            return;
        }
        if (foundPair) {
            if (party.getSwapPlayers().get(index).tile() != party.getSwapPlayers().get(index + 1).tile()) {
                party.getSwapPlayers().get(index).teleport(secondPlayer);
                party.getSwapPlayers().get(index + 1).teleport(firstPlayer);
                if (!(firstPlayer.equals(secondPlayer))) {
                    Player p1 = party.getSwapPlayers().get(index);
                    Player p2 = party.getSwapPlayers().get(index + 1);
                    if (p1.getRaids() != null && p1.getRaids().raiding(p1) && GreatOlm.insideChamber(party.getSwapPlayers().get(index)) || p2.getRaids() != null && p2.getRaids().raiding(p2) && GreatOlm.insideChamber(party.getSwapPlayers().get(index + 1))) {
                        return;
                    }
                    party.getSwapPlayers().get(index).message("Yourself and " + party.getSwapPlayers().get(index + 1).getUsername() + " have swapped places!");
                    party.getSwapPlayers().get(index + 1).message("Yourself and " + party.getSwapPlayers().get(index).getUsername() + " have swapped places!");
                    if (Utils.isWithinDiagonalDistance(firstPlayer, secondPlayer, 1,1,1)) {
                        party.getSwapPlayers().get(index).hit(party.getGreatOlmNpc(), World.getWorld().random(10, 12), CombatType.MAGIC).checkAccuracy().submit();
                        party.getSwapPlayers().get(index + 1).hit(party.getGreatOlmNpc(), World.getWorld().random(10, 12), CombatType.MAGIC).checkAccuracy().submit();
                    } else if (Utils.isWithinDiagonalDistance(firstPlayer, secondPlayer, 1,1,2)) {
                        party.getSwapPlayers().get(index).hit(party.getGreatOlmNpc(), World.getWorld().random(10, 20), CombatType.MAGIC).checkAccuracy().submit();
                        party.getSwapPlayers().get(index + 1).hit(party.getGreatOlmNpc(), World.getWorld().random(10, 20), CombatType.MAGIC).checkAccuracy().submit();
                    } else {
                        party.getSwapPlayers().get(index).hit(party.getGreatOlmNpc(), World.getWorld().random(20, 33), CombatType.MAGIC).checkAccuracy().submit();
                        party.getSwapPlayers().get(index + 1).hit(party.getGreatOlmNpc(), World.getWorld().random(20, 33), CombatType.MAGIC).checkAccuracy().submit();
                    }
                    party.getSwapPlayers().get(index + 1).graphic(1039);
                    party.getSwapPlayers().get(index).graphic(1039);
                }
            }
        }
    }
}
