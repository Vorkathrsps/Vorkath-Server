package com.cryptic.model.content.packet_actions.interactions.container;

import com.cryptic.model.content.duel.Dueling;
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

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class FirstContainerAction {

    public static void firstAction(Player player, int interfaceId, int slot, int id) {
        if(PacketInteractionManager.checkItemContainerActionInteraction(player, new Item(id), slot, interfaceId, 1)) {
            return;
        }

        if (TradingPost.handleSellingItem(player, interfaceId, id, 1))
            return;
        
        if(player.getRunePouch().removeFromPouch(interfaceId, id, slot,1)) {
            return;
        }

        if(player.getRunePouch().moveToRunePouch(interfaceId, id, slot,1)) {
            return;
        }

        if (interfaceId == EQUIPMENT_CREATION_COLUMN_1 || interfaceId == EQUIPMENT_CREATION_COLUMN_2 || interfaceId == EQUIPMENT_CREATION_COLUMN_3 || interfaceId == EQUIPMENT_CREATION_COLUMN_4 || interfaceId == EQUIPMENT_CREATION_COLUMN_5) {
            if (player.getInterfaceManager().isInterfaceOpen(EquipmentMaking.EQUIPMENT_CREATION_INTERFACE_ID)) {
                EquipmentMaking.initialize(player, id, interfaceId, slot, 1);
            }
        }

        /* Jewellery */
        if (interfaceId == JEWELLERY_INTERFACE_CONTAINER_ONE || interfaceId == JEWELLERY_INTERFACE_CONTAINER_TWO || interfaceId == JEWELLERY_INTERFACE_CONTAINER_THREE) {
            Jewellery.click(player, id, 1);
        }

        /* Place holder */
        if (interfaceId == PLACEHOLDER) {
            player.getBank().placeHolder(id, slot);
        }

        if (interfaceId == InterfaceConstants.EQUIPMENT_DISPLAY_ID) { //do sounds here for equipping
            if (slot == 0) {
                player.getEquipment().unequip(EquipSlot.HEAD);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 1) {
                player.getEquipment().unequip(EquipSlot.CAPE);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 2) {
                player.getEquipment().unequip(EquipSlot.AMULET);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 3) {
                player.getEquipment().unequip(EquipSlot.WEAPON);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 4) {
                player.getEquipment().unequip(EquipSlot.BODY);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 5) {
                player.getEquipment().unequip(EquipSlot.SHIELD);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 7) {
                player.getEquipment().unequip(EquipSlot.LEGS);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 9) {
                player.getEquipment().unequip(EquipSlot.HANDS);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 10) {
                player.getEquipment().unequip(EquipSlot.FEET);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 12) {
                player.getEquipment().unequip(EquipSlot.RING);
                player.getBonusInterface().sendBonuses();
            } else if (slot == 13) {
                player.getEquipment().unequip(EquipSlot.AMMO);
                player.getBonusInterface().sendBonuses();
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
            }
        }

        if (interfaceId == LOOTING_BAG_DEPOSIT_CONTAINER_ID) {
            Item item = player.inventory().get(slot);
            if (item == null) {
                return;
            }

            player.getLootingBag().deposit(item, 1, null);
        }

        if (interfaceId == WITHDRAW_BANK) {
            if (player.getBank().quantityFive) {
                // System.out.println("withdraw 5");
                player.getBank().withdraw(id, slot, 5);
            } else if (player.getBank().quantityTen) {
                // System.out.println("withdraw 10");
                player.getBank().withdraw(id, slot, 10);
            } else if (player.getBank().quantityAll) {
                // System.out.println("withdraw all");
                player.getBank().withdraw(id, slot, Integer.MAX_VALUE);
            } else if (player.getBank().quantityX) {
                // System.out.println("withdraw x: "+player.getBank().currentQuantityX);
                player.getBank().withdraw(id, slot, player.getBank().currentQuantityX);
            } else {
                // System.out.println("withdraw 1");
                player.getBank().withdraw(id, slot, 1);
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
                // System.out.println("deposit 5");
                player.getBank().deposit(slot, 5);
            } else if (player.getBank().quantityTen) {
                // System.out.println("deposit 10");
                player.getBank().deposit(slot, 10);
            } else if (player.getBank().quantityAll) {
                // System.out.println("deposit all");
                player.getBank().deposit(slot, Integer.MAX_VALUE);
            } else if (player.getBank().quantityX) {
                // System.out.println("deposit x: "+player.getBank().currentQuantityX);
                player.getBank().deposit(slot, player.getBank().currentQuantityX);
            } else {
                // System.out.println("deposit 1");
                player.getBank().deposit(slot, 1);
            }
        }

        if (interfaceId == PRICE_CHECKER_DISPLAY_ID) {
            boolean priceChecking = player.getAttribOr(AttributeKey.PRICE_CHECKING, false);
            if (priceChecking) {
                player.getPriceChecker().withdraw(id, 1);
                return;
            }
        }

        if (interfaceId == ShopUtility.ITEM_CHILD_ID || interfaceId == ShopUtility.SLAYER_BUY_ITEM_CHILD_ID) {
            Shop.exchange(player, id, slot, 1, true);
        }

        if (interfaceId == SHOP_INVENTORY) {
            int shop = player.getAttribOr(AttributeKey.SHOP,-1);
            Shop store = World.getWorld().shops.get(shop);
            if (store != null) {
                Shop.exchange(player, id, slot, 1, false);
            }
        }

        if (interfaceId == Dueling.MAIN_INTERFACE_CONTAINER) {
            if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 1, slot, player.getDueling().getContainer(), player.inventory());
            }
        }

        if (interfaceId == REMOVE_INVENTORY_ITEM) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 1, slot, player.inventory(), player.getTrading().getContainer());
            } else if (player.getStatus() == PlayerStatus.DUELING) {
                player.getDueling().handleItem(id, 1, slot, player.inventory(), player.getDueling().getContainer());
            }
        }

        if (interfaceId == Trading.CONTAINER_INTERFACE_ID) {
            if (player.getStatus() == PlayerStatus.TRADING) {
                player.getTrading().handleItem(id, 1, slot, player.getTrading().getContainer(), player.inventory());
            }
        }

        if (interfaceId == PRICE_CHECKER_CONTAINER) {
            player.getPriceChecker().withdraw(id, 1);
        }

        if (interfaceId == INVENTORY_INTERFACE) {
            player.getInventory().refresh();
        }
    }
}
