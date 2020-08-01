package network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


interface QuoteService {
    @GET(".")
    suspend fun getQuote(): ResponseBody

    @GET("img")
    suspend fun getImageQuote(): ResponseBody
}