package com.xapptree.networkrunner

data class NRResponse(
    val code: Int,
    val message: String?,
    val body: Any?,
    val errorBody: String?,
    val isSuccessful: Boolean,
    val raw: String?,
    val headers: String?
)