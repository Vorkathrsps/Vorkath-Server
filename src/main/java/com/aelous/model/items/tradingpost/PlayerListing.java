package com.aelous.model.items.tradingpost;

import com.aelous.model.entity.player.Player;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerListing {

    /**
     * items listed by a single player
     */
    private final List<TradingPostListing> listedItems = Lists.newLinkedList();
    /**
     * items listed by a single player
     */
    public List<TradingPostListing> getListedItems() {
        return listedItems;
    }

    public boolean submit(TradingPostListing item) {
        if (listedItems.contains(item)){
            //System.out.println("submit return: "+item);
            return false;
        }
        //System.out.println("submit add: "+item);
        listedItems.add(item);
        return true;
    }

    public List<TradingPostListing> getSalesMatchingByString(Player player, String itemName) {
        //System.out.println("getSalesMatchingByString: "+itemName);
        return listedItems.stream().filter(listed -> listed.getRemaining() > 0 && listed.getSaleItem().unnote().name().toLowerCase().contains(itemName.toLowerCase()) && !listed.getSellerName().toLowerCase().equalsIgnoreCase(player.getUsername().toLowerCase())).collect(Collectors.toList());
    }

    public List<TradingPostListing> getSalesMatchingByItemId(int itemId) {
        //System.out.println("getSalesMatchingByItemId: "+itemId);
        return listedItems.stream().filter(listed -> listed.getSaleItem().unnote().getId() == itemId).collect(Collectors.toList());
    }

    public TradingPostListing getSaleBySlot(int slot) {
        //System.out.println("getSaleBySlot: "+slot);
        return slot >= listedItems.size() ? null : listedItems.get(slot);
    }

    public void removeListedItem(TradingPostListing sale) {
        //System.out.println("removeListedItem: "+sale);
        this.listedItems.remove(sale);
    } // just theorizing a fix before removing incase i dont need to

    public int getListingBySlot(TradingPostListing saleItem) {
        int index = -1;
        for (int i = 0; i < listedItems.size(); i++) {
            TradingPostListing listed = listedItems.get(i);
            // this is sketchy as fuck like matching price +id instead of exact instance, it could return a sale exactly the same whos not the seller
             if (listed != null && listed.getPrice() == saleItem.getPrice() && listed.getSaleItem().unnote().getId() == saleItem.getSaleItem().unnote().getId()) {
                 //System.out.printf("%s %s matched %s%n", saleItem.getSaleItem().unnote().name(), saleItem.hashCode(),  listed.hashCode());
                 index = i;
                 break;
             }
        }
        return index;
    }

    public void saveListing(TradingPostListing item) {
        int indexId = getListingBySlot(item);
        TradingPostListing storedList = listedItems.get(indexId);
        if (storedList == null)
            return;
        listedItems.set(indexId, item);
    }

    public void updateListing(TradingPostListing item, int bought) {
        int indexId = getListingBySlot(item);
        TradingPostListing storedList = listedItems.get(indexId);
        if (storedList == null) {
            System.err.println("error... loading stored item..");
            return;
        }
        storedList.updateAmount(bought);

        //System.out.println(" update listing: " + storedList.getSaleItem().unnote().name());
    }

    public void updateListing(TradingPostListing item, int amount, boolean price) {
        int indexId = getListingBySlot(item);
        TradingPostListing storedList = listedItems.get(indexId);
        if (storedList == null) {
            System.err.println("error... loading stored item..");
            return;
        }
        if (price) {
            storedList.setPrice(amount);
        } else
            storedList.setQuantity(amount);
        listedItems.set(indexId, storedList);
    }

    @Override
    public String toString() {
        return "PlayerListing{" +
            "listedItems=" + listedItems.toString() +
            '}';
    }
}
