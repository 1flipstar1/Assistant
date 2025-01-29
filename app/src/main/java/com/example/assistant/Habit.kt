package com.example.assistant

data class Habit(
    val name: String,
    var completedDays: Int = 0 // Количество выполненных дней
)
