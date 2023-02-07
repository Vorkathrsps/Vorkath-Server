package com.aelous.network.codec.login;


import com.aelous.GameServer;
import com.aelous.services.database.transactions.GetBanStatusDatabaseTransaction;
import com.aelous.services.database.transactions.verifyOrInsertUserDatabaseTransaction;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.model.entity.player.save.PlayerSave.SaveDetails;
import com.aelous.network.codec.ByteBufUtils;
import com.aelous.network.NetworkUtils;
import com.aelous.utility.PlayerPunishment;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;

import static com.aelous.model.entity.attributes.AttributeKey.*;

public final class LoginResponses {
    private static final Logger logger = LogManager.getLogger(LoginResponses.class);

    public static int evaluateOnGamethread(Player player) {
        //logger.info("evaluating final login.");
        // Done on game thread.
        String host = "";
        if(player.getSession().getChannel() != null) {
            host = ByteBufUtils.getHost(player.getSession().getChannel());
        }

        String finalHost = host;
        long altCount = World.getWorld().getPlayers().stream().filter(p -> p != null && !p.getPlayerRights().isAdministrator(p) && !p.<Boolean>getAttribOr(IS_BOT,false) && finalHost.equals(p.getHostAddress())).count();
        if (altCount >= GameServer.properties().maxAlts) {
            //logger.trace("Maximum number of alts reached for: " +host+" : "+altCount);
            return LOGIN_CONNECTION_LIMIT;
        }
        if (World.getWorld().ls.ONLINE.contains(player.getMobName().toUpperCase()) || World.getWorld().getPlayerByName(player.getUsername()).isPresent()) {
            return LOGIN_ACCOUNT_ONLINE;
        }
        return LOGIN_SUCCESSFUL;
    }

    /**
     * checks bans, version, game update, password
     * @param player
     * @param msg
     * @return
     */
    public static int evaluateAsync(Player player, LoginDetailsMessage msg) {
        //logger.info("evaluating login.");
        // Done on networking thread.

        if(GameServer.boundTime == 0) {// server not on yet
            return UNABLE_TO_CONNECT;
        }

        if (GameServer.isUpdating()) {
            return LOGIN_GAME_UPDATE;
        }

        if (player.getUsername().startsWith(" ") || player.getUsername().endsWith(" ") ||
            !Utils.isValidName(player.getUsername())) {
            return INVALID_CREDENTIALS_COMBINATION;
        }

        if (!msg.getClientVersion().equals(GameServer.properties().gameVersion)) {
            return OLD_CLIENT_VERSION;
        }

        if (GameServer.isStaffOnlyLogins() && !player.getPlayerRights().isModerator(player)) {
            return LOGIN_SERVER_MAINTENANCE;
        }
        if (GameServer.properties().maintenanceMode && !player.getPlayerRights().isModerator(player)) {
            return LOGIN_SERVER_MAINTENANCE;
        }

        if (World.getWorld().getPlayers().isFull()) {
            return LOGIN_WORLD_FULL;
        }

        if (GameServer.properties().production) {
            if (Arrays.stream(NetworkUtils.INVALID_USERNAMES).anyMatch(username -> username.equalsIgnoreCase(player.getUsername()))) {
                return LOGIN_INVALID_USERNAME;
            }
            if (player.getUsername().toLowerCase().startsWith("testbot") && player.getSession().getChannel() != null) {
                // reject people trying to log on to developer rights testbot accs on prod. u can still make dummy bots because channel will be null
                return LOGIN_INVALID_USERNAME;
            }
        }

        if (GameServer.properties().enableSql && GameServer.properties().punishmentsToDatabase) {
            boolean banned = false;
            try {
                //If the server hasn't fully started up yet, let's tell the user the server is updating.
                if (GameServer.getDatabaseService() == null) {
                    return LoginResponses.LOGIN_GAME_UPDATE;
                }
                //Here we use execute instead of submit, since we want this to be executed synchronously and not asynchronously, since we want to wait for the response of the query before continuing execution in this LoginResponses class.
                banned = GameServer.getDatabaseService().execute(new GetBanStatusDatabaseTransaction(player.getUsername()));
            } catch (Exception e) {
                logger.catching(e);
            }
            if (banned) {
                return LoginResponses.LOGIN_DISABLED_ACCOUNT;
            }
        }

        if (GameServer.properties().punishmentsToLocalFile) {
            if (PlayerPunishment.banned(player.getUsername())) {
                return LoginResponses.LOGIN_DISABLED_ACCOUNT;
            }

            if (PlayerPunishment.ipBanned((player.getHostAddress()))) {
                return LoginResponses.LOGIN_DISABLED_ACCOUNT;
            }

            if (PlayerPunishment.macBanned(player.getAttribOr(MAC_ADDRESS, "invalid"))) {
                return LoginResponses.LOGIN_DISABLED_ACCOUNT;
            }
        }

        String enteredPassword = msg.getPassword();//Password received from client
        String name = player.getUsername();
        String newName = name.substring(0, 1).toUpperCase() + name.substring(1);

        File file = new File("./data/saves/characters/" + newName + ".json");
        //System.out.println("Skipping file, doesn't exist: "+file.exists());
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                final SaveDetails details = PlayerSave.SERIALIZE.fromJson(reader, SaveDetails.class);
                if(details == null || details.password() == null) {
                    return LoginResponses.COULD_NOT_COMPLETE_LOGIN;
                }
                final String storedPassword = details.password();
                //System.out.println(String.format("%s vs %s", enteredPassword, storedPassword));
                if (!BCrypt.checkpw(enteredPassword, storedPassword)) {
                    return LoginResponses.LOGIN_INVALID_CREDENTIALS;
                }
            } catch (Throwable t) {
                logger.error("There was an error logging on for " + player.getUsername() + ": ");
                logger.catching(t);
                return LoginResponses.COULD_NOT_COMPLETE_LOGIN;
            }
            try {
                PlayerSave.load(player);
            } catch (Throwable t) {
                logger.error("There was an error loading profile for " + player.getUsername() + ": ");
                logger.catching(t);
                return LoginResponses.COULD_NOT_COMPLETE_LOGIN;
            }

