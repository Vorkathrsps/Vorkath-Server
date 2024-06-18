package com.cryptic.model.content.items.loot;

import com.cryptic.model.content.items.loot.impl.bountycrates.*;
import com.cryptic.model.content.items.loot.impl.caskets.*;
import com.cryptic.model.content.items.loot.impl.keys.CrystalKey;
import com.cryptic.model.content.items.loot.impl.keys.EnhancedCrystalKey;
import com.cryptic.model.content.items.loot.impl.keys.LarransKey;
import com.cryptic.model.content.items.loot.impl.MysteryBox;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import java.util.ArrayList;
import java.util.List;

public class CollectionItemHandler {
    private static final List<CollectionItemListener> clueListeners;
    private static final List<CollectionItemListener> boxListeners;
    private static final List<CollectionItemListener> keyListeners;

    static {
        boxListeners = initBoxes();
        keyListeners = initKeys();
        clueListeners = initClues();
    }

    private static List<CollectionItemListener> initClues() {
        List<CollectionItemListener> listeners = new ArrayList<>();
        listeners.add(new BeginnerCasket());
        listeners.add(new EasyCasket());
        listeners.add(new MediumCasket());
        listeners.add(new HardCasket());
        listeners.add(new EliteCasket());
        listeners.add(new MasterCasket());
        return listeners;
    }

    private static List<CollectionItemListener> initBoxes() {
        List<CollectionItemListener> listeners = new ArrayList<>();
        listeners.add(new MysteryBox());
        listeners.add(new BountyCrateTierOne());
        listeners.add(new BountyCrateTierTwo());
        listeners.add(new BountyCrateTierThree());
        listeners.add(new BountyCrateTierFour());
        listeners.add(new BountyCrateTierFive());
        listeners.add(new BountyCrateTierSix());
        listeners.add(new BountyCrateTierSeven());
        listeners.add(new BountyCrateTierEight());
        listeners.add(new BountyCrateTierNine());
        return listeners;
    }

    private static List<CollectionItemListener> initKeys() {
        List<CollectionItemListener> listeners = new ArrayList<>();
        listeners.add(new LarransKey());
        listeners.add(new CrystalKey());
        listeners.add(new EnhancedCrystalKey());
        return listeners;
    }

    public static boolean rollKeyReward(Player player, int id) {
        for (CollectionItemListener listener : keyListeners) {
            if (listener.isItem(id)) {
                listener.openKey(player);
                return true;
            }
        }
        return false;
    }

    public static void rollClueScrollReward(Player player, int id) {
        for (CollectionItemListener listener : clueListeners) {
            if (listener.isItem(id)) {
                listener.openClue(player);
                return;
            }
        }
    }

    public static boolean rollBoxReward(Player player, int id) {
        for (CollectionItemListener listener : boxListeners) {
            if (listener.isItem(id)) {
                listener.openBox(player);
                return true;
            }
        }
        return false;
    }

    public static List<Item> getBoxRewards(int id) {
        List<Item> temp = new ArrayList<>();
        for (CollectionItemListener listener : boxListeners) {
            if (listener.isItem(id)) {
                for (var reward : listener.rewards()) {
                    int identification = reward.id;
                    int amount = reward.amount;
                    Item item;
                    if (amount == -1) item = new Item(identification);
                    else item = new Item(identification, amount);
                    if (item.noted()) item = item.unnote();
                    temp.add(item);
                }
            }
        }
        return temp;
    }
}
