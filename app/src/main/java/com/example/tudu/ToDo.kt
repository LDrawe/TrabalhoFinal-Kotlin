package com.example.tudu

import android.os.Parcel
import android.os.Parcelable

enum class Priority(val value: String) {
    LOW("Baixa"), MEDIUM("Média"), HIGH("Alta");

    companion object {
        fun fromValue(value: String): Priority {
            return entries.find { it.value == value } ?: LOW
        }
    }
}

enum class Status(val value: String) {
    PENDING("Pendente"), IN_PROGRESS("Em Andamento"), DONE("Concluída"), DELAYED("Atrasada");

    companion object {
        fun fromValue(value: String): Status {
            return entries.find { it.value == value } ?: PENDING
        }
    }
}

class ToDo(
    val id: Int = 1,
    val title: String = "",
    val description: String = "",
    val dataLimite: String = "",
    val priority: Priority = Priority.LOW,
    val status: Status = Status.PENDING
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        Priority.fromValue(parcel.readString() ?: "low"),
        Status.fromValue(parcel.readString() ?: "new")
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeString(dataLimite)
        parcel.writeString(priority.value)
        parcel.writeString(status.value)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ToDo> {
        override fun createFromParcel(parcel: Parcel): ToDo {
            return ToDo(parcel)
        }

        override fun newArray(size: Int): Array<ToDo?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "ToDo(ID='$id', title='$title', description='$description', dataLimite='$dataLimite', priority='${priority.value}', status='${status.value}')"
    }
}
