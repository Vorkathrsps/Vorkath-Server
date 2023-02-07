package com.aelous.model.entity.combat.method.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;

public interface AttackNpcListener {

    boolean allow(Entity player, NPC npc, boolean message);
}
