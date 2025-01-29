package com.example.assistant

import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RemindersAdapter(
    private val reminders: MutableList<Reminder>,
    private val onCompleteClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<RemindersAdapter.ReminderViewHolder>() {

    class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reminderTextView: TextView = itemView.findViewById(R.id.reminderTextView)
        val completeButton: Button = itemView.findViewById(R.id.completeReminderButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reminder, parent, false)
        return ReminderViewHolder(view)
    }

    fun updateData(newReminders: List<Reminder>) {
        reminders.clear()
        reminders.addAll(newReminders)
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]

        // Устанавливаем текст напоминания
        holder.reminderTextView.text = reminder.text

        // Зачёркиваем текст, если напоминание выполнено
        holder.reminderTextView.paintFlags =
            if (reminder.isCompleted) holder.reminderTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            else holder.reminderTextView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

        // Обновляем кнопку (○ или ✔)
        holder.completeButton.text = if (reminder.isCompleted) "☑" else "☐"

        // Обработка одиночного и двойного нажатия
        var lastClickTime = 0L
        val doubleTapTimeout = 300L // Время между тапами для двойного нажатия

        holder.completeButton.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime < doubleTapTimeout) {
                // Двойной тап: удаление
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    onDeleteClick(actualPosition)
                }
            } else {
                // Одиночный тап: завершение напоминания
                val actualPosition = holder.adapterPosition
                if (actualPosition != RecyclerView.NO_POSITION) {
                    onCompleteClick(actualPosition)
                }
            }
            lastClickTime = currentTime
        }
    }


    override fun getItemCount(): Int = reminders.size
}