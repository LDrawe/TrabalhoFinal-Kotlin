package com.example.tudu

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper (context: Context): SQLiteOpenHelper(context, "database.db", null, 2) {
    private val sql = arrayOf(
        "CREATE TABLE todos (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, dataLimite String, priority TEXT, status TEXT)",
        "INSERT INTO todos(title, description, dataLimite, priority, status) VALUES ('Exemplo', 'Descrição exemplo', '2025-03-10', 'ALTA', 'PENDENTE')"
    )

    override fun onCreate(db: SQLiteDatabase?) {
        sql.forEach {
            db?.execSQL(it)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS todos") // Remove a tabela antiga para recriá-la corretamente
            onCreate(db) // Recria a tabela com a estrutura corrigida
        }
    }

    fun todoInsert(title: String, description: String, dataLimite: String, priority: Priority, status: Status): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("title", title)
            put("description", description)
            put("dataLimite", dataLimite)
            put("priority", priority.name)
            put("status", status.name)
        }

        val res = db.insert("todos", null, values)

        if (res == -1L) {
            android.util.Log.e("DBHelper", "Erro ao inserir no banco de dados!")
        } else {
            android.util.Log.i("DBHelper", "Inserção bem-sucedida: ID $res")
        }

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
        val cursor = db.rawQuery("SELECT * FROM todos", null)
        val listaTodos: ArrayList<ToDo> = ArrayList()

        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val idIndex = cursor.getColumnIndex("id")
                val titleIndex = cursor.getColumnIndex("title")
                val descriptionIndex = cursor.getColumnIndex("description")
                val dataLimiteIndex = cursor.getColumnIndex("dataLimite")

                val id = cursor.getInt(idIndex)
                val title = cursor.getString(titleIndex)
                val description = cursor.getString(descriptionIndex)
                val dataLimite = cursor.getString(dataLimiteIndex)

                listaTodos.add(ToDo(id, title, description, dataLimite))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        android.util.Log.e("Lista", listaTodos.toString())
        return listaTodos
    }
}