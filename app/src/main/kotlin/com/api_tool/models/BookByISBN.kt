package com.api_tool.models

data class BookByISBN(
    val title: String?,
    val authors: List<Author>?,
    val publish_date: String?
)