            //Player logged in from a different address ask for account pin!
            if(!player.getHostAddress().equalsIgnoreCase(msg.getHost()) && player.hasAccountPin()) {
                player.putAttrib(ASK_FOR_ACCOUNT_PIN,true);
            }

            player.putAttrib(MAC_ADDRESS, msg.getMac()); // override mac from save game with current mac
            player.setHostAddress(msg.getHost());
            player.getHostAddressMap().put(msg.getHost(), 1);

           /* if(msg.getMac().isEmpty()) {
                return LoginResponses.COULD_NOT_COMPLETE_LOGIN;
            }*/
        } else {
            // new account. encrypt pw and store.
            player.setPassword(BCrypt.hashpw(enteredPassword, BCrypt.gensalt()));
            player.putAttrib(AttributeKey.NEW_ACCOUNT, true);
            player.setCreationIp(msg.getHost());
            player.setCreationDate(new Timestamp(new Date().getTime()));
        }

        if (GameServer.properties().enableSql) {
            //Its this the whole time this connection no server just wasnt running yet
            //System.out.println("Submitting ProfileDatabaseTransaction");
            //Be careful, the execute method is blocking and throws ExecutionException and InterruptedException which should be handled.
            //Server.getDatabaseService().execute(new InsertUserDatabaseTransaction(player));
            GameServer.getDatabaseService().submit(new verifyOrInsertUserDatabaseTransaction(player));
        }

        if (player.getAttribOr(AttributeKey.NEW_ACCOUNT, false)) {
            // save the profile immediately
            PlayerSave.save(player);
        }
        return LOGIN_SUCCESSFUL;
    }

    /**
     * This login opcode signifies a successful login.
     */
    public static final int LOGIN_SUCCESSFUL = 2;

    /**
     * This login opcode is used when the player
     * has entered an invalid username and/or password.
     */
    public static final int LOGIN_INVALID_CREDENTIALS = 3;

    /**
     * This login opcode is used when the account
     * has been disabled.
     */
    public static final int LOGIN_DISABLED_ACCOUNT = 4;

    /**
     * This login opcode is used when the login
     * cannot be completed.
     */
    public static final int COULD_NOT_COMPLETE_LOGIN = 13;

    /**
     * This login opcode is used when the player's IP
     * has been disabled.
     */
    public static final int LOGIN_DISABLED_COMPUTER = 22;

    /**
     * Server still loading
     */
    public static final int UNABLE_TO_CONNECT = 8;

    /**
     * This login opcode is used when the player's IP
     * has been disabled.
     */
    public static final int LOGIN_DISABLED_IP = 27;

    /**
     * This login opcode is used when the account
     * attempting to connect is already online in the server.
     */
    public static final int LOGIN_ACCOUNT_ONLINE = 5;

    /**
     * This login opcode is used when the game has been or
     * is being updated.
     */
    public static final int LOGIN_GAME_UPDATE = 6;

    /**
     * This login opcode is used when the world being
     * connected to is full.
     */
    public static final int LOGIN_WORLD_FULL = 7;

    /**
     * This login opcode is used when the connections
     * from an ip address has exceeded connection limit.
     */
    public static final int LOGIN_CONNECTION_LIMIT = 9;

    /**
     * This login opcode is used when a connection
     * has received a bad session id.
     */
    public static final int LOGIN_BAD_SESSION_ID = 10;

    /**
     * This login opcode is used when the login procedure
     * has rejected the session.
     */
    public static final int LOGIN_REJECT_SESSION = 11;

    /**
     * This login opcode is used when a player has
     * entered invalid credentials.
     */
    public static final int INVALID_CREDENTIALS_COMBINATION = 28;

    /**
     * This login opcode is used when a player has
     * attempted to login with a old client.
     */
    public static final int OLD_CLIENT_VERSION = 30;

    /**
     * New account
     */
    public static final int NEW_ACCOUNT = -1;

    /**
     * This login opcode is used when the player
     * tries using an invalid username.
     */
    public static final int LOGIN_INVALID_USERNAME = 97;

    /**
     * This login opcode is used when the game
     * is under maintenance.
     */
    public static final int LOGIN_SERVER_MAINTENANCE = 98;
}
