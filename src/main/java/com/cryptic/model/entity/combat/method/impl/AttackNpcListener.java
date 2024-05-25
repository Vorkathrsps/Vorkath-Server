package com.cryptic.model.entity.combat.method.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;

public interface AttackNpcListener {

    boolean allow(Entity player, NPC npc, boolean message);
}
