package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.left_hand;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.Attacks;
import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 16, 2021, 18:41
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class CrystalBurst {

    public static void performAttack(Party party) {
        //System.out.println("CrystalBurst");
        party.setLeftHandAttackTimer(20);
        Chain.bound(null).runFn(1, () -> {
            if (party.isLeftHandDead()) {
                return;
            }
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7356));
        }).then(2, () -> {
            int index = 0;

            for (Player member : party.getMembers()) {
                if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                    party.getCrystalBursts()[index] = member.tile();
                    ObjectManager.addObj(new GameObject(30033, party.getCrystalBursts()[index], 10, 3));
                    index++;
                }
            }
            party.setCrystalAmount(index);
        }).then(4, () -> {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7355));
            for (int i = 0; i < party.getCrystalAmount(); i++) {
                ObjectManager.addObj(new GameObject(30034, party.getCrystalBursts()[i], 10, 3));
                for (Player member : party.getMembers()) {
                    if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                        if (member.tile().sameAs(party.getCrystalBursts()[i])) {
                            member.message("The crystal beneath your feet grows rapidly and shunts you to the side.");
                            MovementQueue.clippedStep(member,1);
                            member.hit(party.getGreatOlmNpc(), World.getWorld().random(20,40), CombatType.MAGIC).checkAccuracy().submit();
                        }
                    }
                }
            }
        }).then(2, () -> {
            for (int i = 0; i < party.getCrystalAmount(); i++) {
                World.getWorld().tileGraphic(Attacks.LEFTOVER_CRYSTALS, party.getCrystalBursts()[i],50,0);
                ObjectManager.addObj(new GameObject(-1, party.getCrystalBursts()[i], 10, 3));
            }
        });
    }
}
