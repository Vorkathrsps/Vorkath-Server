package com.aelous.model.content.sigils;

import com.aelous.model.content.sigils.data.tier1.FeralFighter;
import com.aelous.model.content.sigils.data.tier1.Fortifcation;
import com.aelous.model.content.sigils.data.tier1.MenacingMage;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.*;

/**
 * @author Origin
 * @SubAuthor Ynneh
 */
public abstract class SigilHandler {

    private static List<SigilHandler> sigil_list = Lists.newArrayList();

    public abstract void handleActvation(Player player, Entity target, Hit hit, int itemID);

    public abstract int getUntunedId();

    public abstract int getTunedId();

    public abstract boolean effectHandlerCheck(Player player);

    public abstract SigilType getSigilType();

    public abstract void defaultAttribute(Player player);

    private static final int MAXIMUM_ACTIVE_SIGILS = 3;

    public static void handle(Player player, int itemId, boolean activate, boolean unattune) {
        SigilHandler info = getSigil(itemId);
        Entity target = player.getCombat().getTarget();
        Hit hit = player.hit(player, 0, (CombatType) null);
        /**
         * Make Sure The Sigil Is Not Null
         */
        if (info == null) {
            return;
        }
        /**
         * Make Sure We Cannot Activate More Than One Of The Same Sigil
         */
        if (!unattune && player.getActiveSigils().contains(info)) {
            player.getPacketSender().sendMessage("You cannot activate more than one of the same sigil.");
            return;
        }
        /**
         * The Maximum Amount Of Sigils Allowed To Be Activated
         */
        if (!unattune && player.getActiveSigils().size() >= MAXIMUM_ACTIVE_SIGILS) {
            player.getPacketSender().sendMessage("You can only have 3 sigils active at once.");
            return;
        }
        /**
         * Activate The SigilHandler
         */
        if (activate) {
            addSigilData(info, player);
            info.handleActvation(player, target, hit,info.getUntunedId());
            Chain.bound(null).runFn(0, () -> {
                player.performGraphic(new Graphic(info.getSigilType() == SigilType.COMBAT ? 1993 : 1992, GraphicHeight.LOW, 0));
                player.animate(new Animation(info.getSigilType() == SigilType.COMBAT ? 9158 : 9159, Priority.HIGH));
            });
        } else if (unattune) {
            info.clearSigilData(info, info.getUntunedId(), player);
        }
    }

    public static void addSigilData(SigilHandler info, Player player) {
        player.inventory().remove(info.getUntunedId());
        player.inventory().add(info.getTunedId(), 1);
        player.putAttrib(AttributeKey.ATTUNED, true);
        player.activeSigils.add(info);
    }

    public void clearSigilData(SigilHandler info, int itemID, Player player) {
        info.defaultAttribute(player);
        player.activeSigils.remove(info);
        player.getInventory().remove(getTunedId());
        player.getInventory().add(itemID, 1);
        player.clearAttrib(AttributeKey.ATTUNED);
        player.message("<col=804080>Your sigil has depleted...");
    }

    private static AttributeKey[] attributeKeys() {
        return new AttributeKey[]{AttributeKey.SIGIL_OF_FORTIFICATION, AttributeKey.SIGIL_OF_MENACING_MAGE, AttributeKey.SIGIL_OF_THE_FERAL_FIGHTER};
    }

    public static boolean isActive(Player player, SigilHandler handler) {
        return player.getActiveSigils().stream().filter(Objects::nonNull).anyMatch(s -> s.getTunedId() == handler.getTunedId());
    }

    public static SigilHandler getSigil(int itemId) {
        Optional<SigilHandler> untunedSigil = sigil_list.stream().filter(Objects::nonNull).filter(s -> s.getUntunedId() == itemId).findAny();
        return untunedSigil.orElse(getTunedSigil(itemId));
    }

    public static SigilHandler getTunedSigil(int itemId) {
        Optional<SigilHandler> tunedsigil = sigil_list.stream().filter(Objects::nonNull).filter(s -> s.getTunedId() == itemId).findAny();
        return tunedsigil.orElse(null);
    }

    public static boolean isSigil(int itemId) {
        return sigil_list.stream().filter(Objects::nonNull).anyMatch(s -> s.getUntunedId() == itemId) || sigil_list.stream().filter(Objects::nonNull).anyMatch(s -> s.getTunedId() == itemId);
    }

    static {
        sigil_list.add(new Fortifcation());
        sigil_list.add(new MenacingMage());
        sigil_list.add(new FeralFighter());
    }

}

