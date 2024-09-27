package com.api_tool
import com.api_tool.models.Book

// kotlin Math library
import kotlin.math.*
// HTTP client library
import retrofit2.Retrofit
// JSON to GSON
import retrofit2.converter.gson.GsonConverterFactory
// HTTP call
import retrofit2.Call
// handles results (Response) of an async call after an HTTP request completes
import retrofit2.Callback
// wrap data in Response object, ability to access parsed body & HTTP transaction details
import retrofit2.Response

fun main () {

    // Initialize Retrofit with the API baseURL and GSON converter
    val retrofit = Retrofit.Builder()
        // Base URL for the API
        .baseUrl("https://openlibrary.org/")
        // GSON conversion
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // instance of the OpenLibraryAPIService
    val apiService = retrofit.create(OpenLibraryAPIService::class.java)

    // example call to search for books with the title "The Odyssey"
    val call = apiService.searchBooksByTitle("The Odyssey")

    // execute the API call async with "enqueue"
    call.enqueue(object : Callback<BookSearchByTitleResponse> {

        // triggered on successful response
        // override is needed when changing method or property from interface
        override fun onResponse(call: Call<BookSearchByTitleResponse>, response: Response<BookSearchByTitleResponse>) {

            // checks for a 2xx status code
            if (response.isSuccessful) {

                val bookResponse = response.body()

                if (bookResponse != null && bookResponse.docs.isNotEmpty()) {

                    // Print the number of books found
                    println("Books found: ${bookResponse.numFound}")

                    // Loop through the books and print their details
                    bookResponse.docs.forEach { book ->

                        val roundedRating = roundAverageRating(book)

                        if (roundedRating != 0.0) {

                            println(
                                "Title: ${book.title}, " +
                                "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                                "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                                "Average Page Count: ${book.number_of_pages_median ?: "Average Page Count not available."}, " +
                                "Average Rating: ${roundedRating ?: "Average Rating not available."}"
                            )

                        } else {

                            println(
                                "Title: ${book.title}, " +
                                "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                                "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                                "Average Page Count: ${book.number_of_pages_median ?: "Average Page Count not available."}"
                            )

                        }

                    }

                } else {

                    println("There is no book in our Library by that title.")

                }

            } else {

                // code() returns HTTP status code
                println("API call failed with status code: ${response.code()}")

            }
        }

        // triggered on failed response
        override fun onFailure(call: Call<BookSearchByTitleResponse>, t: Throwable) {

            println("Error: ${t.message}")

        }
    })

}

fun roundAverageRating(book: Book): Double {

    val bookRating = floor(book.ratings_average * 100) / 100
    return bookRating

}