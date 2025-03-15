package com.example.tudu

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.tudu.databinding.ListItemTodoBinding
import java.text.SimpleDateFormat
import java.util.*

class ToDoAdapter(
    private val context: Context,
    private val toDoList: ArrayList<ToDo>,
    private val editToDoLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {

    inner class ToDoViewHolder(val binding: ListItemTodoBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val toDo = toDoList[adapterPosition]
                val intent = Intent(context, AddToDo::class.java).apply {
                    putExtra("TODO_ID", toDo.id)
                }
                editToDoLauncher.launch(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        val binding = ListItemTodoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ToDoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        val toDo = toDoList[position]
        // Formatar a dataLimite
        val formattedDate = formatDate(toDo.dataLimite)
        holder.binding.textDataLimite.text = formattedDate

        // Definir os valores nos TextViews
        holder.binding.textTitle.text = toDo.title
        holder.binding.textDescription.text = toDo.description

        // Definir status e prioridade com cores de fundo
        holder.binding.textStatus.text = toDo.status.value
        holder.binding.textPriority.text = toDo.priority.value

        // Definir as cores de fundo com base no status e prioridade
        holder.binding.textStatus.setBackgroundColor(getStatusColor(toDo.status))
        holder.binding.textPriority.setBackgroundColor(getPriorityColor(toDo.priority))
    }

    override fun getItemCount(): Int {
        return toDoList.size
    }

    // Função para formatar a data
    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date: Date? = inputFormat.parse(dateString)
        return if (date != null) {
            outputFormat.format(date)
        } else {
            "Data Inválida"
        }
    }

    // Função para obter a cor do status
    private fun getStatusColor(status: Status): Int {
        return when (status) {
            Status.PENDING -> ContextCompat.getColor(context, R.color.status_pending)
            Status.IN_PROGRESS -> ContextCompat.getColor(context, R.color.status_in_progress)
            Status.DELAYED -> ContextCompat.getColor(context, R.color.status_delayed)
            Status.DONE -> ContextCompat.getColor(context, R.color.status_done)
        }
    }

    // Função para obter a cor da prioridade
    private fun getPriorityColor(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> ContextCompat.getColor(context, R.color.priority_low)
            Priority.MEDIUM -> ContextCompat.getColor(context, R.color.priority_medium)
            Priority.HIGH -> ContextCompat.getColor(context, R.color.priority_high)
        }
    }

    class ToDoDiffCallback(private val oldList: List<ToDo>, private val newList: List<ToDo>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Verifica se o item é o mesmo, geralmente você compara o ID
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // Verifica se o conteúdo do item é o mesmo
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }


    // Função para atualizar a lista
    fun updateList(newList: ArrayList<ToDo>) {
        // Verifica as diferenças entre o novo e o antigo
        val diffResult = DiffUtil.calculateDiff(ToDoDiffCallback(toDoList, newList))

        // Atualiza a lista de dados
        toDoList.clear()
        toDoList.addAll(newList)

        // Notifica apenas as mudanças reais na RecyclerView
        diffResult.dispatchUpdatesTo(this)
    }
}