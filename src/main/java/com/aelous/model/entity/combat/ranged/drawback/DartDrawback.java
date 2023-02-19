package com.aelous.model.entity.combat.ranged.drawback;

public enum DartDrawback {

    BRONZE_DART(806, 219, 212, 41,38,36, 5),
    IRON_DART(807, 219, 212, 41,38,36, 5),
    STEEL_DART(808, 219, 212, 41,38,36, 5),
    BLACK_DART(3093, 219, 212, 41,38,36, 5),
    MITHRIL_DART(809, 219, 212, 41,38,36, 5),
    ADAMANT_DART(810, 219, 212, 41,38,36, 5),
    RUNITE_DART(811, 219, 212, 41,38,36, 5),
    //p
    BRONZE_DART_P1(812, 219, 212, 41,38,36, 5),
    IRON_DART_P1(813, 219, 212, 41,38,36, 5),
    STEEL_DART_P1(814, 219, 212, 41,38,36, 5),
    BLACK_DART_P1(3094, 219, 212, 41,38,36, 5),
    MITHRIL_DART_P1(815, 219, 212, 41,38,36, 5),
    ADAMANT_DART_P1(816, 219, 212, 41,38,36, 5),
    RUNITE_DART_P1(817, 219, 212, 41,38,36, 5),
    //p+
    BRONZE_DART_P2(5628, 219, 212, 41,38,36, 5),
    IRON_DART_P2(5629, 219, 212, 41,38,36, 5),
    STEEL_DART_P2(5630, 219, 212, 41,38,36, 5),
    BLACK_DART_P2(5631, 219, 212, 41,38,36, 5),
    MITHRIL_DART_P2(5632, 219, 212, 41,38,36, 5),
    ADAMANT_DART_P2(5633, 219, 212, 41,38,36, 5),
    RUNITE_DART_P2(5634, 219, 212, 41,38,36, 5),
    //p++
    BRONZE_DART_P3(5635, 219, 212, 41,38,36, 5),
    IRON_DART_P3(5636, 219, 212, 41,38,36, 5),
    STEEL_DART_P3(5637, 219, 212, 41,38,36, 5),
    BLACK_DART_P3(5638, 219, 212, 41,38,36, 5),
    MITHRIL_DART_P3(5639, 219, 212, 41,38,36, 5),
    ADAMANT_DART_P3(5640, 219, 212, 41,38,36, 5),
    RUNITE_DART_P3(5641, 219, 212, 41,38,36, 5),

    //dragon
    DRAGON_DART(11230, 1123, 1122, 41,38,36, 5),
    DRAGON_DART_P1(11231, 1123, 1122, 41,38,36, 5),
    DRAGON_DART_P2(11233, 1123, 1122, 41,38,36, 5),
    DRAGON_DART_P3(11234, 1123, 1122, 41,38,36, 5);

    public final int dart, gfx, projectile, startSpeed, startHeight, endHeight, stepMultiplier;

    DartDrawback(int dart, int gfx, int projectile, int startSpeed, int startHeight, int endHeight, int stepMultiplier) {
        this.dart = dart;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.stepMultiplier = stepMultiplier;
    }

    public static DartDrawback find(int ammo) {
        for (DartDrawback dartDrawback : DartDrawback.values()) {
            if(dartDrawback.dart == ammo) {
                return dartDrawback;
            }
        }
        return null;
    }
}
