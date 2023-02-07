package com.aelous.model.entity.combat.ranged.drawback;

public enum ThrownaxeDrawback {

    /**
     * Throwing axes
     */
    BRONZE_THROWING_AXE(800, 43, 36,41,38,36),
    IRON_THROWING_AXE(801, 42, 35,41,38,36),
    STEEL_THROWING_AXE(802, 44, 37,41,38,36),
    MITHRIL_THROWING_AXE(803, 45, 38,41,38,36),
    ADAMANT_THROWING_AXE(804, 46, 39,41,38,36),
    RUNE_THROWING_AXE(805, 48, 41,41,38,36),
    DRAGON_THROWING_AXE(20849, 1320, 1319,41,38,36),
    MORRIGANS_THROWING_AXE(22634, 1624, 1623,41,38,36),
    MORRIGANS_JAVELIN(22636, 1620, 11,41,38,36);

    public final int arrow, gfx, projectile, startSpeed, startHeight, endHeight;

    ThrownaxeDrawback(int arrow, int gfx, int projectile, int startSpeed, int startHeight, int endHeight) {
        this.arrow = arrow;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
    }

    public static ThrownaxeDrawback find(int ammo) {
        for (ThrownaxeDrawback knifeDrawback : ThrownaxeDrawback.values()) {
            if(knifeDrawback.arrow == ammo) {
                return knifeDrawback;
            }
        }
        return null;
    }
}
