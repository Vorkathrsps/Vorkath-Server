package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.model.entity.npc.droptables.NpcDropRepository;
import com.cryptic.model.entity.npc.droptables.NpcDropTable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulateCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        var id = Integer.parseInt(parts[1]);

        NpcDropTable table = NpcDropRepository.forNPC(id);
        player.<Integer>setAmountScript("Enter Amount of Kills", value -> {
            int kills = value;
            if (kills <= 0) {
                return false;
            }
            List<Item> simulate = table.simulate(player, kills);

            simulate.sort((o1, o2) -> {
                int oo1 = kills / Math.max(1, o1.getAmount());
                int oo2 = kills / Math.max(1, o2.getAmount());
                return Integer.compare(oo1, oo2);
            });

            Map<Integer, Item> mergedItems = new HashMap<>();
            for (Item item : simulate) {
                int itemId = item.getId();
                if (mergedItems.containsKey(itemId)) {
                    Item existingItem = mergedItems.get(itemId);
                    existingItem.setAmount(existingItem.getAmount() + item.getAmount());
                } else {
                    mergedItems.put(itemId, item);
                }
            }

            List<Item> mergedList = new ArrayList<>(mergedItems.values());

            player.getPacketSender().ifOpenSub(27200);
            for (int index = 0; index < 500; index++) {
                player.getPacketSender().sendItemOnInterfaceSlot(27201, null, index);
            }
            for (int index = 0; index < mergedList.size(); index++) {
                var item = mergedList.get(index);
                player.getPacketSender().sendItemOnInterfaceSlot(27201, item, index);
            }
            return true;
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isModerator(player);
    }
}
