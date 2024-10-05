package com.api_tool

import com.api_tool.models.BookByISBN
import com.api_tool.models.AuthorDetails

import com.api_tool.models.GeminiRequest
import com.api_tool.models.GeminiResponse

import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body
import retrofit2.http.Query
import retrofit2.http.Path

// define how retrofit interacts with the OL API
// interface describes the requests the app will make
interface OpenLibraryAPIService {

    // GET request to open library book search endpoint
    @GET("search.json")
    // Defines the API call to search for books by title
    suspend fun searchBooksByTitle(
        @Query("title") title: String?,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: String? = null
    ): BookSearchResponse

    @GET("search.json")
    suspend fun searchBooksByAuthor(
        @Query("author") author: String?,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: String? = null
    ): BookSearchResponse

    @GET("isbn/{isbn}.json")
    suspend fun searchBookByISBN(
        @Path("isbn") isbn: String?
    ): BookByISBN

    @GET("authors/{authorKey}.json")
    suspend fun getAuthorDetails(
        @Path("authorKey") authorKey: String?
    ): AuthorDetails

}

interface GoogleGeminiAPIService {

    @POST("/v1beta/models/gemini-1.5-flash:generateText")
    suspend fun getRecommendations(
        @Body request: GeminiRequest
    ): GeminiResponse

}