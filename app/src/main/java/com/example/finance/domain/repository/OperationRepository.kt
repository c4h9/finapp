package com.example.finance.domain.repository

import com.example.finance.data.dao.CategorySum
import com.example.finance.data.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
    suspend fun insertOperation(operation: OperationEntity): Long
    fun getAllOperations(): Flow<List<OperationEntity>>
    fun getIncomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?>
    fun getOutcomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?>
    suspend fun deleteOperationsByIds(operationIds: List<Int>)
    suspend fun deleteOperationsByCategoryName(categoryName: String)
    fun getSumsPerCategoryForPeriod(startTime: Long, endTime: Long): Flow<List<CategorySum>>
    suspend fun updateOperation(operation: OperationEntity)
}

