package com.example.tudu

import android.content.Intent
import android.os.Bundle
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
            val listaTodos = db.listSelectAllToDos()
            adapter.updateList(listaTodos)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        val listaTodos = db.listSelectAllToDos()
        adapter = ToDoAdapter(this, listaTodos, addToDoLauncher)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.buttonInserir.setOnClickListener {
            val intent = Intent(this, AddToDo::class.java)
            addToDoLauncher.launch(intent)
        }

        binding.btnViewDone.setOnClickListener {
            val intent = Intent(this, Concluded::class.java)
            addToDoLauncher.launch(intent)
        }
    }
}