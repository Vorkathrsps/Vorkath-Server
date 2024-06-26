package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class LeafBladedBattleAxe implements DamageModifyingListener {
    int[] npcs = new int[]{NpcIdentifiers.TUROTH, NpcIdentifiers.TUROTH_427, NpcIdentifiers.TUROTH_428, NpcIdentifiers.TUROTH_429, NpcIdentifiers.TUROTH_430, NpcIdentifiers.TUROTH_431, NpcIdentifiers.TUROTH_432, NpcIdentifiers.SPIKED_TUROTH,NpcIdentifiers.KURASK, NpcIdentifiers.KURASK_410, NpcIdentifiers.KURASK_411, NpcIdentifiers.KING_KURASK};
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            final Entity target = player.getCombat().getTarget();
            if (player.getCombat().getCombatType() == CombatType.MELEE) {
                if (player.getEquipment().hasAt(EquipSlot.WEAPON, ItemIdentifiers.LEAFBLADED_BATTLEAXE)) {
                    if (target instanceof NPC npc) {
                        if (ArrayUtils.contains(npcs, npc.id())) {
                            var damage = hit.getDamage();
                            var increased = 1.175;
                            var output = damage * increased;
                            hit.setDamage((int) output);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
