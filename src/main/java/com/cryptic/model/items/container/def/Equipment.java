package com.cryptic.model.items.container.def;

import com.cryptic.model.entity.combat.weapon.WeaponType;
import lombok.Data;

@Data
public class Equipment {
    private int slot;
    private int astab;
    private int aslash;
    private int acrush;
    private int amagic;
    private int arange;
    private int dstab;
    private int dslash;
    private int dcrush;
    private int dmagic;
    private int drange;
    private int str;
    private int rstr;
    private int mdmg;
    private int prayer;
    private int aspeed;
    private boolean is2h;
    private WeaponType weaponType;
    @Override
    public String toString() {
        return "Equipment{" +
            "slot=" + slot +
            "is2h=" + is2h +
            ", astab=" + astab +
            ", aslash=" + aslash +
            ", acrush=" + acrush +
            ", amagic=" + amagic +
            ", arange=" + arange +
            ", dstab=" + dstab +
            ", dslash=" + dslash +
            ", dcrush=" + dcrush +
            ", dmagic=" + dmagic +
            ", drange=" + drange +
            ", str=" + str +
            ", rstr=" + rstr +
            ", mdmg=" + mdmg +
            ", prayer=" + prayer +
            ", aspeed=" + aspeed +
            '}';
    }
}
