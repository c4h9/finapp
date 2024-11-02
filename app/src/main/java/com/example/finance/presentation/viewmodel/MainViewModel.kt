package com.example.finance.presentation.viewmodel
import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finance.NotificationInfo
import com.example.finance.data.database.AppDatabase
import com.example.finance.data.datastore.KeywordsDataStore
import com.example.finance.data.entity.BudgetEntity
import com.example.finance.data.entity.CategoryEntity
import com.example.finance.data.entity.OperationEntity
import com.example.finance.data.repository.BudgetRepositoryImpl
import com.example.finance.data.repository.CategoryRepositoryImpl
import com.example.finance.data.repository.NotificationRepositoryImpl
import com.example.finance.data.repository.OperationRepositoryImpl
import com.example.finance.domain.entity.Category
import com.example.finance.domain.entity.CategoryIconType
import com.example.finance.domain.repository.BudgetRepository
import com.example.finance.domain.repository.CategoryRepository
import com.example.finance.domain.repository.NotificationListener
import com.example.finance.domain.repository.NotificationRepository
import com.example.finance.domain.repository.OperationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

private val Context.dataStore by preferencesDataStore(name = "app_prefs")
private val IS_FIRST_LAUNCH_KEY = booleanPreferencesKey("is_first_launch")
private val DONT_SHOW_PERMISSION_SCREEN_KEY = booleanPreferencesKey("dont_show_permission_screen")

object InitialData {
    val allCategories = listOf(
        Category("Еда", CategoryIconType.Fastfood, false),
        Category("Транспорт", CategoryIconType.Fastfood, false),
        Category("Дом", CategoryIconType.Home, false),
        Category("Работа", CategoryIconType.Work, false),
        Category("Спорт", CategoryIconType.FitnessCenter, false),
        Category("Покупки", CategoryIconType.ShoppingCart, false),
        Category("Развлечения", CategoryIconType.Movie, false),
        Category("Зарплата", CategoryIconType.Fastfood, true),
        Category("Подработка", CategoryIconType.Fastfood, true),
        Category("Депозит", CategoryIconType.Home, true),
        Category("Стипендия", CategoryIconType.Work, true)
    )
}

class MainViewModel(application: Application) : AndroidViewModel(application), NotificationListener {

    private val notificationRepository: NotificationRepository
    private val operationRepository: OperationRepository
    private val categoryRepository: CategoryRepository
    private val budgetRepository: BudgetRepository

    private val _selectedPeriod = MutableStateFlow("Месяц")
    val selectedPeriod: StateFlow<String> = _selectedPeriod.asStateFlow()

    fun setSelectedPeriod(period: String) {
        _selectedPeriod.value = period
    }

    private val _incomesForCurrentPeriod = MutableStateFlow<Double?>(0.0)
    val incomesForCurrentPeriod: StateFlow<Double?> = _incomesForCurrentPeriod.asStateFlow()

    private val _outcomesForCurrentPeriod = MutableStateFlow<Double?>(0.0)
    val outcomesForCurrentPeriod: StateFlow<Double?> = _outcomesForCurrentPeriod.asStateFlow()

    private val _budget = MutableStateFlow(0.0)
    val budget: StateFlow<Double> = _budget.asStateFlow()

    private val _incomesForCurrentMonth = MutableStateFlow(0.0)
    val incomesForCurrentMonth: StateFlow<Double> = _incomesForCurrentMonth.asStateFlow()

    private val _outcomesForCurrentMonth = MutableStateFlow(0.0)
    val outcomesForCurrentMonth: StateFlow<Double> = _outcomesForCurrentMonth.asStateFlow()

    private val _notificationText = MutableStateFlow("")
    val notificationText = _notificationText.asStateFlow()

    val allCategories = InitialData.allCategories

    private val _selectedCategories = mutableStateListOf<Category>()
    val selectedCategories: SnapshotStateList<Category> get() = _selectedCategories

    private val _navigateToNextScreen = MutableStateFlow(false)
    val navigateToNextScreen: StateFlow<Boolean> get() = _navigateToNextScreen

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _operations = MutableStateFlow<List<OperationEntity>>(emptyList())
    val operations: StateFlow<List<OperationEntity>> = _operations.asStateFlow()

    private val keywordsDataStore = KeywordsDataStore(application)

    private val _keywords = MutableStateFlow<List<String>>(listOf("Покупка"))
    val keywords: StateFlow<List<String>> = _keywords.asStateFlow()

    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> get() = _isDataLoaded

    private val _isFirstLaunch = MutableStateFlow<Boolean?>(null)
    val isFirstLaunch: StateFlow<Boolean?> get() = _isFirstLaunch

    private val _dontShowPermissionScreen = MutableStateFlow<Boolean?>(null)
    val dontShowPermissionScreen: StateFlow<Boolean?> get() = _dontShowPermissionScreen

