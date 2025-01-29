package com.example.assistant

data class Note(
    val content: String
) {
    override fun toString(): String {
        return content // Возвращаем только текст заметки
    }
}
