package com.example.tudu

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tudu.databinding.ListItemTodoBinding

class ToDoAdapter(private val context: Context, private val toDoList: ArrayList<ToDo>) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    inner class ToDoViewHolder(val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = ListItemTodoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val toDo = toDoList[position]
        holder.binding.textTitle.text = toDo.title
        holder.binding.textDescription.text = toDo.description
        holder.binding.textDataLimite.text = toDo.dataLimite
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    fun updateList(newList: ArrayList<ToDo>) {
        toDoList.clear()
        toDoList.addAll(newList)
        notifyItemInserted(newList.size)
    }
}

