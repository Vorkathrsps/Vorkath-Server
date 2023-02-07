package com.aelous.model.entity.combat.ranged.drawback;

public enum DblArrowDrawBack {

    BRONZE_ARROW(882, 1104),
    IRON_ARROW(884, 1105),
    STEEL_ARROW(886, 1106),
    MITHRIL_ARROW(888, 1107),
    ADAMANT_ARROW(890, 1108),
    RUNITE_ARROW(892, 1109),
    AMETHYST_ARROW(21326, 1383),
    ICE_ARROW(78, 1110),
    DRAGON_ARROW(11212, 1111),
    DRAGON_ARROW_20389(20389, 1111),
    DRAGON_ARROW_P(11227, 1111),
    DRAGON_ARROW_P_PLUS(11228, 1111),
    DRAGON_ARROW_P_PLUS_PLUS(11229, 1111),
    OBBY_RING(442, 11),
    CRYSTAL_BOW(250, 249),
    CRAWS_BOW(1611, 1574);
    //ids continue.. cba getting atm. broad and fire arrows (regicide quest?)

    public int arrow, gfx;

    DblArrowDrawBack(int arrow, int gfx) {
        this.arrow = arrow;
        this.gfx = gfx;
    }

    public static DblArrowDrawBack find(int ammo) {
        for (DblArrowDrawBack dblArrowDrawBack : DblArrowDrawBack.values()) {
            if(dblArrowDrawBack.arrow == ammo) {
                return dblArrowDrawBack;
            }
        }
        return null;
    }
}
