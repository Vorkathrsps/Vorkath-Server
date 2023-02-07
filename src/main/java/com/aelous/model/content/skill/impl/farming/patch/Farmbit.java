package com.aelous.model.content.skill.impl.farming.patch;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.utility.timers.TimerKey;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Bart on 6/2/2016.
 */
public enum Farmbit {

    CAMELOT_ALLOTMENT_SOUTH(AttributeKey.FARMSTATE_CAMELOT, 8, 11062, 529, TimerKey.FM_CAMELOT_ALLOT_SOUTH, 8553),
    CAMELOT_ALLOTMENT_NORTH(AttributeKey.FARMSTATE_CAMELOT, 0, 11062, 529, TimerKey.FM_CAMELOT_ALLOT_NORTH, 8552),
    CAMELOT_HERBS(AttributeKey.FARMSTATE_CAMELOT, 24, 11062, 529, TimerKey.FM_CAMELOT_HERBS, 8151),
    CAMELOT_FLOWERS(AttributeKey.FARMSTATE_CAMELOT, 16, 11062, 529, TimerKey.FM_CAMELOT_FLOWER, 7848),

    ARDY_ALLOTMENT_SOUTH(AttributeKey.FARMSTATE_ARDOUGNE, 8, 10548, 529, TimerKey.FM_ARDY_ALLOT_SOUTH, 8555),
    ARDY_ALLOTMENT_NORTH(AttributeKey.FARMSTATE_ARDOUGNE, 0, 10548, 529, TimerKey.FM_ARDY_ALLOT_NORTH, 8554),
    ARDY_HERBS(AttributeKey.FARMSTATE_ARDOUGNE, 24, 10548, 529, TimerKey.FM_ARDY_HERBS, 8152),
    ARDY_FLOWERS(AttributeKey.FARMSTATE_ARDOUGNE, 16, 10548, 529, TimerKey.FM_ARDY_FLOWER, 7849),

    FALLY_ALLOTMENT_SOUTH(AttributeKey.FARMSTATE_FALADOR, 8, 12083, 529, TimerKey.FM_FALLY_ALLOT_SOUTH, 8551),
    FALLY_ALLOTMENT_NORTH(AttributeKey.FARMSTATE_FALADOR, 0, 12083, 529, TimerKey.FM_FALLY_ALLOT_NORTH, 8550),
    FALLY_HERBS(AttributeKey.FARMSTATE_FALADOR, 24, 12083, 529, TimerKey.FM_FALLY_HERBS, 8150),
    FALLY_FLOWERS(AttributeKey.FARMSTATE_FALADOR, 16, 12083, 529, TimerKey.FM_FALLY_FLOWER, 7847),

    CANIFIS_ALLOTMENT_SOUTH(AttributeKey.FARMSTATE_CANIFIS, 8, 14391, 529, TimerKey.FM_CANIFIS_ALLOT_SOUTH, 8557),
    CANIFIS_ALLOTMENT_NORTH(AttributeKey.FARMSTATE_CANIFIS, 0, 14391, 529, TimerKey.FM_CANIFIS_ALLOT_NORTH, 8556),
    CANIFIS_HERBS(AttributeKey.FARMSTATE_CANIFIS, 24, 14391, 529, TimerKey.FM_CANIFIS_HERBS, 8153),
    CANIFIS_FLOWERS(AttributeKey.FARMSTATE_CANIFIS, 16, 14391, 529, TimerKey.FM_CANIFIS_FLOWER, 7850)

	/*ZEAH_ALLOTMENT_SOUTH(AttributeKey.FARMSTATE_ZEAH, 8, 7222, 529, TimerKey.FM_ZEAH_ALLOT_SOUTH, 27114),
	ZEAH_ALLOTMENT_NORTH(AttributeKey.FARMSTATE_ZEAH, 0, 7222, 529, TimerKey.FM_ZEAH_ALLOT_NORTH, 27113),
	ZEAH_HERBS(AttributeKey.FARMSTATE_ZEAH, 24, 7222, 529, TimerKey.FM_ZEAH_HERBS, 27115),
	ZEAH_FLOWERS(AttributeKey.FARMSTATE_ZEAH, 16, 7222, 529, TimerKey.FM_ZEAH_FLOWER, 27111),*/;

    public AttributeKey attrib;
    public int bitStart, visibleRegion, varp, obj;
    public TimerKey timer;

    Farmbit(AttributeKey attrib, int bitStart, int visibleRegion, int varp, TimerKey timer, int obj) {
        this.attrib = attrib;
        this.bitStart = bitStart;
        this.visibleRegion = visibleRegion;
        this.varp = varp;
        this.timer = timer;
        this.obj = obj;
    }

    //TODo confirm with Jak
    public static List<Farmbit> ALLOTMENTS = Arrays.asList(CAMELOT_ALLOTMENT_NORTH, CAMELOT_ALLOTMENT_SOUTH,
        ARDY_ALLOTMENT_NORTH, ARDY_ALLOTMENT_SOUTH,
        FALLY_ALLOTMENT_NORTH, FALLY_ALLOTMENT_SOUTH,
        CANIFIS_ALLOTMENT_NORTH, CANIFIS_ALLOTMENT_SOUTH/*,ZEAH_ALLOTMENT_NORTH, ZEAH_ALLOTMENT_SOUTH*/);

    public static List<Farmbit> HERBS = Arrays.asList(CAMELOT_HERBS, ARDY_HERBS, FALLY_HERBS, CANIFIS_HERBS/*, ZEAH_HERBS*/);

    public static List<Farmbit> FLOWERS = Arrays.asList(CAMELOT_FLOWERS, ARDY_FLOWERS, FALLY_FLOWERS, CANIFIS_FLOWERS/*, ZEAH_FLOWERS*/);
}
