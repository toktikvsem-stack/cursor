# 📥 Как скачать проект на компьютер

## 🎯 Важно понять!

### ✅ Что ЕСТЬ в проекте (готово к работе):
- ✅ **Весь исходный код** (42 файла .kt, .xml)
- ✅ **Все настройки** (build.gradle, AndroidManifest)
- ✅ **Все ресурсы** (strings, themes, colors)
- ✅ **Вся документация** (5 MD файлов)
- ✅ **Скрипты сборки** (gradlew, build_apk.sh)

### ❌ Чего НЕТ (и не нужно):
- ❌ `.gradle/` - временные файлы Gradle (создадутся сами)
- ❌ `build/` - скомпилированные файлы (создадутся при сборке)
- ❌ `.idea/` - настройки Android Studio (создадутся при открытии)
- ❌ `local.properties` - путь к вашему SDK (создадите сами)

**Это нормально!** Эти файлы генерируются автоматически.

---

## 🚀 Способы скачать проект

### Способ 1: Через Git (рекомендуется) ⭐

Проект уже в Git репозитории!

#### Шаг 1: Найдите URL репозитория

Если вы работаете в GitHub/GitLab/Bitbucket:
```bash
# В вашем workspace уже есть .git
cd /workspace
git remote -v
```

#### Шаг 2: Клонируйте на компьютер

```bash
# На вашем компьютере:
git clone <URL_репозитория>
cd FashionAssistant
```

#### Шаг 3: Откройте в Android Studio

```
Android Studio → File → Open → выберите папку
```

**Готово!** Gradle автоматически скачает все зависимости.

---

### Способ 2: Скачать как ZIP архив 📦

Если у вас есть веб-интерфейс Git:

1. **На странице репозитория нажмите "Code" → "Download ZIP"**
2. **Распакуйте архив на компьютере**
3. **Откройте папку в Android Studio**

---

### Способ 3: Создать архив вручную 🗜️

Если у вас прямой доступ к файлам:

```bash
# В workspace:
cd /workspace
tar -czf FashionAssistant.tar.gz \
  --exclude='.git' \
  --exclude='.gradle' \
  --exclude='build' \
  --exclude='*.apk' \
  .

# Или ZIP:
zip -r FashionAssistant.zip . \
  -x "*.git*" "*.gradle*" "*build*" "*.apk"
```

Скачайте архив и распакуйте на компьютере.

---

### Способ 4: Копирование файлов 📋

Если можете копировать файлы напрямую:

```bash
# Скопируйте всю папку /workspace на компьютер
# Главное - НЕ копируйте:
# - .gradle/
# - build/
# - .idea/
```

---

## 📂 Что должно быть в скачанной папке

После скачивания у вас должна быть такая структура:

```
FashionAssistant/
├── 📄 README.md                    ← Описание
├── 📄 QUICK_START.md               ← Быстрый старт
├── 📄 BUILD_APK.md                 ← Как создать APK
├── 📄 INSTALL_GUIDE.md             ← Установка
├── 📄 ARCHITECTURE.md              ← Архитектура
├── 📄 DEVELOPMENT_GUIDE.md         ← Для разработчиков
├── 📄 PROJECT_SUMMARY.md           ← Итоги
├── 📄 HOW_TO_DOWNLOAD.md           ← Этот файл
│
├── 📄 build.gradle.kts             ← Главный build
├── 📄 settings.gradle.kts          ← Настройки Gradle
├── 📄 gradle.properties            ← Свойства
├── 📄 .gitignore                   ← Git ignore
│
├── 🗂️ gradle/                      ← Gradle wrapper
│   └── wrapper/
│       └── gradle-wrapper.properties
│
├── 📜 gradlew                      ← Скрипт Gradle (Linux/Mac)
├── 📜 gradlew.bat                  ← Скрипт Gradle (Windows)
├── 📜 build_apk.sh                 ← Автосборка (Linux/Mac)
├── 📜 build_apk.bat                ← Автосборка (Windows)
│
└── 🗂️ app/                         ← Главная папка приложения
    ├── 📄 build.gradle.kts        ← Build конфиг приложения
    ├── 📄 proguard-rules.pro      ← ProGuard правила
    │
    └── 🗂️ src/main/
        ├── 📄 AndroidManifest.xml
        │
        ├── 🗂️ java/com/fashionassistant/app/
        │   ├── 📄 MainActivity.kt
        │   ├── 📄 FashionAssistantApp.kt
        │   │
        │   ├── 🗂️ data/           ← 10 файлов
        │   │   ├── model/
        │   │   ├── dao/
        │   │   ├── database/
        │   │   └── repository/
        │   │
        │   ├── 🗂️ domain/         ← 1 файл
        │   │   └── OutfitEvaluator.kt
        │   │
        │   ├── 🗂️ ui/             ← 14 файлов
        │   │   ├── screens/       (5 экранов)
        │   │   ├── viewmodel/     (2 ViewModel)
        │   │   ├── components/
        │   │   ├── navigation/
        │   │   └── theme/
        │   │
        │   └── 🗂️ di/             ← 1 файл
        │       └── AppModule.kt
        │
        └── 🗂️ res/                ← Ресурсы
            ├── values/
            │   ├── strings.xml
            │   ├── colors.xml
            │   └── themes.xml
            ├── xml/
            ├── drawable/
            └── mipmap-anydpi-v26/
```

**Всего: 42 исходных файла + документация**

---

## ✅ Проверка скачанного проекта

После скачивания проверьте:

