package com.cryptic.model.entity.combat.upgrades;

import com.cryptic.model.World;
import com.cryptic.model.items.container.def.EquipmentData;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ItemUpgrade {
    final int id;
    EquipmentData data;

    public ItemUpgrade(int id) {
        this.id = id;
        this.data = World.getWorld().getEquipmentLoader().getInfo(id);
    }

    public UpgradeData create() {
        return new UpgradeData(
            this.data.getEquipment().getAstab(),
            this.data.getEquipment().getAslash(),
            this.data.getEquipment().getAcrush(),
            this.data.getEquipment().getAmagic(),
            this.data.getEquipment().getArange(),
            this.data.getEquipment().getDstab(),
            this.data.getEquipment().getDslash(),
            this.data.getEquipment().getDcrush(),
            this.data.getEquipment().getDmagic(),
            this.data.getEquipment().getDrange(),
            this.data.getEquipment().getStr(),
            this.data.getEquipment().getRstr(),
            this.data.getEquipment().getMdmg(),
            this.data.getEquipment().getPrayer(),
            this.data.getEquipment().getAspeed()
        );
    }

}
