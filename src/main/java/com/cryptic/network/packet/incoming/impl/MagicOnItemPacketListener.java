package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.GameConstants;
import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.content.skill.impl.magic.JewelleryEnchantment;
import com.cryptic.model.content.skill.impl.smithing.Bar;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.magic.spells.MagicClickSpells;
import com.cryptic.model.entity.combat.magic.spells.Spell;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.RequiredItem;
import com.cryptic.model.items.ground.GroundItem;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.network.packet.incoming.IncomingHandler;

import java.util.Arrays;
import java.util.Optional;

import static com.cryptic.utility.ItemIdentifiers.*;

/**
 * Handles the packet for using magic spells on items ingame.
 *
 * @author Professor Oak
 */
public class MagicOnItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (packet.getOpcode() == IncomingHandler.MAGIC_ON_ITEM_OPCODE) {
            final int slot = packet.readShort();
            final int itemId = packet.readShortA();
            final int childId = packet.readShort();
            final int spellId = packet.readShortA();

            if (player.locked()) {
                return;
            }

            // Some checks
            if (slot < 0 || slot > 27) {
                return;
            }
            Item item = player.inventory().get(slot);
            if (item == null || item.getId() != itemId) {
                return;
            }

            final Optional<MagicClickSpells.MagicSpells> magicSpell = MagicClickSpells.MagicSpells.find(spellId);

            if (magicSpell.isEmpty()) {
                return;
            }

            player.debugMessage("[MagicOnItemPacket] spell=" + magicSpell + " itemId=" + itemId + " slot=" + slot + " childId=" + childId);

            if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.getBankPin().openIfNot();
                return;
            }

            if (player.askForAccountPin()) {
                player.sendAccountPinMessage();
                return;
            }

            if (!player.dead()) {
                player.stopActions(false); // Seems like this does not cancel moving on rs? I can alch while I run.

                //Do actions...
                final MagicClickSpells.MagicSpells magicSpell2 = magicSpell.get();
                final Spell spell = magicSpell2.getSpell();
                final int value = ItemDefinition.cached.get(item.getId()).highAlchValue();

                switch (magicSpell2) {
                    case SUPERHEAT_ITEM:
                        if (player.getSpellbook() != MagicSpellbook.NORMAL)
                            return;
                        if (!player.getClickDelay().elapsed(500)) {
                            return;
                        }

                        Optional<Bar> data = Bar.getDefinitionByItem(item.getId());

                        if (data.isEmpty()) {
                            player.message("You can not super heat this item!");
                            return;
                        }

                        for (RequiredItem requiredItem : data.get().getOres()) {
                            if (!player.inventory().containsAll(requiredItem.getItem())) {
                                player.message("You do not contain the required items to super heat!");
                                return;
                            }
                        }

                        if (player.getSkills().xpLevel(Skills.SMITHING) < data.get().getLevelReq()) {
                            player.message("You need a smithing level of " + data.get().getLevelReq() + " to do super heat this item!");
                            return;
                        }

                        player.animate(722);
                        player.graphic(148);
                        player.getPacketSender().sendTab(6);
                        for (RequiredItem requiredItem : data.get().getOres()) {
                            player.inventory().removeAll(requiredItem.getItem());
                        }
                        player.inventory().addAll(new Item(data.get().getBar()));
                        player.getSkills().addXp(Skills.MAGIC, spell.baseExperience());
                        player.getSkills().addXp(Skills.SMITHING, data.get().getXpReward());
                        player.getClickDelay().reset();
                        return;
                    case ENCHANT_SAPPHIRE:
                    case ENCHANT_DIAMOND:
                    case ENCHANT_EMERALD:
                    case ENCHANT_ONYX:
                    case ENCHANT_ZENYTE:
                    case ENCHANT_DRAGONSTONE:
                    case ENCHANT_RUBY_TOPAZ:
                        if (JewelleryEnchantment.check(player, itemId, spellId)) {
                            if (!spell.canCast(player, null, spell.deleteRunes())) {
                                return;
                            }
                            JewelleryEnchantment.enchantItem(player, itemId);
                        }
                        return;
                    case LOW_ALCHEMY:
                        if (!item.rawtradable() || item.getId() == BLOOD_MONEY || item.getId() == COINS_995) {
                            player.message("You can't alch that item.");
                            return;
                        }

                        if (item.getValue() == 0) {
                            player.message("You cannot alch that item.");
                            return;
                        }

                        if (!spell.canCast(player, null, spell.deleteRunes())) {
                            return;
                        }

                        double a = value * 0.60;
                        int coinAmountToGive = (int) (value - a);

                        player.sendPrivateSound(98, 0);
                        spell.cast(player, null);

                        item = new Item(item.getId(), 1);

                        player.inventory().remove(item, slot);
                        player.inventory().add(COINS_995, coinAmountToGive);
                        return;
                    case HIGH_ALCHEMY:
                        if (!item.rawtradable() || item.getId() == BLOOD_MONEY || item.getId() == COINS_995 || item.getId() == PLATINUM_TOKEN) {
                            player.message("You can't alch that item.");
                            return;
                        }

                        if (!spell.canCast(player, null, spell.deleteRunes())) {
                            return;
                        }

                        if (item.getValue() == 0) {
                            player.sendInformationMessage("You cannot alch this item.");
                            return;
                        }

                        coinAmountToGive = value;

                        if (player.hasAttrib(AttributeKey.ALCHEMANIAC_BOOST)) {
                            coinAmountToGive *= 1.30D;
                        }

                        player.sendPrivateSound(97, 0);
                        spell.cast(player, null);

                        item = new Item(item.getId(), 1);

                        player.inventory().remove(item, slot);

                        player.inventory().add(COINS_995, coinAmountToGive);
                        return;
                }
            }
        }

        if (packet.getOpcode() == IncomingHandler.MAGIC_ON_GROUND_ITEM_OPCODE) {
            final int groundItemX = packet.readLEShort();
            final int groundItemId = packet.readUnsignedShort();
            final int groundItemY = packet.readLEShort();
            final int spellId = packet.readShortA();

            if (!player.locked() && !player.dead()) {
                Tile tile = new Tile(groundItemX, groundItemY, player.tile().level);
                Optional<MagicClickSpells.MagicSpells> spell = MagicClickSpells.MagicSpells.find(spellId);
                if (spell.isEmpty()) {
                    return;
                }

                Optional<GroundItem> groundItem = Optional.of(new GroundItem(new Item(groundItemId), tile, player));
                player.putAttrib(AttributeKey.INTERACTED_GROUNDITEM, groundItem.get());
                player.putAttrib(AttributeKey.INTERACTION_OPTION, 4);

                switch (spell.get()) {
                    case TELEKINETIC_GRAB:
                        player.sendPrivateSound(3006, 0);
                        break;
                }
            }
        }
    }
}
