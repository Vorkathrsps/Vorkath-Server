package com.cryptic.utility.chainedwork;

import com.cryptic.model.action.Action;
import com.cryptic.core.task.Task;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.stream.Collectors;


/**
 * A utility class to chain code functions together which depend on delays like OSS's {@code delay(1)}.
 * The internal clock/delay system is built upon {@link TaskManager}
 *
 * <br>Has similiar fields to {@link Action} but doesnt have Mob as fixed parent generic type
 *
 * @author Jak | Shadowrs tardisfan121@gmail.com
 * @version 25/4/2020
 */
@SuppressWarnings("ALL")
public class Chain<T> {

    private static final Logger logger = LogManager.getLogger(Chain.class);

    @FunctionalInterface
    public interface ChainBuilder<T> {
        Chain<T> build(T owner);
    }

    public static ChainBuilder BUILDER;


    //public StackTraceElement fromLocation;
    public static <T> Chain<T> bound(T owner) {
        //noinspection unchecked
        return BUILDER.build(owner);
    }

    public static <T> Chain<T> noCtx() {
        return bound(null);
    }

    /**
     * no context - same as {@link #unbound()}
     */
    public static Chain<?> noCtxRepeat() {
        return bound(null).name("repeatingChain");
    }

    private void findSource() {
    }

    public String source() {
        return null;
    }

    public Chain<T> name(String name) {
        return null;
    }

    /**
     * these are predicates which indicate a state in which the chain should break. Example, if an NPC is dead/despawned
     * @param predicates
     */
    public Chain<T> cancelWhen(BooleanSupplier predicates) {
        return null;
    }

    public Chain<T> waitForTile(Tile tile, Runnable work) {
        return null;
    }

    public Chain<T> waitForArea(Area area, Runnable work) {
        return null;
    }

    /**
     * repeats forever every tickBetweenLoop ticks. Only stops when CONDITION evaluates to true or stop or interrupt is called
     * @param tickBetweenLoop
     * @param condition
     * @return
     */
    public Chain<T> waitUntil(int tickBetweenLoop, BooleanSupplier condition, Runnable work) {
        return null;
    }

    /**
     * repeats forever every tickBetweenLoop ticks. Must be stopped MANUALLY by calling task.stop from the consumer.
     * <br> Check usages for examples.
     * @param tickBetweenLoop
     * @return
     */
    public Chain<T> repeatingTask(int tickBetweenLoop, Consumer<Task> work) {
        return null;
    }

    /**
     * no context/owner task.
     * @param startAfterTicks
     * @param work
     * @return
     */
    public static Chain runGlobal(int startAfterTicks, Runnable work) {
        return null;
    }

    /**
     * The first function to run, kicks off the internal {@link Task} via {@link TaskManager}. Only runs ONCE.
     */
    public Chain<T> runFn(int startAfterTicks, Runnable work) {
        return null;
    }

    public Chain<T> delay(int startAfterTicks, Runnable work) {
        return null;
    }

    // this is private on purpose, internal class use only
    private void startChainExecution() {

    }

    public void __TESTING_ONLY_doWork() {
    }

    private void attemptWork() {

    }

    /**
     * Adds a function which is run immidiately after the previous chain completes
     */
    public Chain<T> then(Runnable nextWork) {
        return null;
    }

    /**
     * Adds a function which will execute X ticks after the previous work completes.
     */
    public Chain<T> then(int startDelay, Runnable nextWork) {
        return null;
    }

    public Chain<T> thenCancellable(Runnable nextWork) {
        return null;
    }

    public Chain<T> thenCancellable(int startDelay, Runnable nextWork) {
        return null;
    }

    /**
     * see {@link Chain#repeatingTask(int, Consumer)}.
     * @param tickBetweenLoop
     * @param condition The condition TRUE when the task will stop/complete. Runs forever until true.
     * @return
     */
    public Chain<T> repeatIf(int tickBetweenLoop, BooleanSupplier condition/* Runnable WORK is integrated into CONDITION*/) {
        return null;
    }

    public String info() {
        return null;
    }

    public String sourceMethodsOnly() {
        return null;
    }

    public String id() {
        return null;
    }

    public void setNextNode(Chain<T> nextNode) {
    }

    public void setOnInterrupted(Runnable onInterrupted) {
    }

    public boolean getInterrupted() {
        return false;
    }

    public void setInterrupted(boolean b) {
    }
    public Task getTask() {
        return null;
    }
}
