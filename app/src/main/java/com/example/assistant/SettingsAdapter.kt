package com.example.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SettingsAdapter(
    private val habits: MutableList<Habit>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.HabitSettingsViewHolder>() {

    inner class HabitSettingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitNameTextView: TextView = itemView.findViewById(R.id.habitSettingsTextView)
        val deleteButton: Button = itemView.findViewById(R.id.deleteHabitButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitSettingsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit_settings, parent, false)
        return HabitSettingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitSettingsViewHolder, position: Int) {
        val habit = habits[position]
        holder.habitNameTextView.text = habit.name

        holder.deleteButton.setOnClickListener {
            onDeleteClick(position) // Вызываем обработчик из MainActivity
        }
    }

    override fun getItemCount(): Int = habits.size
}


