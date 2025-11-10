# Архитектура Fashion Assistant

## Обзор

Fashion Assistant построен на чистой архитектуре с разделением на слои для обеспечения масштабируемости и тестируемости.

## Архитектурные слои

### 1. Data Layer (Слой данных)
**Назначение**: Управление данными и их источниками

#### Компоненты:
- **Models** (`data/model/`)
  - `ClothingItem` - Модель вещи в гардеробе
  - `Outfit` - Модель образа
  - Enums: `ClothingCategory`, `Season`

- **DAOs** (`data/dao/`)
  - `ClothingItemDao` - CRUD операции для вещей
  - `OutfitDao` - CRUD операции для образов

- **Database** (`data/database/`)
  - `AppDatabase` - Room database конфигурация
  - `Converters` - Type converters для Room

- **Repositories** (`data/repository/`)
  - `ClothingRepository` - Абстракция для работы с вещами
  - `OutfitRepository` - Абстракция для работы с образами

**Преимущества**:
- Централизованное управление данными
- Легко добавить удаленные источники (API)
- Тестируемость через моки

### 2. Domain Layer (Слой бизнес-логики)
**Назначение**: Бизнес-правила и логика приложения

#### Компоненты:
- **OutfitEvaluator** (`domain/OutfitEvaluator.kt`)
  - Оценка образов по правилам
  - Анализ цветовой гармонии
  - Генерация рекомендаций

**Use Cases** (для будущего расширения):
```kotlin
// Примеры use cases для добавления:
- GetWardrobeItemsUseCase
- CreateOutfitUseCase
- EvaluateOutfitUseCase
- SyncWithCloudUseCase
```

**Преимущества**:
- Независимость от UI и данных
- Переиспользуемая логика
- Легко заменить правила на ML-модель

### 3. Presentation Layer (Слой представления)
**Назначение**: UI и взаимодействие с пользователем

#### Компоненты:

**ViewModels** (`ui/viewmodel/`)
- `WardrobeViewModel` - Управление состоянием гардероба
- `OutfitViewModel` - Управление состоянием образов

**Screens** (`ui/screens/`)
- `WardrobeScreen` - Список вещей
- `AddItemScreen` - Добавление вещи
- `OutfitsScreen` - Список образов
- `CreateOutfitScreen` - Создание образа
- `OutfitDetailScreen` - Детали образа

**Components** (`ui/components/`)
- `ImagePickerDialog` - Выбор фото

**Navigation** (`ui/navigation/`)
- `Screen` - Определения маршрутов

**Theme** (`ui/theme/`)
- `Theme.kt` - Material 3 тема
- `Type.kt` - Типографика

**Преимущества**:
- Реактивный UI через State/Flow
- Переиспользуемые компоненты
- Простое тестирование UI

### 4. DI Layer (Dependency Injection)
**Назначение**: Управление зависимостями

#### Компоненты:
- **AppModule** (`di/AppModule.kt`)
  - Провайдеры для Database, DAOs
  - Singleton instances

**Преимущества**:
- Слабая связанность
- Легкая замена реализаций
- Упрощенное тестирование

## Паттерны проектирования

### 1. MVVM (Model-View-ViewModel)
```
View (Compose) → ViewModel → Repository → DAO → Database
       ↑             ↓
       └─── StateFlow ───┘
```

**Преимущества**:
- Разделение UI и бизнес-логики
- Сохранение состояния при поворотах экрана
- Тестируемость без UI

### 2. Repository Pattern
```kotlin
interface Repository {
    fun getData(): Flow<Data>
    suspend fun insert(data: Data)
}

class RepositoryImpl(
    private val localDao: Dao,
    // private val remoteApi: Api  // Для будущего
): Repository { ... }
```

**Преимущества**:
- Абстракция источников данных
- Легко добавить кэширование
- Единая точка доступа к данным

### 3. Strategy Pattern (для AI)
```kotlin
interface OutfitEvaluationStrategy {
    fun evaluate(items: List<ClothingItem>): OutfitEvaluation
}

class RuleBasedStrategy : OutfitEvaluationStrategy { ... }
class MLBasedStrategy : OutfitEvaluationStrategy { ... }
```

**Возможность расширения**:
- Легко добавить новые алгоритмы оценки
- A/B тестирование разных подходов

### 4. Observer Pattern (через Flow)
```kotlin
val items: StateFlow<List<ClothingItem>> = 
    repository.getAllItems()
        .stateIn(viewModelScope, ...)
```

## Поток данных

### Добавление вещи в гардероб
```
User Input (AddItemScreen)
    ↓
ViewModel.addClothingItem()
    ↓
Repository.insertItem()
    ↓
DAO.insertItem()
    ↓
Room Database
    ↓
Flow emission
    ↓
ViewModel updates StateFlow
    ↓
UI recomposes (WardrobeScreen)
```

### Создание и оценка образа
```
User selects items (CreateOutfitScreen)
    ↓
ViewModel.toggleItemSelection()
    ↓
User clicks "Evaluate"
    ↓
ViewModel.evaluateCurrentSelection()
    ↓
Repository.getItemsByIds()
    ↓
OutfitEvaluator.evaluateOutfit()
    ↓
ViewModel updates evaluation StateFlow
    ↓
UI shows score and feedback
    ↓
User saves outfit
    ↓
Repository.insertOutfit()
```

## База данных

### Схема Room Database

