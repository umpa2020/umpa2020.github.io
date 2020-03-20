package com.umpa2020.tracer.util

import android.util.Log
import android.widget.Toast
import com.umpa2020.tracer.App
import java.net.UnknownHostException

object Logg {
    private fun formattedMessage(): String {
        val level = 4
        val trace = Thread.currentThread().stackTrace[level]
        val fileName = trace.fileName
        val classPath = trace.className
        val className = classPath.substring(classPath.lastIndexOf(".") + 1)
        val methodName = trace.methodName
        val lineNumber = trace.lineNumber
        return "$className.$methodName($fileName:$lineNumber)"
    }

    fun v(tag: String, msg: String?) {
        Log.v(tag, formattedMessage() + "|" + msg)
    }

    fun d(tag: String, msg: String?) {
        Log.d(tag, formattedMessage() + "|" + msg)
    }

    fun i(tag: String, msg: String?) {
        Log.i(tag, formattedMessage() + "|" + msg)
    }

    fun w(tag: String, msg: String?) {
        Log.w(tag, formattedMessage() + "|" + msg)
    }

    fun w(tag: String, e: Throwable) {
        Log.w(tag, formattedMessage() + "|" + e.toString())

        if (e is UnknownHostException) {
            Toast.makeText(App.instance, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    fun w(tag: String, e: Exception) {
        Log.w(tag, formattedMessage() + "|" + e.toString())
    }

    fun e(tag: String, msg: String?) {
        Log.e(tag, formattedMessage() + "|" + msg)
    }
}
