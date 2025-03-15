package com.example.tudu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, "database.db", null, 2) {
    private val sql = arrayOf(
        "CREATE TABLE todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, dataLimite DATE, priority TEXT, status TEXT)",
        "INSERT INTO todos(title, description, dataLimite, priority, status) VALUES ('Exemplo', 'Descrição exemplo', '2025-03-20', 'Alta', 'Pendente')"
    )

    override fun onCreate(db: SQLiteDatabase?) {
        sql.forEach {
            db?.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS todos")
            onCreate(db)
        }
    }

    fun todoInsert(title: String, description: String, dataLimite: String, priority: Priority, status: Status): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("dataLimite", dataLimite)
            put("priority", priority.value)
            put("status", status.value)
        }
        val res = db.insert("todos", null, values)
        db.close()
        return res
    }

    fun updateToDo(id: Int, title: String, description: String, dataLimite: String, priority: Priority, status: Status): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("dataLimite", dataLimite)
            put("priority", priority.value)
            put("status", status.value)
        }
        val res = db.update("todos", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun deleteToDo(id: Int): Int {
        val db = this.writableDatabase
        val res = db.delete("todos", "id = ?", arrayOf(id.toString()))
        db.close()
        return res
    }

    fun listSelectAllToDos(): ArrayList<ToDo> {
        val db = this.readableDatabase

        // Alteração da consulta SQL para filtrar e ordenar
        val cursor = db.rawQuery(
            "SELECT * FROM todos WHERE status IN ('PENDENTE', 'Em Andamento', 'Atrasada') " +
                    "ORDER BY dataLimite ASC, priority DESC",
            null
        )
        val listaTodos: ArrayList<ToDo> = ArrayList()

        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val idIndex = cursor.getColumnIndex("id")
                val titleIndex = cursor.getColumnIndex("title")
                val descriptionIndex = cursor.getColumnIndex("description")
                val dataLimiteIndex = cursor.getColumnIndex("dataLimite")
                val priorityIndex = cursor.getColumnIndex("priority")
                val statusIndex = cursor.getColumnIndex("status")

                val id = cursor.getInt(idIndex)
                val title = cursor.getString(titleIndex)
                val description = cursor.getString(descriptionIndex)
                val dataLimite = cursor.getString(dataLimiteIndex)
                val priority = Priority.fromValue(cursor.getString(priorityIndex)) // Conversão segura
                val status = Status.fromValue(cursor.getString(statusIndex)) // Conversão segura

                listaTodos.add(ToDo(id, title, description, dataLimite, priority, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        android.util.Log.e("Lista", listaTodos.toString())
        return listaTodos
    }


    fun getToDoById(id: Int): ToDo? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todos WHERE id = ?", arrayOf(id.toString()))

        var todo: ToDo? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow("title"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val dataLimite = cursor.getString(cursor.getColumnIndexOrThrow("dataLimite"))

            val priorityIndex = cursor.getColumnIndex("priority")
            val statusIndex = cursor.getColumnIndex("status")

            val priority = Priority.fromValue(cursor.getString(priorityIndex))  // Correção aqui
            val status = Status.fromValue(cursor.getString(statusIndex))  // Correção aqui

            todo = ToDo(id, title, description, dataLimite, priority, status)
        }

        cursor.close()
        db.close()
        return todo
    }

    fun markToDoAsCompleted(id: Int): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("status", Status.DONE.value)
        }
        val res = db.update("todos", values, "id = ?", arrayOf(id.toString()))
        db.close()
        return res
    }
}
