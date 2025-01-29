package com.example.assistant

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import com.example.assistant.SettingsAdapter
import androidx.cardview.widget.CardView
import android.view.View




//теперь блок увеличивается через раз, но только если нажать на кнопку какого нибудь напоминания. Мне кажется нужно добавить обновление блока после добавления напоминания

class MainActivity : AppCompatActivity() {

    private lateinit var addNoteButton: Button
    private lateinit var addHabitButton: Button
    private lateinit var settingsButton: Button
    private lateinit var addReminderButton: Button
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var remindersRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var remindersAdapter: RemindersAdapter

    private val sharedPreferences by lazy {
        getSharedPreferences("NotesApp", Context.MODE_PRIVATE)
    }

    private val notes = mutableListOf<Note>()
    private val habits = mutableListOf<Habit>()
    private val reminders = mutableListOf<Reminder>()
    private var visibleRemindersCount = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addNoteButton = findViewById(R.id.addNoteButton)
        addHabitButton = findViewById(R.id.addHabitButton)
        settingsButton = findViewById(R.id.settingsButton)
        addReminderButton = findViewById(R.id.addReminderButton)
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        habitsRecyclerView = findViewById(R.id.habitsRecyclerView)
        remindersRecyclerView = findViewById(R.id.remindersRecyclerView)

        // Инициализация RecyclerView для заметок
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(notesRecyclerView.context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)
        notesRecyclerView.addItemDecoration(dividerItemDecoration)

        loadNotes()
        notesAdapter = NotesAdapter(notes) { position ->
            notes.removeAt(position)
            saveNotes()
            notesAdapter.notifyItemRemoved(position)
        }
        notesRecyclerView.adapter = notesAdapter

        loadHabits()
        habitsAdapter = HabitsAdapter(habits, onCompleteClick = { position ->
            habits[position].completedDays++
            saveHabits()
            habitsAdapter.notifyItemChanged(position)
        }, onDeleteClick = { position ->
            habits.removeAt(position)
            saveHabits()
            habitsAdapter.notifyItemRemoved(position)
        })
        habitsRecyclerView.layoutManager = LinearLayoutManager(this)
        habitsRecyclerView.adapter = habitsAdapter

        settingsAdapter = SettingsAdapter(habits) { position ->
            if (position in habits.indices) {
                habits.removeAt(position)
                saveHabits()
                settingsAdapter.notifyItemRemoved(position)
                habitsAdapter.notifyDataSetChanged()
            }
        }

        loadReminders()

        val showMoreButton = findViewById<Button>(R.id.showMoreRemindersButton)
        showMoreButton.setOnClickListener {
            // Увеличиваем количество видимых напоминаний на 4
            visibleRemindersCount += 2

            // Обновляем отображение
            updateRemindersView()
        }



        remindersAdapter = RemindersAdapter(reminders,
            onCompleteClick = { position ->
                reminders[position].isCompleted = !reminders[position].isCompleted
                saveReminders()
                remindersAdapter.notifyItemChanged(position)
            },
            onDeleteClick = { position ->
                reminders.removeAt(position)
                saveReminders()
                remindersAdapter.notifyItemRemoved(position)
            }
        )


        remindersRecyclerView.layoutManager = LinearLayoutManager(this)
        remindersRecyclerView.adapter = remindersAdapter



