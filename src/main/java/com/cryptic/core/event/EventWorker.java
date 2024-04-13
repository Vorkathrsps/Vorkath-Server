package com.cryptic.core.event;

import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public final class EventWorker {

    public static final ContinuationScope CONTINUATION_SCOPE =
        new ContinuationScope(EventWorker.class.getName());

    private static final Queue<Event<?>> events = new ConcurrentLinkedDeque<>();

    public static void process() {
        events.removeIf(event -> !event.tick());
    }

/*    @Nullable
    public static <T, E extends Event<T>> E startEvent(
        Consumer<Event<T>> eventConsumer,
        @Nullable T context) {
        final E event = createEvent(eventConsumer, context);
        if (!event.tick()) {
            return event;
        }
        if (events.offer(event)) {
            return event;
        }
        return null;
    }*/

    public static boolean startEvent(Event<?> event) {
        if (!event.tick()) {
            return true;
        }
        return events.offer(event);
    }

}
