package com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.specials;

import com.aelous.model.content.raids.chamber_of_xeric.great_olm.GreatOlm;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.OlmAnimations;
import com.aelous.model.content.raids.chamber_of_xeric.great_olm.attacks.Attacks;
import com.aelous.model.content.raids.party.Party;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | May, 16, 2021, 18:58
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class FallingCrystals {

    public static void performAttack(Party party, int height) {
        //System.out.println("FallingCrystals");
        party.getGreatOlmNpc().performGreatOlmAttack(party);
        party.setOlmAttackTimer(6);

        party.teamMessage("The Basilisk sounds a cry...");
        TaskManager.submit(new Task("FallingCrystals:performAttackTask1",1, party, true) {
            int tick = 0;

            @Override
            public void execute() {
                if (party.getGreatOlmNpc().dead() || party.isSwitchingPhases()) {
                    stop();
                }
                Player player = party.randomPartyPlayer();

                if(player.dead() || !player.isRegistered()) {
                    stop();
                    return;
                }
                if (tick == 1) {
                   player.message(Color.RED.wrap("The Basilisk has chosen you as its target - watch out!"));
                }
                if (tick == 2) {
                    OlmAnimations.resetAnimation(party);
                }
                for (int iz = 0; iz < 23; iz++) {
                    if (tick == 23) {
                        stop();
                    }
                    if (tick == (2 * iz) + 3) {
                        if (player.getRaids() != null && player.getRaids().raiding(player) && GreatOlm.insideChamber(player))
                           player.graphic(Attacks.RED_CIRCLE);
                    }
                    if (tick == (2 * iz) + 2) {
                        if (player.getRaids() != null && player.getRaids().raiding(player) && GreatOlm.insideChamber(player)) {
                            Tile pos =player.tile();
                            new Projectile(pos, new Tile(pos.getX(), pos.getY() - 1, height), 0, Attacks.CRYSTAL, 130, 55, 240, 0, 0).sendProjectile();
                            Chain.bound(null).runFn(1, () -> {
                                World.getWorld().tileGraphic(Attacks.LEFTOVER_CRYSTALS,pos,50,0);
                                for (Player member : party.getMembers()) {
                                    if (member != null && member.getRaids() != null && member.getRaids().raiding(member) && GreatOlm.insideChamber(member)) {
                                        if (member.tile().sameAs(pos)) {
                                            member.hit(party.getGreatOlmNpc(), World.getWorld().random(25), 2, CombatType.MAGIC).checkAccuracy().submit();
                                            member.message("The falling crystal shatters into you.");
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                tick++;
            }
        });

    }
}
