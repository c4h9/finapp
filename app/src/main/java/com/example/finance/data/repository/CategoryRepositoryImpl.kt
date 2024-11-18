package com.example.finance.data.repository

import com.example.finance.data.dao.CategoryDao
import com.example.finance.data.entity.CategoryEntity
import com.example.finance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryImpl(private val categoryDao: CategoryDao) : CategoryRepository {
    override suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    override fun getAllCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    override suspend fun deleteCategory(categoryName: String) {
        return categoryDao.deleteCategory(categoryName)
    }

    override suspend fun doesCategoryExist(categoryName: String): Boolean {
        return categoryDao.doesCategoryExist(categoryName) > 0
    }

}