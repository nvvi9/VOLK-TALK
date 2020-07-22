package network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl("https://neurovolk.xyz/")
    .build()


interface QuoteService {
    @GET(".")
    suspend fun getQuote(): String

    @GET("img")
    suspend fun getImageQuote(): String
}


object Service {
    val retrofitService: QuoteService by lazy {
        retrofit.create(QuoteService::class.java)
    }
}