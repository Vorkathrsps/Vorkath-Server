package com.cryptic.model.content.sigils.data.tier1;

import com.cryptic.model.content.sigils.SigilHandler;
import com.cryptic.model.content.sigils.SigilType;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.masks.impl.animations.Priority;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

public class MenacingMage extends SigilHandler {

    @Override
    public void handleActvation(Player player, Entity target, Hit hit, int itemID) {
        //TODO 20% Chance to curse the target
        /**
         * Only One Curse Can Be Active At A Time
         */
        player.putAttrib(AttributeKey.SIGIL_OF_MENACING_MAGE, true);
        player.message("<col=804080>You feel a surge of power draining from your sigil...");
        System.out.println("rolling for activation roll");
        if (!effectHandlerCheck(player)) {
            if (Utils.securedRandomChance(0.20D)) {
                player.performGraphic(new Graphic(getSigil(itemID).getSigilType() == SigilType.COMBAT ? 1993 : 1992, GraphicHeight.LOW, 0));
                player.animate(new Animation(getSigil(itemID).getSigilType() == SigilType.COMBAT ? 9158 : 9159, Priority.HIGH));
                player.putAttrib(AttributeKey.MENACING_CURSE, true);
                for (int i = 0; i < 12; i++) {
                    Chain.bound(null).runFn(i * 2, () -> {
                        Hit bleed = target.hit(player, 2, 0, CombatType.MAGIC).checkAccuracy();
                        bleed.submit();
                    });
                }
                Chain.bound(null).runFn(12, () -> player.clearAttrib(AttributeKey.MENACING_CURSE));
            }
        }
    }

    @Override
    public int getUntunedId() {
        return 26078;
    }

    @Override
    public int getTunedId() {
        return 26077;
    }

    @Override
    public boolean effectHandlerCheck(Player player) {
        return player.hasAttrib(AttributeKey.MENACING_CURSE);
    }

    @Override
    public SigilType getSigilType() {
        return SigilType.COMBAT;
    }

    @Override
    public void defaultAttribute(Player player) {
        player.clearAttrib(AttributeKey.SIGIL_OF_MENACING_MAGE);
    }
}
