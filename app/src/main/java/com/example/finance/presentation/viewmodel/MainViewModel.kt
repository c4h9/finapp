package com.example.finance.presentation.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.NotificationInfo
import com.example.finance.data.database.AppDatabase
import com.example.finance.data.entity.OperationEntity
import com.example.finance.data.repository.NotificationRepositoryImpl
import com.example.finance.data.repository.OperationRepositoryImpl
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import com.example.finance.domain.repository.NotificationListener
import com.example.finance.domain.repository.NotificationRepository
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application), NotificationListener {

    private val notificationRepository: NotificationRepository
    private val operationRepository: OperationRepository

    private val _notificationText = MutableStateFlow("")
    val notificationText = _notificationText.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(initialCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _operations = MutableStateFlow<List<OperationEntity>>(emptyList())
    val operations: StateFlow<List<OperationEntity>> = _operations.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        notificationRepository = NotificationRepositoryImpl(application)
        operationRepository = OperationRepositoryImpl(database.operationDao())

        viewModelScope.launch {
            operationRepository.getAllOperations().collect { operationsList ->
                _operations.value = operationsList
            }
        }
    }

    private fun initialCategories(): List<Category> {
        return listOf(
            Category("Еда", CategoryIconType.Fastfood),
            Category("Транспорт", CategoryIconType.DirectionsCar),
            Category("Дом", CategoryIconType.Home),
            Category("Работа", CategoryIconType.Work),
            Category("Спорт", CategoryIconType.FitnessCenter),
            Category("Покупки", CategoryIconType.ShoppingCart),
            Category("Развлечения", CategoryIconType.Movie)
        )
    }

    fun addCategory(category: Category) {
        _categories.value = _categories.value + category
    }

    fun getNotificationAccessPermission() {
        notificationRepository.getNotificationAccessPermission()
    }

    fun bindNotificationService() {
        notificationRepository.setNotificationListener(this)
    }

    fun unbindNotificationService() {
        notificationRepository.removeNotificationListener()
    }

    fun addOperation(category: Category, amount: Double) {
        viewModelScope.launch {
            val operation = OperationEntity(
                categoryName = category.name,
                iconName = category.iconType.name,
                amount = amount
            )
            operationRepository.insertOperation(operation)
        }
    }


    override fun onNotificationReceived(notificationInfo: NotificationInfo) {
        viewModelScope.launch {
            _notificationText.value += "\nNotification Message from: ${notificationInfo.packageName}" +
                    "\nTitle: ${notificationInfo.title}" +
                    "\nText: ${notificationInfo.text}" +
                    "\nInfo: ${notificationInfo.infoText}\n"
        }
    }

    override fun onListenerStatusChange() {
        // TODO
    }
}
