package com.aelous.model.content.sigils.data;

import com.aelous.model.World;
import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.sigils.SigilType;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.concurrent.atomic.AtomicReference;

public class FeralFighter extends SigilHandler {
    @Override
    public void handleActvation(Player player, int itemID) {
        Item weapon = player.getEquipment().get(EquipSlot.WEAPON);
        double speed = player.getBaseAttackSpeed();
        player.putAttrib(AttributeKey.SIGIL_OF_THE_FERAL_FIGHTER, true);
        if (Utils.securedRandomChance(0.10D)) {
            Chain.bound(null).runFn(0, () -> {

            });
        }
    }

    @Override
    public int getUntunedId() {
        return 26075;
    }

    @Override
    public int getTunedId() {
        return 26074;
    }

    @Override
    public SigilType getSigilType() {
        return SigilType.COMBAT;
    }
}
