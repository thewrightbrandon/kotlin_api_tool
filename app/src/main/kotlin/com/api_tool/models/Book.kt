package com.api_tool.models

data class Book(
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val ratings_average: Double
)