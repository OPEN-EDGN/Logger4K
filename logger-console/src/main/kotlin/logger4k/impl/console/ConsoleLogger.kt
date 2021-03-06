/*
 * Copyright (c) 2020, OpenEDGN. All rights reserved.
 * HOME Page: https://github.com/OpenEDGN
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package logger4k.impl.console

import com.github.openEdgn.logger4k.ILogger
import com.github.openEdgn.logger4k.LoggerLevel
import com.github.openEdgn.logger4k.SimpleLogger

class ConsoleLogger(override val name: String) : SimpleLogger() {
    override fun printLogger(date: Long, level: LoggerLevel, message: String) {
        printlnLog(date, level, message, null)
    }

    override fun printLogger(date: Long, level: LoggerLevel, message: String, exception: Throwable) {
        printlnLog(date, level, message, exception)
    }

    private fun printlnLog(date: Long, level: LoggerLevel, message: String, exception: Throwable?) {
        if (level.level < ConsoleLogConfig.loggerLevelInt) {
            return
        }
        val threadInfo = Thread.currentThread()
        ConsoleLogConfig.threadPool.execute {
            if (level.level >= LoggerLevel.WARN.level) {
                ConsoleLogConfig.error
            } else {
                ConsoleLogConfig.output
            }.println(
                if (exception != null) format(
                    name, date, level, threadInfo, message + "\r\n" +
                            ThrowableUtils.format(exception)
                ) else format(
                    name,
                    date,
                    level,
                    threadInfo,
                    message
                )
            )
        }

    }

    private fun format(
        name: String,
        date: Long,
        level: LoggerLevel,
        threadInfo: Thread,
        message: String
    ): String {
        val res = StringBuilder()
        res.append("")
            .append(ConsoleLogConfig.dateFormat.format(date))
            .append(" - ")
            .append(formatThreadName(threadInfo))
            .append("/")
            .append(level.name[0])
            .append(" - ")
            .append(name)
            .append(" -> ")
            .append(message)
        return res.toString()
    }

    private val threadNameLength = 12
    private fun formatThreadName(threadInfo: Thread): String {
        val tName = threadInfo.name
        return if (tName.length <= threadNameLength) {
            String.format("%-${threadNameLength}s", tName)
        } else {
            var replace = tName.replace(Regex("[a-z]"), "")
            if (replace.length > threadNameLength) {
                replace = replace.substring(0, 12)
            }
            String.format("%-${threadNameLength}s", replace)
        }
    }


    override fun traceOnly(function: ILogger.() -> Unit): ILogger {
        if (ConsoleLogConfig.loggerLevelInt <= LoggerLevel.TRACE.level) {
            function(this)
        }
        return this
    }

    override fun debugOnly(function: ILogger.() -> Unit): ILogger {
        if (ConsoleLogConfig.loggerLevelInt <= LoggerLevel.DEBUG.level) {
            function(this)
        }
        return this
    }

    override val isDebug: Boolean
        get() = ConsoleLogConfig.loggerLevelInt <= LoggerLevel.DEBUG.level

}
