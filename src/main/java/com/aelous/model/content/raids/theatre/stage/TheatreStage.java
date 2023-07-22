package com.aelous.model.content.raids.theatre.stage;

import lombok.Getter;

public enum TheatreStage {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6);

    @Getter public final int stage;

    TheatreStage(int stage) {
        this.stage = stage;
    }
}
