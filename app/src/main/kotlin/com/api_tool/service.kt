package com.api_tool

import com.api_tool.models.BookByISBN

// represents an HTTP call
import retrofit2.Call
// makes retrofit aware of the HTTP call being made, appended to baseURL
import retrofit2.http.GET
// appends query parameters to the URL, in this case we are adding ?title=
import retrofit2.http.Query
// pass value dynamically into URL
import retrofit2.http.Path

// define how retrofit interacts with the OL API
// interface describes the requests the app will make
interface OpenLibraryAPIService {

    // GET request to open library book search endpoint
    @GET("search.json")
    // Defines the API call to search for books by title
    fun searchBooksByTitle(
        @Query("title") title: String?,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: String? = null
    ): Call<BookSearchResponse>

    @GET("search.json")
    fun searchBooksByAuthor(
        @Query("author") author: String?,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: String? = null
    ): Call<BookSearchResponse>

    @GET("isbn/{isbn}.json")
    fun searchBookByISBN(
        @Path("isbn") isbn: String
    ): Call<BookByISBN>



}