package com.cryptic.model.entity.events.star;

import lombok.Getter;

@Getter
public enum StarStage {
    ONE(41020),
    TWO(41021),
    THREE(41223),
    FOUR(41224),
    FIVE(41225),
    SIX(41226),
    SEVEN(41228),
    EIGHT(41229);

    private final int objectId;

    StarStage(int objectId) {
        this.objectId = objectId;
    }

    public StarStage getNextStage() {
        int nextIndex = (this.ordinal() + 1) % values().length;
        return values()[nextIndex];
    }
}
