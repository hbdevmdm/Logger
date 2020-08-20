package com.hb.logger.util

import com.hb.logger.Logger
import okhttp3.*
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

public class LoggerInterceptor : Interceptor {

    val logger = Logger("LoggerInterceptor")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestTime = System.currentTimeMillis()
        val requestUrl = request.url.toUrl().toString()
        val requestMethod = request.method // POST, GET , DELETE , PUT
        val requestParams = JSONArray()
        val requestHeaders = JSONArray()
        var responseString = ""
        var responseBodyLength = ""
        var responseContentType = ""
        var responseTime = System.currentTimeMillis()
        var response: Response?
        try {

            if (requestMethod == "POST") {
                val requestBody = request.body
                if (requestBody != null) {
                    when (request.body) {
                        is FormBody -> {
                            val formBody = request.body as FormBody
                            val size = formBody.size
                            for (i in 0 until size) {
                                val key = formBody.name(i)
                                val value = formBody.value(i)
                                requestParams.put(createJSONObject(key, value, "text"))
                            }
                        }
                        is MultipartBody -> {
                            val multipartBody = request.body as MultipartBody
                            val partList = multipartBody.parts
                            for (i in partList) {
                                if (i.body.contentType()?.type?.equals("text")!!) { //"text", "image", "audio", "video"
                                    val key = getKeyFromContentDisposition(i.headers?.get("Content-Disposition")!!)
                                    val value = bodyToString(i.body)
                                    requestParams.put(createJSONObject(key, value, "text"))
                                } else {
                                    val key = getKeyFromContentDisposition(i.headers?.get("Content-Disposition")!!)
                                    requestParams.put(createJSONObject(key, "", i.body.contentType()?.type
                                            ?: ""))
                                }
                            }
                        }
                        else -> {
                            var bodyString = bodyToString(requestBody)
                            try {
                                val jsonBody = JSONObject(bodyString)
                                bodyString = jsonBody.toString(1)
                            } catch (e: java.lang.Exception) {

                            }
                            requestParams.put(createJSONObject("body", bodyString, "text"))
                        }
                    }

                }
            }

            request.headers.names().forEach {
                val key = it
                val value = request.headers[it] ?: ""
                requestHeaders.put(createJSONObject(key, value, "text"))
            }

            response = chain.proceed(request)
            responseTime = System.currentTimeMillis()
            val responseBody = response.body

            if (responseBody != null) {
                val originalResponseBodyString = responseBody.string()
                responseString = originalResponseBodyString

                val responseContentLength = responseBody.contentLength()

                responseBodyLength = responseContentLength.toString()
                responseContentType = responseBody.contentType().toString()

                //To beautify json response
                if (responseContentType.contains("application/json")) {
                    responseString = if (responseString.startsWith("[")) {
                        val jsonArray = JSONArray(responseString)
                        jsonArray.toString(1)
                    } else {
                        val jsonObject = JSONObject(responseString)
                        jsonObject.toString(1)
                    }
                }
                response = response.newBuilder().body(originalResponseBodyString.toResponseBody(responseBody.contentType())).build()
            }


            logger.dumpNetworkEvent(reqTime = requestTime.toString(),
                    requestUrl = requestUrl,
                    requestMethod = requestMethod,
                    requestParams = requestParams.toString(1),
                    requestHeaders = requestHeaders.toString(1),
                    responseTime = responseTime.toString(),
                    responseString = responseString,
                    responseContentLength = responseBodyLength,
                    responseCode = response.code.toString(),
                    responseContentType = responseContentType)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            logger.dumpNetworkEvent(reqTime = requestTime.toString(),
                    requestUrl = requestUrl,
                    requestMethod = requestMethod,
                    requestParams = requestParams.toString(1),
                    requestHeaders = requestHeaders.toString(1),
                    responseTime = responseTime.toString(),
                    responseString = "{\"error\":\"${e.message.toString()}\"}",
                    responseContentLength = responseBodyLength,
                    responseCode = (-1).toString(),
                    responseContentType = responseContentType)
            throw e
        } finally {


        }
    }


    private fun createJSONObject(key: String, value: String, type: String): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("key", key)
        jsonObject.put("value", value)
        jsonObject.put("type", type)
        return jsonObject
    }

    private fun getKeyFromContentDisposition(value: String): String {
        val pat = Pattern.compile("(?<=name=\")\\w+")
        val mat = pat.matcher(value)
        while (mat.find()) {
            return (mat.group())
        }
        return ""
    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
    }
}
