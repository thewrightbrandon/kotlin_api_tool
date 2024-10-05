package com.api_tool

import com.api_tool.book_classes.Book
import com.api_tool.gemini_classes.*

// env variables
import io.github.cdimascio.dotenv.dotenv
// Math library
import kotlin.math.*
// HTTP client library
import retrofit2.*
// async API calls
import kotlinx.coroutines.*
// JSON to GSON
import retrofit2.converter.gson.GsonConverterFactory

// global limit value, remember constant global variables are named in all CAPS
const val DEFAULT_LIMIT_STRING = "10"

fun main () = runBlocking {

    // Initialize Retrofit with the API baseURL and GSON converter
    val retrofitOpenLibrary = Retrofit.Builder()
        // Base URL for the API
        .baseUrl("https://openlibrary.org/")
        // GSON conversion
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // instance of the OpenLibraryAPIService
    val openLibraryService = retrofitOpenLibrary.create(OpenLibraryAPIService::class.java)

    // prompt user for which search criteria they'd like to use
    println("Input number of desired search parameter: 1) Title 2) Author 3) ISBN")
    val choice = readLine()

    // when() handles like a switch statement with more flexibility
    when (choice) {

        "1" -> {
            println("Enter book title: ")
            val title = readLine()
            if (title != null) {
                handleSearchWithOptions(openLibraryService, title, "title")
            }
        }

        "2" -> {
            println("Enter author name: ")
            val author = readLine()
            if (author != null) {
                handleSearchWithOptions(openLibraryService, author, "author")
            }
        }

        "3" -> {
            println("Enter book ISBN 13 from Open Library: ")
            // ISBN will be dynamically added to the API endpoint using @Path
            val isbn = readLine()
            if (isbn != null) {
                searchByISBN(openLibraryService, isbn)
            }
        }

        else -> {
            println("Invalid choice, please try again.")
        }

    }

}

// handles additional query parameters for certain search types
suspend fun handleSearchWithOptions(openLibraryService: OpenLibraryAPIService, searchParam: String, searchType: String) {

    println("Enter one of the following sort options (optional): new, old, rating, random")
    val sort = readLine()
    println()

    when (searchType) {

        "title" -> searchByTitle(openLibraryService, searchParam, sort)
        "author" -> searchByAuthor(openLibraryService, searchParam, sort)

    }

}

// search by book title
suspend fun searchByTitle(openLibraryService: OpenLibraryAPIService, title: String, sort: String?) {
    //try-catch blocks will handle any errors during the API call
    try {
        // execute API call in coroutine context
        val searchResult = openLibraryService.searchBooksByTitle(title, sort, limit = DEFAULT_LIMIT_STRING)

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
                    println()

                } else {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}"
                    )
                    println()

                }

            }

            // Call for recommendations based on the title searched
            getRecommendations(title)

        } else {

            println("There is no book in our Library by that title.")

        }

    } catch (e: Exception) {

        println("Error: ${e.message}")

    }

}

// search books by author
suspend fun searchByAuthor(openLibraryService: OpenLibraryAPIService, author: String, sort: String?) {

    try {

        val searchResult = openLibraryService.searchBooksByAuthor(author, sort, limit = DEFAULT_LIMIT_STRING)

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
                    println()

                } else {

                    println(
                        "Title: ${book.title}, " +
                        "Author: ${book.author_name?.joinToString() ?: "Author unknown."}, " +
                        "Publish Year: ${book.first_publish_year ?: "Publish Year not available."}"
                    )
                    println()

                }

            }

            // Call for recommendations based on the author searched
            getRecommendations(author)

        } else {

            println("No books found by author: $author")

        }

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}

// search for specific book using ISBN
suspend fun searchByISBN(openLibraryService: OpenLibraryAPIService, isbn: String) {

    try {

        val book = openLibraryService.searchBookByISBN(isbn)

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
                "Publish Year: ${book.publish_date ?: "Publish Year not available."}"
            )
            println()

            // do not continue if there is no list of authors returned
            return

        }

        // authors is a list of objects, get the key within the first object
        val authorKeys = authors.map { it.key }
        val authorKey = authorKeys[0].substringAfterLast("/")

        // define AuthorDetails interface to handle author endpoint data
        val authorDetails = openLibraryService.getAuthorDetails(authorKey)

        println(
            "Title: ${book.title}, " +
            "Author: ${authorDetails.personal_name}, " +
            "Publish Year: ${book.publish_date ?: "Publish Year not available."}"
        )
        println()

    } catch (e: Exception) {

        println("Error: ${e.message}")

    }

}

suspend fun getRecommendations(userSearch: String) {

    val dotenv = dotenv()
    val googleAPIKey = dotenv["GOOGLE_APPLICATION_API_KEY"]

    val retrofitGemini = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleGeminiService = retrofitGemini.create(GoogleGeminiAPIService::class.java)

    val request = GeminiRequest(
        contents = listOf(
            Content(parts = listOf(Part(text = "Recommend books or authors similar to: $userSearch")))
        ),
        generationConfig = GenerationConfig(
            temperature = 0.8,
            maxOutputTokens = 300
        )
    )

    try {

        // Call the Gemini API
        val response = googleGeminiService.getRecommendations(googleAPIKey, request)

        val recommendations = response.candidates
            .flatMap { it.content.parts }
            .firstOrNull()
            ?.text ?: "No recommendation found."

        println("Here are some suggestions based on $userSearch")
        println()
        println(recommendations)

    } catch (e: Exception) {

        e.printStackTrace()
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