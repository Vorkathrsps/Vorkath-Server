package com.cryptic.model.content.mechanics.itemonitem;

public enum Items {
    ;

    public final int id;
    public final int[] conversion;

    Items(int id, int[] conversion) {
        this.id = id;
        this.conversion = conversion;
    }
}
