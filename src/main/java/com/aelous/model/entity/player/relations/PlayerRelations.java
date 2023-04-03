package com.aelous.model.entity.player.relations;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This file represents a player's relation with other world entities,
 * this manages adding and removing friends who we can chat with and also
 * adding and removing ignored players who will not be able to message us or see us online.
 *
 * @author relex lawl
 * Redone a bit by Gabbe
 */
public class PlayerRelations {

    /**
     * The player's current friend status, checks if others will be able to see them online.
     */
    private PrivateChatStatus status = PrivateChatStatus.ON;

    /**
     * This map contains the player's friends list.
     */
    private final List<String> friendList = new ArrayList<>(200);

    /**
     * This map contains the player's ignore list.
     */
    private final List<String> ignoreList = new ArrayList<>(100);

    /**
     * The player's current private message index.
     */
    private int privateMessageId = 1;

    /**
     * Gets the current private message index.
     * @return    The current private message index + 1.
     */
    public int getPrivateMessageId() {
        return privateMessageId++;
    }

    /**
     * Sets the current private message index.
     * @param privateMessageId    The new private message index value.
     * @return                    The PlayerRelations instance.
     */
    public PlayerRelations setPrivateMessageId(int privateMessageId) {
        this.privateMessageId = privateMessageId;
        return this;
    }

    public void setStatus(PrivateChatStatus status) {
        this.status = status;
        broadcastSocialStatusChanged();
    }

    public PrivateChatStatus getStatus() {
        return this.status;
    }

    /**
     * Gets the player's friend list.
     * @return    The player's friends.
     */
    public List<String> getFriendList() {
        return friendList;
    }

    /**
     * Gets the player's ignore list.
     * @return    The player's ignore list.
     */
    public List<String> getIgnoreList() {
        return ignoreList;
    }

    public void onLogout() {
        if (status.equals(PrivateChatStatus.OFF)) // wont be visible on players lists anyway
            return;
        for (Player players : World.getWorld().getPlayers()) {
            if (players == null) {
                continue;
            }
            if (players.getRelations().hasFriend(player)) {
                // we're set as friends only, other player can't see us. no need to change
                if (status.equals(PrivateChatStatus.FRIENDS_ONLY) && !friendList.contains(players.getUsername()))
                    continue;
                if (ignoreList.contains(players.getUsername())) { // other player cant see us
                    continue;
                }
                players.getPacketSender().sendFriend(player.getUsername(), 0);
            }
        }
    }

    public boolean hasFriend(Player player) {
        return friendList.contains(player.getUsername());
    }

    /**
     * Updates our state to all people who have the player added.
     */
    public void broadcastSocialStatusChanged() {
        for (Player friend : World.getWorld().getPlayers()) {
            if (friend == null) {
                continue;
            }
            if (!friend.getRelations().hasFriend(player)) { // irrelevent
                continue;
            }
            if (status == PrivateChatStatus.OFF) { // we've gone offline.
                friend.getPacketSender().sendFriend(player.getUsername(), 0);
                continue;
            }
            if (status.equals(PrivateChatStatus.FRIENDS_ONLY) && !friendList.contains(friend.getUsername())) {
                // friend isn't on our list, they cant see us
                friend.getPacketSender().sendFriend(player.getUsername(), 0);
                continue;
            }
            // online or visible to friends
            friend.getPacketSender().sendFriend(player.getUsername(), 1);

        }
    }

    public void sendChatOptionStates() {
        int privateChat = status == PrivateChatStatus.OFF ? 2
            : status == PrivateChatStatus.FRIENDS_ONLY ? 1 : 0;
        player.getPacketSender().sendChatOptions(0, privateChat, 0);
    }

    public void sendAllFriends() {
        ArrayList<String> offlineToSend = new ArrayList<>(friendList);
        for (Player friend : World.getWorld().getPlayers()) {
            if (friend == null) {
                continue;
            }
            if (player.getRelations().hasFriend(friend)) {

                // we're not on their friends list and they're status=friends. will appear as offline
                if (friend.getRelations().status.equals(PrivateChatStatus.FRIENDS_ONLY)
                    && !friend.getRelations().getFriendList().contains(player.getUsername()))
                    continue;

                // they're hiding or we're ignored. will appear as offline
                if (friend.getRelations().status.equals(PrivateChatStatus.OFF)
                    || friend.getRelations().getIgnoreList().contains(player.getUsername())) {
                    continue;
                }
                // player is online and visible to us!
                offlineToSend.remove(friend.getUsername());
                player.getPacketSender().sendFriend(friend.getUsername(), 1);
            }
        }
        // remaining players are offline.
        for (String l : offlineToSend) {
            player.getPacketSender().sendFriend(l, 0);
        }
    }

    public void sendIgnores() {
        for (String l : ignoreList) {
            player.getPacketSender().sendAddIgnore(l);
        }
    }

    public void sendDeleteFriend(String name) {
        player.getPacketSender().sendDeleteFriend(name);
    }

    public void sendAddIgnore(String name) {
        player.getPacketSender().sendAddIgnore(name);
    }

    public void sendDeleteIgnore(String name) {
        player.getPacketSender().sendDeleteIgnore(name);
    }

    public void onLogin() {
        player.getPacketSender().sendFriendStatus(2);
        sendIgnores();
        sendAllFriends();
        broadcastSocialStatusChanged();
        sendChatOptionStates();
    }

