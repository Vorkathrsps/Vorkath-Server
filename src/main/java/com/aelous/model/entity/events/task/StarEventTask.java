package com.aelous.model.entity.events.task;

import com.aelous.core.task.Task;
import com.aelous.model.entity.events.StarEvent;
import com.aelous.model.entity.events.stage.StarStage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class StarEventTask extends Task {
    static private LocalDateTime lastDepletionTime;
    static StarEvent starEvent = StarEvent.getInstance();

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
        StarStage starStage = starEvent.getActiveStar().get().getStarStage();
        StarStage nextStage = starStage.getNextStage();
        if (starEvent.getActiveStar().isPresent()) {
            if (starEvent.getActiveStar().get().getDustCount() == 325) {
                if (nextStage != StarStage.ONE) {
                    starEvent.getActiveStar().get().setStarStage(nextStage);
                    starEvent.getActiveStar().get().depleteAndSetNextStage(nextStage.getObjectId());
                } else {
                    starEvent.terminateActiveStar();
                }
                lastDepletionTime = currentTime;
            } else {
                if (lastDepletionTime.until(currentTime, ChronoUnit.MINUTES) >= 15) {
                    if (nextStage != StarStage.ONE) {
                        starEvent.getActiveStar().get().setStarStage(nextStage);
                        starEvent.getActiveStar().get().depleteAndSetNextStage(nextStage.getObjectId());
                    } else {
                        starEvent.terminateActiveStar();
                    }
                    lastDepletionTime = currentTime;
                }
            }
        }
    }
}

