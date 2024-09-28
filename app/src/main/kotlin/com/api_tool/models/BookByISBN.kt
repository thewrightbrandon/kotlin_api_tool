package com.api_tool.models

data class BookByISBN(
    val title: String?,
    val author_name: List<Author>?,
    val publish_date: Int?,
    val number_of_pages_median: Int?,
)