package com.cryptic.clientscripts.impl.skills;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.clientscripts.interfaces.InterfaceBuilder;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;

public class SkillTab extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.SKILL_TAB;
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {
        player.clearAttrib(AttributeKey.SKILL_INFORMATION);

        if (button == ComponentID.SKILL_ATTACK_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.ATTACK.ordinal() + 1);
        } else if (button == ComponentID.SKILL_STRENGTH_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.STRENGTH.ordinal() + 1);
        } else if (button == ComponentID.SKILL_DEFENCE_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.DEFENCE.ordinal() + 1);
        } else if (button == ComponentID.SKILL_RANGED_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.RANGED.ordinal() + 1);
        } else if (button == ComponentID.SKILL_PRAYER_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.PRAYER.ordinal() + 1);
        } else if (button == ComponentID.SKILL_MAGIC_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.MAGIC.ordinal() + 1);
        } else if (button == ComponentID.SKILL_RUNECRAFTING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.RUNECRAFTING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_CONSTRUCTION_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.CONSTRUCTION.ordinal() + 1);
        } else if (button == ComponentID.SKILL_HITPOINTS_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.HITPOINTS.ordinal() + 1);
        } else if (button == ComponentID.SKILL_AGILITY_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.AGILITY.ordinal() + 1);
        } else if (button == ComponentID.SKILL_HERBLORE_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.HERBLORE.ordinal() + 1);
        } else if (button == ComponentID.SKILL_THIEVING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.THIEVING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_CRAFTING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.CRAFTING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_FLETCHING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.FLETCHING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_SLAYER_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.SLAYER.ordinal() + 1);
        } else if (button == ComponentID.SKILL_HUNTER_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.HUNTER.ordinal() + 1);
        } else if (button == ComponentID.SKILL_MINING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.MINING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_SMITHING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.SMITHING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_FISHING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.FISHING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_COOKING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.COOKING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_FIREMAKING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.FIREMAKING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_WOODCUTTING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.WOODCUTTING.ordinal() + 1);
        } else if (button == ComponentID.SKILL_FARMING_COMPONENT) {
            player.putAttrib(AttributeKey.SKILL_INFORMATION, SkillEnum.FARMING.ordinal() + 1);
        }

        GameInterface.SKILL_INFORMATION.open(player);
    }
}
