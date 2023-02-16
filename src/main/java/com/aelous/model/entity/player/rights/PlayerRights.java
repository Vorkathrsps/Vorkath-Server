package com.aelous.model.entity.player.rights;

import com.aelous.model.entity.player.Player;
import com.intellij.openapi.util.text.StringUtil;

/**
 * @author Ynneh
 */
public enum PlayerRights {


    PLAYER( -1, -1),

    SUPPORT(505, 0),

    SENIOR_SUPPORT(505, 0),

    MODERATOR(494, 1),

    SENIOR_MODERATOR(1313, 1),

    ADMINISTRATOR(495, 2),

    SENIOR_ADMINISTRATOR(1314, 2),

    COMMUNITY_MANAGER(495, 2),

    DEVELOPER(495, 2),

    OWNER(495, 2),

    ;

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
        /** No need for String in enum **/
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

    public boolean isDeveloper(Player player) {
        return player.getPlayerRights().ordinal() >= DEVELOPER.ordinal() || isOwner(player);
    }

    public boolean isOwner(Player player) {
        return player.getPlayerRights().ordinal() >= OWNER.ordinal();
    }

    public boolean isStaffMember(Player player) {
        return player.getPlayerRights().ordinal() >= MODERATOR.ordinal();
    }

    /** Gets the crown display. */
    public static String getCrown(Player player) {
        return player.getPlayerRights().equals(PLAYER) ? "" : "<img=" + (player.getPlayerRights().getSpriteId()) + ">";
    }

    public static boolean is(Player player, PlayerRights rights) {
        return player.getPlayerRights().ordinal() >= rights.ordinal();
    }
}
