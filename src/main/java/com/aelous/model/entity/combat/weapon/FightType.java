package com.aelous.model.entity.combat.weapon;

/**
 * A collection of constants that each represent a different fighting type.
 *
 * @author lare96
 */
public enum FightType {

    SALAMANDER_SCORCH(43, 1, FightStyle.AGGRESSIVE, AttackType.NONE),
    SALAMANDER_FLARE(43, 1, FightStyle.AGGRESSIVE, AttackType.NONE),
    SALAMANDER_BLAZE(43, 1, FightStyle.AGGRESSIVE, AttackType.NONE),

    DINHS_PUMMEL(43, 1, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    DINHS_BLOCK(43, 2, FightStyle.DEFENSIVE, AttackType.CRUSH),

    STAFF_BASH(43, 0, FightStyle.ACCURATE, AttackType.CRUSH),
    STAFF_POUND(43, 1, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    STAFF_FOCUS(43, 2, FightStyle.DEFENSIVE, AttackType.CRUSH),

    HAMMER_POUND(43, 0, FightStyle.ACCURATE, AttackType.CRUSH),
    HAMMER_PUMMEL(43, 1, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    HAMMER_BLOCK(43, 2, FightStyle.DEFENSIVE, AttackType.CRUSH),

    SCYTHE_REAP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    SCYTHE_CHOP(43, 1, FightStyle.AGGRESSIVE, AttackType.STAB),
    SCYTHE_JAB(43, 2, FightStyle.CONTROLLED, AttackType.CRUSH),
    SCYTHE_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    BATTLEAXE_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    BATTLEAXE_HACK(43, 1, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    BATTLEAXE_SMASH(43, 2, FightStyle.AGGRESSIVE, AttackType.SLASH),
    BATTLEAXE_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    ABYSSAL_BLUDGEON_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    ABYSSAL_BLUDGEON_SLASH(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    ABYSSAL_BLUDGEON_SMASH(43, 2, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    ABYSSAL_BLUDGEON_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    DAGGER_STAB(43, 0, FightStyle.ACCURATE, AttackType.STAB),
    DAGGER_LUNGE(43, 1, FightStyle.AGGRESSIVE, AttackType.STAB),
    DAGGER_SLASH(43, 2, FightStyle.AGGRESSIVE, AttackType.SLASH),
    DAGGER_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.STAB),

    SWORD_STAB(43, 0, FightStyle.ACCURATE, AttackType.STAB),
    SWORD_LUNGE(43, 1, FightStyle.AGGRESSIVE, AttackType.STAB),
    SWORD_SLASH(43, 2, FightStyle.AGGRESSIVE, AttackType.SLASH),
    SWORD_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.STAB),

    RAPIER_STAB(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    RAPIER_LUNGE(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    RAPIER_SLASH(43, 2, FightStyle.AGGRESSIVE, AttackType.SLASH),
    RAPIER_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    SCIMITAR_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    SCIMITAR_SLASH(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    SCIMITAR_LUNGE(43, 2, FightStyle.CONTROLLED, AttackType.SLASH),
    SCIMITAR_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    LONGSWORD_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    LONGSWORD_SLASH(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    LONGSWORD_LUNGE(43, 2, FightStyle.CONTROLLED, AttackType.STAB),
    LONGSWORD_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    MACE_POUND(43, 0, FightStyle.ACCURATE, AttackType.CRUSH),
    MACE_PUMMEL(43, 1, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    MACE_SPIKE(43, 2, FightStyle.CONTROLLED, AttackType.STAB),
    MACE_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.CRUSH),

    SPEAR_LUNGE(43, 0, FightStyle.CONTROLLED, AttackType.STAB),
    SPEAR_SWIPE(43, 1, FightStyle.CONTROLLED, AttackType.SLASH),
    SPEAR_POUND(43, 2, FightStyle.CONTROLLED, AttackType.CRUSH),
    SPEAR_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.STAB),

    TWOHANDEDSWORD_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    TWOHANDEDSWORD_SLASH(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    TWOHANDEDSWORD_SMASH(43, 2, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    TWOHANDEDSWORD_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    PICKAXE_SPIKE(43, 0, FightStyle.ACCURATE, AttackType.STAB),
    PICKAXE_IMPALE(43, 1, FightStyle.AGGRESSIVE, AttackType.STAB),
    PICKAXE_SMASH(43, 2, FightStyle.AGGRESSIVE, AttackType.CRUSH),
    PICKAXE_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.STAB),

    CLAWS_CHOP(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    CLAWS_SLASH(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    CLAWS_LUNGE(43, 2, FightStyle.CONTROLLED, AttackType.STAB),
    CLAWS_BLOCK(43, 3, FightStyle.DEFENSIVE, AttackType.SLASH),

    HALBERD_JAB(43, 0, FightStyle.CONTROLLED, AttackType.SLASH),
    HALBERD_SWIPE(43, 1, FightStyle.AGGRESSIVE, AttackType.SLASH),
    HALBERD_FEND(43, 2, FightStyle.DEFENSIVE, AttackType.SLASH),

    UNARMED_PUNCH(43, 0, FightStyle.ACCURATE, AttackType.NONE),
    UNARMED_KICK(43, 1, FightStyle.AGGRESSIVE, AttackType.NONE),
    UNARMED_BLOCK(43, 2, FightStyle.DEFENSIVE, AttackType.NONE),

    WHIP_FLICK(43, 0, FightStyle.ACCURATE, AttackType.SLASH),
    WHIP_LASH(43, 1, FightStyle.CONTROLLED, AttackType.SLASH),
    WHIP_DEFLECT(43, 2, FightStyle.DEFENSIVE, AttackType.SLASH),

    BOLT_ACCURATE(43, 0, FightStyle.ACCURATE, AttackType.BOLT),
    BOLT_RAPID(43, 1, FightStyle.AGGRESSIVE, AttackType.BOLT),
    BOLT_LONGRANGE(43, 2, FightStyle.DEFENSIVE, AttackType.BOLT),

    ARROW_ACCURATE(43, 0, FightStyle.ACCURATE, AttackType.ARROW),
    ARROW_RAPID(43, 1, FightStyle.AGGRESSIVE, AttackType.ARROW),
    ARROW_LONGRANGE(43, 2, FightStyle.DEFENSIVE, AttackType.ARROW),

    THROWING_ACCURATE(43, 0, FightStyle.ACCURATE, AttackType.THROWN),
    THROWING_RAPID(43, 1, FightStyle.AGGRESSIVE, AttackType.THROWN),
    THROWING_LONGRANGE(43, 2, FightStyle.DEFENSIVE, AttackType.THROWN),

    SHORT_FUSE(43, 0, FightStyle.ACCURATE, AttackType.THROWN),
    MEDIUM_FUSE(43, 1, FightStyle.AGGRESSIVE, AttackType.THROWN),
    LONG_FUSE(43, 2, FightStyle.DEFENSIVE, AttackType.THROWN),

    POWERED_STAFF_ACCURATE_1(43, 0, FightStyle.ACCURATE, AttackType.ACCURATE_MAGIC),
    POWERED_STAFF_ACCURATE_2(43, 1, FightStyle.ACCURATE, AttackType.ACCURATE_MAGIC_2),
    POWERED_STAFF_LONGRANGE(43, 2, FightStyle.DEFENSIVE, AttackType.LONGRANGE_MAGIC)

    ;

    /** The parent config id. */
    private int parentId;

    /** The child config id. */
    private int childId;

    /** The fighting style. */
    private final FightStyle style;

    /** The attack type. */
    private final AttackType attackType;

    /**
     * Create a new {@link FightType}.
     * @param parentId
     *            the parent config id.
     * @param childId
     *            the child config id.
     * @param style
     *            the fighting style.
     */
    FightType(int parentId, int childId, FightStyle style, AttackType attackType) {
        this.parentId = parentId;
        this.childId = childId;
        this.style = style;
        this.attackType = attackType;
    }

    /**
     * Gets the parent config id.
     *
     * @return the parent id.
     */
    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    /**
     * Gets the child config id.
     *
     * @return the child id.
     */
    public int getChildId() {
        return childId;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    /**
     * Gets the fighting style.
     *
     * @return the fighting style.
     */
    public FightStyle getStyle() {
        return style;
    }

    public AttackType getAttackType() {
        return attackType;
    }

}
