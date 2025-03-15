package com.example.tudu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tudu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ToDoAdapter

    private val addToDoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val db = DBHelper(this)
            val listaTodos = db.listSelectAllToDos() // Recupera todos os ToDos do banco
            adapter.updateList(listaTodos) // Atualiza a lista no adaptador
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        val listaTodos = db.listSelectAllToDos()
        Log.d("TesteBancoDedados", "Lista de ToDos: $listaTodos")
        adapter = ToDoAdapter(this, listaTodos, addToDoLauncher)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this) // Define o LayoutManager

        binding.buttonInserir.setOnClickListener {
            val intent = Intent(this, AddToDo::class.java)
            addToDoLauncher.launch(intent)
        }
    }
}