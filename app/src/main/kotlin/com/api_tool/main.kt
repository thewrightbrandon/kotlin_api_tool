package com.api_tool

import com.api_tool.models.Book
import com.api_tool.models.BookByISBN

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

    // prompt user for which search criteria they'd like t use
    println("Search by: 1) Title 2) Author 3) ISBN")
    val choice = readLine()

    // when() handles like a switch statement with more flexibility
    when (choice) {

        "1" -> {
            println("Enter book title:")
            val title = readLine()
            if (title != null) {
                handleSearchWithOptions(apiService, title, "title")
            }
        }

        "2" -> {
            println("Enter author name:")
            val author = readLine()
            if (author != null) {
                handleSearchWithOptions(apiService, author, "author")
            }
        }

        "3" -> {
            println("Enter ISBN:")
            // ISBN will be dynamically added to the API endpoint using @Path
            val isbn = readLine()
            if (isbn != null) {
                searchByISBN(apiService, isbn)
            }
        }

        else -> {
            println("Invalid choice, please try again.")
        }

    }

}

// global limit value, remember constant global variables are named in all CAPS
const val DEFAULT_LIMIT = "10"

// handles additional query parameters for certain search types
fun handleSearchWithOptions(apiService: OpenLibraryAPIService, searchParam: String, searchType: String) {

    println("Enter sort option (optional): new, old, rating, random")
    val sort = readLine()

    when (searchType) {

        "title" -> searchByTitle(apiService, searchParam, sort)
        "author" -> searchByAuthor(apiService, searchParam, sort)

    }

}

// search by book title
fun searchByTitle(apiService: OpenLibraryAPIService, title: String, sort: String?) {

    // add default limit value to only return 10 books
    val call = apiService.searchBooksByTitle(title, sort, limit = DEFAULT_LIMIT)

    // execute the API call async with "enqueue"
    call.enqueue(object : Callback<BookSearchResponse> {

        // triggered on successful response
        // override is needed when changing method or property from interface
        override fun onResponse(call: Call<BookSearchResponse>, response: Response<BookSearchResponse>) {

            // checks for a 2xx status code
            if (response.isSuccessful) {

                val searchResult = response.body()

                if (searchResult != null && searchResult.docs.isNotEmpty()) {

                    // Print the number of books found
                    println("Books found: ${searchResult.numFound}")

                    // Loop through the books and print their details
                    searchResult.docs.forEach { book ->

                        val roundedRating = roundAverageRating(book)

                        if (roundedRating != 0.0) {

                            println(
                                "Title: ${book.title}, " +
                                "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                                "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                                "Average Page Count: ${book.number_of_pages_median ?: "Average Page Count not available."}, " +
                                "Average Rating: $roundedRating"
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
        override fun onFailure(call: Call<BookSearchResponse>, t: Throwable) {

            println("Error: ${t.message}")

        }

    })

}

// search books by author
fun searchByAuthor(apiService: OpenLibraryAPIService, author: String, sort: String?) {

    val call = apiService.searchBooksByAuthor(author, sort, limit = DEFAULT_LIMIT)

    call.enqueue(object : Callback<BookSearchResponse> {

        override fun onResponse(call: Call<BookSearchResponse>, response: Response<BookSearchResponse>) {

            if (response.isSuccessful) {

                val searchResult = response.body()

                if (searchResult != null && searchResult.docs.isNotEmpty()) {

                    println("Books found: ${searchResult.numFound}")

                    searchResult.docs.forEach { book ->

                        val roundedRating = roundAverageRating(book)

                        if (roundedRating != 0.0) {

                            println(
                                "Title: ${book.title}, " +
                                "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                                "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                                "Average Page Count: ${book.number_of_pages_median ?: "Average Page Count not available."}, " +
                                "Average Rating: $roundedRating"
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

                    println("No books found by author: $author")

                }

            }
        }

        override fun onFailure(call: Call<BookSearchResponse>, t: Throwable) {

            println("Error: ${t.message}")

        }

    })

}

// search for specific book using ISBN
fun searchByISBN(apiService: OpenLibraryAPIService, isbn: String) {

    val call = apiService.searchBookByISBN(isbn)

    call.enqueue(object : Callback<BookByISBN> {

        override fun onResponse(call: Call<BookByISBN>, response: Response<BookByISBN>) {

            if (response.isSuccessful) {

                val book = response.body()

                if (book != null) {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.publish_date ?: "Publish Year not available."}, " +
                        "Average Page Count: ${book.number_of_pages_median ?: "Average Page Count not available."}"
                    )

                } else {

                    println("No book found for ISBN: $isbn")

                }

            }

        }

        override fun onFailure(call: Call<BookByISBN>, t: Throwable) {

            println("Error: ${t.message}")

        }

    })

}

fun roundAverageRating(book: Book): Double {

    val bookRating = floor(book.ratings_average * 100) / 100
    return bookRating

}