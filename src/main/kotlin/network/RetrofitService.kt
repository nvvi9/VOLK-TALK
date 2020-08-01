package network

import okhttp3.OkHttpClient
import retrofit2.Retrofit


object RetrofitService {

    private val okHttpClient = OkHttpClient.Builder()
        .addNetworkInterceptor {
            it.proceed(
                it.request()
                    .newBuilder()
                    .header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20100101 Firefox/10.0")
                    .build()
            )
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://neurovolk.xyz/")
        .build()

    val quoteService: QuoteService by lazy {
        retrofit.create(QuoteService::class.java)
    }
}