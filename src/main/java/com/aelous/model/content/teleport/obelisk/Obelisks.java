package com.aelous.model.content.teleport.obelisk;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.object.ObjectManager;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Obelisks are the objects that exist in the wilderness that aid player teleportation.
 * Once activated, any player within the obelisk boundary is moved to another obelisk.

 * @author Patrick van Elderen
 * @date March 15, 2020, 20:52:24 PM
 */
public class Obelisks extends PacketInteraction {

    /**
     * A single instance of this class
     */
    private static final Obelisks singleton = new Obelisks();

    /**
     * Returns the single instance of the Obelisks class
     *
     * @return the instance
     */
    public static Obelisks get() {
        return singleton;
    }

    public enum Obelisk {
        WILDERNESS_13(14829, 3156, 3620, 0, false),
        WILDERNESS_19(14830, 3227, 3667, 0, false),
        WILDERNESS_27(14827, 3035, 3732, 0, false),
        WILDERNESS_35(14828, 3106, 3794, 0, false),
        WILDERNESS_44(14826, 2980, 3866, 0, false),
        WILDERNESS_50(14831, 3307, 3916, 0, false);

        private final int id;
        private final int x;
        private final int y;
        private final int h;
        private boolean active;

        Obelisk(int id, int x, int y, int h, boolean active) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.h = h;
            this.active = active;
        }

        public int getId() {
            return id;
        }

        public Tile tile() {
            return new Tile(x, y, h);
        }

        public boolean active() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public static Obelisk object(int id) {
            for (Obelisk obelisk : values()) {
                if (obelisk.id == id) {
                    return obelisk;
                }
            }
            return null;
        }

       public static Obelisk random(Obelisk exclude) {
            ArrayList<Obelisk> locations = new ArrayList<>(Arrays.asList(values()));
            locations.remove(exclude);
            return locations.get(Utils.random(locations.size()));
        }
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        //Obelisk in level 13 wilderness
        if(obj.getId() == 14829) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_13, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }

        //Obelisk in level 19 wilderness
        if(obj.getId() == 14830) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_19, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }

        //Obelisk in level 27 wilderness
        if(obj.getId() == 14827) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_27, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }

        //Obelisk in level 35 wilderness
        if(obj.getId() == 14828) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_35, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }

        //Obelisk in level 44 wilderness
        if(obj.getId() == 14826) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_44, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }

        //Obelisk in level 50 wilderness
        if(obj.getId() == 14831) {
            switch(option) {
                case 1:
                    activateObelisk(player, Obelisk.WILDERNESS_50, obj);
                    break;
                case 2:
                    //TODO tp to destination
                    break;
                case 3:
                    //TODO choose destination
                    break;
            }
            return true;
        }
        return false;
    }

    private void activateObelisk(Player player, Obelisk obelisk, GameObject obj) {
        activateObelisk(player, obelisk, obj, Obelisk.random(obelisk).tile());
    }

    private void activateObelisk(Player player, Obelisk obelisk, GameObject obj, Tile destTile) {
        if (obelisk.active()) {
            player.message("The obelisk is already active!");
            return;
        }
        obelisk.active = true;
        int x = obelisk.x;
        int y = obelisk.y;

        List<Player> teleported = new LinkedList<>();

        // The interacted obelisk coordinates are from the enum at the top of this class.
        // The coordinates below are therefore fixed, and do not change depending on if you clicked the NE/SE/SW/NW pillar.

        //Replace the world object to the temp one

        GameObject swObelisk = new GameObject(14825, new Tile(x, y).transform(-2, -2, 0), obj.getType(), obj.getRotation());
        ObjectManager.addObj(swObelisk);

        GameObject nwObelisk = new GameObject(14825, new Tile(x, y).transform(-2, +2, 0), obj.getType(), obj.getRotation());
        ObjectManager.addObj(nwObelisk);

        GameObject seObelisk = new GameObject(14825, new Tile(x, y).transform(+2, -2, 0), obj.getType(), obj.getRotation());
        ObjectManager.addObj(seObelisk);

        GameObject neObelisk = new GameObject(14825, new Tile(x, y).transform(+2, +2, 0), obj.getType(), obj.getRotation());
        ObjectManager.addObj(neObelisk);
        Chain.bound(null).runFn(11, () -> {
            ObjectManager.replaceWith(swObelisk, new GameObject(obj.getId(), new Tile(x, y).transform(-2, -2, 0), obj.getType(), obj.getRotation()));
            ObjectManager.replaceWith(nwObelisk, new GameObject(obj.getId(), new Tile(x, y).transform(-2, +2, 0), obj.getType(), obj.getRotation()));
            ObjectManager.replaceWith(seObelisk, new GameObject(obj.getId(), new Tile(x, y).transform(+2, -2, 0), obj.getType(), obj.getRotation()));
            ObjectManager.replaceWith(neObelisk, new GameObject(obj.getId(), new Tile(x, y).transform(+2, +2, 0), obj.getType(), obj.getRotation()));
        }).then(1, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                int plrx = p.tile().x;
                int plry = p.tile().y;

                if (plrx >= x - 1 && plrx <= x + 1 && plry >= y - 1 && plry <= y + 1) {
                     if (p.getTimers().has(TimerKey.TELEBLOCK) || p.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                        p.message("You're teleblocked and cannot travel with obelisks.");
                    } else if (!p.locked() || !p.stunned()) {
                        p.stopActions(true);
                        p.lock();
                        p.animate(3945);
                        teleported.add(p);
                    }
                }
            });
        }).then(2, () -> {
            teleported.forEach(p -> {
                //Tile relative = obelisk.tile() - p.tile();
                //p.teleport(destTile - relative);
                p.teleport(destTile);
                p.message("Ancient magic teleports you somewhere in the wilderness.");
                p.animate(-1);
                obelisk.setActive(false);
                p.unlock();
            });
        });

        if(teleported.isEmpty()) {
            obelisk.setActive(false);
        }
    }

}
