import com.api_tool.book_classes.Book

data class BookSearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)