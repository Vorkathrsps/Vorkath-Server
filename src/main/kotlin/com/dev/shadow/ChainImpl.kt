package com.dev.shadow

import com.cryptic.core.task.Task
import com.cryptic.core.task.TaskManager
import com.cryptic.model.entity.Entity
import com.cryptic.model.entity.player.Player
import com.cryptic.model.map.position.Area
import com.cryptic.model.map.position.Tile
import com.cryptic.utility.chainedwork.Chain
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.function.BooleanSupplier
import java.util.function.Consumer

/**
 * A utility class to chain code functions together which depend on delays like OSS's `delay(1)`.
 * The internal clock/delay system is built upon [TaskManager]
 *
 * <br></br>Has similiar fields to [Action] but doesnt have Mob as fixed parent generic type
 *
 * @author Jak | Shadowrs tardisfan121@gmail.com
 * @version 25/4/2020
 */
class ChainImpl<T> : Chain<T>() {
    /**
     * A key assocated with this chain, any type is accepted.
     */
    var owner: T? = null

    /**
     * The name of this task, optional
     */
    var name: String? = null

    /**
     * the next chain to execute one this one completes.
     */
    var nextNode: ChainImpl<T>? = null

    fun getCancelCondition(): BooleanSupplier? {
        return cancelCondition
    }

    fun setCancelCondition(cancelCondition: BooleanSupplier?) {
        this.cancelCondition = cancelCondition
    }

    fun getExecuteCondition(): BooleanSupplier? {
        return executeCondition
    }

    fun setExecuteCondition(executeCondition: BooleanSupplier?) {
        this.executeCondition = executeCondition
    }

    private var cancelCondition: BooleanSupplier? = null

    private var executeCondition: BooleanSupplier? = null

    override fun getTask(): Task? {
        return task
    }

    fun setTask(task: Task?) {
        this.task = task
    }

    fun getWork(): Any? {
        return work
    }

    fun setWork(work: Any?) {
        this.work = work
    }

    /**
     * The actual running task this chain represents.
     */
    private var task: Task? = null

    private var cycleDelay: Int = 1

    /**
     * The function to run inside
     * <br></br>types allowed = [Runnable], Consumer[<]
     */
    private var work: Any? = null

    /**
     * If this task will repeat. Normally this task chain will execute ONCE and stop straight away.
     */
    var repeats: Boolean = false

    fun isInterrupted(): Boolean {
        return interrupted
    }

    override fun setInterrupted(interrupted: Boolean) {
        this.interrupted = interrupted
    }

    /**
     * When interrupted, when [Task.onStop] runs it won't call the after hook.
     */
    private var interrupted: Boolean = false

    private var fromLocation: List<StackWalker.StackFrame>? = null

    private fun findSource() {
    }

    override fun source(): String {
        return ""
    }

    override fun name(name: String): Chain<T> {
        this.name = name
        return this
    }

    /**
     * these are predicates which indicate a state in which the chain should break. Example, if an NPC is dead/despawned
     * @param predicates
     */
    override fun cancelWhen(predicates: BooleanSupplier): Chain<T> {
        cancelCondition = predicates
        return this
    }

    override fun waitForTile(tile: Tile, work: Runnable): Chain<T> {
        if (this.work != null) {
            nextNode = bound(owner) as ChainImpl<T> // make a new one
            nextNode!!.work = work // init work
            nextNode!!.name = name // re-use the name
            nextNode!!.executeCondition =
                BooleanSupplier { (owner as Player?)!!.tile().x == tile.x && (owner as Player?)!!.tile().y == tile.y }
            nextNode!!.cycleDelay = 1
            nextNode!!.repeats = true
            nextNode!!.findSource()
            return nextNode!!
        }
        executeCondition =
            BooleanSupplier { (owner as Entity?)!!.tile().x == tile.x && (owner as Entity?)!!.tile().y == tile.y }
        cycleDelay = 1
        this.work = work
        repeats = true
        startChainExecution()
        return this
    }

