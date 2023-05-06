package com.aelous.model.phase;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Origin
 * @Date: 5/6/2023
 */
public class Phase {
    @Getter @Setter
    PhaseStage stage;
    public Phase(PhaseStage stage) {
        this.stage = stage;
    }
}
