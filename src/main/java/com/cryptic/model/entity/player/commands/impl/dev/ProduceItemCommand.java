
package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.items.Item;

public class ProduceItemCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                sendProduceItem("What would you like to make?",2, 1135, 1099,1115,1121);
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
