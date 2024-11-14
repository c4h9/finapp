package com.example.finance.data.repository

import com.example.finance.data.dao.CategorySum
import com.example.finance.data.dao.OperationDao
import com.example.finance.data.entity.OperationEntity
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.Flow

class OperationRepositoryImpl(private val operationDao: OperationDao) : OperationRepository {
    override suspend fun insertOperation(operation: OperationEntity): Long {
        return operationDao.insertOperation(operation)
    }

    override fun getAllOperations(): Flow<List<OperationEntity>> {
        return operationDao.getAllOperations()
    }

    override fun getIncomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?> {
        return operationDao.getIncomeSumForPeriod(startTime, endTime)
    }

    override fun getOutcomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?> {
        return operationDao.getOutcomeSumForPeriod(startTime, endTime)
    }

    override suspend fun deleteOperationsByIds(operationIds: List<Int>) {
        operationDao.deleteOperationsByIds(operationIds)
    }

    override suspend fun deleteOperationsByCategoryName(categoryName: String) {
        operationDao.deleteOperationsByCategoryName(categoryName)
    }

    override fun getSumsPerCategoryForPeriod(startTime: Long, endTime: Long): Flow<List<CategorySum>> {
        return operationDao.getSumsPerCategoryForPeriod(startTime, endTime)
    }

    override suspend fun updateOperation(operation: OperationEntity) {
        operationDao.updateOperation(operation)
    }
}

