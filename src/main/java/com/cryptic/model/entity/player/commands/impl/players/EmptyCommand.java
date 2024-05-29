package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.cs2.impl.dialogue.Dialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class EmptyCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(WildernessArea.isInWilderness(player)) {
            player.message("You can't use this command in the wilderness.");
            return;
        }
        player.getDialogueManager().start(new EmptyInventoryDialogue());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    private final static class EmptyInventoryDialogue extends Dialogue {

        @Override
        protected void start(Object... parameters) {
            sendStatement("Are you sure you wish to empty your inventory?", "This cannot be undone.");
            setPhase(0);
        }

        @Override
        public void next() {
            if (isPhase(0)) {
                sendOption(DEFAULT_OPTION_TITLE, "Yes", "No");
                setPhase(1);
            }
        }

        @Override
        public void select(int option) {
            if (isPhase(1)) {
                setPhase(2);
                switch (option) {
                    case 1 -> {
                        player.stopActions(false);
                        player.inventory().clear(true);
                        stop();
                    }
                    case 2 -> stop();
                }
            }
        }
    }

}
