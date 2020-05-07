package com.xapptree.networkrunner

import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback

object NetworkRunner {
    class Payload {
        private var type: RequestType? = null
        private var url: String = ""
        private var headers: Map<String, String>? = null
        private var request: Any? = null
        private var requestCode: Int = 0
        private var callback: NetworkRunnerCallback? = null
        private var output: Class<*>? = null

        fun type(type: RequestType) = apply { this.type = type }
        fun url(url: String) = apply { this.url = url }
        fun headersMap(headers: Map<String, String>) = apply { this.headers = headers }
        fun request(request: Any) = apply { this.request = request }
        fun requestCode(requestCode: Int) = apply { this.requestCode = requestCode }
        fun responseType(output: Class<*>) = apply { this.output = output }
        fun callback(callback: NetworkRunnerCallback) = apply { this.callback = callback }

        fun executeAsync() {
            if (type == null)
                throw IllegalArgumentException("RequestType is missing...")

            if (callback == null)
                throw IllegalArgumentException("NetworkClientCallback is not implemented or not instance is not passed!!")

            val call: Call<String>? =
                ApiHelper.createCall(PayloadData(url, type!!, headers, request))

            call?.enqueue(object : Callback<String?> {
                override fun onResponse(
                    call: Call<String?>,
                    response: retrofit2.Response<String?>
                ) {
                    val responseObj = NRResponse(
                        response.code(),
                        response.message(),
                        Gson().fromJson(response.body(), output),
                        Gson().toJson(response.errorBody()),
                        response.isSuccessful,
                        "",
                        Gson().toJson(response.headers())
                    )
                    callback?.onResponse(responseObj, requestCode)
                }

                override fun onFailure(call: Call<String?>, t: Throwable) {
                    callback?.onFailure(requestCode, t)
                }
            })
        }

        fun execute(): NRResponse? {
            if (type == null)
                throw IllegalArgumentException("RequestType is missing...")

            val call: Call<String>? =
                ApiHelper.createCall(PayloadData(url, type!!, headers, request))
            val response = call?.execute()
            return NRResponse(
                response!!.code(),
                response.message(),
                Gson().fromJson<Any?>(response.body(), output),
                Gson().toJson(response.errorBody()),
                response.isSuccessful,
                "",
                Gson().toJson(response.headers())
            )
        }
    }

}