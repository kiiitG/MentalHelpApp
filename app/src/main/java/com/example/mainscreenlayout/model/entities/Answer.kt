package com.example.mainscreenlayout.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "answers")
data class Answer(
    val answers: List<String>,
    val depressed: Int,
    val anxious: Int,
    val stress: Int,
    val problem: String,
    @PrimaryKey
    val id: String = UUID.randomUUID().toString()
)