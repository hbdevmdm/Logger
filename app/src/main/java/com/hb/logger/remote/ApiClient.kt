package com.hb.logger.remote

import android.content.Intent
import com.dc.retroapi.builder.RetrofitClientBuilder
import com.dc.retroapi.interceptor.DataConverterInterceptor
import com.dc.retroapi.interceptor.EncryptionStrategy
import com.dc.retroapi.interceptor.RequestInterceptor
import com.dc.retroapi.interceptor.ResponseInterceptor
import com.google.gson.JsonElement
import com.hb.logger.util.LoggerInterceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory


/*.baseUrl(ApiUtils.getWsUrl())*/
/*.baseUrl("http://192.168.39.8/mrs_staff_boat/mrs_staff_boat/WS/")*/

class ApiClient {

    companion object {
        val tokenExpireCodes = arrayListOf("-200", "-300")
        val apiService: ApiService by lazy {
            return@lazy ApiClient().service
        }
    }

    private val service: ApiService
        get() {
            return RetrofitClientBuilder()
                .baseUrl(ApiUtils.getWsUrl())
                .connectionTimeout(30)
                .readTimeout(30)
                .writeTimeout(50)
                .addConverterFactory(GsonConverterFactory.create())
                .addInterceptor(RequestInterceptor(object :
                    RequestInterceptor.OnRequestInterceptor { //to provide common header & params
                    override fun provideHeaderMap(): HashMap<String, String> {
                        val map = HashMap<String, String>()
                        map["ws_data"] = "android"
                        return map
                    }

                    override fun provideBodyMap(): HashMap<String, String> {
                        val map = HashMap<String, String>()
/*                        val appPreference = AppPreference()
                        map["ws_token"] = appPreference.getToken(MainApplication.context)*/
                        return map
                    }
                }))
                .addInterceptor(LoggerInterceptor())
                .addLogInterceptor(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(DataConverterInterceptor()) // used for managing data object/array issue @DataAsObject @DataAsList
                .addEncryptionInterceptor(
                    "CIT@WS!", EncryptionStrategy.REQUEST_RESPONSE,
                    true, "ws_checksum",
                    excludeFromEncryption = arrayListOf("ws_token")
                ) // used for making encrypted request and to generate checksum
                .create(ApiService::class.java)
        }
}