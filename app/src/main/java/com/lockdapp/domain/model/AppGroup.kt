package com.lockdapp.domain.model

data class AppGroup(
    val id: Long,
    val name: String,
    val packages: List<String>,
)
