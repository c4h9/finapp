package com.example.finance.domain.repository

import com.example.finance.domain.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

interface OperationRepository {
    suspend fun insertOperation(operation: OperationEntity): Long
    fun getAllOperations(): Flow<List<OperationEntity>>
}

