package com.example.assistant

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(
    private val notes: MutableList<Note>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteTextView: TextView = itemView.findViewById(R.id.noteTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.noteTextView.text = notes[position].content

        // Устанавливаем видимость кнопки удаления
        holder.deleteButton.visibility = if (holder.adapterPosition == position) View.VISIBLE else View.GONE

        holder.noteTextView.setOnClickListener {
            // Обработка двойного нажатия
            if (holder.deleteButton.visibility == View.VISIBLE) {
                holder.deleteButton.visibility = View.GONE // Скрываем кнопку, если она уже видима
            } else {
                holder.deleteButton.visibility = View.VISIBLE // Показываем кнопку для текущей заметки
            }
            notifyDataSetChanged() // Обновляем адаптер
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(holder.adapterPosition)
            holder.deleteButton.visibility = View.GONE // Скрываем кнопку после удаления
            notifyDataSetChanged() // Обновляем адаптер
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}