```sql
-- clothing_items table
CREATE TABLE clothing_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    imagePath TEXT NOT NULL,
    category TEXT NOT NULL,  -- Enum as String
    color TEXT NOT NULL,
    season TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);

-- outfits table
CREATE TABLE outfits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    itemIds TEXT NOT NULL,  -- Comma-separated IDs
    score REAL NOT NULL,
    feedback TEXT NOT NULL,
    createdAt INTEGER NOT NULL
);
```

**Примечание**: Использование `itemIds` как строки - упрощение для MVP. В production рекомендуется:
```sql
-- Отдельная таблица связей (many-to-many)
CREATE TABLE outfit_items (
    outfit_id INTEGER,
    item_id INTEGER,
    position INTEGER,  -- Порядок в образе
    PRIMARY KEY (outfit_id, item_id),
    FOREIGN KEY (outfit_id) REFERENCES outfits(id),
    FOREIGN KEY (item_id) REFERENCES clothing_items(id)
);
```

## Расширяемость

### Добавление новой функции (пример: Погодные рекомендации)

1. **Data Layer**: Добавить модель погоды
```kotlin
data class WeatherInfo(
    val temperature: Float,
    val condition: WeatherCondition
)
```

2. **Repository**: Добавить источник данных погоды
```kotlin
interface WeatherRepository {
    suspend fun getCurrentWeather(): WeatherInfo
}
```

3. **Domain**: Использовать в оценке
```kotlin
class WeatherAwareEvaluator @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun evaluate(items: List<ClothingItem>): OutfitEvaluation {
        val weather = weatherRepository.getCurrentWeather()
        // Учитывать погоду в оценке
    }
}
```

4. **UI**: Показать погоду в интерфейсе
```kotlin
@Composable
fun CreateOutfitScreen() {
    val weather by viewModel.weather.collectAsState()
    // UI с погодой
}
```

### Добавление ML-модели

1. Добавить TensorFlow Lite в зависимости
```kotlin
implementation("org.tensorflow:tensorflow-lite:2.13.0")
```

2. Создать новый evaluator
```kotlin
class MLOutfitEvaluator @Inject constructor() {
    private val interpreter: Interpreter by lazy {
        // Загрузка модели из assets
    }
    
    fun evaluate(items: List<ClothingItem>): Float {
        // Подготовка входных данных
        // Inference
        // Возврат результата
    }
}
```

3. Использовать через DI
```kotlin
@Provides
fun provideOutfitEvaluator(
    mlEvaluator: MLOutfitEvaluator
): OutfitEvaluationStrategy = mlEvaluator
```

### Портирование на iOS

**Kotlin Multiplatform подход**:

```
shared/
├── commonMain/
│   ├── domain/           # Общая логика
│   ├── data/model/       # Модели данных
│   └── repository/       # Интерфейсы репозиториев
├── androidMain/
│   └── data/             # Android-специфичная реализация
└── iosMain/
    └── data/             # iOS-специфичная реализация

android/
└── ui/                   # Jetpack Compose UI

ios/
└── ui/                   # SwiftUI
```

## Тестирование

### Unit тесты
```kotlin
class OutfitEvaluatorTest {
    private lateinit var evaluator: OutfitEvaluator
    
    @Before
    fun setup() {
        evaluator = OutfitEvaluator()
    }
    
    @Test
    fun `complete outfit with matching colors gets high score`() {
        val items = createTestOutfit()
        val result = evaluator.evaluateOutfit(items)
        assertTrue(result.score >= 70f)
    }
}
```

### Repository тесты (с моками)
```kotlin
class ClothingRepositoryTest {
    @Mock lateinit var dao: ClothingItemDao
    private lateinit var repository: ClothingRepository
    
    @Test
    fun `repository returns items from dao`() = runTest {
        val testItems = listOf(createTestItem())
        whenever(dao.getAllItems()).thenReturn(flowOf(testItems))
        
        val result = repository.getAllItems().first()
        assertEquals(testItems, result)
    }
}
```

### UI тесты (Compose)
```kotlin
class WardrobeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun emptyWardrobeShowsPlaceholder() {
        composeTestRule.setContent {
            WardrobeScreen(onAddItemClick = {})
        }
        
        composeTestRule.onNodeWithText("Гардероб пуст").assertExists()
    }
}
```

## Производительность

### Оптимизации:
1. **LazyColumn/LazyGrid** для списков
2. **Flow** вместо LiveData (меньше overhead)
3. **Coil** для эффективной загрузки изображений
4. **Room** с индексами для быстрых запросов
5. **Kotlin Coroutines** для асинхронности

### Возможные улучшения:
- Пагинация для больших гардеробов
- Кэширование оценок образов
- Предзагрузка изображений
- Background processing для AI

## Безопасность

### Текущие меры:
- Локальное хранилище (Room)
- Приватная директория для изображений
- Проверка разрешений перед использованием

### Для production:
- Шифрование БД (SQLCipher)
- Secure Storage для sensitive data
- ProGuard/R8 обфускация
- SSL pinning для API

## Заключение

Архитектура спроектирована с учетом:
- ✅ Чистый код и разделение ответственности
- ✅ Легкое расширение функционала
- ✅ Тестируемость на всех уровнях
- ✅ Готовность к масштабированию
- ✅ Возможность портирования на iOS

Следование этим принципам позволяет быстро добавлять новые функции без нарушения существующего кода.
