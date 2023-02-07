package com.aelous.utility;

import com.aelous.model.World;

/**
 * @author Patrick van Elderen | March, 10, 2021, 09:45
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class TickDelay {

    private long end;

    public void reset() {
        end = 0;
    }

    public void setEnd(long newEnd) {
        end = newEnd;
    }

    public void delay(int ticks) {
        end = World.getWorld().getEnd(ticks);
    }

    public void delaySeconds(int seconds) {
        delay(Utils.secondsToTicks(seconds));
    }

    public void addDelaySeconds(int seconds) {
        end += Utils.secondsToTicks(seconds);
    }


    public boolean isDelayed() {
        return !World.getWorld().isPast(end);
    }

    public boolean isDelayed(int extra) {
        return !World.getWorld().isPast(end + extra);
    }

    public int remaining() {
        return (int) (end - World.getWorld().currentTick());
    }

    public int remainingToMins() {
        return remaining() / (1000 * 60 / 600);
    }

    public boolean finished() {
        return World.getWorld().isPast(end);
    }
}
