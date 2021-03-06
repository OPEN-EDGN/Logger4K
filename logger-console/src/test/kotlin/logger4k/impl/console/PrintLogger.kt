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

import com.github.openEdgn.logger4k.LoggerFactory
import org.junit.jupiter.api.Test
import java.text.SimpleDateFormat
import java.util.*

internal class PrintLogger {
    /**
     * 默认情况下无日志抛出
     * @return Unit
     */
    @Test
    fun test1() {
        val logger = LoggerFactory.getLogger(javaClass)
        logger.trace("Hello World")
        logger.debug("Hello World")
        logger.info("Hello World")
        logger.warn("Hello World")
        logger.error("Hello World")
        val thread = Thread {
            logger.error("New Thread")
        }
        thread.name = "Test Thread"
        thread.start()
    }

    @Test
    fun test2() {
        System.setProperty("logger.level", "debug")
        // 开启 DEBUG 模式代码
        val logger = LoggerFactory.getLogger(javaClass)
        logger.debugOnly {
            debug("isDebug : {} ,date: {}", isDebug, SimpleDateFormat("yyyy-MM-dd").format(Date()))
        }
        logger.trace("Hello World")
        logger.debug("Hello World")
        logger.info("Hello World")
        logger.warn("Hello World")
        logger.error("Hello World")
    }
}
