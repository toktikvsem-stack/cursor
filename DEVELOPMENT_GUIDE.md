# Руководство по разработке Fashion Assistant

## Начало работы

### Настройка окружения

1. **Установите Android Studio**
   - Скачайте с [developer.android.com](https://developer.android.com/studio)
   - Версия: Hedgehog (2023.1.1) или новее

2. **Настройте JDK**
   - File → Project Structure → SDK Location
   - Убедитесь, что используется JDK 17

3. **Установите Android SDK**
   - Tools → SDK Manager
   - Минимум: API 26 (Android 8.0)
   - Рекомендуется: API 34 (Android 14)

4. **Настройте эмулятор**
   - Tools → Device Manager
   - Create Device → Pixel 6
   - System Image: API 34 (Android 14)

### Первый запуск

```bash
# Клонирование проекта
git clone <repository-url>
cd FashionAssistant

# Синхронизация Gradle
# В Android Studio: File → Sync Project with Gradle Files

# Запуск приложения
# Нажмите Run (▶️) или Shift+F10
```

## Структура кода

### Соглашения по именованию

#### Файлы
- **Screen**: `*Screen.kt` (например, `WardrobeScreen.kt`)
- **ViewModel**: `*ViewModel.kt`
- **Repository**: `*Repository.kt`
- **DAO**: `*Dao.kt`
- **Model**: Без суффикса (например, `ClothingItem.kt`)
- **Component**: Описательное имя (например, `ImagePickerDialog.kt`)

#### Классы и интерфейсы
```kotlin
// PascalCase для классов
class WardrobeViewModel

// PascalCase для интерфейсов
interface ClothingRepository

// Enums
enum class ClothingCategory

// Data classes
data class ClothingItem(...)
```

#### Функции и переменные
```kotlin
// camelCase для функций
fun evaluateOutfit()

// camelCase для переменных
val selectedItems: StateFlow<Set<Long>>

// Константы UPPER_SNAKE_CASE
const val MAX_SCORE = 100f
```

#### Composables
```kotlin
// PascalCase с аннотацией @Composable
@Composable
fun WardrobeScreen(...)

@Composable
fun ClothingItemCard(...)
```

### Организация imports

```kotlin
// 1. Android/Java
import android.os.Bundle
import androidx.compose.runtime.*

// 2. Third-party
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 3. Внутренние
import com.fashionassistant.app.data.model.ClothingItem
import com.fashionassistant.app.domain.OutfitEvaluator
```

## Добавление новых функций

### 1. Добавление нового экрана

**Шаг 1**: Создать sealed class для навигации
```kotlin
// ui/navigation/Screen.kt
sealed class Screen(val route: String) {
    // ...
    object NewFeature : Screen("new_feature")
}
```

**Шаг 2**: Создать Composable-экран
```kotlin
// ui/screens/NewFeatureScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewFeatureScreen(
    onNavigateBack: () -> Unit,
    viewModel: NewFeatureViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Feature") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        // UI content
    }
}
```

**Шаг 3**: Добавить в навигацию
```kotlin
// MainActivity.kt
composable(Screen.NewFeature.route) {
    NewFeatureScreen(
        onNavigateBack = { navController.popBackStack() }
    )
}
```

### 2. Добавление новой модели данных

**Шаг 1**: Создать Entity
```kotlin
// data/model/NewEntity.kt
@Entity(tableName = "new_entities")
data class NewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)
```

**Шаг 2**: Создать DAO
```kotlin
// data/dao/NewEntityDao.kt
@Dao
interface NewEntityDao {
    @Query("SELECT * FROM new_entities")
    fun getAll(): Flow<List<NewEntity>>
    
    @Insert
    suspend fun insert(entity: NewEntity): Long
    
    @Delete
    suspend fun delete(entity: NewEntity)
}
```

**Шаг 3**: Обновить Database
```kotlin
// data/database/AppDatabase.kt
@Database(
    entities = [
        ClothingItem::class, 
        Outfit::class,
        NewEntity::class  // Добавить
    ],
    version = 2,  // Увеличить версию!
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    // ...
    abstract fun newEntityDao(): NewEntityDao
}
```

**Шаг 4**: Создать Repository
```kotlin
// data/repository/NewEntityRepository.kt
@Singleton
class NewEntityRepository @Inject constructor(
    private val dao: NewEntityDao
) {
    fun getAll() = dao.getAll()
    suspend fun insert(entity: NewEntity) = dao.insert(entity)
    suspend fun delete(entity: NewEntity) = dao.delete(entity)
}
```

**Шаг 5**: Создать ViewModel
```kotlin
// ui/viewmodel/NewFeatureViewModel.kt
@HiltViewModel
class NewFeatureViewModel @Inject constructor(
    private val repository: NewEntityRepository
) : ViewModel() {
    
    val entities: StateFlow<List<NewEntity>> = 
        repository.getAll()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    
    fun addEntity(entity: NewEntity) {
        viewModelScope.launch {
            repository.insert(entity)
        }
    }
}
```

### 3. Добавление нового типа оценки

```kotlin
// domain/evaluators/NewEvaluationStrategy.kt
interface EvaluationStrategy {
    fun evaluate(items: List<ClothingItem>): OutfitEvaluation
}

class SeasonalEvaluationStrategy @Inject constructor() : EvaluationStrategy {
    override fun evaluate(items: List<ClothingItem>): OutfitEvaluation {
        val seasonScore = evaluateSeasonalMatch(items)
        val baseEvaluation = OutfitEvaluator().evaluateOutfit(items)
        
        return baseEvaluation.copy(
            score = (baseEvaluation.score + seasonScore) / 2
        )
    }
    
    private fun evaluateSeasonalMatch(items: List<ClothingItem>): Float {
        // Логика оценки сезонности
    }
}
```

Использование:
```kotlin
@HiltViewModel
class OutfitViewModel @Inject constructor(
    private val evaluationStrategy: EvaluationStrategy  // Inject стратегию
) : ViewModel() {
    
    fun evaluateCurrentSelection() {
        viewModelScope.launch {
            val items = clothingRepository.getItemsByIds(selectedItems.value.toList())
            val evaluation = evaluationStrategy.evaluate(items)
            _currentEvaluation.value = evaluation
        }
    }
}
```

## Работа с UI

### Compose Best Practices

#### 1. Разделение UI на компоненты
```kotlin
// Плохо: монолитный Composable
@Composable
fun BigScreen() {
    Column {
        // 200+ строк UI кода
    }
}

// Хорошо: разбить на компоненты
@Composable
fun BigScreen() {
    Column {
        HeaderSection()
        ContentSection()
        FooterSection()
    }
}

@Composable
private fun HeaderSection() { /* ... */ }
```

#### 2. State hoisting
```kotlin
// Плохо: состояние внутри компонента
@Composable
fun ItemCard() {
    var isSelected by remember { mutableStateOf(false) }
    // ...
}

// Хорошо: состояние поднято наверх
@Composable
fun ItemCard(
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    // ...
}
```

#### 3. Использование модификаторов
```kotlin
// Хорошо: передавать модификатор как параметр
@Composable
fun CustomCard(
    modifier: Modifier = Modifier,  // Всегда первый параметр
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier  // Применить сначала внешний
            .fillMaxWidth()   // Потом внутренние
            .padding(16.dp)
    ) {
        content()
    }
}
```

#### 4. Side effects
```kotlin
// LaunchedEffect для одноразовых действий
LaunchedEffect(Unit) {
    viewModel.loadInitialData()
}

// DisposableEffect для cleanup
DisposableEffect(Unit) {
    val listener = createListener()
    onDispose {
        listener.cleanup()
    }
}
```

### Работа с темой

```kotlin
// Использование цветов темы
Text(
    text = "Hello",
    color = MaterialTheme.colorScheme.primary  // ✅
)

Text(
    text = "World",
    color = Color.Blue  // ❌ Жёсткий цвет
)

// Использование типографики
Text(
    text = "Title",
    style = MaterialTheme.typography.titleLarge  // ✅
)

Text(
    text = "Body",
    fontSize = 16.sp  // ❌ Жёсткий размер
)
```

## Работа с данными

### Coroutines и Flow

```kotlin
// В ViewModel
class MyViewModel @Inject constructor(
    private val repository: MyRepository
) : ViewModel() {
    
    // StateFlow для UI состояния
    val items: StateFlow<List<Item>> = repository.getItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // suspend функции для действий
    fun saveItem(item: Item) {
        viewModelScope.launch {
            try {
                repository.insert(item)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
```

### Room запросы

```kotlin
@Dao
interface ItemDao {
    // Flow для реактивных обновлений
    @Query("SELECT * FROM items")
    fun getAll(): Flow<List<Item>>
    
    // suspend для одноразовых операций
    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getById(id: Long): Item?
    
    // suspend для insert/update/delete
    @Insert
    suspend fun insert(item: Item): Long
    
    // Сложные запросы с параметрами
    @Query("""
        SELECT * FROM items 
        WHERE category = :category 
        AND color LIKE '%' || :colorQuery || '%'
        ORDER BY createdAt DESC
    """)
    fun searchByCategory(
        category: String,
        colorQuery: String
    ): Flow<List<Item>>
}
```

## Тестирование

### Unit тесты (ViewModels)

```kotlin
class WardrobeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()
    
    @Mock
    private lateinit var repository: ClothingRepository
    
    private lateinit var viewModel: WardrobeViewModel
    
    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = WardrobeViewModel(repository)
    }
    
    @Test
    fun `deleteClothingItem calls repository`() = runTest {
        val item = ClothingItem(id = 1, name = "Test")
        
        viewModel.deleteClothingItem(item)
        advanceUntilIdle()
        
        verify(repository).deleteItem(item)
    }
}
```

### UI тесты (Compose)

```kotlin
@RunWith(AndroidJUnit4::class)
class WardrobeScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
    
    @Test
    fun clickingAddButton_navigatesToAddScreen() {
        var navigated = false
        
        composeTestRule.setContent {
            WardrobeScreen(
                onAddItemClick = { navigated = true }
            )
        }
        
        composeTestRule
            .onNodeWithContentDescription("Добавить вещь")
            .performClick()
        
        assertTrue(navigated)
    }
}
```

### Integration тесты (Repository + DAO)

```kotlin
@RunWith(AndroidJUnit4::class)
class ClothingRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var repository: ClothingRepository
    
    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        
        repository = ClothingRepository(database.clothingItemDao())
    }
    
    @After
    fun tearDown() {
        database.close()
    }
    
    @Test
    fun insertAndRetrieveItem() = runTest {
        val item = ClothingItem(name = "Test", ...)
        val id = repository.insertItem(item)
        
        val retrieved = repository.getItemById(id)
        assertEquals(item.name, retrieved?.name)
    }
}
```

## Debugging

### Логирование

```kotlin
import android.util.Log

private const val TAG = "WardrobeViewModel"

class WardrobeViewModel {
    fun loadItems() {
        Log.d(TAG, "Loading items from repository")
        
        viewModelScope.launch {
            try {
                // ...
                Log.i(TAG, "Successfully loaded ${items.size} items")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading items", e)
            }
        }
    }
}
```

### Timber (рекомендуется)

```kotlin
// В Application классе
class FashionAssistantApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}

// В коде
Timber.d("Loading items")
Timber.e(exception, "Error occurred")
```

### Compose Layout Inspector

1. Tools → Layout Inspector
2. Выберите запущенный процесс
3. Инспектируйте Compose иерархию в реальном времени

### Database Inspector

1. View → Tool Windows → App Inspection
2. Database Inspector
3. Просмотр таблиц и данных в реальном времени

## CI/CD (для будущего)

### GitHub Actions пример

```yaml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
    
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Build debug APK
      run: ./gradlew assembleDebug
    
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: debug-apk
        path: app/build/outputs/apk/debug/*.apk
```

## Troubleshooting

### Проблема: Gradle sync failed
**Решение**: 
```bash
# Очистить кэш Gradle
./gradlew clean
./gradlew --stop
# File → Invalidate Caches / Restart
```

### Проблема: Room database migration failed
**Решение**:
```kotlin
// Для разработки: пересоздать БД
Room.databaseBuilder(...)
    .fallbackToDestructiveMigration()  // Временно!
    .build()
```

### Проблема: Compose preview не работает
**Решение**:
```kotlin
@Preview(showBackground = true)
@Composable
fun PreviewWardrobeScreen() {
    FashionAssistantTheme {
        // Используйте моки данных
        WardrobeScreenContent(
            items = listOf(/* mock items */),
            onAddClick = {}
        )
    }
}
```

## Чек-лист перед коммитом

- [ ] Код скомпилирован без ошибок
- [ ] Тесты пройдены (`./gradlew test`)
- [ ] Lint проверка пройдена (`./gradlew lint`)
- [ ] Добавлены/обновлены комментарии для сложной логики
- [ ] Нет закомментированного кода
- [ ] Нет `TODO` или `FIXME` (или они задокументированы)
- [ ] Код отформатирован (Ctrl+Alt+L)
- [ ] Imports оптимизированы (Ctrl+Alt+O)

## Полезные ресурсы

- [Android Developer Docs](https://developer.android.com/)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)
- [Hilt Documentation](https://dagger.dev/hilt/)
- [Room Documentation](https://developer.android.com/training/data-storage/room)

---

**Happy Coding! 🚀**
