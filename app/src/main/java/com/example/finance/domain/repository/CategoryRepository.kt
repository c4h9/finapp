package com.example.finance.domain.repository

import com.example.finance.data.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun insertCategory(category: CategoryEntity): Long
    fun getAllCategories(): Flow<List<CategoryEntity>>
    suspend fun deleteCategory(categoryName: String)
}