package com.cryptic.clientscripts.impl.healthhud;

import com.cryptic.clientscripts.constants.ScriptID;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.Varbits;
import com.cryptic.interfaces.Varps;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.chainedwork.Chain;

public record HealthHud(NPC npc) {

    public void set(Player player) {
        GameInterface.HEALTH_HUD.open(player);
        player.putAttrib(AttributeKey.HEALTH_HUD_ACTIVE, true);
        player.varps().setVarp(Varps.HEALTH_HUD_NPC_ID, this.npc.getId());
        player.varps().setVarbit(Varbits.HEALTH_HUD_CURRENT_HP, this.npc.hp());
        player.varps().setVarbit(Varbits.HEALTH_HUD_MAX_HP, this.npc.maxHp());
        player.varps().sendTempVarbit(Varbits.HEALTH_HUD_VISIBILTY, 1);
        player.getPacketSender().runClientScriptNew(ScriptID.HEALTH_HUDE_FADE, 1, 0, 0);
        player.getPacketSender().runClientScriptNew(ScriptID.HEALTH_HUDE_FADE_IN, 19857408, 19857410, 19857412, 19857413, 19857416, 19857418, 19857428, 19857421, 19857422, 19857423, 19857417, 19857414, 19857415, 19857419, 19857426, 19857427, 19857424, 19857425, 19857411);
    }

    public void clear(Player player) {
        player.clearAttrib(AttributeKey.HEALTH_HUD_ACTIVE);
        player.getPacketSender().runClientScriptNew(ScriptID.HEALTH_HUDE_FADE_OUT, 19857413, 19857416, 19857414, 19857415, 19857417, 19857419, 19857421, 19857422, 19857423, 19857428, 19857426, 19857427, 19857424, 19857425, 0);
        Chain.noCtx().runFn(7, () -> {
            player.varps().setVarp(Varps.HEALTH_HUD_NPC_ID, -1);
            player.varps().setVarbit(Varbits.HEALTH_HUD_CURRENT_HP, -1);
            player.varps().setVarbit(Varbits.HEALTH_HUD_MAX_HP, -1);
            player.varps().sendTempVarbit(Varbits.HEALTH_HUD_VISIBILTY, 0);
            GameInterface.HEALTH_HUD.close(player);
        });
    }

    public void sync(Player player) {
        if (!player.hasAttrib(AttributeKey.HEALTH_HUD_ACTIVE) && !npc.dead()) {
            this.set(player);
            return;
        }
        if (npc.dead() && player.hasAttrib(AttributeKey.HEALTH_HUD_ACTIVE)) {
            this.clear(player);
            return;
        }
        if (npc.hp() != npc.maxHp() && player.hasAttrib(AttributeKey.HEALTH_HUD_ACTIVE)) {
            player.varps().setVarbit(Varbits.HEALTH_HUD_CURRENT_HP, this.npc.hp());
        }
    }
}
