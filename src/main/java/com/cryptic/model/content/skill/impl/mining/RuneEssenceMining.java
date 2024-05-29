package com.cryptic.model.content.skill.impl.mining;

import com.cryptic.model.action.impl.UnwalkableAction;
import com.cryptic.model.content.skill.perks.SkillingSets;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.cs2.impl.dialogue.DialogueManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import java.util.Optional;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.RUNE_ESSENCE_34773;

/**
 * Created by Bart on 10/27/2015.
 */
public class RuneEssenceMining extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == RUNE_ESSENCE_34773) {
                mineEssence(player);
                return true;
            }
            if (obj.getId() == EXIT_PORTAL) {
                player.lockNoDamage();
                player.graphic(110, GraphicHeight.HIGH, 30);
                player.message("You step through the portal.");
                Chain.bound(player).runFn(2, () -> {
                    player.teleport(3253, 3401);
                    player.unlock();
                });
                return true;
            }
        }
        return false;
    }

    private static final int EXIT_PORTAL = 7479;

    private void mineEssence(Player player) {
        Optional<Pickaxe> pick = Mining.findPickaxe(player);

        if (pick.isEmpty()) {
            player.getDialogueManager().sendStatement( "You need a pickaxe to mine this rock. You do not have a pickaxe", "which you have the Mining level to use.");
            return;
        }

        if (player.inventory().isFull()) {
            player.message("Your inventory is too full to hold any more rune stones.");
            player.animate(-1);
            return;
        }

        Chain.bound(player).runFn(1, () -> player.message("You swing your pick at the rock."));

        player.action.execute(new UnwalkableAction(player, 1) {
            int ticks = 1; // ticks at start

            @Override
            protected void execute() {
                ticks--;

                if (ticks == 0) {
                    if (player.inventory().isFull()) {
                        player.message("Your inventory is too full to hold any more rune stones.");
                        player.animate(-1);
                        return;
                    }

                    player.animate(pick.get().anim);
                    Chain.bound(player).runFn(pick.get().getDelay(), () -> {
                        Item essence = new Item(player.getSkills().level(Skills.MINING) >= 30 ? 7936 : 1436);
                        for (var set : SkillingSets.VALUES) {
                            if (set.getSkillType().equals(Skill.MINING)) {
                                if (player.getEquipment().containsAll(set.getSet())) {
                                    essence = essence.note();
                                }
                            }
                        }
                        player.inventory().add(essence);
                        player.getSkills().addXp(Skills.MINING, 5.0);
                    });
                }

                if (ticks == 0) {
                    ticks = 4;
                }
            }
        });
    }


}
