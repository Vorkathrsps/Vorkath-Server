package com.cryptic.model.entity.combat.method.impl.npcs.fightcaves;

import com.cryptic.model.content.minigames.impl.fight_caves.FightCavesMinigame;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.cryptic.model.entity.attributes.AttributeKey.ATTACKING_ZONE_RADIUS_OVERRIDE;
import static com.cryptic.utility.Utils.random;

/**
 * @author Origin | December, 23, 2020, 14:35
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TzTokJad extends NPC {

    private final ArrayList<YtHurKot> healers = new ArrayList<>();

    public TzTokJad(int id, Tile tile) {
        super(id, tile);
        this.getCombatInfo().setAggressive(true);
        this.respawns(false);
        this.putAttrib(ATTACKING_ZONE_RADIUS_OVERRIDE, 30);
    }

    /**
     * Spawn healers.
     */
    public void spawnHealers(Player player) {

        if(player.getMinigame() == null || !(player.getMinigame() instanceof FightCavesMinigame)) {
            return;
        }

        if (!healers.isEmpty()) {
            return;
        }

        IntStream.range(0, 4 - healers.size()).forEach(i -> {
            Tile tile = FightCavesMinigame.COORDINATES[random(FightCavesMinigame.COORDINATES.length)].transform(0, 0, tile().getLevel());

            YtHurKot npc = new YtHurKot(NpcIdentifiers.YTHURKOT, tile,this);

            healers.add(npc);
            FightCavesMinigame minigame = (FightCavesMinigame) player.getMinigame();
            minigame.addNpc(npc);
            World.getWorld().registerNpc(npc);
            npc.respawns(false);
        });
    }

    /**
     * Removes the healer from the healers list.
     *
     * @param healer
     */
    public void removeHealer(YtHurKot healer) {
        healers.remove(healer);
    }
}
