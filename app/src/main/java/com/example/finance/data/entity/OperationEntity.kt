package com.example.finance.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryName: String,
    val iconName: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)
