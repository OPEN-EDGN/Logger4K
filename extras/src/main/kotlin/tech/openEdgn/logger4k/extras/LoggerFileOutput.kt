/*
 * Copyright (c) 2020, Open EDGN. All rights reserved.
 *
 * HOME Page: https://github.com/Open-EDGN
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.openEdgn.logger4k.extras

import tech.openEdgn.logger4k.*
import tech.openEdgn.logger4k.extras.utils.FileUtils
import java.io.File
import java.io.FileOutputStream
import java.io.PrintStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.concurrent.Executors

class LoggerFileOutput : IOutput {

    /**
     * 不写入日志文件
     */
    @Volatile
    var skipLoggerFile = false

    private val threadPool = Executors.newCachedThreadPool()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    private val loggerFileName: String
        get() = "log-${dateFormat.format(System.currentTimeMillis())}.log"

    private val loggerFile: File
        get() = File(ELoggerConfig.logOutputDirectory, loggerFileName)


    @Volatile
    private var printStream: PrintStream? = null

    @Volatile
    private var fileHashCode = -1
    private val loggerFilePrintStream: PrintStream?
        get() {
            if (skipLoggerFile) return null
            if (FileUtils.checkDirectory(ELoggerConfig.logOutputDirectory).not()) {
                //  日志文件夹
                return null
            }
            if (loggerFile.isDirectory) {
                LoggerConfig.consoleErrorOutputStream.println("path :[${loggerFile.absolutePath}] is directory.")
                return null
            }
            if (loggerFile.exists().not()) {
                loggerFile.createNewFile()
                //  logger file not exists
            }
            synchronized(this) {
                val hashCode = loggerFileName.hashCode()
                // 判断日志文件是否变更
                if (printStream == null || hashCode != fileHashCode) {
                    printStream?.let {
                        it.println("日志已经结束.")
                        it.println("EOF")
                        try {
                            it.close()
                        } catch (e: Exception) {
                            LoggerConfig.consoleErrorOutputStream.println("关闭日志文件时出现问题. [${e.message}]")
                        }
                    }
                    printStream = PrintStream(FileOutputStream(loggerFile,true), true, Charsets.UTF_8.name())
                    printStream?.let {
                        if (loggerFile.length() == 0L) {
                            it.println("日志记录开始 >  ${loggerFile.name} <<EOF ")
                            //新日志文件
                        } else {
                            it.println()
                            it.println("继续日志会话.")
                            //日志文件已存在
                        }
                    }
                    fileHashCode = hashCode
                }
            }
            return printStream
        }

    override fun writeLine(item: LoggerItem) {
        threadPool.submit(LoggerItemRunnable(item))
    }

    override fun close() {
        for (runnable in threadPool.shutdownNow()) {
            runnable.run()
        }
        loggerFilePrintStream?.println("日志写入结束.")
        loggerFilePrintStream?.close()
    }

    inner class LoggerItemRunnable(private val item: LoggerItem) : Runnable {
        override fun run() {
            if ((item.level == LoggerLevel.DEBUG && LoggerConfig.isDebug) || item.level != LoggerLevel.DEBUG) {
                val lineFormat = LoggerConfig.itemFormat(item)
                if (item.level.levelInt < LoggerLevel.WARN.levelInt) {
                    LoggerConfig.consoleOutputStream
                } else {
                    LoggerConfig.consoleErrorOutputStream
                }.println(lineFormat)
                if (item.level != LoggerLevel.DEBUG) {
                    loggerFilePrintStream?.println(lineFormat)
                    // DEBUG 日志不写入文件！！！
                }
            }
        }
    }
}