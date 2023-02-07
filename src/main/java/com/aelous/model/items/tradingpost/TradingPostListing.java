package com.aelous.model.items.tradingpost;

import com.aelous.model.items.Item;
import com.aelous.utility.Utils;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Ynneh
 */
public class TradingPostListing {

    private final String sellerName;
    private String lastBuyerName;

    private Item saleItem;

    private long price;

    private final long timeListed;

    public int amountSold;

    /**
     * aka placeholder for funds received from sales, more like a coffer
     */
    public long profit;

    public long transactionTime;

    public TradingPostListing(String sellerName, Item saleItem, long price) {
        this.sellerName = sellerName;
        this.saleItem = saleItem;
        this.price = price;
        this.timeListed = System.currentTimeMillis();
    }

    public int getAmountSold() {
        return amountSold;
    }

    public int getTotalAmount() {
        return saleItem.getAmount();
    }

    public long getPrice() {
        return price;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Item getSaleItem() {
        return saleItem;
    }

    public long getTimeListed() {
        return timeListed;
    }

    public int getRemaining() {
        return getTotalAmount() - getAmountSold();
    }

    public String getLastBuyerName() {
        return lastBuyerName;
    }

    public void setLastBuyerName(String name) {
        this.lastBuyerName = name;
    }

    public void setLastTransactionTime(long transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTransactionTime() {
        return Utils.getCalanderDate(transactionTime);
    }

    public List<String> buyersInfo = Lists.newArrayList();

    public String getListedTime() {
        return Utils.getCalanderDate(timeListed);
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        Item currentItem = saleItem;
        this.saleItem = new Item(currentItem.getId(), quantity);
    }

    public void updateAmount(int sold) {
        this.amountSold += sold;
        this.profit += price * sold;
    }

    public void resetProfit() {
        this.profit = 0;
    }

    @Override
    public String toString() {
        return "TradingPostListing{" +
            "sellerName='" + sellerName + '\'' +
            ", lastBuyerName='" + lastBuyerName + '\'' +
            ", saleItem=" + saleItem +
            ", price=" + price +
            ", timeListed=" + timeListed +
            ", amountSold=" + amountSold +
            ", profit=" + profit +
            ", transactionTime=" + transactionTime +
            ", buyersInfo=" + buyersInfo +
            '}';
    }
}
