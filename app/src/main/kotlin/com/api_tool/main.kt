package com.api_tool

import com.api_tool.models.Book

// Math library
import kotlin.math.*
// HTTP client library
import retrofit2.*
// async API calls
import kotlinx.coroutines.*
// JSON to GSON
import retrofit2.converter.gson.GsonConverterFactory

fun main () = runBlocking {

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
    println("Search by number: 1) Title 2) Author 3) ISBN")
    val choice = readLine()

    // when() handles like a switch statement with more flexibility
    when (choice) {

        "1" -> {
            println("Enter book title: ")
            val title = readLine()
            if (title != null) {
                handleSearchWithOptions(apiService, title, "title")
            }
        }

        "2" -> {
            println("Enter author name: ")
            val author = readLine()
            if (author != null) {
                handleSearchWithOptions(apiService, author, "author")
            }
        }

        "3" -> {
            println("Enter ISBN: ")
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
const val DEFAULT_LIMIT_STRING = "10"

// handles additional query parameters for certain search types
suspend fun handleSearchWithOptions(apiService: OpenLibraryAPIService, searchParam: String, searchType: String) {

    println("Enter sort option (optional): new, old, rating, random")
    val sort = readLine()

    when (searchType) {

        "title" -> searchByTitle(apiService, searchParam, sort)
        "author" -> searchByAuthor(apiService, searchParam, sort)

    }

}

// search by book title
suspend fun searchByTitle(apiService: OpenLibraryAPIService, title: String, sort: String?) {
    //try-catch blocks will handle any errors during the API call
    try {
        // execute API call in coroutine context
        val searchResult = apiService.searchBooksByTitle(title, sort, limit = DEFAULT_LIMIT_STRING)

        if (searchResult.docs.isNotEmpty()) {

            searchResult.docs.forEach { book ->

                val roundedRating = roundAverageRating(book)

                if (roundedRating != 0.0) {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                        "Average Rating: $roundedRating"
                    )

                } else {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, "
                    )

                }

            }

        } else {

            println("There is no book in our Library by that title.")

        }

    } catch (e: Exception) {

        println("Error: ${e.message}")

    }

}

// search books by author
suspend fun searchByAuthor(apiService: OpenLibraryAPIService, author: String, sort: String?) {

    try {

        val searchResult = apiService.searchBooksByAuthor(author, sort, limit = DEFAULT_LIMIT_STRING)

        if (searchResult.docs.isNotEmpty()) {

            println("Books found: ${searchResult.numFound}")

            searchResult.docs.forEach { book ->

                val roundedRating = roundAverageRating(book)

                if (roundedRating != 0.0) {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, " +
                        "Average Rating: $roundedRating"
                    )

                } else {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}, "
                    )

                }

            }

        } else {

            println("No books found by author: $author")

        }

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// search for specific book using ISBN
suspend fun searchByISBN(apiService: OpenLibraryAPIService, isbn: String) {

    try {

        val book = apiService.searchBookByISBN(isbn)

        // author_name is not returned when searching book by ISBN
        // instead we need to get author details from a separate API request
        val authors = book.authors

        if (book.title == null) {

            println("No book found for ISBN: $isbn")
            // do not continue if there is no book title returned
            return
        }

        if (authors.isNullOrEmpty()) {

            println(
                "Title: ${book.title}, " +
                "Author: Author unknown., " +
                "Publish Year: ${book.publish_date ?: "Publish Year not available."}, "
            )
            // do not continue if there is no list of authors returned
            return
        }

        // authors is a list of objects, get the key within the first object
        val authorKeys = authors.map { it.key }
        val authorKey = authorKeys[0].substringAfterLast("/")
        // define AuthorDetails interface to handle author endpoint data
        val authorDetails = apiService.getAuthorDetails(authorKey)

        println(
            "Title: ${book.title}, " +
            "Author: ${authorDetails.personal_name}, " +
            "Publish Year: ${book.publish_date ?: "Publish Year not available."}, "
        )

    } catch (e: Exception) {

        println("Error: ${e.message}")

    }

}

/**
 * rounds the average rating of the book to two decimal places
 * we are checking if the rating is null in the service
 *
 * @param book - the book object containing the ratings_average
 * @return the rounded average rating
 */
fun roundAverageRating(book: Book): Double {

    val bookRating = round(book.ratings_average * 100) / 100
    return bookRating

}