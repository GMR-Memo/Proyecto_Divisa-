package com.example.proyectodivisaff.data

import com.example.proyectodivisaff.model.ExchangeRateResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    // Para la Activity (corrutinas)
    @GET("v6/{apiKey}/latest/{base}")
    suspend fun getMoney(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): ExchangeRateResponse

    // Para el Worker (síncrono con .execute())
    @GET("v6/{apiKey}/latest/{base}")
    fun getLatestRates(
        @Path("apiKey") apiKey: String,
        @Path("base") base: String
    ): retrofit2.Call<ExchangeRateResponse>

}

//object retrofit_service{
  //  fun makeRetrofitService():RetrofitService{
    //    return Retrofit.Builder()
      //      .baseUrl()
        //    .addConverterFactory(GsonConverterFactory.create())
          //  .build().create(RetrofitService::class.java)
    //}
//}