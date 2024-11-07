package com.example.finance.domain.repository

import com.example.finance.data.entity.BudgetEntity
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    suspend fun insertBudget(budget: BudgetEntity): Long
    fun getBudget(): Flow<BudgetEntity?>
}