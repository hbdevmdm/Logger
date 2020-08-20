package com.hb.logger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.hb.logger.databinding.ActivityMainBinding
import com.hb.logger.remote.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logger = Logger(this::class.java.simpleName)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.btnMakeCrash.setOnClickListener {
            Logger.makeCrash()
        }

        binding.btnGotoLogs.setOnClickListener {
            Logger.launchActivity()
        }

        binding.btnCallService.setOnClickListener {
            performLogin()
        }

        binding.btnCustomLog.setOnClickListener{
            logger.debugEvent("Custom event", "Log Custom Event")
        }
    }


    private fun performLogin() {
        ApiClient.apiService.login("email", "password")
            .enqueue(object : Callback<Response<String>> {
                override fun onFailure(call: Call<Response<String>>, t: Throwable) {

                }

                override fun onResponse(
                    call: Call<Response<String>>,
                    response: Response<Response<String>>
                ) {

                }

            })
    }
}
