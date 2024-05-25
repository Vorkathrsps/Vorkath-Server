package com.cryptic.model.items.container.def;

import com.cryptic.model.entity.combat.weapon.WeaponType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Equipment {
    @JsonProperty("slot")
    private int slot;
    @JsonProperty("astab")
    private int astab;
    @JsonProperty("aslash")
    private int aslash;
    @JsonProperty("acrush")
    private int acrush;
    @JsonProperty("amagic")
    private int amagic;
    @JsonProperty("arange")
    private int arange;
    @JsonProperty("dstab")
    private int dstab;
    @JsonProperty("dslash")
    private int dslash;
    @JsonProperty("dcrush")
    private int dcrush;
    @JsonProperty("dmagic")
    private int dmagic;
    @JsonProperty("drange")
    private int drange;
    @JsonProperty("str")
    private int str;
    @JsonProperty("rstr")
    private int rstr;
    @JsonProperty("mdmg")
    private int mdmg;
    @JsonProperty("prayer")
    private int prayer;
    @JsonProperty("aspeed")
    private int aspeed;
    @JsonProperty("is2h")
    private boolean is2h;
    @JsonProperty("weaponType")
    private WeaponType weaponType;
}
