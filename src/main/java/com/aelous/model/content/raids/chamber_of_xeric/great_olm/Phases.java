package com.aelous.model.content.raids.chamber_of_xeric.great_olm;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.model.content.raids.RaidsNpc.BONUS_HP_PER_PLAYER;

/**
 * @author Patrick van Elderen | May, 16, 2021, 13:06
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Phases {

    private static final String[] phases = new String[]{"@gre@acid", "@mag@crystal", "@red@flame"};

    public static void raisePower(Party party) {
        int random = World.getWorld().random(2);
        if(!party.getPhaseAttack().contains(phases[random])) {
            party.teamMessage("The Basilisk rises with the power of " + phases[random] + ".");
            party.getPhaseAttack().add(phases[random]);
        } else {
            if(!party.getPhaseAttack().contains(phases[0])) {
                party.teamMessage("The Basilisk rises with the power of " + phases[0] + ".");
                party.getPhaseAttack().add(phases[0]);
            } else if(!party.getPhaseAttack().contains(phases[1])) {
                party.teamMessage("The Basilisk rises with the power of " + phases[1] + ".");
                party.getPhaseAttack().add(phases[1]);
            } else if(!party.getPhaseAttack().contains(phases[2])) {
                party.teamMessage("The Basilisk rises with the power of " + phases[2] + ".");
                party.getPhaseAttack().add(phases[2]);
            }
        }
    }

    public static final int OLM_LEFT_HAND = 7555;
    public static final int OLM_HEAD = 7554;
    public static final int OLM_RIGHT_HAND = 7553;

    public static void startPhase1(Party party, int height) {
        if(party.getCurrentPhase() >= 1) {
            return;
        }

        party.setCurrentPhase(1);

        party.setLeftHandTile(new Tile(3238, 5733, height));
        party.setGreatOlmTile(new Tile(3238, 5738, height));
        party.setRightHandTile(new Tile(3238, 5743, height));

        NPC leftHandNpc = NPC.of(7555, party.getLeftHandTile()); // left claw
        NPC greatolmNpc = NPC.of(7554, party.getGreatOlmTile()); // olm head
        NPC rightHandNpc = NPC.of(7553, party.getRightHandTile());// right claw

        party.setLeftHandNpc(leftHandNpc); // left claw
        party.setGreatOlmNpc(greatolmNpc); // olm head
        party.setRightHandNpc(rightHandNpc);// right claw

        leftHandNpc.setHitpoints((int) (leftHandNpc.hp() * (1 + (BONUS_HP_PER_PLAYER * (party.getSize() - 1)))));
        greatolmNpc.setHitpoints((int) (greatolmNpc.hp() * (1 + (BONUS_HP_PER_PLAYER * (party.getSize() - 1)))));
        rightHandNpc.setHitpoints((int) (rightHandNpc.hp() * (1 + (BONUS_HP_PER_PLAYER * (party.getSize() - 1)))));

        World.getWorld().registerNpc(party.getLeftHandNpc());
        World.getWorld().registerNpc(party.getGreatOlmNpc());
        World.getWorld().registerNpc(party.getRightHandNpc());

        party.setLeftHandObject(new GameObject(29883, party.getLeftHandTile(), 10, 1));
        party.setGreatOlmObject(new GameObject(29880, party.getGreatOlmTile(), 10, 1));
        party.setRightHandObject(new GameObject(29886, party.getRightHandTile(), 10, 1));

        ObjectManager.addObj(party.getLeftHandObject());
        ObjectManager.addObj(party.getGreatOlmObject());
        ObjectManager.addObj(party.getRightHandObject());
        Chain.bound(null).runFn(1, () -> {
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getLeftHandObject(), 7354));
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getGreatOlmObject(), 7335));
            party.forPlayers(player -> player.getPacketSender().sendObjectAnimation(party.getRightHandObject(), 7350));
        }).then(4, () -> {
            party.setLeftHandObject(new GameObject(29884, party.getLeftHandTile(), 10, 1));
            party.setGreatOlmObject(new GameObject(29881, party.getGreatOlmTile(), 10, 1));
            party.setRightHandObject(new GameObject(29887, party.getRightHandTile(), 10, 1));

            ObjectManager.addObj(party.getLeftHandObject());
            ObjectManager.addObj(party.getGreatOlmObject());
            ObjectManager.addObj(party.getRightHandObject());
            party.setTransitionPhase(false);
            party.setLeftHandDead(false);
            party.setRightHandDead(false);
            party.setSwitchingPhases(false);
            raisePower(party);
        });
        party.getGreatOlmNpc().spawnDirection(-1);
        party.getGreatOlmNpc().lastDirection(-1);
    }
}
