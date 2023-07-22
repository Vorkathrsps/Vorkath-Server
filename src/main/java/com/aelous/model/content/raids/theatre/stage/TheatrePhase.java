package com.aelous.model.content.raids.theatre.stage;

import lombok.Getter;
import lombok.Setter;

public class TheatrePhase {
    @Getter @Setter TheatreStage stage;
    public TheatrePhase(TheatreStage stage) {
        this.stage = stage;
    }

}
