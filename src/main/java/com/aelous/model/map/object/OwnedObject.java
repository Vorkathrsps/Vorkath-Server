package com.aelous.model.map.object;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;

import java.util.Optional;

/**
 * @author Patrick van Elderen | April, 18, 2021, 14:46
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public abstract class OwnedObject extends GameObject {

    //Unique identifer for this object.
    private final String identifier;

    //This objects owner uuid.
    private final int ownerUID;

    public OwnedObject(Player owner, String identifier, int id, Tile tile, int type, int direction) {
        super(id, tile, type, direction);
        this.ownerUID = owner.getIndex();
        this.identifier = identifier;
    }

    public OwnedObject(int ownerUID, String identifier, int id, Tile tile, int type, int direction) {
        super(id, tile, type, direction);
        this.ownerUID = ownerUID;
        this.identifier = identifier;
    }

    public void destroy() {
        World.getWorld().deregisterOwnedObject(this);
        this.remove();
    }

    public abstract void tick();

    public boolean isOwner(Player player) {
        return player.getIndex() == ownerUID;
    }

    public Player getOwner() {
        return World.getWorld().getPlayer(ownerUID, true);
    }

    public Optional<Player> getOwnerOpt() {
        return World.getWorld().getPlayerByUid(ownerUID);
    }

    public int getOwnerUID() {
        return ownerUID;
    }

    public String getIdentifier() {
        return identifier;
    }
}
