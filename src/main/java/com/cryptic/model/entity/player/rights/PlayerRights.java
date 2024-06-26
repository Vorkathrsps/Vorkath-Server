package com.cryptic.model.entity.player.rights;

import com.cryptic.model.entity.player.Player;
import com.intellij.openapi.util.text.StringUtil;

/**
 * @author Ynneh
 */
public enum PlayerRights {

    PLAYER(-1, 0),
    SUPPORT(2170, 1),
    MODERATOR(2171, 2),
    ADMINISTRATOR(2172, 3),
    COMMUNITY_MANAGER(2174, 4),
    OWNER(2175, 5);

    private final int spriteId;

    private final int right;

    PlayerRights(int spriteId, int right) {
        this.spriteId = spriteId;
        this.right = right;
    }

    @Override
    public String toString() {
        return "PlayerRights{" + "name='" + getName() + '\'' + '}';
    }

    public final String getName() {
        return StringUtil.capitalize(this.name().replaceAll("_", " ").toLowerCase());
    }

    public int getRightsId() {
        return right;
    }

    public final int getSpriteId() {
        return spriteId;
    }

    public boolean isSupport(Player player) {
        return player.getPlayerRights().ordinal() >= SUPPORT.ordinal() || isOwner(player);
    }

    public boolean isModerator(Player player) {
        return player.getPlayerRights().ordinal() >= MODERATOR.ordinal() || isOwner(player);
    }

    public boolean isAdministrator(Player player) {
        return player.getPlayerRights().ordinal() >= ADMINISTRATOR.ordinal() || isOwner(player);
    }

    public boolean isCommunityManager(Player player) {
        return player.getPlayerRights().ordinal() >= COMMUNITY_MANAGER.ordinal() || isOwner(player);
    }

    public boolean isOwner(Player player) {
        return player.getPlayerRights().ordinal() >= OWNER.ordinal();
    }

    public boolean isStaffMember(Player player) {
        return player.getPlayerRights().ordinal() >= SUPPORT.ordinal();
    }

    /** Gets the crown display. */
    public static String getCrown(Player player) {
        return player.getPlayerRights().equals(PLAYER) ? "" : "<img=" + (player.getPlayerRights().getSpriteId()) + ">";
    }

    public static boolean is(Player player, PlayerRights rights) {
        return player.getPlayerRights().ordinal() >= rights.ordinal();
    }
}
