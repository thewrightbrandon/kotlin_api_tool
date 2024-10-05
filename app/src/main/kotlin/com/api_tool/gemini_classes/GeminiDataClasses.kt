package com.api_tool.gemini_classes

// data class for the request body
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig
)

// data class for individual content objects
data class Content(
    val parts: List<Part>
)

// data class for individual parts
data class Part(
    val text: String
)

// data class for generation configuration
data class GenerationConfig(
    // Controls the randomness of the output
    val temperature: Double,
    // Maximum number of output tokens
    val maxOutputTokens: Int
)

// data class for the response from the API
data class GeminiResponse(
    val candidates: List<Candidate>
)

// Data class for individual candidates in the response
data class Candidate(
    val content: CandidateContent,
)

data class CandidateContent(
    val parts: List<PartResponse>,
)

// Data class for individual parts in the candidate content
data class PartResponse(
    val text: String
)