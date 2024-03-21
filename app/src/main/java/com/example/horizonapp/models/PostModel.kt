package com.example.horizonapp.models

data class PostModel(
    val name: String,
    val category: String,
    val description: String,
    val imageAlpha: String, // New property for image alpha
    val rating: Double
)
