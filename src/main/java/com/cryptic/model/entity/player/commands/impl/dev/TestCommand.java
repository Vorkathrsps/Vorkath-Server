
package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.npc.HealthHud;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.items.Item;

public class TestCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendItemDestroy(new Item(22675,0),"THING");
                setPhase(0);
            }

            @Override
            protected void next() {}

            @Override
            protected void select(int option) {
                if (option == 0) {
                    System.out.println("YES");
                } else if (option == 1) {
                    System.out.println("NA");
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) { //kkl
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
