package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.PlayerStatus;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.utility.Color;
import com.cryptic.utility.PlayerPunishment;
import com.cryptic.utility.Utils;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class IpBanCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("Ban Player", value -> {
            final String name = (String) value;
            final String input = Utils.formatText(name);
            GameEngine.getInstance().submitLowPriority(() -> {
                Optional<Player> other = World.getWorld().getPlayerByName(input);
                if (other.isPresent()) {
                    final Player banned = other.get();
                    ban(player, banned);
                } else {
                    tryOffline(player, name);
                }
            });
            return true;
        });
    }

    private void ban(Player player, Player banned) {
        GameEngine.getInstance().addSyncTask(() -> {
            final String bannedUsername = banned.getUsername();
            final PlayerStatus status = player.getStatus();
            final boolean isOnline = World.getWorld().getPlayers().entities.containsValue(banned);
            if (bannedUsername.equalsIgnoreCase(player.getUsername())) {
                player.message(Color.MITHRIL.wrap("<img=13> You cannot ban yourself.</img>"));
                return;
            }

            if (banned.getPlayerRights().isStaffMember(banned) && !player.getPlayerRights().isAdministrator(player)) {
                player.message(Color.MITHRIL.wrap("<img=13> You cannot ban this player.</img>"));
                return;
            }

            if (PlayerPunishment.ipBanned(banned.getHostAddress()) || PlayerPunishment.ipBanned(banned.getCreationIp())) {
                player.message(Color.MITHRIL.wrap("<img=13> Player " + bannedUsername + " already has an active ban.</img>"));
                return;
            }

            if (isOnline) {
                if (PlayerStatus.TRADING.equals(status)) {
                    banned.getTrading().abortTrading();
                }

                if (PlayerStatus.DUELING.equals(status)) {
                    banned.getDueling().onDeath();
                }

                if (PlayerStatus.DUELING.equals(status)) {
                    banned.getDueling().onDeath();
                }
            }

            PlayerPunishment.addToIPBanList(banned.getHostAddress());

            if (!Objects.equals(banned.getHostAddress(), banned.getCreationIp())) {
                PlayerPunishment.addToIPBanList(banned.getCreationIp());
            }

            if (isOnline) {
                banned.requestLogout();
            }
            player.message(Color.MITHRIL.wrap("<img=13> Player " + bannedUsername + " was successfully banned.</img>"));
        });
    }

    private void tryOffline(Player staff, String p2name) {
        AtomicReference<Player> offlineUser = new AtomicReference<>();
        offlineUser.set(new Player());
        offlineUser.get().setUsername(p2name);
        try {
            if (PlayerSave.loadOfflineWithoutPassword(offlineUser.get())) {
                if (!PlayerSave.playerExists(offlineUser.get().getUsername())) {
                    staff.message(Color.RED.wrap("<img=13> There is no such player profile.</img>"));
                    return;
                }

                staff.message(Color.MITHRIL.wrap("<img=13> Found Offline Player: " + offlineUser.get().getUsername() + "</img>"));
                ban(staff, offlineUser.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isModerator(player);
    }
}
