package com.cryptic.model.content.sigils;

import com.cryptic.model.content.sigils.data.SigilData;
import com.cryptic.model.content.sigils.io.*;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.Combat;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
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
        sigils.add(new Consistency());
        sigils.add(new FormidableFighter());
        sigils.add(new Resistance());
        return sigils;
    }

    @Override
    public void processResistance(Entity attacker, Entity target, Hit hit) {
        if (!(attacker instanceof NPC)) return;
        if (target instanceof Player player) {
            for (AbstractSigilHandler sigil : handler) {
                if (sigil.attuned(player)) {
                    sigil.resistanceModification(attacker, player, hit);
                }
            }
        }
    }

    @Override
    public void processDamage(Player player, Hit hit) {
        if (WildernessArea.inWilderness(player.tile())) return;
        Combat combat = player.getCombat();
        if (combat == null) return;
        CombatType combatType = combat.getCombatType();
        if (combatType == null) return;
        Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return;
        for (AbstractSigilHandler sigil : handler) {
            if (sigil.attuned(player)) {
                if (sigil.validateCombatType(player)) {
                    sigil.damageModification(player, hit);
                }
            }
        }
    }

    @Override
    public void process(Player player, Entity target) {
        if (WildernessArea.inWilderness(player.tile())) return;
        Combat combat = player.getCombat();
        if (combat == null) return;
        CombatType combatType = combat.getCombatType();
        if (combatType == null) return;
        Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return;
        for (AbstractSigilHandler sigil : handler) {
            if (sigil.attuned(player)) {
                if (sigil.validateCombatType(player)) {
                    sigil.process(player, target);
                }
            }
        }
    }

    @Override
    public void sigilAccuracyBonus(Player player, Entity target, RangeAccuracy rangeAccuracy, MagicAccuracy magicAccuracy, MeleeAccuracy meleeAccuracy) {
        if (WildernessArea.inWilderness(player.tile())) return;
        Combat combat = player.getCombat();
        if (combat == null) return;
        CombatType combatType = combat.getCombatType();
        if (combatType == null) return;
        Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return;
        for (AbstractSigilHandler sigil : handler) {
            if (sigil.attuned(player)) {
                if (sigil.validateCombatType(player))
                    sigil.accuracyModification(player, target, rangeAccuracy, magicAccuracy, meleeAccuracy);
            }
        }
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        var total = player.<Integer>getAttribOr(AttributeKey.TOTAL_SIGILS_ACTIVATED, 0);
        int activationCap = 3;
        switch (player.getMemberRights()) {
            case DIAMOND_MEMBER, DRAGONSTONE_MEMBER -> activationCap = 4;
            case ONYX_MEMBER, ZENYTE_MEMBER -> activationCap = 5;
        }
        if (option == 1) {
            for (var sigil : SigilData.values()) {
                if (item.getId() == sigil.unattuned) {
                    if (player.hasAttrib(sigil.attributeKey)) {
                        player.message(Color.RED.wrap("You cannot have more than one of the same sigil activated."));
                        return false;
                    }
                    if (total == activationCap) {
                        player.message(Color.RED.wrap("You can only have " + activationCap + " sigil's activated at one time."));
                        return false;
                    }
                    total += 1;
                    player.putAttrib(sigil.attributeKey, true);
                    player.putAttrib(AttributeKey.TOTAL_SIGILS_ACTIVATED, total);
                    player.animate(713);
                    player.graphic(1970, GraphicHeight.HIGH, 20);
                    player.getInventory().replace(sigil.unattuned, sigil.attuned, true);
                    return true;
                }
            }
        } else if (option == 2) {
            for (var sigil : SigilData.values()) {
                if (item.getId() == sigil.attuned) {
                    total -= 1;
                    player.putAttrib(AttributeKey.TOTAL_SIGILS_ACTIVATED, total);
                    player.clearAttrib(sigil.attributeKey);
                    player.getInventory().replace(sigil.attuned, sigil.unattuned, true);
                    return true;
                }
            }
        }
        return false;
    }
}
