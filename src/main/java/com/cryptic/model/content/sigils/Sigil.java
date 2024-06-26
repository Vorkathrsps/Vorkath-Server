package com.cryptic.model.content.sigils;

import com.cryptic.model.content.sigils.data.SigilData;
import com.cryptic.model.content.sigils.combat.*;
import com.cryptic.model.content.sigils.misc.*;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.Combat;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sigil extends PacketInteraction implements SigilListener {
    private static final List<AbstractSigil> handler;

    static {
        List<AbstractSigil> sigilList = new ArrayList<>(List.of(
            new FeralFighter(),
            new MenacingMage(),
            new RuthlessRanger(),
            new DeftStrikes(),
            new MeticulousMage(),
            new Consistency(),
            new FormidableFighter(),
            new Resistance(),
            new Precision(),
            new Fortification(),
            new Stamina(),
            new Alchemaniac(),
            new Exaggeration(),
            new Devotion(),
            new LastRecall(),
            new RemoteStorage(),
            new Ninja(),
            new InfernalSmith(),
            new PiousProtection(),
            new Aggression(),
            new TreasureHunter()
        ));
        handler = Collections.unmodifiableList(sigilList);
    }

    @Override
    public void processResistance(final Entity attacker, final Entity target, final Hit hit) {
        if (!(attacker instanceof NPC)) return;
        if (target instanceof Player player) {
            for (SigilData data : SigilData.values()) {
                for (AbstractSigil sigil : handler) {
                    if (sigil == null) continue;
                    if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                        sigil.resistanceModification(attacker, player, hit);
                    }
                }
            }
        }
    }

    @Override
    public void processDamage(final Player player, final Hit hit) {
        if (WildernessArea.inWilderness(player.tile())) return;
        Combat combat = player.getCombat();
        if (combat == null) return;
        CombatType combatType = combat.getCombatType();
        if (combatType == null) return;
        Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return;
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil sigil : handler) {
                if (sigil == null) continue;
                if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                    if (sigil.validateCombatType(player)) {
                        sigil.damageModification(player, hit);
                    }
                }
            }
        }
    }

    @Override
    public void process(final Player player, final Entity target) {
        if (WildernessArea.inWilderness(player.tile()) && target instanceof Player) return;
        final Combat combat = player.getCombat();
        if (combat == null) return;
        final CombatType combatType = combat.getCombatType();
        if (combatType == null) return;
        final Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return;
        if (combatTarget == null) return;
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil sigil : handler) {
                if (sigil == null) continue;
                if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                    if (sigil.validateCombatType(player)) {
                        sigil.processCombat(player, target);
                    }
                }
            }
        }
    }

    @Override
    public double processAccuracy(final Player player, final Entity target, final AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (WildernessArea.inWilderness(player.tile()) && target instanceof Player) return boost;
        final Combat combat = player.getCombat();
        if (combat == null) return boost;
        final CombatType combatType = combat.getCombatType();
        if (combatType == null) return boost;
        final Entity combatTarget = combat.getTarget();
        if (combatTarget instanceof Player) return boost;
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil sigil : handler) {
                if (sigil == null) continue;
                if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                    if (sigil.validateCombatType(player))
                        boost += sigil.accuracyModification(player, target, accuracy);
                }
            }
        }
        return boost;
    }

    @Override
    public int processOffensiveEquipmentModification(final Player player) {
        int boost = 0;
        if (player == null) return 0;
        if (player.getCombat().getTarget() instanceof Player) return 0;
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil sigil : handler) {
                if (sigil == null) continue;
                if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                    boost += sigil.modifyOffensiveEquipmentBonuses(player);
                }
            }
        }
        return boost;
    }

    @Override
    public int processDefensiveEquipmentModification(final Player player) {
        int boost = 0;
        if (player == null) return 0;
        if (player.getCombat().getTarget() instanceof Player) return 0;
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil sigil : handler) {
                if (sigil == null) continue;
                if (data.handler.equals(sigil.getClass()) && sigil.attuned(player)) {
                    boost += sigil.modifyDefensiveEquipmentBonuses(player);
                }
            }
        }
        return boost;
    }

    @Override
    public void HandleLogin(final Player player) {
        for (SigilData data : SigilData.values()) {
            for (AbstractSigil listener : handler) {
                if (listener == null) continue;
                if (data.handler.equals(listener.getClass()) && listener.attuned(player)) {
                    listener.processMisc(player);
                }
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
            for (SigilData data : SigilData.values()) {
                if (item.getId() == data.unattuned) {
                    if (player.hasAttrib(data.attributeKey)) {
                        player.message(Color.RED.wrap("You cannot have more than one of the same sigil activated."));
                        return false;
                    }
                    if (total == activationCap) {
                        player.message(Color.RED.wrap("You can only have " + activationCap + " sigil's activated at one time."));
                        return false;
                    }
                    if (player.hasAttrib(AttributeKey.NINJA) && data.unattuned == ItemIdentifiers.SIGIL_OF_THE_FERAL_FIGHTER_26075) {
                        player.message(Color.RED.wrap("<shad=0>Sigil of the Feral Fighter cannot be activated with Sigil of the Ninja."));
                        return false;
                    }
                    if (player.hasAttrib(AttributeKey.FERAL_FIGHTER) && data.unattuned == ItemIdentifiers.SIGIL_OF_THE_NINJA_28526) {
                        player.message(Color.RED.wrap("<shad=0>Sigil of the Ninja cannot be activated with Sigil of the Feral Fighter."));
                        return false;
                    }
                    total += 1;
                    player.putAttrib(data.attributeKey, true);
                    player.putAttrib(AttributeKey.TOTAL_SIGILS_ACTIVATED, total);
                    player.animate(713);
                    player.graphic(data.graphic, GraphicHeight.HIGH, 20);
                    player.getInventory().replace(data.unattuned, data.attuned, true);
                    for (AbstractSigil listener : handler) {
                        if (listener == null) throw new RuntimeException("Exception in AbstractSigil");
                        if (data.handler.equals(listener.getClass()) && listener.attuned(player)) {
                            listener.processMisc(player);
                            break;
                        }
                    }
                    return true;
                }
            }
        } else if (option == 2) {
            for (SigilData data : SigilData.values()) {
                if (item.getId() == data.attuned) {
                    for (AbstractSigil listener : handler) {
                        if (listener == null) throw new RuntimeException("Exception in AbstractSigil");
                        if (data.handler.equals(listener.getClass()) && listener.attuned(player)) {
                            listener.onRemove(player);
                            break;
                        }
                    }
                    total -= 1;
                    player.putAttrib(AttributeKey.TOTAL_SIGILS_ACTIVATED, total);
                    player.clearAttrib(data.attributeKey);
                    player.getInventory().replace(data.attuned, data.unattuned, true);
                    return true;
                }
            }
        }
        return false;
    }
}
