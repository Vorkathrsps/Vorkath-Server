package com.aelous.model.entity.combat.method.impl.npcs.bosses.zulrah;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Bart on 3/6/2016.
 *
 * Represents a single phase, including form and positioning.
 */
public class ZulrahPhase {

    public ZulrahForm getForm() {
        return form;
    }

    public ZulrahPosition getZulrahPosition() {
        return zulrahPosition;
    }

    public List<ZulrahConfig> getConfig() {
        return config;
    }

    private ZulrahForm form;
    private ZulrahPosition zulrahPosition;
    private List<ZulrahConfig> config = new ArrayList<>(0);

    public boolean hasConfig(ZulrahConfig cfg) {
        return config.contains(cfg);
    }

    public ZulrahPhase(ZulrahForm form, ZulrahPosition zulrahPosition, List<ZulrahConfig> config) {
        this.form = form;
        this.zulrahPosition = zulrahPosition;
        this.config = config;
    }

    public ZulrahPhase(ZulrahForm form, ZulrahPosition zulrahPosition) {
        this.form = form;
        this.zulrahPosition = zulrahPosition;
    }

    @Override
    public String toString() {
        return "ZulrahPhase{" +
            "form=" + form +
            ", zulrahPosition=" + zulrahPosition +
            ", config=" + Arrays.toString(config.stream().map(ZulrahConfig::toString).toArray()) +
            '}';
    }
}
