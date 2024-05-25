package com.cryptic.model.content.skill.impl.prayer;

public enum Ashes {
    FIENDISH_ASHES(25766, 5.0),
    VILE_ASHES(25769, 12.5),
    MALICIOUS_ASHES(25772, 32.5),
    ABYSSAL_ASHES(25775, 42.5),
    INFERNAL_ASHES(25778, 55.0);

    public final int id;
    public final double experience;

    Ashes(int id, double experience) {
        this.id = id;
        this.experience = experience;
    }

    static final Ashes[] values = Ashes.values();

    public static Ashes get(int itemId) {
        for (Ashes ash : values) {
            if (itemId == ash.id) {
                return ash;
            }
        }
        return null;
    }
}
