package com.cryptic.clientscripts.impl.skills.information;

import com.cryptic.clientscripts.ComponentID;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public enum SkillEnum {
    ATTACK,
    STRENGTH,
    RANGED,
    MAGIC,
    DEFENCE,
    HITPOINTS,
    PRAYER,
    AGILITY,
    HERBLORE,
    THIEVING,
    CRAFTING,
    RUNECRAFTING,
    MINING,
    SMITHING,
    FISHING,
    COOKING,
    FIREMAKING,
    WOODCUTTING,
    FLETCHING,
    SLAYER,
    FARMING,
    CONSTRUCTION,
    HUNTER;

    public static final Int2ObjectMap<SkillEnum> BUTTON_TO_SKILL_MAP = new Int2ObjectOpenHashMap<>();

    static {
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_ATTACK_COMPONENT, SkillEnum.ATTACK);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_STRENGTH_COMPONENT, SkillEnum.STRENGTH);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_DEFENCE_COMPONENT, SkillEnum.DEFENCE);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_RANGED_COMPONENT, SkillEnum.RANGED);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_PRAYER_COMPONENT, SkillEnum.PRAYER);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_MAGIC_COMPONENT, SkillEnum.MAGIC);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_RUNECRAFTING_COMPONENT, SkillEnum.RUNECRAFTING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_CONSTRUCTION_COMPONENT, SkillEnum.CONSTRUCTION);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_HITPOINTS_COMPONENT, SkillEnum.HITPOINTS);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_AGILITY_COMPONENT, SkillEnum.AGILITY);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_HERBLORE_COMPONENT, SkillEnum.HERBLORE);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_THIEVING_COMPONENT, SkillEnum.THIEVING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_CRAFTING_COMPONENT, SkillEnum.CRAFTING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_FLETCHING_COMPONENT, SkillEnum.FLETCHING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_SLAYER_COMPONENT, SkillEnum.SLAYER);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_HUNTER_COMPONENT, SkillEnum.HUNTER);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_MINING_COMPONENT, SkillEnum.MINING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_SMITHING_COMPONENT, SkillEnum.SMITHING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_FISHING_COMPONENT, SkillEnum.FISHING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_COOKING_COMPONENT, SkillEnum.COOKING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_FIREMAKING_COMPONENT, SkillEnum.FIREMAKING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_WOODCUTTING_COMPONENT, SkillEnum.WOODCUTTING);
        BUTTON_TO_SKILL_MAP.put(ComponentID.SKILL_FARMING_COMPONENT, SkillEnum.FARMING);
    }
}