    /**
     * Adds a player to the associated-player's friend list.
     * @param name    The user name of the player to add to friend list.
     */
    public void addFriend(String name) {
        if (name == null)
            return;
        if (name.equalsIgnoreCase(player.getUsername())) {
            return;
        }
        if (friendList.size() >= 200) {
            player.message("Your friend list is full!");
            return;
        }
        if (ignoreList.contains(name)) {
            player.message("Please remove " + name + " from your ignore list first.");
            return;
        }
        if (friendList.contains(name)) {
            player.message(name + " is already on your friends list!");
        } else {
            friendList.add(name);
            Optional<Player> friend = World.getWorld().getPlayerByName(name);

            int weCanSeeThem = friend.map(player1 -> player1.getRelations().allowedToSeeMe(player)).orElse(0);
            player.getPacketSender().sendFriend(name, weCanSeeThem);

            friend.ifPresent(value -> {
                if (player.getRelations().allowedToSeeMe(friend.get()) == 1)
                    // only send our status if they can see us
                    friend.get().getPacketSender().sendFriend(player.getUsername(), 1);
            });
        }
    }

    private int allowedToSeeMe(Player player) {
        return getStatus() == PrivateChatStatus.OFF ? 0 :

            // we're ignored
            ignoreList.contains(player.getUsername()) ? 0 :

                // friends mode but we're not on their list
                getStatus() == PrivateChatStatus.FRIENDS_ONLY && !hasFriend(player) ? 0 :

                    // by all other accounts, we should be visible
                    1;
    }

    /*
     * Checks if a player is friend with someone.
     */
    public boolean isFriendWith(String player) {
        return friendList.contains(player);
    }

    /**
     * Deletes a friend from the associated-player's friends list.
     * @param username    The user name of the friend to delete.
     */
    public void deleteFriend(String username) {
        if (username.equalsIgnoreCase(player.getUsername())) {
            return;
        }
        if (friendList.contains(username)) {
            friendList.remove(username);
            sendDeleteFriend(username);

            Optional<Player> friend = World.getWorld().getPlayerByName(username);

            friend.ifPresent(value -> {
                friend.get().getPacketSender().sendFriend(player.getUsername(), player.getRelations().allowedToSeeMe(friend.get()));
            });
        } else {
            player.message("This player is not on your friends list!");
        }
    }

    /**
     * Adds a player to the associated-player's ignore list.
     * @param username    The user name of the player to add to ignore list.
     */
    public void addIgnore(String username) {
        if (username.equalsIgnoreCase(player.getUsername())) {
            return;
        }
        if (ignoreList.size() >= 100) {
            player.message("Your ignore list is full!");
            return;
        }
        if (friendList.contains(username)) {
            player.message("Please remove " + username + " from your friend list first.");
            return;
        }
        if (ignoreList.contains(username)) {
            player.message(username + " is already on your ignore list!");
        } else {
            ignoreList.add(username);
            sendAddIgnore(username);

            Optional<Player> friend = World.getWorld().getPlayerByName(username);
            friend.ifPresent(value -> {
                friend.get().getPacketSender().sendFriend(player.getUsername(), player.getRelations().allowedToSeeMe(friend.get()));
            });
        }
    }

    /**
     * Deletes an ignored player from the associated-player's ignore list.
     * @param username    The user name of the ignored player to delete from ignore list.
     */
    public void deleteIgnore(String username) {
        if (username.equalsIgnoreCase(player.getUsername())) {
            return;
        }
        if (ignoreList.contains(username)) {
            ignoreList.remove(username);
            sendDeleteIgnore(username);
            Optional<Player> friend = World.getWorld().getPlayerByName(username);
            friend.ifPresent(value -> {
                friend.get().getPacketSender().sendFriend(player.getUsername(), player.getRelations().allowedToSeeMe(friend.get()));
            });
        } else {
            player.message("This player is not on your ignore list!");
        }
    }

    /**
     * Sends a private message to {@code friend}.
     * @param friend    The player to private message.
     * @param message    The message being sent in bytes.
     * @param size        The size of the message.
     */
    public void message(Player friend, byte[] message, int size) {
        if (friend == null || message == null) {
            player.message("This player is currently offline.");
            return;
        }
        if (player.muted()) {
            player.message("You are muted and cannot send private messages.");
            return;
        }
        if(friend.getRelations().ignoreList.contains(player.getUsername())) {
            player.message("You are being ignored and cannot send private messages.");
            return;
        }
        if (friend.getRelations().status.equals(PrivateChatStatus.FRIENDS_ONLY)
            && !friend.getRelations().friendList.contains(player.getUsername())
            || friend.getRelations().status.equals(PrivateChatStatus.OFF)) {
            player.message("This player is currently offline.");
            return;
        }
        if (status == PrivateChatStatus.OFF) {
            setStatus(PrivateChatStatus.FRIENDS_ONLY);
        }
        friend.getPacketSender().sendPrivateMessage(player.getPlayerRights().getRightsId(),  player.getIronManStatus().ordinal(), player.getMemberRights().ordinal(), player, message, size);
    }

    /**
     * Represents a player's friends list status, whether
     * others will be able to see them online or not.
     */
    public enum PrivateChatStatus {
        ON,
        FRIENDS_ONLY,
        OFF
    }

    /**
     * The PlayerRelations constructor.
     * @param player    The associated-player.
     */
    public PlayerRelations(Player player) {
        this.player = checkNotNull(player, "player == null");
    }

    /**
     * The associated player.
     */
    private final Player player;

}
