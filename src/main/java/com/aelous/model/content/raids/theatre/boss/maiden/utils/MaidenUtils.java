package com.aelous.model.content.raids.theatre.boss.maiden.utils;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Area;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MaidenUtils {
    public static List<NPC> bloodOrbs = new ArrayList<>();
    public static List<NPC> nylocas = new ArrayList<>();
    public static List<NPC> orbSpawns = new ArrayList<>();
    public static final Area IGNORED = new Area(3185, 4444, 3189, 4449);
    public static final Area MAIDEN_AREA = new Area(3160, 4435, 3187, 4458);

}
