package com.aelous.model.content.sigils;

import com.aelous.model.content.sigils.data.Fortifcation;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.masks.impl.animations.Priority;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.chainedwork.Chain;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Origin
 * @SubAuthor Ynneh
 */
public abstract class SigilHandler {

    private static List<SigilHandler> sigil_list = Lists.newArrayList();

    public abstract void handleActvation(Player player, int itemID);

    public abstract int getUntunedId();

    public abstract int getTunedId();

    public abstract SigilType getSigilType();

    private static final int MAXIMUM_ACTIVE_SIGILS = 3;

    public static void handle(Player player, int itemId, boolean activate, boolean unattune) {
        SigilHandler info = getSigil(itemId);
        /**
         * Make Sure The Sigil Is Not Null
         */
        if (info == null) {
            return;
        }
        /**
         * Make Sure We Cannot Activate More Than One Of The Same Sigil
         */
        if (player.getActiveSigils().contains(info)) {
            player.getPacketSender().sendMessage("You cannot activate more than one of the same sigil.");
            return;
        }
        /**
         * The Maximum Amount Of Sigils Allowed To Be Activated
         */
        if (player.getActiveSigils().size() >= MAXIMUM_ACTIVE_SIGILS) {
            player.getPacketSender().sendMessage("You can only have 3 sigils active at once.");
            return;
        }
        /**
         * Activate The SigilHandler
         */
        if (activate) {
            addSigilData(info, player);
            if (!procCheck(player)) {
                info.handleActvation(player, info.getUntunedId());
            }
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
        player.activeSigils.remove(info);
        player.getInventory().remove(getTunedId());
        player.getInventory().add(itemID, 1);
        player.clearAttrib(AttributeKey.ATTUNED);
        player.message("<col=804080>Your sigil has depleted...");
    }

    public static boolean procCheck(Player player) {
        return player.getAttribOr(AttributeKey.SIGIL_OF_FORTIFICATION, false);
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
    }

}

