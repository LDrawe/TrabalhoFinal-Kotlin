package com.example.tudu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
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

    fun applyDateMask(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isUpdating) return

                isUpdating = true

                var newText = s.toString().filter { it.isDigit() }
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

    fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

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
        applyDateMask(binding.editDataLimite)


        val db = DBHelper(this)

        val spinnerPriority = binding.priority
        val spinnerStatus = binding.status

        // Configurando o Spinner de Prioridade
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Priority.entries.map { it.value }) // Pegamos o valor legível do enum

        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPriority.adapter = priorityAdapter

        spinnerPriority.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedPriority = Priority.entries[position] // Obtém o valor do enum correspondente
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Configurando o Spinner de Status
        val statusAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            Status.entries.map { it.value } // Pegamos o valor legível do enum
        )
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = Status.entries[position] // Obtém o valor do enum correspondente
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.btnSave.setOnClickListener {
            val title = binding.editTitle.text.toString().trim()
            val description = binding.editDescription.text.toString().trim()
            val dataLimite = binding.editDataLimite.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || dataLimite.isEmpty()) {
                Toast.makeText(applicationContext, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(dataLimite)) {
                Toast.makeText(applicationContext, "Data inválida!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Obtendo os valores selecionados dos Spinners
            val selectedPriority = Priority.entries[binding.priority.selectedItemPosition] // Enum de Prioridade
            val selectedStatus = Status.entries[binding.status.selectedItemPosition] // Enum de Status

            // Inserindo no banco de dados
            val res = db.todoInsert(title, description, dataLimite, selectedPriority, selectedStatus)

            if (res > 0) {
                Toast.makeText(applicationContext, "Tarefa criada!", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK) // Define o resultado da atividade
                finish() // Volta para a tela anterior
            } else {
                Toast.makeText(applicationContext, "Erro ao criar tarefa: $res", Toast.LENGTH_SHORT).show()
            }
        }
    }
}