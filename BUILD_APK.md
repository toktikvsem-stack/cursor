# 📦 Как создать APK файл

## ⚡ Быстрый способ (рекомендуется)

### Вариант 1: Через Android Studio (самый простой)

1. **Откройте проект в Android Studio**
   ```
   File → Open → выберите папку /workspace
   ```

2. **Дождитесь синхронизации Gradle** (2-3 минуты)

3. **Создайте APK**
   ```
   Build → Build Bundle(s) / APK(s) → Build APK(s)
   ```

4. **Готово!** APK файл будет здесь:
   ```
   /workspace/app/build/outputs/apk/debug/app-debug.apk
   ```

5. **Найдите файл**
   - Android Studio покажет уведомление с ссылкой "locate"
   - Или откройте папку вручную

**Размер APK**: ~15-20 MB

---

## 🖥️ Вариант 2: Через командную строку (для продвинутых)

### Требования:
- ✅ Android Studio установлена
- ✅ Android SDK настроен
- ✅ JDK 17 установлен
- ✅ Переменные окружения настроены

### Windows:

```batch
cd C:\путь\к\workspace
gradlew.bat assembleDebug
```

### macOS / Linux:

```bash
cd /путь/к/workspace
chmod +x gradlew
./gradlew assembleDebug
```

### Результат:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📱 Вариант 3: Release APK (для публикации)

### Шаг 1: Создайте keystore (один раз)

```bash
keytool -genkey -v -keystore fashion-assistant.keystore \
  -alias fashion-key -keyalg RSA -keysize 2048 -validity 10000
```

Введите данные:
- Пароль keystore: **запомните!**
- Имя, организация, город, и т.д.

### Шаг 2: Настройте подпись

Создайте файл `keystore.properties` в корне проекта:

```properties
storePassword=ваш_пароль
keyPassword=ваш_пароль
keyAlias=fashion-key
storeFile=../fashion-assistant.keystore
```

### Шаг 3: Обновите `app/build.gradle.kts`

Добавьте перед `android {`:

```kotlin
val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}
```

Добавьте в `android {` перед `buildTypes`:

```kotlin
signingConfigs {
    create("release") {
        if (keystorePropertiesFile.exists()) {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }
}
```

Обновите `release` в `buildTypes`:

```kotlin
release {
    isMinifyEnabled = true
    isShrinkResources = true
    signingConfig = signingConfigs.getByName("release")
    proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
    )
}
```

### Шаг 4: Соберите Release APK

```bash
./gradlew assembleRelease
```

### Результат:
```
app/build/outputs/apk/release/app-release.apk
```

**Размер**: ~10-12 MB (меньше благодаря ProGuard)

---

## 🚀 Автоматическая сборка (готовый скрипт)

Я создал скрипт `build_apk.sh` - просто запустите его!

```bash
chmod +x build_apk.sh
./build_apk.sh
```

---

## 📲 Установка APK на устройство

### Способ 1: ADB (через USB)

1. **Включите отладку по USB** на телефоне:
   ```
   Настройки → О телефоне → 7 раз тапнуть "Номер сборки"
   Настройки → Для разработчиков → Отладка по USB (вкл)
   ```

2. **Подключите телефон к компьютеру**

3. **Установите APK**:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Способ 2: Прямая установка

1. **Скопируйте APK на телефон**
   - Через USB
   - Через облако (Google Drive, Dropbox)
   - Отправьте себе по email

2. **Откройте APK на телефоне**
   - Найдите файл через "Мои файлы"
   - Тапните на него

3. **Разрешите установку**
   ```
   Настройки → Безопасность → 
   Неизвестные источники (разрешить для этого приложения)
   ```

4. **Нажмите "Установить"**

### Способ 3: Из Android Studio

```
Run → Run 'app'
Или просто нажмите ▶️
```

---

## 🔍 Проверка APK

### Посмотреть информацию об APK:

```bash
# Размер
ls -lh app/build/outputs/apk/debug/app-debug.apk

# Детальная информация
aapt dump badging app/build/outputs/apk/debug/app-debug.apk

# Или через Android Studio:
Build → Analyze APK → выберите файл
```

### Что вы увидите:
- **Package name**: com.fashionassistant.app
- **Version**: 1.0
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Permissions**: CAMERA, READ_MEDIA_IMAGES

---

## ❗ Частые проблемы

### Проблема 1: "SDK not found"

