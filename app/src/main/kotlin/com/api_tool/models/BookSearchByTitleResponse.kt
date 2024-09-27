import com.api_tool.models.Book

data class BookSearchByTitleResponse(
    val numFound: Int,
    val start: Int,
    val docs: List<Book>
)