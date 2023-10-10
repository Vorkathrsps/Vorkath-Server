package com.cryptic.model.content.raids.theatre.area;

import com.cryptic.model.content.raids.theatre.boss.nylocas.NylocasMinions;
import com.cryptic.model.content.raids.theatre.boss.nylocas.NylocasVasilias;
import com.cryptic.model.content.raids.theatre.stage.TheatreStage;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.Controller;
import com.cryptic.utility.chainedwork.Chain;

import java.util.Collections;
import java.util.Random;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

public class TheatreAreaController extends Controller {
    private final Tile[] fromTile = new Tile[]{
        new Tile(3282, 4249),
        new Tile(3295, 4235),
        new Tile(3309, 4248),
        new Tile(3282, 4248),
        new Tile(3296, 4235),
        new Tile(3309, 4249)
    };
    int[] npcs = new int[]{NYLOCAS_ISCHYROS_8342, NYLOCAS_TOXOBOLOS_8343, NYLOCAS_HAGIOS};
    public static int finalIndex;
    public static final Area ROOM = new Area(3290, 4243, 3301, 4254);

    public TheatreAreaController() {
        super(Collections.emptyList());
    }

    public Tile getRandomTile() {
        Tile[] tileArray = fromTile;
        if (tileArray.length == 0) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(tileArray.length);
        return tileArray[randomIndex];
    }

    public int getRandomNPC() {
        Random random = new Random();
        finalIndex = random.nextInt(npcs.length);
        return npcs[finalIndex];
    }

    @Override
    public void enter(Player player) {
        var theatreInstance = player.getTheatreInstance();

        if (theatreInstance.hasInitiatedNylocasVasilias || player.getTheatreStage() == TheatreStage.THREE) {
            return;
        }

        theatreInstance.setHasInitiatedNylocasVasilias(true);

        Chain.noCtx().repeatingTask(5, work -> {
            if (theatreInstance.isDisposed()) {
                work.stop();
                return;
            }
            int wave = theatreInstance.wave.get();

            if (wave == 50) {
                spawnNylocasVasilias(player);
                clearNylocasAndPillars(player);
                work.stop();
                return;
            }

            theatreInstance.wave.getAndIncrement();
            spawnNylocasMinions(player);
        });
    }

    private void spawnNylocasVasilias(Player player) {
        var theatreInstance = player.getTheatreInstance();
        NylocasVasilias nylocasVasilias = new NylocasVasilias(8355, new Tile(3294, 4247, theatreInstance.getzLevel()), theatreInstance);
        nylocasVasilias.setInstance(theatreInstance);
        nylocasVasilias.spawn(false);
        theatreInstance.wave.getAndSet(0);
    }

    private void spawnNylocasMinions(Player player) {
        var theatreInstance = player.getTheatreInstance();
        NylocasMinions nylocasMinions = new NylocasMinions(getRandomNPC(), getRandomTile().transform(0, 0, theatreInstance.getzLevel()), theatreInstance);
        nylocasMinions.setInstance(theatreInstance);
        nylocasMinions.spawn(false);
        theatreInstance.getNylocas().add(nylocasMinions);
    }

    private void clearNylocasAndPillars(Player player) {
        var theatreInstance = player.getTheatreInstance();

        theatreInstance.getNylocas().forEach(n -> {
            if (n != null) {
                n.die();
            }
        });

        theatreInstance.getPillarList().forEach(p -> {
            if (p != null) {
                p.die();
            }
        });

        theatreInstance.getNylocas().clear();
        theatreInstance.getPillarList().clear();
    }

    @Override
    public void leave(Player player) {

    }

    @Override
    public void process(Player player) {

    }

    @Override
    public void onMovement(Player player) {

    }

    @Override
    public boolean canTeleport(Player player) {
        return false;
    }

    @Override
    public boolean canAttack(Player attacker, Entity target) {
        return true;
    }

    @Override
    public void defeated(Player player, Entity entity) {

    }

    @Override
    public boolean canTrade(Player player, Player target) {
        return true;
    }

    @Override
    public boolean isMulti(Entity entity) {
        return true;
    }

    @Override
    public boolean canEat(Player player, int itemId) {
        return true;
    }

    @Override
    public boolean canDrink(Player player, int itemId) {
        return true;
    }

    @Override
    public void onPlayerRightClick(Player player, Player other, int option) {

    }

    @Override
    public boolean handleObjectClick(Player player, GameObject object, int option) {
        return true;
    }

    @Override
    public boolean handleNpcOption(Player player, NPC npc, int option) {
        return true;
    }

    @Override
    public boolean useInsideCheck() {
        return true;
    }

    @Override
    public boolean inside(Entity entity) {
        return entity.getAsPlayer().getTheatreInstance() != null && ROOM.transformArea(0, 0, 0, 0, entity.getAsPlayer().getTheatreInstance().getzLevel()).contains(entity.tile());
    }
}