**Решение**:
```bash
# Найдите путь к SDK
# Android Studio → Settings → Appearance & Behavior → 
# System Settings → Android SDK

# Создайте local.properties в корне проекта:
echo "sdk.dir=/path/to/Android/Sdk" > local.properties

# Windows пример:
# sdk.dir=C\:\\Users\\YourName\\AppData\\Local\\Android\\Sdk

# macOS пример:
# sdk.dir=/Users/YourName/Library/Android/sdk

# Linux пример:
# sdk.dir=/home/YourName/Android/Sdk
```

### Проблема 2: "Java version mismatch"

**Решение**:
```bash
# Проверьте версию Java
java -version

# Должна быть JDK 17
# Установите правильную версию:
# https://adoptium.net/temurin/releases/?version=17
```

### Проблема 3: Gradle sync failed

**Решение**:
```bash
# Очистите кэш
./gradlew clean
./gradlew --stop

# В Android Studio:
# File → Invalidate Caches / Restart
```

### Проблема 4: "Out of memory"

**Решение**: Отредактируйте `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

---

## 📊 Типы сборки

### Debug APK
- ✅ Быстрая сборка (2-5 минут)
- ✅ Включает отладочную информацию
- ✅ Можно устанавливать сразу
- ❌ Больший размер (~18 MB)
- ❌ Не оптимизирован
- **Для**: тестирования, разработки

### Release APK
- ❌ Медленная сборка (5-10 минут)
- ❌ Нужен keystore (подпись)
- ✅ Оптимизирован (ProGuard)
- ✅ Меньший размер (~12 MB)
- ✅ Быстрее работает
- **Для**: публикации в Google Play, передачи пользователям

---

## 🎯 Где использовать APK

### ✅ Можно:
- Тестирование на своих устройствах
- Передать знакомым для тестирования
- Внутреннее использование в компании
- Прототипирование и демо

### ❌ Нельзя (без Google Play):
- Публичное распространение
- Монетизация
- Автоматические обновления
- Высокий уровень доверия пользователей

### 🏪 Для публикации в Google Play:

1. **Создайте аккаунт разработчика** ($25 один раз)
2. **Соберите Release AAB** (не APK!):
   ```bash
   ./gradlew bundleRelease
   ```
3. **Загрузите в Google Play Console**
4. **Заполните описание, скриншоты**
5. **Отправьте на проверку** (1-3 дня)

---

## 📦 Bundle vs APK

### APK (рекомендуется для тестирования)
- Один файл для всех устройств
- Больший размер
- Можно установить напрямую
- Формат: `.apk`

### AAB (рекомендуется для Google Play)
- Google Play создаёт оптимизированные APK для каждого устройства
- Меньший размер скачивания
- Нельзя установить напрямую
- Формат: `.aab`

Сборка AAB:
```bash
./gradlew bundleDebug
# или
./gradlew bundleRelease
```

Результат: `app/build/outputs/bundle/debug/app-debug.aab`

---

## ⏱️ Время сборки

| Тип | Первая сборка | Повторная |
|-----|--------------|-----------|
| Debug APK | 5-10 мин | 30-60 сек |
| Release APK | 10-15 мин | 2-3 мин |
| Debug AAB | 5-10 мин | 30-60 сек |
| Release AAB | 10-15 мин | 2-3 мин |

*Время зависит от мощности компьютера

---

## ✅ Чек-лист перед сборкой

- [ ] Android Studio установлена
- [ ] Проект открыт и синхронизирован
- [ ] Нет ошибок компиляции
- [ ] Android SDK установлен
- [ ] JDK 17 установлен
- [ ] Достаточно места на диске (~5 GB)
- [ ] Интернет есть (для скачивания зависимостей)

---

## 🎓 Полезные команды

```bash
# Информация о сборке
./gradlew tasks

# Очистка проекта
./gradlew clean

# Сборка Debug APK
./gradlew assembleDebug

# Сборка Release APK
./gradlew assembleRelease

# Сборка Debug AAB
./gradlew bundleDebug

# Сборка Release AAB
./gradlew bundleRelease

# Запуск тестов
./gradlew test

# Установка на подключенное устройство
./gradlew installDebug

# Всё сразу (build + install)
./gradlew installDebug

# Проверка зависимостей
./gradlew dependencies

# Lint проверка
./gradlew lint
```

---

**Успешной сборки! 🚀**

Если возникнут проблемы - смотрите раздел "Частые проблемы" выше.
