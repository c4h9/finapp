package com.example.finance.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "operations")
data class OperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val categoryName: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

