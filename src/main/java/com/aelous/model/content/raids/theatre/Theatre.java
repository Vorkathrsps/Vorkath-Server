package com.aelous.model.content.raids.theatre;

import com.aelous.model.content.raids.theatre.nylocas.VasiliasListener;
import com.aelous.model.entity.player.Player;

/**
 * @Author: Origin
 * @Date: 7/16/2023
 */
public class Theatre extends VasiliasListener {
    public Theatre(Player player) {
        super(player);
    }

    public void constructRoom() {
        super.buildRoom();
    }

    public void start() {
        super.initiate();
    }

    public void clearRoom() {
        super.clearRoom();
    }

}