```bash
cd FashionAssistant

# Должны быть:
ls build.gradle.kts           # ✓ Есть
ls app/build.gradle.kts       # ✓ Есть
ls gradlew                    # ✓ Есть
ls app/src/main/AndroidManifest.xml  # ✓ Есть

# Должны быть Kotlin файлы:
find app/src -name "*.kt" | wc -l
# Должно показать: 29 файлов

# Должны быть XML ресурсы:
find app/src/main/res -name "*.xml" | wc -l
# Должно показать: 8-10 файлов
```

Если всё это есть - **проект скачан правильно!** ✅

---

## 🔧 Что делать после скачивания

### Шаг 1: Откройте в Android Studio

```
1. Запустите Android Studio
2. File → Open
3. Выберите папку FashionAssistant
4. Нажмите "OK"
```

### Шаг 2: Дождитесь Gradle Sync

Android Studio автоматически:
- ✅ Скачает все зависимости
- ✅ Создаст `.gradle/` папку
- ✅ Создаст `build/` папку
- ✅ Настроит проект

**Время**: 2-5 минут (первый раз)

### Шаг 3: Создайте local.properties (если нужно)

Если Android Studio не нашёл SDK автоматически:

```properties
# Создайте файл local.properties в корне:
sdk.dir=/path/to/Android/Sdk
```

**Где найти путь**:
```
Android Studio → Settings → 
Appearance & Behavior → System Settings → 
Android SDK → Android SDK Location
```

### Шаг 4: Соберите проект

```
Build → Make Project
Или: Ctrl+F9 (Win) / Cmd+F9 (Mac)
```

Если всё успешно - можете создавать APK!

---

## 🎯 Создание APK после скачивания

### Вариант 1: Через Android Studio
```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

### Вариант 2: Автоматический скрипт

**Windows:**
```batch
build_apk.bat
```

**Linux/Mac:**
```bash
chmod +x build_apk.sh
./build_apk.sh
```

### Вариант 3: Командная строка

```bash
# Windows:
gradlew.bat assembleDebug

# Linux/Mac:
./gradlew assembleDebug
```

**Результат**: `app/build/outputs/apk/debug/app-debug.apk`

---

## 🆘 Частые проблемы после скачивания

### Проблема 1: "Gradle sync failed"

**Причина**: Не хватает зависимостей или SDK

**Решение**:
```bash
# Попробуйте вручную:
./gradlew clean
./gradlew build --refresh-dependencies
```

---

### Проблема 2: "SDK not found"

**Причина**: Android Studio не нашёл SDK

**Решение**: Создайте `local.properties`:
```properties
sdk.dir=C:\\Users\\YourName\\AppData\\Local\\Android\\Sdk
```

---

### Проблема 3: "Can't find some files"

**Причина**: Скачался не полный проект

**Решение**: 
1. Проверьте что скачали всю папку, не только часть
2. Убедитесь что есть `app/src/main/` папка
3. Перескачайте проект

---

### Проблема 4: "gradlew not executable"

**Причина**: Скрипт не исполняемый (Linux/Mac)

**Решение**:
```bash
chmod +x gradlew
chmod +x build_apk.sh
```

---

## 📊 Сколько весит проект

| Что | Размер |
|-----|--------|
| Исходный код (без .git) | ~1-2 MB |
| С .git историей | ~2-3 MB |
| После Gradle sync | ~300-500 MB |
| После первой сборки | ~500 MB - 1 GB |

**Это нормально!** Gradle скачивает все библиотеки локально.

---

## 🎓 Что нужно для работы

После скачивания проекта вам нужно:

### ✅ Установить (если ещё нет):

1. **Android Studio** 
   - https://developer.android.com/studio
   - ~1 GB скачивание

2. **JDK 17** (обычно идёт с Android Studio)
   - https://adoptium.net/temurin/releases/?version=17

3. **Android SDK** (устанавливается через Android Studio)
   - API 26-34

### ✅ Иметь на компьютере:

- **~5 GB места** на диске
- **Интернет** для первой сборки
- **4 GB RAM** минимум (8 GB рекомендуется)

---

## 💡 Рекомендации

### Для первого раза:

1. **Скачайте через Git** (если есть репозиторий)
2. **Откройте в Android Studio** (не в редакторе!)
3. **Дождитесь полной синхронизации** (важно!)
4. **Не трогайте файлы вручную** первые 5 минут

### После успешного открытия:

- ✅ Можете редактировать код
- ✅ Можете создавать APK
- ✅ Можете запускать на эмуляторе
- ✅ Можете делать коммиты в Git

---

## ✅ Чек-лист "Проект скачан правильно"

Отметьте что у вас есть:

- [ ] Папка проекта на компьютере
- [ ] Файл `build.gradle.kts` в корне
- [ ] Папка `app/` с файлом `build.gradle.kts`
- [ ] Папка `app/src/main/java/`
- [ ] Файл `AndroidManifest.xml`
- [ ] 29 файлов .kt в `app/src/main/java/`
- [ ] Файлы `gradlew` и `build_apk.sh`
- [ ] Папка `gradle/wrapper/`
- [ ] Документация (README.md и др.)

Если всё отмечено - **проект готов к работе!** 🎉

---

## 🎯 Что дальше?

После успешного скачивания:

1. **Откройте проект** в Android Studio
2. **Прочитайте** `BUILD_APK.md` - как создать APK
3. **Создайте APK** любым способом
4. **Установите на телефон** и тестируйте!

---

**Успехов с проектом! 🚀**

P.S. Если что-то непонятно - все инструкции в других MD файлах!