    override fun waitForArea(area: Area, work: Runnable): Chain<T> {
        if (this.work != null) {
            nextNode = bound(owner) as ChainImpl<T> // make a new one
            nextNode!!.work = work // init work
            nextNode!!.name = name // re-use the name
            nextNode!!.executeCondition =
                BooleanSupplier { (owner as Player?)!!.tile().x == area.x1 && (owner as Player?)!!.tile().y == area.y1 }
            nextNode!!.cycleDelay = 1
            nextNode!!.repeats = true
            nextNode!!.findSource()
            return nextNode!!
        }
        executeCondition =
            BooleanSupplier { (owner as Entity?)!!.tile().x == area.x2 && (owner as Entity?)!!.tile().y == area.y2 }
        cycleDelay = 1
        this.work = work
        repeats = true
        startChainExecution()
        return this
    }

    /**
     * repeats forever every tickBetweenLoop ticks. Only stops when CONDITION evaluates to true or stop or interrupt is called
     * @param tickBetweenLoop
     * @param condition
     * @return
     */
    override fun waitUntil(tickBetweenLoop: Int, condition: BooleanSupplier, work: Runnable): Chain<T> {
        if (this.work != null) {
            nextNode = bound(owner) as ChainImpl<T> // make a new one
            nextNode!!.work = work // init work
            nextNode!!.name = name // re-use the name
            nextNode!!.executeCondition = condition
            nextNode!!.cycleDelay = tickBetweenLoop
            nextNode!!.repeats = true
            nextNode!!.findSource()
            return nextNode!!
        }
        executeCondition = condition
        cycleDelay = tickBetweenLoop
        this.work = work
        repeats = true
        startChainExecution()
        return this
    }

    /**
     * repeats forever every tickBetweenLoop ticks. Must be stopped MANUALLY by calling task.stop from the consumer.
     * <br></br> Check usages for examples.
     * @param tickBetweenLoop
     * @return
     */
    override fun repeatingTask(tickBetweenLoop: Int, work: Consumer<Task>): Chain<T> {
        if (this.work != null) {
            nextNode = bound(owner) as ChainImpl<T> // make a new one
            nextNode!!.work = work // init work
            nextNode!!.name = name // re-use the name
            nextNode!!.cycleDelay = tickBetweenLoop
            nextNode!!.repeats = true
            nextNode!!.findSource()
            return nextNode!!
        }
        this.work = work
        cycleDelay = tickBetweenLoop
        repeats = true
        startChainExecution()
        return this
    }

    /**
     * The first function to run, kicks off the internal [Task] via [TaskManager]. Only runs ONCE.
     */
    override fun runFn(startAfterTicks: Int, work: Runnable): Chain<T> {
        var startAfterTicks = startAfterTicks
        if (startAfterTicks < 1) {
            logger.error("bad code", RuntimeException("StartAfterTicks must be greater than 0. change to 1 or higher."))
            startAfterTicks = 1
        }
        if (this.work != null) {
            return then(startAfterTicks, work)
        }
        cycleDelay = startAfterTicks
        this.work = work
        startChainExecution()
        return this
    }

    override fun delay(startAfterTicks: Int, work: Runnable): Chain<T> {
        return runFn(startAfterTicks, work)
    }

    // this is private on purpose, internal class use only
    private fun startChainExecution() {
        if (cycleDelay == 0) {
            // run instantly
            attemptWork()
        } else {
            task = object : Task(if (name != null) name else "", cycleDelay, false) {
                override fun execute() {
                    attemptWork()
                    if (!repeats) stop()
                }

                override fun onStop() {
                    if (interrupted) {
                        logger.debug("chain interrupted, wont continue to next. context: $owner")
                        return
                    }
                    super.onStop()
                    if (nextNode != null) {
                        nextNode!!.startChainExecution()
                    }
                }
            }.bind(owner)
            // just cloning exists fromLocation which should filter properly already
            task!!.parent = this
            TaskManager.submit(task)
        }
    }

    override fun __TESTING_ONLY_doWork() {
    }

    private fun attemptWork() {
        if (interrupted) {
            logger.debug("chain interrupted, wont continue to next . context: $owner")
            return
        }
        if (cancelCondition != null && cancelCondition!!.asBoolean) {
            if (DEBUG_CHAIN) {
                println("[DEBUG_CHAIN] Cancel condition was True, stopping work for $owner")
            }
            repeats = false // condition to cancel was true, stop looping
            nextNode = null
            return
        }
        if (executeCondition != null) {
            if (!executeCondition!!.asBoolean) {
                if (DEBUG_CHAIN) {
                    println("[DEBUG_CHAIN] execution condition false. Won't run for $owner")
                }
                return
            }
            repeats = false // condition to execute the task (aka stop looping) is true
        }
        if (work != null) {
            if (work is Runnable) (work as Runnable).run()
            else if (work is Consumer<*>) (work as Consumer<Task?>).accept(task)
            else {
                System.err.println("Unknown workload type: " + work!!.javaClass)
            }
        }
    }

