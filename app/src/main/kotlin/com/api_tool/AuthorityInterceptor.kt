package com.api_tool

import okhttp3.Interceptor

class AuthorityInterceptor(private val googleAPIKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val original = chain.request()
        val request = original.newBuilder()
            .header("Authorization", "Bearer $googleAPIKey")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(request)

    }

}