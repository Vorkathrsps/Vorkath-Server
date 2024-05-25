package com.cryptic.model.content.packet_actions.interactions.container;

import com.cryptic.model.content.duel.Dueling;
import com.cryptic.model.entity.combat.magic.autocasting.Autocasting;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.inter.impl.BonusesInterface;
import com.cryptic.model.content.skill.impl.crafting.impl.Jewellery;
import com.cryptic.model.content.skill.impl.smithing.EquipmentMaking;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.model.items.trade.Trading;
import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerStatus;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.shop.Shop;
import com.cryptic.model.items.container.shop.ShopUtility;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

import static com.cryptic.model.content.skill.impl.smithing.EquipmentMaking.*;
import static com.cryptic.model.inter.InterfaceConstants.*;
import static com.cryptic.model.items.container.shop.ShopUtility.SPIRTE_SHOP_ITEM_CHILD_ID;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FirstContainerAction {

    public static void firstAction(Player player, int interfaceId, int slot, int id) {
        if (PacketInteractionManager.checkItemContainerActionInteraction(player, new Item(id), slot, interfaceId, 1)) {
            return;
        }

        if (TradingPost.handleSellingItem(player, interfaceId, id, 1))
            return;

        if (player.getRunePouch().removeFromPouch(interfaceId, id, slot, 1)) {
            return;
        }

        if (player.getRunePouch().moveToRunePouch(interfaceId, id, slot, 1)) {
            return;
        }

        if (interfaceId == EQUIPMENT_CREATION_COLUMN_1 || interfaceId == EQUIPMENT_CREATION_COLUMN_2 || interfaceId == EQUIPMENT_CREATION_COLUMN_3 || interfaceId == EQUIPMENT_CREATION_COLUMN_4 || interfaceId == EQUIPMENT_CREATION_COLUMN_5) {
            if (player.getInterfaceManager().isInterfaceOpen(EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID)) {
                EquipmentMaking.initialize(player, id, interfaceId, slot, 1);
                return;
            }
        }

        /* Jewellery */
        if (interfaceId == JEWELLERY_INTERFACE_CONTAINER_ONE || interfaceId == JEWELLERY_INTERFACE_CONTAINER_TWO || interfaceId == JEWELLERY_INTERFACE_CONTAINER_THREE) {
            Jewellery.click(player, id, 1);
            return;
        }

        /* Place holder */
        if (interfaceId == PLACEHOLDER) {
            player.getBank().placeHolder(id, slot);
            return;
        }

        if (interfaceId == InterfaceConstants.EQUIPMENT_DISPLAY_ID) { //do sounds here for equipping
            if (slot == 0) {
                player.getEquipment().unequip(EquipSlot.HEAD);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 1) {
                player.getEquipment().unequip(EquipSlot.CAPE);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 2) {
                player.getEquipment().unequip(EquipSlot.AMULET);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 3) {
                if (player.getCombat().getAutoCastSpell() != null) {
                    Autocasting.setAutocast(player, null);
                }
                player.getEquipment().unequip(EquipSlot.WEAPON);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 4) {
                player.getEquipment().unequip(EquipSlot.BODY);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 5) {
                player.getEquipment().unequip(EquipSlot.SHIELD);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 7) {
                player.getEquipment().unequip(EquipSlot.LEGS);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 9) {
                player.getEquipment().unequip(EquipSlot.HANDS);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 10) {
                player.getEquipment().unequip(EquipSlot.FEET);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 12) {
                player.getEquipment().unequip(EquipSlot.RING);
                player.getBonusInterface().sendBonuses();
                return;
            } else if (slot == 13) {
                player.getEquipment().unequip(EquipSlot.AMMO);
                player.getBonusInterface().sendBonuses();
                return;
            }
            return;
        }

        if (interfaceId == LOOTING_BAG_BANK_CONTAINER_ID) {
            Item item = player.getLootingBag().get(slot);
            if (item == null) {
                return;
            }

            boolean banking = player.getAttribOr(AttributeKey.BANKING, false);

            if (banking) {
                player.getLootingBag().withdrawBank(item.createWithAmount(1), slot);
                return;
            }
        }

        if (interfaceId == LOOTING_BAG_DEPOSIT_CONTAINER_ID) {
            Item item = player.inventory().get(slot);
            if (item == null) {
                return;
            }

            player.getLootingBag().deposit(item, 1, null);
            return;
        }

        if (interfaceId == WITHDRAW_BANK) {
            if (player.getBank().quantityFive) {
                player.getBank().withdraw(id, slot, 5);
                return;
            } else if (player.getBank().quantityTen) {
                player.getBank().withdraw(id, slot, 10);
                return;
            } else if (player.getBank().quantityAll) {
                player.getBank().withdraw(id, slot, Integer.MAX_VALUE);
                return;
            } else if (player.getBank().quantityX) {
                player.getBank().withdraw(id, slot, player.getBank().currentQuantityX);
                return;
            } else {
                player.getBank().withdraw(id, slot, 1);
                return;
            }
        }

        if (interfaceId == INVENTORY_STORE) {
            final Item item = player.inventory().get(slot);

            if (item == null || item.getId() != id) {
                return;
            }

            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);

            if (priceChecking) {
                player.getPriceChecker().deposit(slot, 1);
                return;
            }

            if (player.getBank().quantityFive) {
                player.getBank().deposit(slot, 5);
                return;
            } else if (player.getBank().quantityTen) {
                player.getBank().deposit(slot, 10);
                return;
            } else if (player.getBank().quantityAll) {
                player.getBank().deposit(slot, Integer.MAX_VALUE);
                return;
            } else if (player.getBank().quantityX) {
                player.getBank().deposit(slot, player.getBank().currentQuantityX);
                return;
            } else {
                player.getBank().deposit(slot, 1);
                return;
            }
        }

        if (interfaceId == PRICE_CHECKER_DISPLAY_ID) {
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().withdraw(id, 1);
                return;
            }
        }

        if (interfaceId == ShopUtility.ITEM_CHILD_ID || interfaceId == ShopUtility.SLAYER_BUY_ITEM_CHILD_ID || interfaceId == SPIRTE_SHOP_ITEM_CHILD_ID) {
            Shop.exchange(player, id, slot, 1, true);
            return;
        }

        if (interfaceId == SHOP_INVENTORY) {
            int shop = player.getAttribOr(AttributeKey.SHOP, -1);
            Shop store = World.getWorld().shops.get(shop);
            if (store != null) {
                Shop.exchange(player, id, slot, 1, false);
                return;
            }
        }

        if (interfaceId == Dueling.MAIN_INTERFACE_CONTAINER) {
            if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 1, slot, player.getDueling().getContainer(), player.inventory());
                return;
            }
        }

        if (interfaceId == REMOVE_INVENTORY_ITEM) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 1, slot, player.inventory(), player.getTrading().getContainer());
                return;
            } else if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 1, slot, player.inventory(), player.getDueling().getContainer());
                return;
            }
        }

        if (interfaceId == Trading.CONTAINER_INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 1, slot, player.getTrading().getContainer(), player.inventory());
                return;
            }
        }

        if (interfaceId == PRICE_CHECKER_CONTAINER) {
            player.getPriceChecker().withdraw(id, 1);
            return;
        }

        if (interfaceId == INVENTORY_INTERFACE) {
            player.getInventory().refresh();
        }
    }
}
