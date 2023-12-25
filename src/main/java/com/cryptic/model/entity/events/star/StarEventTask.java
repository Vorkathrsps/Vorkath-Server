package com.cryptic.model.entity.events.star;

import com.cryptic.core.task.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class StarEventTask extends Task {
    private static LocalDateTime lastDepletionTime;
    private static final StarEvent starEvent = StarEvent.getInstance();

    public StarEventTask() {
        super("StarEventTask", StarEvent.STAR_EVENT_INTERVAL, true);
        lastDepletionTime = LocalDateTime.now();
    }

    @Override
    protected void execute() {
        starEvent.startCrashedStarEvent();
    }

    public static void checkDepletionTask() {
        LocalDateTime currentTime = LocalDateTime.now();
        Optional<StarStage> activeStarStage = starEvent.getActiveStar()
            .map(CrashedStar::getStarStage);
        Optional<StarStage> nextStage = activeStarStage.map(StarStage::getNextStage);

        if (starEvent.getActiveStar().isPresent()) {
            int dustCount = starEvent.getActiveStar().get().getDustCount();
            if (dustCount == 325) {
                if (nextStage.isPresent() && nextStage.get() != StarStage.ONE) {
                    starEvent.getActiveStar().get().setStarStage(nextStage.get());
                    starEvent.getActiveStar().get().depleteAndSetNextStage(nextStage.get().getObjectId());
                } else {
                    starEvent.terminateActiveStar();
                }
                lastDepletionTime = currentTime;
            } else {
                if (lastDepletionTime.until(currentTime, ChronoUnit.MINUTES) >= 15) {
                    if (nextStage.isPresent() && nextStage.get() != StarStage.ONE) {
                        starEvent.getActiveStar().get().setStarStage(nextStage.get());
                        starEvent.getActiveStar().get().depleteAndSetNextStage(nextStage.get().getObjectId());
                    } else {
                        starEvent.terminateActiveStar();
                    }
                    lastDepletionTime = currentTime;
                }
            }
        }
    }
}
