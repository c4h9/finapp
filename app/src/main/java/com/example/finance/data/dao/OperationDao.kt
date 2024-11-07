package com.example.finance.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.finance.data.entity.OperationEntity
import kotlinx.coroutines.flow.Flow

data class CategorySum(
    val categoryName: String,
    val total: Double
)

@Dao
interface OperationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: OperationEntity): Long

    @Query("SELECT * FROM operations ORDER BY timestamp DESC")
    fun getAllOperations(): Flow<List<OperationEntity>>

    @Query("""
        SELECT SUM(amount) FROM operations 
        WHERE categoryName IN (SELECT name FROM categories WHERE isIncome = 1) 
        AND timestamp BETWEEN :startTime AND :endTime
        """)
    fun getIncomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?>

    @Query("""
            SELECT SUM(amount) FROM operations 
            WHERE categoryName IN (SELECT name FROM categories WHERE isIncome = 0) 
            AND timestamp BETWEEN :startTime AND :endTime
        """)
    fun getOutcomeSumForPeriod(startTime: Long, endTime: Long): Flow<Double?>

    @Query("DELETE FROM operations WHERE id IN (:operationIds)")
    suspend fun deleteOperationsByIds(operationIds: List<Int>)

    @Query("""
    SELECT categoryName, SUM(amount) as total FROM operations 
    WHERE categoryName IN (SELECT name FROM categories) 
    AND timestamp BETWEEN :startTime AND :endTime 
    GROUP BY categoryName
        """)
    fun getSumsPerCategoryForPeriod(startTime: Long, endTime: Long): Flow<List<CategorySum>>
}

