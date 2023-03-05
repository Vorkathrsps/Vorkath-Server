package com.aelous.model.content.sigils.data.tier1;

import com.aelous.model.content.sigils.SigilHandler;
import com.aelous.model.content.sigils.SigilType;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class FeralFighter extends SigilHandler {

    public static final FeralFighter INSTANCE = new FeralFighter();

    @Override
    public void handleActvation(Player player, Entity target, Hit hit, int itemID) {
        AtomicInteger count = new AtomicInteger();
        player.putAttrib(AttributeKey.SIGIL_OF_THE_FERAL_FIGHTER, true);
        //if (!effectHandlerCheckSpecial(player)) {
        //if (Utils.securedRandomChance(0.9)) {
        player.getBaseAttackSpeed();
        player.performGraphic(new Graphic(getSigil(itemID).getSigilType() == SigilType.COMBAT ? 1993 : 1992, GraphicHeight.LOW, 0));
        player.animate(new Animation(getSigil(itemID).getSigilType() == SigilType.COMBAT ? 9158 : 9159, Priority.HIGH));
        player.message("<col=804080>You feel a surge of power draining from your sigil...");
    }


    private boolean handleHitCancellation(Player player, int count) {
        if (count >= 3) {
            System.out.println("cancelling");
        } else {
            System.out.println("not cancelled");
        }
        return count >= 3 ? player.getAttribOr(AttributeKey.HIT_COUNT_FERAL_FIGHTER, false) : false;
    }

    @Override
    public int getUntunedId() {
        return 26075;
    }

    @Override
    public int getTunedId() {
        return 26074;
    }

    private boolean effectHandlerCheckSpecial(Player player) {
        return player.hasAttrib(AttributeKey.HIT_COUNT_FERAL_FIGHTER);
    }

    @Override
    public boolean effectHandlerCheck(Player player) {
        return player.hasAttrib(AttributeKey.FERAL_FIGHTER);
    }

    @Override
    public SigilType getSigilType() {
        return SigilType.COMBAT;
    }

    @Override
    public void defaultAttribute(Player player) {
        player.clearAttrib(AttributeKey.SIGIL_OF_THE_FERAL_FIGHTER);
    }
}
