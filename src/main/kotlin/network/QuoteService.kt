package network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url

private const val BASE_URL = "https://neurovolk.xyz/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()


interface QuoteService {
    @GET
    fun getQuoteAsync(@Url url: String = BASE_URL): Deferred<String>

    @GET("img")
    fun getImageQuoteAsync(): Deferred<String>
}


object Service {
    val retrofitService: QuoteService by lazy {
        retrofit.create(QuoteService::class.java)
    }
}