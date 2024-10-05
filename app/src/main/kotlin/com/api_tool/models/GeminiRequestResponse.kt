package com.api_tool.models

data class GeminiRequest(
    val model: String = "gemini-1.5-flash",
    val prompt: String,
    // limit on response length
    val maxTokens: Int = 150,
    // controls the randomness of the output
    val temperature: Double = 0.7
)

data class GeminiResponse(
    val id: String,
    val objectType: String,
    val created: Long,
    val choices: List<Choice>
)

data class Choice(
    val text: String
)