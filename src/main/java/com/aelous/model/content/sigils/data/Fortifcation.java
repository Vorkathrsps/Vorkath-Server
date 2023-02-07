package com.aelous.model.content.sigils.data;

import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.sigils.SigilType;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.utility.chainedwork.Chain;

/**
 *  @Author Origin
 *  3/7/2022
 */

public class Fortifcation extends SigilHandler {

    public static final Fortifcation INSTANCE = new Fortifcation();

    @Override
    public void handleActvation(Player player) {
        int boost = 12;
            Chain.bound(null).runFn(1, () -> {
                player.putAttrib(AttributeKey.SIGIL_OF_FORTIFICATION, true);
                player.message("<col=804080>You feel a surge of power draining from your sigil...");
            }).then(2, player::unlock).then(3, () -> {
                EquipmentInfo.Bonuses playerBonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
                playerBonuses.stab += boost;
                playerBonuses.slash += boost;
                playerBonuses.crush += boost;
                playerBonuses.mage += boost;
                playerBonuses.range += boost;
                System.out.println(playerBonuses.range);
            }).then(40, () -> {
                player.message("<col=804080>Your sigil has depleted...");
                player.clearAttrib(AttributeKey.SIGIL_OF_FORTIFICATION);
            });
        }

    @Override
    public int getUntunedId() {
        return 26006;
    }

    @Override
    public int getTunedId() {
        return 26005;
    }

    @Override
    public SigilType getSigilType() {
        return SigilType.COMBAT;
    }
}
