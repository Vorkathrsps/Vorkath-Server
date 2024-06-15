package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CheckBankCommand implements Command {
    private static final Logger logger = LogManager.getLogger(CheckBankCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("Lookup Player", value -> {
            String name = (String) value;
            final String player2 = Utils.formatText(name);
            Optional<Player> plr = World.getWorld().getPlayerByName(player2);
            if (plr.isPresent()) {
                sendBank(player, plr.get());
                player.message("Opening bank for Online Player: " + plr.get().getUsername());
                logger.info("Bank for {} is: {}", player2, plr.get().getBank().toString());
                return true;
            }
            getOffline(player, name);
            return true;
        });
    }

    private void getOffline(Player staff, String p2name) {
        AtomicReference<Player> offlineUser = new AtomicReference<>();
        offlineUser.set(new Player());
        offlineUser.get().setUsername(p2name);
        try {
            if (PlayerSave.loadOfflineWithoutPassword(offlineUser.get())) {
                if(!PlayerSave.playerExists(offlineUser.get().getUsername())) {
                    staff.message(Color.RED.wrap("There is no such player profile."));
                    return;
                }

                staff.message(Color.MITHRIL.wrap("Found Offline Player: " + offlineUser.get().getUsername()));
                sendBank(staff, offlineUser.get());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendBank(Player staff, Player other) {
        staff.getPacketSender().sendInterface(27200);
        staff.getPacketSender().sendString(27202, other.getUsername() + "'s Bank");
        for (int index = 0; index < 500; index++) staff.getPacketSender().sendItemOnInterfaceSlot(27201, null, index);
        Item[] bank = other.getBank().getItems();
        List<Item> temp = new ArrayList<>();
        for (Item item : bank) {
            if (item == null) continue;
            if (item.untradable()) continue;
            temp.add(item);
        }
        sortValue(temp);
        for (int index = 0; index < temp.size(); index++) {
            var item = temp.get(index);
            staff.getPacketSender().sendItemOnInterfaceSlot(27201, item, index);
        }
        staff.getPacketSender().sendScrollbarHeight(27300, 1500);
    }

    public void sortValue(List<Item> bank) {
        bank.sort((o1, o2) -> {
            o1 = o1.unnote();
            o2 = o2.unnote();
            ItemDefinition def = ItemDefinition.getInstance(o1.getId());
            ItemDefinition def2 = ItemDefinition.getInstance(o2.getId());
            int v1 = 0;
            int v2 = 0;
            if (def != null) {
                v1 = o1.getValue() * o1.getAmount();
            }
            if (def2 != null) {
                v2 = o2.getValue() * o2.getAmount();
            }
            return Integer.compare(v2, v1);
        });
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isModerator(player));
    }

}
