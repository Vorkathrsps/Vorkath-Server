package com.aelous.model.entity.combat.weapon;

/**
 * All of the interfaces for weapons and the data needed to display these
 * interfaces properly.
 *
 * @author lare96
 */
public enum WeaponType {

    //Range
    BOW(1764,1767, new FightType[]{FightType.ARROW_ACCURATE, FightType.ARROW_RAPID, FightType.ARROW_LONGRANGE}, 7549, 7561),
    BOWFA(1764,1767, new FightType[]{FightType.ARROW_ACCURATE, FightType.ARROW_RAPID, FightType.ARROW_LONGRANGE}, 7549, 7561),
    CROSSBOW(1764,1767, new FightType[]{FightType.BOLT_ACCURATE, FightType.BOLT_RAPID, FightType.BOLT_LONGRANGE}, 7549, 7561),
    THROWN(4446,4449, new FightType[]{FightType.THROWING_ACCURATE, FightType.THROWING_RAPID, FightType.THROWING_LONGRANGE}, 7649, 7661),
    CHINCHOMPA(24899,428, new FightType[]{FightType.SHORT_FUSE, FightType.MEDIUM_FUSE, FightType.LONG_FUSE}),
    SALAMANDER(22899, 428, new FightType[]{FightType.SALAMANDER_SCORCH, FightType.SALAMANDER_FLARE, FightType.SALAMANDER_BLAZE}),


    //Magic
    MAGIC_STAFF(328, 355, new FightType[]{FightType.STAFF_BASH, FightType.STAFF_POUND, FightType.STAFF_FOCUS}, 7474, 7486),
    //Melee
    UNARMED(5855, 5857, new FightType[]{FightType.UNARMED_PUNCH, FightType.UNARMED_KICK, FightType.UNARMED_BLOCK}),
    FIXED_DEVICE(-1, -1, new FightType[]{}),//TODO
    HAMMER(425, 428, new FightType[]{FightType.HAMMER_POUND, FightType.HAMMER_PUMMEL, FightType.HAMMER_BLOCK}, 7474, 7486),
    SCYTHE(776, 779, new FightType[]{FightType.SCYTHE_REAP, FightType.SCYTHE_CHOP, FightType.SCYTHE_JAB, FightType.SCYTHE_BLOCK}),
    AXE(1698, 1701, new FightType[]{FightType.BATTLEAXE_CHOP, FightType.BATTLEAXE_HACK, FightType.BATTLEAXE_SMASH, FightType.BATTLEAXE_BLOCK}, 7499, 7511),
    DAGGER(2276, 2279, new FightType[]{FightType.DAGGER_STAB, FightType.DAGGER_LUNGE, FightType.DAGGER_SLASH, FightType.DAGGER_BLOCK}, 7574, 7586),
    SWORD(2276, 2279, new FightType[]{FightType.SWORD_STAB, FightType.SWORD_LUNGE, FightType.SWORD_SLASH, FightType.SWORD_BLOCK}, 7574, 7586),
    LONGSWORD(2423, 2426, new FightType[]{FightType.LONGSWORD_CHOP, FightType.LONGSWORD_SLASH, FightType.LONGSWORD_LUNGE, FightType.LONGSWORD_BLOCK}, 7599, 7611),
    MACE(3796, 3799, new FightType[]{FightType.MACE_POUND, FightType.MACE_PUMMEL, FightType.MACE_SPIKE, FightType.MACE_BLOCK}, 7624, 7636),
    SPEAR(4679, 4682, new FightType[]{FightType.SPEAR_LUNGE, FightType.SPEAR_SWIPE, FightType.SPEAR_POUND, FightType.SPEAR_BLOCK}, 7674, 7686),
    TWOHANDED(4705, 4708, new FightType[]{FightType.TWOHANDEDSWORD_CHOP, FightType.TWOHANDEDSWORD_SLASH, FightType.TWOHANDEDSWORD_SMASH, FightType.TWOHANDEDSWORD_BLOCK}, 7699, 7711),
    PICKAXE(5570, 5573, new FightType[]{FightType.PICKAXE_SPIKE, FightType.PICKAXE_IMPALE, FightType.PICKAXE_SMASH, FightType.PICKAXE_BLOCK}, 7724, 7736),
    CLAWS(7762, 7765, new FightType[]{FightType.CLAWS_CHOP, FightType.CLAWS_SLASH, FightType.CLAWS_LUNGE, FightType.CLAWS_BLOCK}, 7800, 7812),
    HALBERD(8460, 8463, new FightType[]{FightType.HALBERD_JAB, FightType.HALBERD_SWIPE, FightType.HALBERD_FEND}, 8493, 8505),
    WHIP(12290, 12293, new FightType[]{FightType.WHIP_FLICK, FightType.WHIP_LASH, FightType.WHIP_DEFLECT}, 12323, 12335),
    DINHS_BULWARK(11799, 428, new FightType[]{FightType.DINHS_PUMMEL, FightType.DINHS_BLOCK}, 7474, 7486),
    GHRAZI_RAPIER(2276, 2279, new FightType[]{FightType.RAPIER_STAB, FightType.RAPIER_LUNGE, FightType.RAPIER_SLASH, FightType.RAPIER_BLOCK}, 7574, 7586),
    POWERED_STAFF(24496, 24497, new FightType[]{FightType.POWERED_STAFF_ACCURATE_1, FightType.POWERED_STAFF_ACCURATE_2, FightType.POWERED_STAFF_LONGRANGE}),
    MJOLNIR(-1, -1, new FightType[]{});//TODO

