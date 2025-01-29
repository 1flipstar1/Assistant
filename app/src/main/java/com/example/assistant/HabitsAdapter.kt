package com.example.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class HabitsAdapter(
    private val habits: MutableList<Habit>,
    private val onCompleteClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    // ViewHolder для элемента привычки
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val habitNameTextView: TextView = itemView.findViewById(R.id.habitTextView)
        val completedDaysTextView: TextView = itemView.findViewById(R.id.completedDaysTextView)
        val completeButton: Button = itemView.findViewById(R.id.completeButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]

        // Привязка данных к элементам View
        holder.habitNameTextView.text = habit.name
        holder.completedDaysTextView.text = "${habit.completedDays} дней"

        // Обработка нажатий на кнопку выполнения
        holder.completeButton.setOnClickListener {
            onCompleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return habits.size
    }
}

