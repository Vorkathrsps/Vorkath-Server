package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class SpellbookCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        // Known exploit
        if (command.contains("\r") || command.contains("\n")) {
            return;
        }
        if (parts.length < 2) {
            player.message("Spellbook usage: ::spellbook 0-3 or ::spellbook modern, ancient, lunar, arc");
            return;
        }

        if(WildernessArea.isInWilderness(player) && !player.getPlayerRights().isCommunityManager(player)) {
            player.message("You can't use this command here.");
            return;
        }

        MagicSpellbook book = switch (parts[1].toLowerCase()) {
            case "0", "normal", "regular", "modern" -> MagicSpellbook.NORMAL;
            case "1", "ancients", "ancient" -> MagicSpellbook.ANCIENTS;
            case "2", "lunar" -> MagicSpellbook.LUNAR;
            case "3", "arc" -> MagicSpellbook.ARCEUUS;
            default -> player.getSpellbook();
        };
        MagicSpellbook.changeSpellbook(player, book, true);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }
}
