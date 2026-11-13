package ua.edu.lab3.mazegame.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.logging.ConsoleHandler
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord
import java.util.logging.Logger

/**
 * Централізований логер застосунку згідно з вимогами лабораторної роботи №3.
 * Налаштовує стандартний java.util.logging.Logger із власним форматуванням
 * та дублює потік у системний Logcat.
 */
object AppLogger {
    private const val GLOBAL_TAG = "MazeGameLogger"
    private val logger: Logger = Logger.getLogger(GLOBAL_TAG)

    init {
        logger.useParentHandlers = false
        logger.level = Level.ALL

        val handler = ConsoleHandler().apply {
            level = Level.ALL
            formatter = object : Formatter() {
                private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

                override fun format(record: LogRecord): String {
                    val timestamp = dateFormat.format(Date(record.millis))
                    return "[$timestamp] [${record.level.name}] ${record.message}\n"
                }
            }
        }
        logger.addHandler(handler)
        info("AppLogger initialized successfully.")
    }

    fun info(message: String) {
        logger.log(Level.INFO, message)
        Log.i(GLOBAL_TAG, message)
    }

    fun debug(message: String) {
        logger.log(Level.FINE, message)
        Log.d(GLOBAL_TAG, message)
    }

    fun warn(message: String) {
        logger.log(Level.WARNING, message)
        Log.w(GLOBAL_TAG, message)
    }

    fun error(message: String, throwable: Throwable? = null) {
        logger.log(Level.SEVERE, message, throwable)
        Log.e(GLOBAL_TAG, message, throwable)
    }
}
