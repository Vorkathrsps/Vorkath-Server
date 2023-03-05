package com.aelous.model.content.sigils.data.tier1;

import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.sigils.SigilType;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.EquipmentInfo;

/**
 * @Author Origin
 * 3/7/2022
 */

public class Fortifcation extends SigilHandler {

    public static final Fortifcation INSTANCE = new Fortifcation();

    @Override
    public void handleActvation(Player player, Entity target, Hit hit, int itemID) {
        int boost = 12;
        player.putAttrib(AttributeKey.SIGIL_OF_FORTIFICATION, true);
        player.message("<col=804080>You feel a surge of power draining from your sigil...");
        EquipmentInfo.Bonuses playerBonuses = EquipmentInfo.totalBonuses(player, World.getWorld().equipmentInfo());
        playerBonuses.stab =  playerBonuses.stab + boost;
        playerBonuses.slash = boost;
        playerBonuses.crush = boost;
        playerBonuses.mage = boost;
        playerBonuses.range = boost;
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
    public boolean effectHandlerCheck(Player player) {
        return false;
    }

    @Override
    public SigilType getSigilType() {
        return SigilType.COMBAT;
    }

    @Override
    public void defaultAttribute(Player player) {
        player.clearAttrib(AttributeKey.SIGIL_OF_FORTIFICATION);
    }
}
