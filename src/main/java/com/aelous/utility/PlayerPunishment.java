package com.aelous.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * @author Patrick van Elderen | January, 16, 2021, 10:18
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class PlayerPunishment {

    private static final Logger logger = LogManager.getLogger(PlayerPunishment.class);

    /**
     * The directory for sanction
     */
    private static final File SANCTION_DIRECTORY = new File("./data/saves/");

    /**
     * Bans & mutes.
     */
    public static Set<String> ipBans = new HashSet<>(),
        macBannedUsers = new HashSet<>(),
        ipMutes = new HashSet<>(),
        mutedNames = new HashSet<>();

    public static ArrayList<String> bans = new ArrayList<>();

    public static void init() {
        banUsers();
        macBannedUsers();
        banIps();
        muteUsers();
        muteIps();
    }

    /**
     * Removes a banned user from the banned list.
     **/
    public static void unban(String name) {
        bans.remove(name);
        deleteFromFile("./data/saves/usersbanned.txt", name);
    }

    public static void unmute(String name) {
        mutedNames.remove(name);
        deleteFromFile("./data/saves/usersmuted.txt", name);
    }

    /**
     * Removes an IP address from the IPmuted list.
     */
    public static void unIPMuteUser(String name) {
        ipMutes.remove(name);
        deleteFromFile("./data/saves/ipsmuted.txt", name);
    }

    /**
     * Adds an IP address to the IPMuted list.
     */
    public static void ipmute(String IP) {
        ipMutes.add(IP);
        addIpToMuteFile(IP);
    }

    public static void removeFromIPBanList(String IP) {
        ipBans.remove(IP);
    }

    /**
     * Adds a user to the banned list.
     **/
    public static void addNameToBanList(String name) {
        bans.add(name);
        addNameToFile(name);
    }

    /**
     * Checks if a player is already on the ban list.
     */
    public static boolean banned(String name) {
        return bans.contains(name);
    }

    /**
     * Checks if a player is already on the mute list.
     */
    public static boolean muted(String name) {
        return mutedNames.contains(name);
    }

    /**
     * Checks if a player is already on the IP mute list.
     */
    public static boolean IPmuted(String name) {
        return ipMutes.contains(name);
    }

    public static void mute(String name) {
        mutedNames.add(name);
        addUserToFile(name);
    }

    /**
     * Adds a user to the muted list.
     */
    public static void muteUsers() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader("./data/saves/usersmuted.txt"))) {
                String data = null;
                while ((data = in.readLine()) != null) {
                    mutedNames.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an IP address to the IPMuted list.
     */
    public static void muteIps() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader("./data/saves/ipsmuted.txt"))) {
                String data = null;
                while ((data = in.readLine()) != null) {
                    ipMutes.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds an user to the IPBanned list.
     **/
    public static void addToIPBanList(String IP) {
        ipBans.add(IP);
        addIPToFile(IP,"./data/saves/ipsbanned.txt");
    }

    public static boolean ipBanned(String IP) {
        return ipBans.contains(IP);
    }

    /**
     * Reads all users from text file then adds them all to the ban list.
     **/
    public static void banUsers() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader("./data/saves/usersbanned.txt"))) {
                String data = null;
                while ((data = in.readLine()) != null) {
                    // DONT USE methods that write to files!
                    bans.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void macBannedUsers() {
        File file = new File("./data/saves/macbanned.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                if (!macBannedUsers.contains(line) && !line.isEmpty()) {
                    macBannedUsers.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addMacBan(String address) {
        if (!macBannedUsers.contains(address) && !address.isEmpty()) {
            macBannedUsers.add(address);
            updateMacBanFile();
        }
    }

    public static void removeMacBan(String address) {
        if (macBannedUsers.contains(address) && !address.isEmpty()) {
            macBannedUsers.remove(address);
            updateMacBanFile();
        }
    }

    private static void updateMacBanFile() {
        try (BufferedWriter out = new BufferedWriter(new FileWriter("./data/saves/macbanned.txt"))) {
            for (String mac : macBannedUsers) {
                out.write(mac);
                out.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads all the IPs from text file then adds them all to ban list.
     **/
    public static void banIps() {
        try {
            try (BufferedReader in = new BufferedReader(new FileReader("./data/saves/ipsbanned.txt"))) {
                String data = null;
                while ((data = in.readLine()) != null) {
                    ipBans.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeIpBan(String remove) throws IOException {
        List<String> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("./data/saves/ipsbanned.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                data.add(line);
            }
        }
        data.removeIf(s -> s.equals(remove));
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("./data/saves/ipsbanned.txt"))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Reload the list of banned ips.
     */
    public static void resetIpBans() {
        ipBans = new HashSet<>();
        banIps();
    }

    /**
     * Writes the user into the text file when using the ::ban command.
     **/
    public static void addNameToFile(String name) {
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter("./data/saves/usersbanned.txt", true))) {
                out.newLine();
                out.write(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the user into the text file when using the ::mute command.
     */
    public static void addUserToFile(String Name) {
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter("./data/saves/usersmuted.txt", true))) {
                out.newLine();
                out.write(Name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the IP into the text file when using the ::ipban command.
     **/
    public static void addIPToFile(String IP, String directory) {
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter(directory, true))) {
                out.newLine();
                out.write(IP);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the IP into the text file when using the ::mute command.
     */
    public static void addIpToMuteFile(String Name) {
        try {
            try (BufferedWriter out = new BufferedWriter(new FileWriter("./data/saves/ipsmuted.txt", true))) {
                out.newLine();
                out.write(Name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Void needed to delete users from a file.
     */
    public synchronized static void deleteFromFile(String file, String name) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            ArrayList<String> contents = new ArrayList<>();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                }
                line = line.trim();
                String[] args = line.split("-");
                if (!args[0].equalsIgnoreCase(name) && !line.equals("")) {
                    contents.add(line);
                }
            }
            r.close();
            BufferedWriter w = new BufferedWriter(new FileWriter(file));
            for (String line : contents) {
                w.write(line, 0, line.length());
                w.newLine();
            }
            w.flush();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFromFile(File file, String name) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            ArrayList<String> contents = new ArrayList<String>();
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                if (!line.equalsIgnoreCase(name)) {
                    contents.add(line);
                }
            }
            r.close();
            BufferedWriter w = new BufferedWriter(new FileWriter(file));
            for (String line : contents) {
                w.write(line, 0, line.length());
                w.newLine();
            }
            w.flush();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean macBanned(String address) {
        return macBannedUsers.contains(address);
    }
}
