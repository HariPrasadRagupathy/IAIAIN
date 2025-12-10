package com.hp.iaiain.core.logging

interface Logger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warn(tag: String, message: String, throwable: Throwable? = null)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Simple console logger implementation
 */
class ConsoleLogger : Logger {
    override fun debug(tag: String, message: String) {
        println("DEBUG [$tag] $message")
    }

    override fun info(tag: String, message: String) {
        println("INFO [$tag] $message")
    }

    override fun warn(tag: String, message: String, throwable: Throwable?) {
        println("WARN [$tag] $message")
        throwable?.printStackTrace()
    }

    override fun error(tag: String, message: String, throwable: Throwable?) {
        println("ERROR [$tag] $message")
        throwable?.printStackTrace()
    }
}

/**
 * Global logger instance
 */
object AppLogger {
    private var logger: Logger = ConsoleLogger()

    fun setLogger(newLogger: Logger) {
        logger = newLogger
    }

    fun debug(tag: String, message: String) = logger.debug(tag, message)
    fun info(tag: String, message: String) = logger.info(tag, message)
    fun warn(tag: String, message: String, throwable: Throwable? = null) =
        logger.warn(tag, message, throwable)
    fun error(tag: String, message: String, throwable: Throwable? = null) =
        logger.error(tag, message, throwable)
}

