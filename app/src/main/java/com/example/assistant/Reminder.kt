package com.example.assistant

data class Reminder(
    val text: String,
    var isCompleted: Boolean = false // Для отслеживания статуса выполнения
)
