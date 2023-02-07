package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.GameEngine;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;
import com.aelous.utility.FileUtil;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.aelous.utility.CustomItemIdentifiers.WEAPON_MYSTERY_BOX;
import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 30, 2021
 */
public class PromoCodeCommand implements Command {

    private long lastCommandUsed;

    public static boolean PROMO_CODE_COMMAND_ENABLED = true;

    private static final Logger fpkMerkLogs = LogManager.getLogger("FpkMerkLogs");
    private static final Level FPK_MERK_LOGS;

    static {
        FPK_MERK_LOGS = Level.getLevel("FPK_MERK");
    }

    private static final Set<String> promoCodeClaimedIP = new HashSet<>(), promoCodeClaimedMAC = new HashSet<>();

    private static final String directory = "./data/saves/promo_codes/PromoCodeClaimed.txt";

    public static void init() {
        promoClaimed();
    }

    private static void promoClaimed() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader(PromoCodeCommand.directory))) {
                String data;
                while ((data = in.readLine()) != null) {
                    promoCodeClaimedIP.add(data);
                    promoCodeClaimedMAC.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final List<Item> ITEM_LIST = Arrays.asList(
        new Item(BLOOD_MONEY, 25_000),
        new Item(ABYSSAL_TENTACLE, 1),
        new Item(DHAROKS_ARMOUR_SET, 1),
        new Item(WEAPON_MYSTERY_BOX, 1)
    );

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (!PROMO_CODE_COMMAND_ENABLED) {
            return;
        }

        //Only allow this command to be used every 30 seconds.
        if (System.currentTimeMillis() - lastCommandUsed >= 30_000) {
            lastCommandUsed = System.currentTimeMillis();
            if (command.length() <= 10) {
                player.message("Invalid usage of the command ::promocode.");
                return;
            }

            String username = Utils.formatText(command.substring(10)); // after "promocode "

            if (username.equalsIgnoreCase("fpk merk") || username.equalsIgnoreCase("vihtic")) {
                var IP = player.getHostAddress();
                var creationIP = player.getCreationIp();
                var MAC = player.<String>getAttribOr(AttributeKey.MAC_ADDRESS, "invalid");
                var promoClaimed = player.<Boolean>getAttribOr(AttributeKey.PROMO_CODE_CLAIMED, false);
                var fileAlreadyContainsAddress = FileUtil.claimed(IP, MAC, directory);

                player.message(Color.RED.wrap("Checking for availability this could take a moment..."));

                GameEngine.getInstance().submitLowPriority(() -> {

                    /// this file checking code has to go in low prio which is not the game thread so it doesn't lag
                    try {
                        if (FileUtil.existingPlayer(IP, MAC)) {
                            player.message(Color.RED.wrap("You cannot claim this promo code as an existing player."));
                            return; // Existing player
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    // then afterwards, we run the next code back on the game thread

                    GameEngine.getInstance().addSyncTask(() -> {
                        // all this code is back in game thread

                        if (!creationIP.equalsIgnoreCase(IP)) {
                            player.message(Color.RED.wrap("Your IP doesn't match your creation IP, you cannot claim this promo code."));
                            return; // IP changer
                        }

                        //Check if the player doesn't have a spoofed mac address
                        if (IP.isEmpty() || MAC.isEmpty() || MAC.equalsIgnoreCase("invalid")) {
                            player.message(Color.RED.wrap("You are not on a valid IP or MAC address."));
                            return; // No valid mac address
                        }

                        //Check if the player has already claimed the promo code
                        if (promoClaimed || fileAlreadyContainsAddress) {
                            player.message(Color.RED.wrap("You've already claimed a promo code and cannot claim another."));
                            return; // Already claimed one
                        }

                        //Add the player address to the file
                        // more file io

                        GameEngine.getInstance().submitLowPriority(() -> {
                            FileUtil.addAddressToClaimedList(IP, MAC, promoCodeClaimedIP, promoCodeClaimedMAC, directory);
                            if(username.equalsIgnoreCase("fpk merk")) {
                                FileUtil.writeTextToDir("FPK Merk "+IP, directory);
                            } else if(username.equalsIgnoreCase("vihtic")) {
                                FileUtil.writeTextToDir("Vihtic "+IP, directory);
                            }
                        });

                        player.inventory().addOrBank(ITEM_LIST);

                        Utils.sendDiscordInfoLog(player.getUsername() + " claimed the fpk merk promo code.", "fpk_merk");
                        fpkMerkLogs.log(FPK_MERK_LOGS, player.getUsername() + " claimed the fpk merk promo code.");

                        //Mark as opened
                        player.putAttrib(AttributeKey.PROMO_CODE_CLAIMED, true);
                    });
                });
            } else {
                player.message(username + " doesn't have a promo code.");
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
