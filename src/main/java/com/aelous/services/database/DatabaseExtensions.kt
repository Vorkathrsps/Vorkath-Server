package com.aelous.services.database

import com.aelous.GameServer
import java.sql.Connection
import java.util.function.Consumer

/**
 * this immidiately submits the work to db.
 */
fun query(con: VoidDbt.(Connection) -> Unit) {
    object: VoidDbt() {
        override fun executeVoid(connection: Connection) {
            this.connection = connection
            con(connection)
        }
    }.submit()
}

abstract class VoidDbt : VoidDatabaseTransaction() {
    var connection: Connection? = null
}

/**
 * create a query. you must call #submit manually on this result.
 */
fun makeQuery(con: VoidDbt.(Connection) -> Unit): VoidDbt {
    return object: VoidDbt() {
        override fun executeVoid(connection: Connection) {
            this.connection = connection
            con(connection)
        }
    }
}

/**
 * create a query. you must call #submit manually on this result.
 */
fun <T> query(con: Dbt<T>.(Connection) -> T): Dbt<T> {
    return object: Dbt<T>() {
        override fun execute(connection: Connection): T {
            this.connection = connection
            return con(connection)
        }
    }
}

abstract class Dbt<T> : DatabaseTransaction<T>() {
    var connection: Connection? = null
}

/**
 * submits query to db for execution
 */
@JvmName("submitT")
fun <T> DatabaseTransaction<T>.submit(): DatabaseTransaction<T> {
    GameServer.getDatabaseService().submit(this)
    return this
}

/**
 * submits query to db for execution
 */
fun DatabaseTransaction<out Any?>.submit(): DatabaseTransaction<out Any?> {
    GameServer.getDatabaseService().submit(this)
    return this
}

/**
 * submits query to db for execution. consume accepts the query's result and runs code thread-safely on gamethread.
 */
fun <T> DatabaseTransaction<T>.submit(consumer: Consumer<T>): DatabaseTransaction<T> {
    GameServer.getDatabaseService().submit(this, consumer)
    return this
}

/**
 * submits query to db for execution. consume accepts the query's result and runs code thread-safely on gamethread.
 */
fun <T> DatabaseTransaction<T>.submit(consumer: (T) -> Unit): DatabaseTransaction<T> {
    GameServer.getDatabaseService().submit(this, consumer)
    return this
}

/**
 * submits query to db for execution. consume accepts the query's result and runs code thread-safely on gamethread.
 */
fun <T> DatabaseTransaction<T>.onDatabase(database: DatabaseService, consumer: (T) -> Unit): DatabaseTransaction<T> {
    database.submit(this, consumer)
    return this
}
