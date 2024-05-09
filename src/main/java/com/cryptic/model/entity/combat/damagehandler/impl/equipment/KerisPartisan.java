package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.commons.lang.ArrayUtils;

public class KerisPartisan implements DamageModifyingListener {
    final int[] scarabites = new int[]{729, 794, 797, 798, 799, 1127, 1782, 4192, 6343, 11484, 11508, 11510, 11511, 11569, 11697, 11723, 11724, 11725, 11726, 11727};
    final int[] kalphites = new int[]{128, 138, 955, 956, 957, 958, 959, 960, 961, 962, 963, 965, 966, 4303, 4304, 6500, 6501, 6637, 6638, 6653, 6654, 7032, 10509};

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            Entity target = hit.getTarget();
            if (CombatType.MELEE.equals(combatType)) {
                if (target instanceof NPC npc) {
                    if (ArrayUtils.contains(scarabites, npc.id()) || ArrayUtils.contains(kalphites, npc.id())) {
                        if (player.getEquipment().contains(ItemIdentifiers.KERIS_PARTISAN)) {
                            int damage = hit.getDamage();
                            final boolean attribute = player.hasAttrib(AttributeKey.EXO_SKELETON);
                            if (Utils.rollDie(50, 1) && !attribute) {
                                player.putAttrib(AttributeKey.EXO_SKELETON, true);
                                player.message(Color.WHITE.wrap("You slip your weapon through a gap in the creature's chitin, landing a vicious blow."));
                            }
                            if (attribute) {
                                damage *= 3;
                                hit.setDamage(damage);
                                player.clearAttrib(AttributeKey.EXO_SKELETON);
                                return true;
                            }
                            damage = (int) (damage * 1.33D);
                            hit.setDamage(damage);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
