package com.cryptic.model.content.items.boxes;

import com.cryptic.model.content.items.boxes.impl.LarransKey;
import com.cryptic.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class CollectionItemHandler {
    private static final List<CollectionItemListener> boxListeners;
    private static final List<CollectionItemListener> keyListeners;

    static {
        boxListeners = initBoxes();
        keyListeners = initKeys();
    }

    private static List<CollectionItemListener> initBoxes() {
        List<CollectionItemListener> listeners = new ArrayList<>();
        return listeners;
    }

    private static List<CollectionItemListener> initKeys() {
        List<CollectionItemListener> listeners = new ArrayList<>();
        listeners.add(new LarransKey());
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

    public static boolean rollBoxReward(Player player, int id) {
        for (CollectionItemListener listener : boxListeners) {
            if (listener.isItem(id)) {
                listener.openBox(player);
                return true;
            }
        }
        return false;
    }
}