    /**
     * Adds a function which is run immidiately after the previous chain completes
     */
    override fun then(nextWork: Runnable): Chain<T> {
        if (this.work == null) {
            return runFn(1, nextWork)
        }
        nextNode = bound(owner) as ChainImpl<T> // make a new one
        nextNode!!.work = nextWork // init work
        nextNode!!.name = name // re-use the name
        nextNode!!.findSource()
        return nextNode!!
    }

    /**
     * Adds a function which will execute X ticks after the previous work completes.
     */
    override fun then(startDelay: Int, nextWork: Runnable): Chain<T> {
        if (this.work == null) {
            return runFn(startDelay, nextWork)
        }
        nextNode = bound(owner) as ChainImpl<T> // make a new one
        nextNode!!.work = nextWork // init work
        nextNode!!.name = name // re-use the name
        nextNode!!.cycleDelay = startDelay
        nextNode!!.findSource()
        return nextNode!!
    }

    override fun thenCancellable(nextWork: Runnable): Chain<T> {
        if (this.work == null) {
            return runFn(1, nextWork)
        }
        nextNode = bound(owner) as ChainImpl<T> // make a new one
        nextNode!!.work = nextWork // init work
        nextNode!!.name = name // re-use the name
        nextNode!!.cancelCondition = cancelCondition
        nextNode!!.findSource()
        return nextNode!!
    }

    override fun thenCancellable(startDelay: Int, nextWork: Runnable): Chain<T> {
        if (this.work == null) {
            return runFn(startDelay, nextWork)
        }
        nextNode = bound(owner) as ChainImpl<T> // make a new one
        nextNode!!.work = nextWork // init work
        nextNode!!.name = name // re-use the name
        nextNode!!.cycleDelay = startDelay
        nextNode!!.cancelCondition = cancelCondition
        if (nextNode!!.cancelCondition == null) {
            System.err.println("warning: using thenCancellable but no cancel condition exists")
        }
        nextNode!!.findSource()
        return nextNode!!
    }

    /**
     * see [Chain.repeatingTask].
     * @param tickBetweenLoop
     * @param condition The condition TRUE when the task will stop/complete. Runs forever until true.
     * @return
     */
    override fun repeatIf(
        tickBetweenLoop: Int,
        condition: BooleanSupplier /* Runnable WORK is integrated into CONDITION*/
    ): Chain<T> {
        if (this.work == null) {
            work = null // SEE CONDITION - condition IS the workload! intrgrated into one method for execute+evaluate
            name = name // re-use the name
            cancelCondition = condition // NOTE : this is actually a 2 in 1 version of work.
            // cancel condition will evaluate and itself is the Runnable Work.
            cycleDelay = tickBetweenLoop
            repeats = true
            findSource()
            return this
        }
        nextNode = bound(owner) as ChainImpl<T> // make a new one
        nextNode!!.work =
            null // SEE CONDITION - condition IS the workload! intrgrated into one method for execute+evaluate
        nextNode!!.name = name // re-use the name
        nextNode!!.cancelCondition = condition // NOTE : this is actually a 2 in 1 version of work.
        // cancel condition will evaluate and itself is the Runnable Work.
        nextNode!!.cycleDelay = tickBetweenLoop
        nextNode!!.repeats = true
        nextNode!!.findSource()
        return nextNode!!
    }

    override fun info(): String {
        return ""
    }

    override fun sourceMethodsOnly(): String {
        return ""
    }

    override fun id(): String {
        return "Chain@" + Integer.toHexString(hashCode())
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChainImpl::class.java)

        var DEBUG_CHAIN: Boolean = false

        fun <T> noCtx(): Chain<T?> {
            return bound(null)
        }

        /**
         * no context - same as [.unbound]
         */
        fun noCtxRepeat(): Chain<*> {
            return bound<Any?>(null).name("repeatingChain")
        }

        /**
         * no context/owner task.
         * @param startAfterTicks
         * @param work
         * @return
         */
        fun runGlobal(startAfterTicks: Int, work: Runnable?): Chain<*> {
            return bound<Any?>(null).runFn(startAfterTicks, work)
        }
    }
}
