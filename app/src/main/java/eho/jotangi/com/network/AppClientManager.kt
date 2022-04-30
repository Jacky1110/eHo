package eho.jotangi.com.network

import eho.jotangi.com.network.ApiService
import eho.jotangi.com.utils.JotangiUtilConstants
import eho.jotangi.com.utils.smartwatch.WatchApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AppClientManager private constructor(){

    lateinit var service: ApiService
    lateinit var watchservice: WatchApiService

    private var logging = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Timber.i("interceptor msg: $message")
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private var okHttpClient = OkHttpClient().newBuilder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(120, TimeUnit.SECONDS).addInterceptor(logging).build()

    fun init() {
        val retrofit = Retrofit.Builder()
            .baseUrl(JotangiUtilConstants.DOMAIN)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
        service = retrofit.create(ApiService::class.java)

        val retrofit2 = Retrofit.Builder()
            .baseUrl(JotangiUtilConstants.DOMAIN2)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        watchservice = retrofit2.create(WatchApiService::class.java)
    }

    companion object {
        val instance by lazy { AppClientManager() }
    }

}