        addNoteButton.setOnClickListener { showAddNoteDialog() }
        addHabitButton.setOnClickListener { showAddHabitDialog() }
        settingsButton.setOnClickListener { showSettingsDialog() }
        addReminderButton.setOnClickListener { showAddReminderDialog() }
    }

    private fun toggleReminderCompletion(position: Int) {
        reminders[position].isCompleted = !reminders[position].isCompleted
        saveReminders() // Сохраняем изменения
        remindersAdapter.notifyItemChanged(position) // Обновляем адаптер
    }

    private fun saveNotes() {
        val editor = sharedPreferences.edit()
        val notesString = notes.joinToString(",") { it.content }
        editor.putString("saved_notes", notesString)
        editor.apply()
    }

    private fun loadNotes() {
        val savedNotesString = sharedPreferences.getString("saved_notes", "")
        if (!savedNotesString.isNullOrEmpty()) {
            val savedNotes = savedNotesString.split(",").map { Note(it) }
            notes.addAll(savedNotes)
        }
    }




    private fun saveHabits() {
        val editor = sharedPreferences.edit()
        val habitsString = habits.joinToString(";") { "${it.name},${it.completedDays}" }
        editor.putString("saved_habits", habitsString)
        editor.apply()
    }

    private fun loadHabits() {
        val savedHabitsString = sharedPreferences.getString("saved_habits", "")
        if (!savedHabitsString.isNullOrEmpty()) {
            val savedHabits = savedHabitsString.split(";").mapNotNull { habitData ->
                val parts = habitData.split(",")
                if (parts.size == 2) {
                    try {
                        Habit(parts[0], parts[1].toInt())
                    } catch (e: NumberFormatException) {
                        null // Игнорируем некорректные данные
                    }
                } else {
                    null // Игнорируем некорректные данные
                }
            }
            habits.addAll(savedHabits)
        }
    }


    private fun saveReminders() {
        val editor = sharedPreferences.edit()
        val remindersString = reminders.joinToString(";") { "${it.text},${it.isCompleted}" }
        editor.putString("saved_reminders", remindersString)
        editor.apply()
    }

    private fun loadReminders() {
        val savedRemindersString = sharedPreferences.getString("saved_reminders", "")
        reminders.clear() // Убедитесь, что список очищен перед загрузкой
        if (!savedRemindersString.isNullOrEmpty()) {
            val savedReminders = savedRemindersString.split(";").mapNotNull { reminderData ->
                val parts = reminderData.split(",")
                if (parts.size == 2) {
                    try {
                        Reminder(parts[0], parts[1].toBoolean())
                    } catch (e: Exception) {
                        null // Игнорируем некорректные данные
                    }
                } else {
                    null
                }
            }
            reminders.addAll(savedReminders)
        }
    }

    private fun updateRemindersView() {
        val showMoreButton = findViewById<Button>(R.id.showMoreRemindersButton)
        val currentVisibleReminders = reminders.take(visibleRemindersCount).toMutableList()

        // Обновляем данные в адаптере
        remindersAdapter.updateData(currentVisibleReminders)

        // Показываем или скрываем кнопку "Показать больше"
        if (visibleRemindersCount < reminders.size) {
            showMoreButton.visibility = View.VISIBLE
        } else {
            showMoreButton.visibility = View.GONE
        }
    }



    private fun showAddNoteDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_note, null)
        val noteEditText = dialogView.findViewById<EditText>(R.id.dialogNoteEditText)

        AlertDialog.Builder(this)
            .setTitle("Добавить заметку")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val text = noteEditText.text.toString()
                if (text.isNotBlank()) {
                    notes.add(Note(text))
                    saveNotes()
                    notesAdapter.notifyItemInserted(notes.size - 1)
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showAddHabitDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        val habitEditText = dialogView.findViewById<EditText>(R.id.dialogHabitEditText)

        AlertDialog.Builder(this)
            .setTitle("Добавить привычку")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val name = habitEditText.text.toString()
                if (name.isNotBlank()) {
                    habits.add(Habit(name))
                    saveHabits()
                    habitsAdapter.notifyItemInserted(habits.size - 1)
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun showAddReminderDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_reminder, null)
        val reminderEditText = dialogView.findViewById<EditText>(R.id.dialogReminderEditText)

        AlertDialog.Builder(this)
            .setTitle("Добавить напоминание")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val text = reminderEditText.text.toString()
                if (text.isNotBlank()) {
                    // Добавляем новое напоминание
                    reminders.add(Reminder(text))
                    saveReminders() // Сохраняем напоминания
                    reminders.add(Reminder(text))
                    saveReminders()
                    updateRemindersView()

                    // Уведомляем адаптер
                    remindersAdapter.notifyItemInserted(reminders.size - 1)

                    // Обновляем RecyclerView и CardView
                    remindersRecyclerView.requestLayout()
                    val parentCardView = findViewById<CardView>(R.id.remindersCardView)
                    parentCardView.requestLayout()
                }
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }









    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val settingsRecyclerView = dialogView.findViewById<RecyclerView>(R.id.settingsRecyclerView)

        settingsRecyclerView.layoutManager = LinearLayoutManager(this)
        settingsRecyclerView.adapter = settingsAdapter

        AlertDialog.Builder(this)
            .setTitle("Настройки привычек")
            .setView(dialogView)
            .setPositiveButton("Закрыть") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
