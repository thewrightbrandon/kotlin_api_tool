package com.api_tool.book_classes

data class BookByISBN(
    val title: String?,
    val authors: List<Author>?,
    val publish_date: String?
)