/*
 * Copyright (c) 2020, Open EDGN. All rights reserved.
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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.openEdgn.logger4k.extras.utils

import tech.openEdgn.logger4k.LoggerConfig
import tech.openEdgn.logger4k.getLogger
import java.io.File
import java.io.IOException

object FileUtils {
    private val logger =getLogger()
    /**
     * 测试此目录是否具有读写权限,不存在则会创建
     * @param directory File 目录
     * @return Boolean 如果目录只读或存在文件则返回False
     */
    fun checkDirectory(directory: File): Boolean {
        if (directory.exists()){
            directory.mkdirs()
        }
        if (directory.isFile){
            LoggerConfig.commandErrOutput.println("directory [${directory.absolutePath}] is File.")
            return false
        }
        if (directory.canWrite().not()){
            LoggerConfig.commandErrOutput.println("directory [${directory.absolutePath}] Read-Only.")
            return false
        }
        return true
    }
}