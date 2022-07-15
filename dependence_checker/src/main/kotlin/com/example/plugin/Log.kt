package com.example.plugin

import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

object Log {

    private var logger: Logger? = null

    fun init(logger: Logger) {
        if (Log.logger == null) {
            Log.logger = logger
        }
    }

    fun debug(msg: String) {
        println(msg)
    }

    fun info(msg: String) {
        println(msg)
    }

    fun waring(msg: String) {
        logger?.log(LogLevel.WARN, msg)
    }

    fun error(msg: String) {
        logger?.log(LogLevel.ERROR, msg)
    }


}