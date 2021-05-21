package com.hb.logger.remote


import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {


    /*@POST("customer_login")*/
    /*@RequestExclusionStrategy(RequestExclusionStrategy.ENCRYPTION + "," + RequestExclusionStrategy.CHECKSUM)*/
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<Response<String>>

}