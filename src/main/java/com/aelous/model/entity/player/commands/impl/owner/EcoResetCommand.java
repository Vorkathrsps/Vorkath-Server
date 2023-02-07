package com.aelous.model.entity.player.commands.impl.owner;

import com.aelous.GameEngine;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Patrick van Elderen | February, 21, 2021, 16:53
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class EcoResetCommand implements Command {

    public static class AtomicStorage {
        public AtomicInteger topPlayers = new AtomicInteger(0);
        public AtomicInteger toScanAmt = new AtomicInteger(0);
        public AtomicInteger scannedCount = new AtomicInteger(0);
        public ConcurrentLinkedQueue<Player> loaded = new ConcurrentLinkedQueue<>();
    }

    private static final boolean HARD_RESET = false;
    private static final boolean ECO_RESET = false;

    private static final Logger logger = LogManager.getLogger(EcoResetCommand.class);

    HashSet<String> checkedPlayers = new HashSet<>();

    private void submitOfflineScan(Player dev, final HashSet<String> checkedPlayers) {
        AtomicStorage storage = new AtomicStorage();
        GameEngine.getInstance().submitLowPriority(() -> {
            try (Stream<Path> walk = Files.walk(Paths.get("data", "saves", "characters"))) {
                List<String> result = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
                ArrayList<Player> offlineToScan = new ArrayList<>(result.size());
                for (String p2name : result) {
                    String rsn = p2name.substring(p2name.lastIndexOf("\\") + 1, p2name.lastIndexOf("."));
                    p2name = Utils.formatText(rsn);
                    //If we checked the online player, skip them.
                    if (checkedPlayers.contains(p2name)) {
                        continue;
                    }
                    //We probably don't need to add them to checked players, but lets do it anyway.
                    checkedPlayers.add(p2name);
                    Player opp = new Player();
                    opp.setUsername(Utils.formatText(p2name));
                    offlineToScan.add(opp);
                }

                dev.getPacketSender().sendLogout();

                storage.toScanAmt.set(offlineToScan.size());

                for (Player player : offlineToScan) {
                    GameEngine.getInstance().submitLowPriority(() -> {
                        try {
                            if (PlayerSave.loadOfflineWithoutPassword(player)) {
                                storage.loaded.add(player);

                                if(HARD_RESET) {
                                    player.completelyResetAccount();
                                }

                                if(ECO_RESET) {
                                    player.ecoResetAccount();
                                }

                                // println every 20 files scanned for progress updates
                                int current = storage.scannedCount.addAndGet(1);
                                int goal = storage.toScanAmt.get();

                                if (current % 20 == 0) {
                                    // every 20
                                    logger.info("offline profile scanning {}/{}  ({}%) complete. {} remaining...", current, goal, (1d * current) / goal * 100, goal - current);
                                }

                                if (current == goal) {
                                    logger.info("scanning complete! saving info...");
                                }
                            } else {
                                dev.message("Something wrong went resetting the account for offline Player " + player.getUsername());
                                logger.error("Something wrong went resetting the account for offline Player " + player.getUsername());
                            }
                        } catch (Exception e) {
                            dev.message("Something wrong went resetting the account for offline Player " + player.getUsername());
                            logger.error("Something wrong went resetting the account for offline Player " + player.getUsername());
                            logger.catching(e);
                        }
                    });
                }
            } catch (Exception e) {
                logger.catching(e);
            }
        });
    }

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.requestLogout();
        Chain.bound(null).runFn(5, () -> {
            checkedPlayers.clear();
            submitOfflineScan(player, new HashSet<>(checkedPlayers));
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isOwner(player);
    }
}
