package com.cryptic.model.content.sigils.data.tier1;

import com.cryptic.model.content.sigils.SigilHandler;
import com.cryptic.model.content.sigils.SigilType;
import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.EquipmentInfo;

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
