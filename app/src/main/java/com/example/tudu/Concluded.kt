package com.example.tudu

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tudu.databinding.ActivityConcludedBinding

class Concluded : AppCompatActivity() {
    private lateinit var binding: ActivityConcludedBinding
    private lateinit var adapter: ToDoAdapter

    private val addToDoLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val db = DBHelper(this)
            val listaTodos = db.listDone()
            adapter.updateList(listaTodos)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityConcludedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = DBHelper(this)
        val listaTodos = db.listDone()
        adapter = ToDoAdapter(this, listaTodos, addToDoLauncher)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }
}