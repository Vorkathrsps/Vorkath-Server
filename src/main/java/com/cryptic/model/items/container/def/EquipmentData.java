package com.cryptic.model.items.container.def;

import com.cryptic.model.entity.combat.weapon.WeaponType;
import lombok.Data;

@Data
public class EquipmentData {
    private int id;
    private String name;
    private double weight;
    private boolean equipable;
    private Equipment equipment;
    private WeaponType weaponType;

    @Override
    public String toString() {
        return "EInfo{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", weight=" + weight +
            ", equipable=" + equipable +
            ", equipment=" + equipment +
            ", weapontype=" + weaponType +
            '}';
    }
}
