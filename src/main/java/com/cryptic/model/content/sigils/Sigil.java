package com.cryptic.model.content.sigils;

import com.cryptic.model.content.sigils.data.SigilData;
import com.cryptic.model.content.sigils.io.FeralFighter;
import com.cryptic.model.content.sigils.io.MenacingMage;
import com.cryptic.model.content.sigils.io.RuthlessRanger;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;

import java.util.ArrayList;
import java.util.List;

public class Sigil extends PacketInteraction implements SigilListener {
    private static final List<AbstractSigilHandler> handler;

    static {
        handler = initialize();
    }

    private static List<AbstractSigilHandler> initialize() {
        List<AbstractSigilHandler> sigils = new ArrayList<>();
        sigils.add(new FeralFighter());
        sigils.add(new MenacingMage());
        sigils.add(new RuthlessRanger());
        return sigils;
    }

    @Override
    public void prepare(Player player, Entity target) {
        for (var sigil : handler) {
            if (sigil.attuned(player)) {
                sigil.process(player, target);
            }
        }
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            for (var s : SigilData.values()) {
                if (player.hasAttrib(s.attributeKey)) {
                    player.message(Color.RED.wrap("You cannot have more than one of the same sigil activated."));
                    return false;
                }
                if (item.getId() == s.unattuned) {
                    player.putAttrib(s.attributeKey, true);
                    player.animate(713);
                    player.graphic(1970, GraphicHeight.HIGH, 20);
                    player.getInventory().remove(s.unattuned);
                    player.getInventory().add(s.attuned);
                    return true;
                }
            }
        } else if (option == 2) {
            for (var s : SigilData.values()) {
                if (item.getId() == s.attuned) {
                    player.clearAttrib(s.attributeKey);
                    player.getInventory().remove(s.attuned);
                    player.getInventory().add(s.unattuned);
                    return true;
                }
            }
        }
        return false;
    }
}