    /**
     * The interface that will be displayed on the sidebar.
     */
    private final int interfaceId;

    /**
     * The line that the name of the item will be printed to.
     */
    private final int nameLineId;

    /**
     * The fight types that correspond with this interface.
     */
    private final FightType[] fightType;

    /**
     * The id of the special bar for this interface.
     */
    private final int specialBar;

    /**
     * The id of the special meter for this interface.
     */
    private final int specialMeter;

    /**
     * Creates a new weapon interface.
     *
     * @param interfaceId  the interface that will be displayed on the sidebar.
     * @param nameLineId   the line that the name of the item will be printed to.
     * @param fightType    the fight types that correspond with this interface.
     * @param specialBar   the id of the special bar for this interface.
     * @param specialMeter the id of the special meter for this interface.
     */
    WeaponType(int interfaceId, int nameLineId, FightType[] fightType, int specialBar, int specialMeter) {
        this.interfaceId = interfaceId;
        this.nameLineId = nameLineId;
        this.fightType = fightType;
        this.specialBar = specialBar;
        this.specialMeter = specialMeter;
    }

    /**
     * Creates a new weapon interface.
     *
     * @param interfaceId the interface that will be displayed on the sidebar.
     * @param nameLineId  the line that the name of the item will be printed to.
     * @param fightType   the fight types that correspond with this interface.
     */
    WeaponType(int interfaceId, int nameLineId, FightType[] fightType) {
        this(interfaceId, nameLineId, fightType, -1, -1);
    }

    /**
     * Gets the interface that will be displayed on the sidebar.
     *
     * @return the interface id.
     */
    public int getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the line that the name of the item will be printed to.
     *
     * @return the name line id.
     */
    public int getNameLineId() {
        return nameLineId;
    }

    /**
     * Gets the fight types that correspond with this interface.
     *
     * @return the fight types that correspond with this interface.
     */
    public FightType[] getFightType() {
        return fightType;
    }

    /**
     * Gets the id of the special bar for this interface.
     *
     * @return the id of the special bar for this interface.
     */
    public int getSpecialBar() {
        return specialBar;
    }

    /**
     * Gets the id of the special meter for this interface.
     *
     * @return the id of the special meter for this interface.
     */
    public int getSpecialMeter() {
        return specialMeter;
    }
}
