package com.hb.logger.util

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.os.AsyncTask
import android.os.Environment
import androidx.core.content.FileProvider
import com.hb.logger.Logger
import java.io.File
import java.io.PrintWriter

class ShareUtils {
    companion object {

        @SuppressLint("StaticFieldLeak")
        fun extractAndShareLog(application: Application, onFile: (File?) -> Unit) {
            object : AsyncTask<Void, Void, File>() {
                override fun doInBackground(vararg params: Void?): File {
                    val stringBuilder = StringBuilder()
                    val list = Logger.database?.eventsDao()?.getAll()
                    for (i in list!!) {
                        stringBuilder.append(i.getLogObj()?.generateStandardLog(i) + "\n")
                    }
                    val dir = File(application.cacheDir, "Logger")
                    if (!dir.exists())
                        dir.mkdirs()
                    val file = File(
                        dir,
                        "${CommonUtils.getApplicationName(application)}_Log_${
                            System.currentTimeMillis().toDateFormat()
                        }.log"
                    )

                    val printWriter = PrintWriter(file)
                    printWriter.print(stringBuilder.toString())
                    printWriter.close()
                    return file

                }

                override fun onPostExecute(result: File?) {
                    super.onPostExecute(result)
                    /*shareFile(application,result!!)*/
                    onFile.invoke(result)
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
        }

        fun extractDatabaseAndShare(application: Application,onFile: (File?)->Unit) {
            try {
                val file = File(application.cacheDir, "Logger")
                if (!file.exists())
                    file.mkdirs()

                val data = Environment.getDataDirectory()
                val databaseName = "logger"
                val listOfFiles = arrayListOf<File>()
                listOfFiles.add(
                    File(
                        data,
                        "//data//$application.packageName//databases//$databaseName"
                    )
                )
                listOfFiles.add(
                    File(
                        data,
                        "//data//$application.packageName//databases//$databaseName-shm"
                    )
                )
                listOfFiles.add(
                    File(
                        data,
                        "//data//$application.packageName//databases//$databaseName-wal"
                    )
                )

                val zipFile = File(
                    file,
                    "${CommonUtils.getApplicationName(application)}_Log_${
                        System.currentTimeMillis().toDateFormat()
                    }.zip"
                )
                FileUtils.zip(listOfFiles, zipFile.absolutePath)
                onFile(zipFile)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun shareFile(application: Application, exportDatabaseFile: File?) {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "application/octet-stream"
            val dbUri = FileProvider.getUriForFile(
                application,
                "${application.packageName}.com.hb.logger.provider",
                exportDatabaseFile!!
            )

            shareIntent.putExtra(Intent.EXTRA_STREAM, dbUri)
            shareIntent.putExtra(
                Intent.EXTRA_SUBJECT,
                "${CommonUtils.getApplicationName(application)} Log :: " + System.currentTimeMillis()
                    .toDateTimeFormat()
            )

            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            application.startActivity(Intent.createChooser(shareIntent, "Share File Via"))
        }
    }
}