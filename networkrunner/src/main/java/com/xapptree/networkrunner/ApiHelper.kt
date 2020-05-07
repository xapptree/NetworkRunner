package com.xapptree.networkrunner

import retrofit2.Call

interface IApiHelper {
    fun createCall(payload: PayloadData): Call<String>
}

data class PayloadData(
    var url: String,
    var type: RequestType,
    var headers: Map<String, String>?,
    var request: Any?
)

object ApiHelper : IApiHelper {

    override fun createCall(payload: PayloadData): Call<String> {
        var call: Call<String>? = null
        when (payload.type) {
            RequestType.GET -> {
                call = if (payload.headers == null) {
                    ServiceGenerator.getService().getApiCall(payload.url)
                } else {
                    ServiceGenerator.getService().getApiCall(payload.url, payload.headers)
                }
            }
            RequestType.POST -> {
                if (payload.request == null) {
                    throw IllegalArgumentException("Request object is missing!!")
                }
                call = if (payload.headers == null) {
                    ServiceGenerator.getService().postApiCall(payload.url, payload.request!!)
                } else {
                    ServiceGenerator.getService()
                        .postApiCall(payload.url, payload.headers, payload.request!!)
                }
            }
            RequestType.PUT -> {
                if (payload.request == null) {
                    throw IllegalArgumentException("Request object is missing!!")
                }
                call = if (payload.headers == null) {
                    ServiceGenerator.getService().putApiCall(payload.url, payload.request!!)
                } else {
                    ServiceGenerator.getService()
                        .putApiCall(payload.url, payload.headers, payload.request!!)
                }
            }
            RequestType.PATCH -> {
                if (payload.request == null) {
                    throw IllegalArgumentException("Request object is missing!!")
                }
                call = if (payload.headers == null) {
                    ServiceGenerator.getService().patchApiCall(payload.url, payload.request!!)
                } else {
                    ServiceGenerator.getService()
                        .patchApiCall(payload.url, payload.headers, payload.request!!)
                }
            }
            RequestType.DELETE -> {
                if (payload.request == null) {
                    throw IllegalArgumentException("Request object is missing!!")
                }
                call = if (payload.headers == null) {
                    ServiceGenerator.getService().deleteApiCall(payload.url, payload.request!!)
                } else {
                    ServiceGenerator.getService()
                        .deleteApiCall(payload.url, payload.headers, payload.request!!)
                }
            }
        }

        return call
    }
}