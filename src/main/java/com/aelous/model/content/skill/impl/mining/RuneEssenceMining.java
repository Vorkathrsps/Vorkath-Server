package com.aelous.model.content.skill.impl.mining;

import com.aelous.model.action.impl.UnwalkableAction;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import java.util.Optional;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.RUNE_ESSENCE_34773;

/**
 * Created by Bart on 10/27/2015.
 */
public class RuneEssenceMining extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
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
            DialogueManager.sendStatement(player,"You need a pickaxe to mine this rock. You do not have a pickaxe", "which you have the Mining level to use.");
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
                        player.inventory().add(new Item(player.getSkills().level(Skills.MINING) >= 30 ? 7936 : 1436));
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
