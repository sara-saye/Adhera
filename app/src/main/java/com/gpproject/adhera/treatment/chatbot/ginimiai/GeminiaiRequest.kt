package com.gpproject.adhera.treatment.chatbot.ginimiai

import com.google.gson.annotations.SerializedName

data class GeminiRequest(

    @SerializedName("contents")
    val contents: List<ContentPart>,

    @SerializedName("generationConfig")
    val generationConfig: GeminiConfig?=null
)

data class ContentPart(

    @SerializedName("role")
    val role:String?,

    @SerializedName("parts")
    val parts:List<TextPart>
)

data class TextPart(

    @SerializedName("text")
    val text:String
)

data class GeminiConfig(

    @SerializedName("temperature")
    val temperature:Float
)

data class GeminiResponse(

    @SerializedName("candidates")
    val candidates:List<Candidate>?
)

data class Candidate(

    @SerializedName("content")
    val content:ContentPart?
)