package com.hb.logger.ui.log.core

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.hb.logger.util.CommonUtils
import com.hb.logger.util.toDateFormat
import com.hb.logger.util.toDateTimeFormat
import java.io.File
import java.io.PrintWriter
import java.util.*

open class LogDetailBaseActivity : AppCompatActivity() {
    fun shareLog(value: String, type: String) {

        val dir = File(this.cacheDir, "Logger")
        if (!dir.exists())
            dir.mkdirs()
        val file = File(dir, "${CommonUtils.getApplicationName(this)}_Log_$type.log")


        val printWriter = PrintWriter(file)
        printWriter.print(value)
        printWriter.close()

        val fileUri = FileProvider.getUriForFile(this, "${this.applicationContext.packageName}.com.hb.logger.provider", file)

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/octet-stream"
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT,   "${CommonUtils.getApplicationName(this)} ${type.toLowerCase(Locale.ENGLISH).capitalize()} Log :: " + System.currentTimeMillis().toDateTimeFormat())
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

        startActivity(Intent.createChooser(shareIntent, "Share File Via"))
    }
}
