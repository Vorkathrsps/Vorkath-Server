package com.aelous.services.database; // dont forget to change packaging ^-^

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;


public class Vote implements Runnable {
    public static final String HOST = "208.109.20.54";
    public static final String USER = "yauserv";
    public static final String PASS = "h.sC$OYLu=}%";
    public static final String DATABASE = "yavote";


    private Player player;
    private Connection conn;
    private Statement stmt;

    public NPC npc = World.getWorld().findNPC(1815);

    public Vote(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            player.lastVoteClaim = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30);
            player.message("Claiming vote, please wait.");
            if (!connect(HOST, DATABASE, USER, PASS)) {
                player.npcStatement(npc, new String[] { "Oh dear.. it seems like our", "voting system is down at the moment", "please try again soon."});
                System.out.println("Can't connect to mysql");
                return;
            }
            if (connect(HOST, DATABASE, USER, PASS)) {
                System.out.println("I can connect to mysql");
            }
            String name = player.getUsername().replace(" ", "_");
            ResultSet rs = executeQuery("SELECT * FROM votes WHERE username='"+player.getUsername().toLowerCase()+"' AND claimed=0 AND voted_on != -1");

            boolean sucessful = false;

            while (rs.next()) {
                String ipAddress = rs.getString("ip_address");
                int siteId = rs.getInt("site_id");
                player.getInventory().addOrDrop(new Item(619,1));
                sucessful = true;
                System.out.println("[Vote] Vote claimed by "+name+". (sid: "+siteId+", ip: "+ipAddress+")");
                player.npcStatement(npc, new String[] { "Success!", "Thank you for voting "+player.getUsername()+".", "here is your reward!" });
                rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
                rs.updateRow();
            }

            if (!sucessful) {
                player.npcStatement(npc, new String[] { "Oh dear..", "I cannot seem to find an unclaimed vote for you", "are you sure you've voted?", "please try again later."});
            }

            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    public void destroy() {
        try {
            conn.close();
            conn = null;
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public ResultSet executeQuery(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
