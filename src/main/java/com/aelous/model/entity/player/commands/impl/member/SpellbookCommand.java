package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.model.entity.player.MagicSpellbook;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;

public class SpellbookCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (parts.length < 2) {
            player.message("Spellbook usage: ::spellbook 0-2 or ::spellbook modern, ancient, lunar");
            return;
        }

        if(WildernessArea.inWild(player)) {
            player.message("You can't use this command here.");
            return;
        }

        MagicSpellbook book = switch (parts[1].toLowerCase()) {
            case "0", "normal", "regular", "modern" -> MagicSpellbook.NORMAL;
            case "1", "ancients", "ancient" -> MagicSpellbook.ANCIENT;
            case "2", "lunar" -> MagicSpellbook.LUNAR;
            default -> player.getSpellbook();
        };
        MagicSpellbook.changeSpellbook(player, book, true);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }
}
