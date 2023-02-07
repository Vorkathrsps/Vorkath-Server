package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class EmptyCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(WildernessArea.inWild(player)) {
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
            send(DialogueType.STATEMENT, "Are you sure you wish to empty your inventory?", "This cannot be undone.");
            setPhase(0);
        }

        @Override
        public void next() {
            if (isPhase(0)) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Yes", "No");
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
