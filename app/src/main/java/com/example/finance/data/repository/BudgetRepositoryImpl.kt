package com.example.finance.data.repository

import com.example.finance.data.dao.BudgetDao
import com.example.finance.data.entity.BudgetEntity
import com.example.finance.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class BudgetRepositoryImpl(private val budgetDao: BudgetDao) : BudgetRepository {
    override suspend fun insertBudget(budget: BudgetEntity): Long {
        return budgetDao.insertBudget(budget)
    }

    override fun getBudget(): Flow<BudgetEntity?> {
        return budgetDao.getBudget()
    }
}