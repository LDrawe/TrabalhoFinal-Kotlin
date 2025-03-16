package com.example.tudu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tudu.databinding.ActivityAddToDoBinding
import java.text.SimpleDateFormat
import java.util.Locale

class AddToDo : AppCompatActivity() {

    private lateinit var binding: ActivityAddToDoBinding
    private var todoId: Int? = null
    private lateinit var db: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddToDoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        db = DBHelper(this)
        applyDateMask(binding.editDataLimite)

        setupSpinners()

        // Verifica se recebeu um ID via Intent (modo edição)
        todoId = intent.getIntExtra("TODO_ID", -1).takeIf { it != -1 }

        if (todoId != null) {
            val todo = loadToDoData(todoId!!)
            if (todo?.status == Status.DONE)
                binding.btnComplete.visibility = View.GONE
        } else {
            binding.btnDelete.visibility = View.GONE
            binding.btnComplete.visibility = View.GONE
        }

        binding.btnSave.setOnClickListener { saveToDo() }
        binding.btnDelete.setOnClickListener { deleteToDo() }
        binding.btnComplete.setOnClickListener { markAsCompleted() }
    }

    private fun applyDateMask(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return
                isUpdating = true

                val newText = s.toString().filter { it.isDigit() }
                val formattedText = StringBuilder()

                var index = 0
                for (m in mask) {
                    if (index >= newText.length) break
                    if (m == '#') {
                        formattedText.append(newText[index])
                        index++
                    } else {
                        formattedText.append(m)
                    }
                }

                editText.setText(formattedText)
                editText.setSelection(formattedText.length)
                isUpdating = false
            }
        })
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun convertToISOFormat(date: String): String? {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = inputFormat.parse(date)

            outputFormat.format(parsedDate!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun formatarDataParaExibicao(dataBanco: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val parsedDate = inputFormat.parse(dataBanco)
            parsedDate?.let { outputFormat.format(it) } ?: dataBanco
        } catch (e: Exception) {
            dataBanco // Retorna como está se houver erro
        }
    }

    private fun setupSpinners() {

        val priorityAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item, Priority.entries.map { it.value }
        )
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priority.adapter = priorityAdapter

        val statusAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            Status.entries.map { it.value }
        )

        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.status.adapter = statusAdapter
    }

    private fun loadToDoData(id: Int): ToDo? {
        val todo = db.getToDoById(id)
        if (todo != null) {
            binding.editTitle.setText(todo.title)
            binding.editDescription.setText(todo.description)
            binding.editDataLimite.setText(formatarDataParaExibicao(todo.dataLimite))
            binding.priority.setSelection(Priority.entries.indexOf(todo.priority))
            binding.status.setSelection(Status.entries.indexOf(todo.status))
        }
        return todo
    }

    private fun saveToDo() {
        val title = binding.editTitle.text.toString().trim()
        val description = binding.editDescription.text.toString().trim()
        val dueDate = binding.editDataLimite.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidDate(dueDate)) {
            Toast.makeText(this, "Data inválida!", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedPriority = Priority.entries[binding.priority.selectedItemPosition]
        val selectedStatus = Status.entries[binding.status.selectedItemPosition]

        if (todoId == null) {
            // Criar novo To-Do
            val res = db.todoInsert(title, description,
                convertToISOFormat(dueDate).toString(), selectedPriority, selectedStatus)
            if (res > 0) {
                Toast.makeText(this, "Tarefa criada!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao criar tarefa!", Toast.LENGTH_SHORT).show()
            }
        } else {
            val res = db.updateToDo(todoId!!, title, description,
                convertToISOFormat(dueDate).toString(), selectedPriority, selectedStatus)
            if (res > 0) {
                Toast.makeText(this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao atualizar tarefa!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteToDo() {
        if (todoId != null) {
            val res = db.deleteToDo(todoId!!)
            if (res > 0) {
                Toast.makeText(this, "Tarefa excluída!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao excluir tarefa!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun markAsCompleted() {
        if (todoId != null) {
            val res = db.markToDoAsCompleted(todoId!!)
            if (res > 0) {
                Toast.makeText(this, "Tarefa concluída!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Erro ao concluir tarefa!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
