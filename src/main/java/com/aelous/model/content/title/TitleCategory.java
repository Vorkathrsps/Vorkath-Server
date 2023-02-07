package com.aelous.model.content.title;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by Kaleem on 25/03/2018.
 */
public enum TitleCategory {
    PKING(61391, 61380),
    PVMING(61392, 61381),
    OTHER(61393, 61382);

    public static final Set<TitleCategory> TITLE_CATEGORY_SET = EnumSet.allOf(TitleCategory.class);

    public static Optional<TitleCategory> find(Predicate<TitleCategory> predicate) {
        return TITLE_CATEGORY_SET.stream().filter(predicate).findAny();
    }

    public static boolean perform(Predicate<TitleCategory> predicate, Consumer<TitleCategory> action) {
        Optional<TitleCategory> optional = find(predicate);
        optional.ifPresent(action);
        return optional.isPresent();
    }

    private final int buttonId;
    private final int interfaceId;

    TitleCategory(int buttonId, int interfaceId) {
        this.buttonId = buttonId;
        this.interfaceId = interfaceId;
    }

    public int getButtonId() {
        return buttonId;
    }

    public int getInterfaceId() {
        return interfaceId;
    }
}
