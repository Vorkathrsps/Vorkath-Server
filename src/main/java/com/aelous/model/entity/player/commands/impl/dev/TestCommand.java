
package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;

public class TestCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        //not even sending a msg only dialogue
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.NPC_STATEMENT, SKULLY, 554, "Eyup. They call me Skully. I run this Wilderness Loot", "Chest. What can I do for you?");
                setPhase(0);
            }

            @Override
            protected void next() {
                if(isPhase(0)) {
                    send(DialogueType.NPC_STATEMENT, SKULLY, 554, "Ugly mfer");
                    setPhase(1);
                } else if(isPhase(1)) {
                    stop();
                }
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
