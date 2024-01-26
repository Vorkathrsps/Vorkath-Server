package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.chainedwork.Chain;

public class ObjectAnimationCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        int animation = Integer.parseInt(parts[1]);
        int objectId = Integer.parseInt(parts[2]);

        GameObject gameObject = new GameObject(objectId, new Tile(player.getX(), player.getY(), player.getZ()));
        gameObject.spawn();

        Chain.noCtx().runFn(1, () -> gameObject.animate(animation));
        Chain.noCtx().runFn(30, gameObject::remove);

    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }
}
