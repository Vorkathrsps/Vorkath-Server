package com.aelous.utility.flood;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.aelous.GameConstants;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * An implementation of {@link Runnable} which creates
 * new clients and tries to connect them with the server.
 * 
 * @author Professor Oak
 */
public class Flooder implements Runnable {
    private static final Logger logger = LogManager.getLogger(Flooder.class);

    /**
     * The clients that are currently active.
     * We can use this map to distinguish fake-clients
     * from real ones.
     */
    public final Map<String, FloodClient> clients = new HashMap<String, FloodClient>();

    /**
     * Is this flooder currently running?
     */
    private boolean running;

    /**
     * Starts this flooder if it hasn't
     * been started already.
     */
    public void start() {
        if (!running) {
            running = true;
            Thread t = new Thread(this);
            t.setName(""+GameConstants.SERVER_NAME+"FlooderThread");
            t.start();
        }
    }

    /**
     * Stops this flooder.
     *
     * Any logged in clients will eventually be disconnected
     * from the server automatically for being idle.
     */
    public void stop() {
        running = false;
    }

    /**
     * Attempts to login the amount of given clients.
     * @param amount
     */
    public void login(int amount) {
        //Make sure we have started before logging in clients.
        start();

        //Attempt to login the amount of bots..
        synchronized(clients) {
            for (int i = 0; i < amount; i++) {
                try {
                    String username = "bot" + Integer.toString(clients.size());
                    String password = "bot";
                    new FloodClient(Utils.formatText(username), password).attemptLogin();
                } catch(Exception e) {
                    logger.catching(e);
                }
            }
        }
    }

    /*
    @Override
    public void run() {
        while (running) {
            try {
                Iterator<Entry<String, Client>> i = clients.entrySet().iterator();
                while (i.hasNext()) {
                    Entry<String, Client> entry = i.next();
                    try {
                        entry.getValue().process();
                    } catch(Exception e) {
                        logger.catching(e);
                        i.remove();
                    }
                }
                Thread.sleep(300);
            } catch(Exception e) {
                logger.catching(e);
            }
        }
    }
    */

    @Override
    public void run() {
        while (running) {
            try {
                Iterator<Entry<String, FloodClient>> i = clients.entrySet().iterator();
                while (i.hasNext()) {
                    Entry<String, FloodClient> entry = null;
                    try {
                        entry = i.next();
                    } catch(Throwable t) {

                    } finally {
                        try {
                            entry.getValue().process();
                        } catch(Exception e) {
                            logger.catching(e);
                            i.remove();
                        }
                    }

                }
                Thread.sleep(300);
            } catch(Exception e) {
                logger.catching(e);
            }
        }
    }
}
