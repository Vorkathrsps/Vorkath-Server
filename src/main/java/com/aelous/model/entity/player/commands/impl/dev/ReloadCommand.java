package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.GameEngine;
import com.aelous.model.content.skill.impl.fishing.Fishing;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.utility.loaders.loader.impl.ObjectSpawnDefinitionLoader;
import com.aelous.utility.loaders.loader.impl.BloodMoneyPriceLoader;
import com.aelous.utility.loaders.loader.impl.PresetLoader;
import com.aelous.utility.loaders.loader.impl.ShopLoader;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
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
                    logger.catching(e);
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
                    n.getCombatInfo(World.getWorld().combatInfo(n.id()));
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
            new PresetLoader().run();
            player.message("Finished.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player) || player.getUsername().equalsIgnoreCase("Chaotic jr"));
    }

}
