package com.cryptic.model.content.sigils;

import com.cryptic.model.content.sigils.data.SigilData;
import com.cryptic.model.content.sigils.io.*;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
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
        sigils.add(new DeftStrikes());
        sigils.add(new MeticulousMage());
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
    public void sigilAccuracyBonus(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
        for (var sigil : handler) {
            if (sigil.attuned(player)) {
                sigil.applyBoost(player, target, rangeAccuracy, magicAccuracy, meleeAccuracy);
            }
        }
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 1) {
            for (var sigil : SigilData.values()) {
                if (item.getId() == sigil.unattuned) {
                    if (player.hasAttrib(sigil.attributeKey)) {
                        player.message(Color.RED.wrap("You cannot have more than one of the same sigil activated."));
                        return false;
                    }
                    player.putAttrib(sigil.attributeKey, true);
                    player.animate(713);
                    player.graphic(1970, GraphicHeight.HIGH, 20);
                    player.getInventory().replace(sigil.unattuned, sigil.attuned, true);
                    return true;
                }
            }
        } else if (option == 2) {
            for (var sigil : SigilData.values()) {
                if (item.getId() == sigil.attuned) {
                    player.clearAttrib(sigil.attributeKey);
                    player.getInventory().replace(sigil.attuned, sigil.unattuned, true);
                    return true;
                }
            }
        }
        return false;
    }
}
