package com.example.assistant

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetDialog
import android.widget.TextView



class MainActivity : AppCompatActivity() {

    private lateinit var addNoteButton: Button
    private lateinit var addHabitButton: Button
    private lateinit var settingsButton: ImageButton
    private lateinit var addReminderButton: Button
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var remindersRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var habitsAdapter: HabitsAdapter
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var remindersAdapter: RemindersAdapter
    private lateinit var dialogAllRemAdapter: RemindersAdapter
//    private lateinit var dialogRemView: androidx.constraintlayout.widget.ConstraintLayout



    private val sharedPreferences by lazy {
        getSharedPreferences("NotesApp", Context.MODE_PRIVATE)
    }

    private val notes = mutableListOf<Note>()
    private val habits = mutableListOf<Habit>()
    private val reminders = mutableListOf<Reminder>()
    private var visibleRemindersCount = 4
    private lateinit var showMoreButton: Button // Добавляем поле

    private val adviceList = listOf(
        "Ставьте четкие цели на день, неделю и месяц.",
        "Используйте метод Pomodoro: 25 минут работы, 5 минут отдыха.",
        "Начинайте день с самых важных задач.",
        "Планируйте день заранее, лучше с вечера.",
        "Не перегружайте список дел — ставьте реалистичные задачи.",
        "Используйте правило 2 минут: если дело можно сделать за 2 минуты, сделайте сразу.",
        "Делегируйте задачи, которые могут выполнить другие.",
        "Разбивайте большие задачи на мелкие шаги.",
        "Избегайте многозадачности, фокусируйтесь на одном деле.",
        "Ограничивайте время на каждую задачу, устанавливая таймер.",
        "Выключайте уведомления во время работы.",
        "Проверяйте почту и мессенджеры не чаще 2-3 раз в день.",
        "Используйте чек-листы для повторяющихся задач.",
        "Проводите ревизию задач раз в неделю.",
        "Не бойтесь говорить 'нет' ненужным встречам и просьбам.",
        "Записывайте все задачи, чтобы не держать их в голове.",
        "Оставляйте буферное время между задачами.",
        "Используйте цифровые планировщики или бумажные ежедневники.",
        "Работайте в блоках времени: группируйте похожие задачи.",
        "Автоматизируйте рутинные процессы.",
        "Определите свой самый продуктивный период дня и планируйте важные задачи в это время.",
        "Ставьте дедлайны даже для задач без сроков.",
        "Следите за уровнем энергии, высыпайтесь и отдыхайте.",
        "Используйте принцип 80/20: 20% действий дают 80% результата.",
        "Учитесь быстро принимать решения и не зацикливаться на мелочах.",
        "Запланируйте время на отдых и перерывы.",
        "Создайте удобное рабочее место без отвлекающих факторов.",
        "Не откладывайте неприятные задачи, решайте их сразу.",
        "Проводите цифровую детоксикацию, ограничивая время в соцсетях.",
        "Используйте цветовое кодирование задач для приоритизации.",
        "В конце дня анализируйте, что удалось выполнить.",
        "Планируйте свободное время так же, как и рабочее.",
        "Периодически пересматривайте свои цели и планы.",
        "Отмечайте завершенные задачи, чтобы видеть прогресс.",
        "Учитесь отказывать от лишних встреч и мероприятий.",
        "Развивайте самодисциплину и привычку завершать начатое.",
        "Пишите списки задач коротко и конкретно.",
        "Ограничьте время на чтение новостей и соцсетей.",
        "Используйте приложения для учета времени (Toggl, RescueTime).",
        "Практикуйте утренние ритуалы, чтобы начать день продуктивно.",
        "Старайтесь заканчивать день в одно и то же время.",
        "Пересматривайте список дел и удаляйте ненужные задачи.",
        "Используйте систему 'Must, Should, Want' для приоритизации.",
        "Не пытайтесь быть идеальным — лучше завершить задачу, чем делать идеально.",
        "Выделяйте 10-15 минут в день для планирования будущих задач.",
        "Чередуйте сложные задачи с простыми для баланса.",
        "Учитесь управлять стрессом и расслабляться.",
        "Фокусируйтесь на результате, а не на занятости."
    )



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


            //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        showMoreButton = findViewById(R.id.showMoreRemindersButton)
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

        updateRemindersView()


        showMoreButton.setOnClickListener { showAllRemindersDialog() }


        dialogAllRemAdapter = RemindersAdapter(reminders, onCompleteClick = { position ->
            reminders[position].isCompleted = !reminders[position].isCompleted
            saveReminders()
            updateRemindersView()
            dialogAllRemAdapter.notifyItemChanged(position)
        }, onDeleteClick = { position ->
            reminders.removeAt(position)
            saveReminders()
            updateRemindersView()
            updateAllRemindersView()
        })


        val adviceTextView = findViewById<TextView>(R.id.adviceTextView)
        val newAdviceButton = findViewById<Button>(R.id.newAdviceButton)



        fun setRandomAdvice() {
            val randomAdvice = adviceList.random()
            adviceTextView.text = randomAdvice
        }

        setRandomAdvice()
        newAdviceButton.setOnClickListener { setRandomAdvice() }




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


    private fun showAllRemindersDialog(state: Int = 0) {
        val dialogRemView = layoutInflater.inflate(R.layout.dialog_all_reminders, null)
        val dialogRecyclerView = dialogRemView.findViewById<RecyclerView>(R.id.dialogRemindersRecyclerView)

        dialogRecyclerView.layoutManager = LinearLayoutManager(this)
        dialogRecyclerView.adapter = dialogAllRemAdapter

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(dialogRemView)
        if (state == 0) {
        bottomSheetDialog.show() }
        else {
            bottomSheetDialog.dismiss()
        }
    }


    private fun updateAllRemindersView() {
        val currentVisibleReminders = reminders.take(visibleRemindersCount).toMutableList()

        // Обновляем данные в адаптере
        dialogAllRemAdapter.updateData(currentVisibleReminders)

        if (reminders.size == 0) {
            showAllRemindersDialog(1)
        }

    }



    private fun updateRemindersView() {
        val showMoreButton = findViewById<Button>(R.id.showMoreRemindersButton)
        val currentVisibleReminders = reminders.take(visibleRemindersCount).toMutableList()

        // Обновляем данные в адаптере
        remindersAdapter.updateData(currentVisibleReminders)

        // Показываем или скрываем кнопку "Показать больше"
        if (3 < reminders.size) {
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
                    updateRemindersView()

                    visibleRemindersCount += 1

                    // Уведомляем адаптер
                    remindersAdapter.notifyDataSetChanged()

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
