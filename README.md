# kotlin_api_tool
API integration tool to assist me in learning the fundamentals of Kotlin.

### Implementation (so far)

- Success in setting up Kotlin/Gradle environment (build.gradle.kts)
- Defined an API interface to handle Open Library API request and responses
- Defined several data classes to handle the expected response from the OL API
- Successful responses from four different API calls to the Open Library API
- Take user input to search for a book by title, author, or ISBN
- Take user input to filter book results by params set by the API
- Added a limit to how many results are returned to the user
- Added coroutines to allow for asynchronicity when making requests
- Manipulated the response data from the ISBN book search which was used in a subsequent request to fetch Author Details
- Added AI component (Google Gemini) that will return book suggestions based on book title or author search

### Pain Points

- Fully understanding how the API interface and data classes work together
- How to work with null values and how  to protect runtime from those values, especially in the context of API responses

### Stretch Goals

- Post process Gemini AI response so the last line of response is removed due to max tokens reached
- Add conditional in the recommendations function that adjusts AI prompt text depending on user search query, recommend other books for book title search, recommend authors for author search
- Combine Book with Book data classes and Author with Author data classes to clean up file structure
