package com.cryptic.core.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public final class EventWorker {

    public static final ContinuationScope CONTINUATION_SCOPE =
        new ContinuationScope(EventWorker.class.getName());

    private static final Queue<Event<?>> events = new ConcurrentLinkedDeque<>();

    public static void process() {
        events.removeIf(event -> !event.tick());
    }

    public static boolean startEvent(Event<?> event) {
        if (!event.tick()) {
            return true;
        }
        return events.offer(event);
    }

}
