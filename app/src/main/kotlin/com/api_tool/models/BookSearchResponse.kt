import com.api_tool.models.Book

data class BookSearchResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)