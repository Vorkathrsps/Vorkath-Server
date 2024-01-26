package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.GameEngine;
import com.cryptic.model.content.skill.impl.fishing.Fishing;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.items.container.sounds.SoundLoader;
import com.cryptic.utility.loaders.loader.impl.ObjectSpawnDefinitionLoader;
import com.cryptic.utility.loaders.loader.impl.BloodMoneyPriceLoader;
import com.cryptic.utility.loaders.loader.impl.ShopLoader;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import static java.lang.String.format;

public class ReloadCommand implements Command {

    private static final Logger logger = LogManager.getLogger(ReloadCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        String reload = parts[1];
        if (reload.equalsIgnoreCase("bm")) {
            player.message("Reloading blood money prices...");
            new BloodMoneyPriceLoader().run();
            player.message("Finished.");
        } else if (reload.equalsIgnoreCase("sounds")) {
            player.message("reloading sounds...");
            new SoundLoader().run();
            player.message("finished reloading sounds");
        } else if (reload.equalsIgnoreCase("shops")) {
            player.message("Reloading shops...");
            new ShopLoader().run();
            player.message("Finished.");
        } else if (reload.equalsIgnoreCase("npcs")) {
            player.message("Reloading npcs...");
            GameEngine.getInstance().addSyncTask(() -> {
                for (NPC worldNpcs : World.getWorld().getNpcs()) {
                    if(worldNpcs == null || worldNpcs.def().isPet) {
                        continue;
                    }
                    World.getWorld().unregisterNpc(worldNpcs);
                }
                // Halloween.loadNpcs();
                World.loadNpcSpawns(new File("data/map/npcs"));
                try {
                    Fishing.respawnAllSpots(World.getWorld());
                } catch (FileNotFoundException e) {
                    logger.error("sadge", e);
                }
                player.message(format("Reloaded %d npcs. <col=ca0d0d>Warning: Npcs in Instances will not be respawned.", World.getWorld().getNpcs().size()));
                player.message("<col=ca0d0d>Must be done manually.");
            });
        } else if (reload.equalsIgnoreCase("drops")) {
            player.message("Reloading drops...");
            World.getWorld().loadDrops();
            player.message("Finished.");
        } else if (reload.equalsIgnoreCase("equipinfo")) {
            player.message("Reloading equipment info...");
            World.getWorld().loadEquipmentInfo();
            player.message("Reloaded equip info.");
        } else if (reload.equalsIgnoreCase("combatdefs") || reload.equalsIgnoreCase("npcinfo")) {
            player.message("Reloading npc combat info...");
            World.getWorld().loadNpcCombatInfo();

            // Reload npcs
            World.getWorld().getNpcs().forEach(n -> {
                if (n != null) {
                    n.setCombatInfo(World.getWorld().combatInfo(n.id()));
                }
            });
            player.message("Finished.");
        } else if (reload.equalsIgnoreCase("objects")) {
            player.message("Reloading objects...");
            //TODO ask Jak why this is broken
            /*World.getWorld().getObjects().forEach(obj -> {
                if(obj != null) {
                    ObjectManager.removeObj(obj);
                }
            });*/
            new ObjectSpawnDefinitionLoader().run();
            player.message("Finished.");
        } else if (reload.equalsIgnoreCase("presets")) {
            player.message("Reloading presets...");
           // new PresetLoader().run();
            player.message("Finished.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
