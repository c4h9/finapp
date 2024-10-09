package com.example.finance.data.repository

import com.example.finance.data.dao.OperationDao
import com.example.finance.domain.entity.OperationEntity
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow

class OperationRepositoryImpl(private val operationDao: OperationDao) : OperationRepository {
    override suspend fun insertOperation(operation: OperationEntity): Long {
        return operationDao.insertOperation(operation)
    }

    override fun getAllOperations(): Flow<List<OperationEntity>> {
        return operationDao.getAllOperations()
    }
}

