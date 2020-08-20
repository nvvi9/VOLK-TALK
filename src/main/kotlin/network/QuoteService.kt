package network

import okhttp3.ResponseBody
import retrofit2.http.GET


interface QuoteService {
    @GET(".")
    suspend fun getQuote(): ResponseBody

    @GET("img")
    suspend fun getImageQuote(): ResponseBody
}