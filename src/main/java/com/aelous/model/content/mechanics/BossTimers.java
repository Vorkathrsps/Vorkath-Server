package com.aelous.model.content.mechanics;

import com.aelous.model.entity.player.Player;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Created by Kaleem on 20/09/2017.
 */
public class BossTimers {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("mm:ss");

    public static final Set<String> TRACKED_NPCS = Set.of(
        "Aragog",
        "Callisto",
        "Cerberus",
        "Chaos Fanatic",
        "Corporeal Beast",
        "Crazy archaeologist",
        "Demonic gorilla",
        "Commander Zilyana",
        "Kree'arra",
        "K'ril Tsutsaroth",
        "General Graardor",
        "King Black Dragon",
        "Kraken",
        "Lizardman shaman",
        "Thermonuclear smoke devil",
        "Venenatis",
        "Vet'ion Reborn",
        "Scorpia",
        "Chaos Elemental",
        "Zulrah",
        "Vorkath",
        "Skotizo",
        "Zombies Champion",
        "Tekton",
        "Kalphite Queen",
        "Dagannoth Supreme",
        "Dagannoth Prime",
        "Dagannoth Rex",
        "Giant Mole",
        "Alchemical Hydra",
        "Corrupted Nechryarch",
        "TzTok-Jad"
    );

    private Map<String, Integer> times = new HashMap<>();

    public void submit(String name, int newTime, Player player) {
        if (!TRACKED_NPCS.contains(name)) return;
        if (newTime == 0) {
            return;
        }

        int original = get(name).orElse(Integer.MAX_VALUE);
        String formattedTime = " <col=ff0000>" + LocalTime.ofSecondOfDay(newTime).format(FORMAT) + "<col=0>";

        StringBuilder bldr = new StringBuilder();
        bldr.append("Fight duration:");
        bldr.append(formattedTime);

        if (newTime < original) {
            bldr.append(" (new personal best)");
            times.put(name, newTime);
        } else {
            String oldTimeFormatted = LocalTime.ofSecondOfDay(original).format(FORMAT);
            bldr.append(". Personal best: ").append(oldTimeFormatted);
        }

        player.message(bldr.toString());
        player.getCombat().getFightTimer().reset();
    }

    public Optional<Integer> get(String name) {
        return Optional.ofNullable(times.get(name));
    }

    public Map<String, Integer> getTimes() {
        return times;
    }

    public void setTimes(Map<String, Integer> times) {
        this.times = times;
    }

}
