package com.xapptree.networkrunner

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST()
    fun postApiCall(
        @Url url: String,
        @Body data: Any
    ): Call<String>

    @POST()
    fun postApiCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>?,
        @Body data: Any
    ): Call<String>

    @GET()
    fun getApiCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>?
    ): Call<String>

    @GET()
    fun getApiCall(
        @Url url: String
    ): Call<String>

    @PUT()
    fun putApiCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>?,
        @Body data: Any
    ): Call<String>

    @PUT()
    fun putApiCall(
        @Url url: String,
        @Body data: Any
    ): Call<String>

    @PATCH()
    fun patchApiCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>?,
        @Body data: Any
    ): Call<String>

    @PATCH()
    fun patchApiCall(
        @Url url: String,
        @Body data: Any
    ): Call<String>

    @DELETE()
    fun deleteApiCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>?,
        @Body data: Any
    ): Call<String>

    @DELETE()
    fun deleteApiCall(
        @Url url: String,
        @Body data: Any
    ): Call<String>

}