    init {
        val database = AppDatabase.getDatabase(application)
        categoryRepository = CategoryRepositoryImpl(database.categoryDao())
        notificationRepository = NotificationRepositoryImpl(application)
        operationRepository = OperationRepositoryImpl(database.operationDao())
        budgetRepository = BudgetRepositoryImpl(database.budgetDao())

        viewModelScope.launch {
            initializePreferences()
            _isDataLoaded.value = true
        }
        observeCategories()
        observeOperations()
        observeIncomesAndOutcomes()
        observeKeywords()
        viewModelScope.launch {
            budgetRepository.getBudget().collect { budgetEntity ->
                _budget.value = budgetEntity?.value ?: 0.0
            }
        }
    }

    fun setBudget(value: Double) {
        viewModelScope.launch {
            budgetRepository.insertBudget(BudgetEntity(value = value))
        }
    }

    private suspend fun initializePreferences() {
        val preferences = getApplication<Application>().dataStore.data.first()
        _isFirstLaunch.value = preferences[IS_FIRST_LAUNCH_KEY] ?: true
        _dontShowPermissionScreen.value = preferences[DONT_SHOW_PERMISSION_SCREEN_KEY] ?: false
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryRepository.getAllCategories().collect { categoriesList ->
                _categories.value = categoriesList.map { categoryEntity ->
                    Category(
                        name = categoryEntity.name,
                        iconType = CategoryIconType.valueOf(categoryEntity.iconType),
                        isIncome = categoryEntity.isIncome
                    )
                }
            }
        }
    }

    fun addCategory(category: Category, isIncome: Boolean) {
        viewModelScope.launch {
            val categoryEntity = CategoryEntity(
                name = category.name,
                iconType = category.iconType.name,
                isIncome = isIncome
            )
            categoryRepository.insertCategory(categoryEntity)
        }
    }
    private fun observeIncomesAndOutcomes() {
        viewModelScope.launch {
            _selectedPeriod.collect { period ->
                val (startTime, endTime) = getStartAndEndOfPeriod(period)
                operationRepository.getIncomeSumForPeriod(startTime, endTime).collect { income ->
                    _incomesForCurrentPeriod.value = income ?: 0.0
                }
            }
        }

        viewModelScope.launch {
            _selectedPeriod.collect { period ->
                val (startTime, endTime) = getStartAndEndOfPeriod(period)
                operationRepository.getOutcomeSumForPeriod(startTime, endTime).collect { outcome ->
                    _outcomesForCurrentPeriod.value = outcome ?: 0.0
                }
            }
        }
    }

    private fun getStartAndEndOfPeriod(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis

        when (period) {
            "День" -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Неделя" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Месяц" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "Год" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "За всё время" -> {
                calendar.set(1970, Calendar.JANUARY, 1, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            else -> {
                // Default to current month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        val startTime = calendar.timeInMillis
        return Pair(startTime, endTime)
    }

    private fun getCurrentMonthStartAndEnd(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        val startOfMonth = Calendar.getInstance().apply {
            set(currentYear, currentMonth, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val endOfMonth = Calendar.getInstance().apply {
            set(currentYear, currentMonth + 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, -1)
        }.timeInMillis

        return startOfMonth to endOfMonth
    }

    private fun observeOperations() {
        viewModelScope.launch {
            operationRepository.getAllOperations().collect { operationsList ->
                _operations.value = operationsList
            }
        }
    }

    private fun observeKeywords() {
        viewModelScope.launch {
            keywordsDataStore.keywordsFlow.collect { storedKeywords ->
                _keywords.value = if (storedKeywords.isNotEmpty()) {
                    storedKeywords.toList()
                } else {
                    listOf("Покупка")
                }
            }
        }
    }

    suspend fun setDontShowPermissionScreen(value: Boolean) {
        _dontShowPermissionScreen.value = value
        getApplication<Application>().dataStore.edit { preferences ->
            preferences[DONT_SHOW_PERMISSION_SCREEN_KEY] = value
        }
    }

    fun setInitialCategories(categories: List<Category>) {
        _categories.value = categories
    }

    fun onCategoryCheckedChanged(category: Category, isChecked: Boolean) {
        if (isChecked) {
            _selectedCategories.add(category)
        } else {
            _selectedCategories.remove(category)
        }
    }

    fun onContinueClicked() {
        viewModelScope.launch {
            _selectedCategories.forEach { category ->
                val categoryEntity = CategoryEntity(
                    name = category.name,
                    iconType = category.iconType.name,
                    isIncome = category.isIncome
                )
                categoryRepository.insertCategory(categoryEntity)
            }
            setFirstLaunch(false)
            _navigateToNextScreen.value = true
        }
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        viewModelScope.launch {
            _isFirstLaunch.value = isFirstLaunch
            getApplication<Application>().dataStore.edit { preferences ->
                preferences[IS_FIRST_LAUNCH_KEY] = isFirstLaunch
            }
        }
    }


    fun onNavigatedToNextScreen() {
        _navigateToNextScreen.value = false
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
                val defaultCategory = categories.value.firstOrNull() ?: Category("Help", CategoryIconType.Help, false)
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

    fun deleteOperationsByIds(operationIds: List<Int>) {
        viewModelScope.launch {
            operationRepository.deleteOperationsByIds(operationIds)
        }
    }
}
