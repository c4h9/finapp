package com.example.finance.presentation.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.NotificationInfo
import com.example.finance.data.database.AppDatabase
import com.example.finance.data.datastore.KeywordsDataStore
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

    private val keywordsDataStore = KeywordsDataStore(application)

    private val _keywords = MutableStateFlow<List<String>>(listOf("Покупка"))
    val keywords: StateFlow<List<String>> = _keywords.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        notificationRepository = NotificationRepositoryImpl(application)
        operationRepository = OperationRepositoryImpl(database.operationDao())

        viewModelScope.launch {
            operationRepository.getAllOperations().collect { operationsList ->
                _operations.value = operationsList
            }
            keywordsDataStore.keywordsFlow.collect { storedKeywords ->
                if (storedKeywords.isNotEmpty()) {
                    _keywords.value = storedKeywords.toList()
                } else {
                    _keywords.value = listOf("Покупка")
                }
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
        val notificationText = notificationInfo.text ?: ""
        val notificationTitle = notificationInfo.title ?: ""
        val fullText = "$notificationTitle $notificationText"

        val matchedKeyword = keywords.value.firstOrNull { keyword ->
            fullText.contains(keyword, ignoreCase = true)
        }

        if (matchedKeyword != null) {
            val amount = parseAmountFromNotification(fullText)
            if (amount != null) {
                val defaultCategory = categories.value.firstOrNull() ?: Category("Help", CategoryIconType.Help)
                addOperation(defaultCategory, amount)
                viewModelScope.launch {
                    _notificationText.value += "\nСумма $amount добавлена из уведомления с ключевым словом '$matchedKeyword'"
                }
            } else {
                viewModelScope.launch {
                    _notificationText.value += "\nКлючевое слово '$matchedKeyword' найдено, но не удалось распознать сумму."
                }
            }
        } else {
            viewModelScope.launch {
                _notificationText.value += "\nУведомление не содержит ключевых слов."
            }
        }
    }

    private fun parseAmountFromNotification(text: String): Double? {
        val amountRegex = Regex("""\d+[.,]\d{1,2}""")
        val matchResult = amountRegex.find(text)
        if (matchResult != null) {
            val amountString = matchResult.value.replace(',', '.')
            return amountString.toDoubleOrNull()
        }
        return null
    }


    override fun onListenerStatusChange() {
        // TODO
    }

    fun addKeyword(keyword: String) {
        viewModelScope.launch {
            val newKeywords = _keywords.value + keyword
            _keywords.value = newKeywords
            keywordsDataStore.saveKeywords(newKeywords.toSet())
        }
    }

    fun removeKeyword(keyword: String) {
        viewModelScope.launch {
            val newKeywords = _keywords.value - keyword
            _keywords.value = newKeywords
            keywordsDataStore.saveKeywords(newKeywords.toSet())
        }
    }

}
