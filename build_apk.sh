#!/bin/bash

# 🚀 Fashion Assistant - Автоматическая сборка APK
# Этот скрипт автоматически соберет APK файл

echo "=================================="
echo "🎨 Fashion Assistant - Сборка APK"
echo "=================================="
echo ""

# Цвета для вывода
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Проверка Java
echo "📋 Проверка окружения..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    echo -e "${GREEN}✓${NC} Java найдена: $(java -version 2>&1 | head -1)"
    
    if [ "$JAVA_VERSION" -lt "17" ]; then
        echo -e "${YELLOW}⚠${NC} Рекомендуется JDK 17, у вас версия $JAVA_VERSION"
    fi
else
    echo -e "${RED}✗${NC} Java не найдена!"
    echo "Установите JDK 17: https://adoptium.net/"
    exit 1
fi

# Проверка Android SDK
if [ -z "$ANDROID_HOME" ] && [ -z "$ANDROID_SDK_ROOT" ]; then
    echo -e "${YELLOW}⚠${NC} Android SDK не найден в переменных окружения"
    echo "Пытаюсь найти SDK автоматически..."
    
    # Попытка найти SDK
    if [ -d "$HOME/Android/Sdk" ]; then
        export ANDROID_SDK_ROOT="$HOME/Android/Sdk"
        echo -e "${GREEN}✓${NC} SDK найден: $ANDROID_SDK_ROOT"
    elif [ -d "$HOME/Library/Android/sdk" ]; then
        export ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"
        echo -e "${GREEN}✓${NC} SDK найден: $ANDROID_SDK_ROOT"
    else
        echo -e "${RED}✗${NC} Android SDK не найден!"
        echo "Создайте файл local.properties с путём к SDK:"
        echo "sdk.dir=/path/to/Android/Sdk"
        
        # Спросить пользователя
        read -p "Введите путь к Android SDK (или Enter для выхода): " SDK_PATH
        if [ -z "$SDK_PATH" ]; then
            exit 1
        fi
        
        echo "sdk.dir=$SDK_PATH" > local.properties
        echo -e "${GREEN}✓${NC} local.properties создан"
    fi
else
    echo -e "${GREEN}✓${NC} Android SDK: ${ANDROID_SDK_ROOT:-$ANDROID_HOME}"
fi

echo ""
echo "=================================="
echo "🔨 Начинаю сборку..."
echo "=================================="
echo ""

# Делаем gradlew исполняемым
chmod +x gradlew

# Выбор типа сборки
echo "Выберите тип сборки:"
echo "1) Debug APK (быстро, для тестирования)"
echo "2) Release APK (медленно, оптимизированный)"
echo ""
read -p "Ваш выбор (1 или 2): " BUILD_TYPE

if [ "$BUILD_TYPE" == "2" ]; then
    # Release сборка
    echo ""
    echo "🔐 Release сборка требует keystore для подписи"
    
    if [ ! -f "keystore.properties" ]; then
        echo -e "${YELLOW}⚠${NC} keystore.properties не найден"
        echo ""
        echo "Создать новый keystore? (y/n)"
        read -p "> " CREATE_KEYSTORE
        
        if [ "$CREATE_KEYSTORE" == "y" ]; then
            echo ""
            echo "Создание keystore..."
            keytool -genkey -v -keystore fashion-assistant.keystore \
                -alias fashion-key -keyalg RSA -keysize 2048 -validity 10000
            
            echo ""
            read -p "Введите пароль keystore: " STORE_PASSWORD
            
            # Создаём keystore.properties
            cat > keystore.properties << EOF
storePassword=$STORE_PASSWORD
keyPassword=$STORE_PASSWORD
keyAlias=fashion-key
storeFile=../fashion-assistant.keystore
EOF
            
            echo -e "${GREEN}✓${NC} keystore.properties создан"
        else
            echo "Release сборка невозможна без keystore"
            echo "Собираю Debug APK вместо этого..."
            BUILD_TYPE="1"
        fi
    fi
fi

# Очистка предыдущих сборок
echo ""
echo "🧹 Очистка предыдущих сборок..."
./gradlew clean

# Сборка
echo ""
if [ "$BUILD_TYPE" == "2" ]; then
    echo "🔨 Собираю Release APK (это может занять 5-10 минут)..."
    ./gradlew assembleRelease
    
    APK_PATH="app/build/outputs/apk/release/app-release.apk"
    BUILD_NAME="Release"
else
    echo "🔨 Собираю Debug APK (это может занять 2-5 минут)..."
    ./gradlew assembleDebug
    
    APK_PATH="app/build/outputs/apk/debug/app-debug.apk"
    BUILD_NAME="Debug"
fi

# Проверка результата
echo ""
if [ -f "$APK_PATH" ]; then
    echo "=================================="
    echo -e "${GREEN}✓ Сборка успешна!${NC}"
    echo "=================================="
    echo ""
    
    # Информация о файле
    FILE_SIZE=$(du -h "$APK_PATH" | cut -f1)
    echo "📦 Файл: $APK_PATH"
    echo "📏 Размер: $FILE_SIZE"
    echo ""
    
    # Дополнительная информация (если есть aapt)
    if command -v aapt &> /dev/null; then
        echo "📋 Информация о APK:"
        aapt dump badging "$APK_PATH" | grep -E "package:|sdkVersion:|targetSdkVersion:"
        echo ""
    fi
    
    echo "=================================="
    echo "📱 Как установить:"
    echo "=================================="
    echo ""
    echo "Способ 1: Через ADB"
    echo "  adb install $APK_PATH"
    echo ""
    echo "Способ 2: Скопируйте на телефон"
    echo "  Откройте файл на телефоне и нажмите 'Установить'"
    echo ""
    echo "Способ 3: Отправьте себе"
    echo "  Email, Telegram, WhatsApp - любым способом"
    echo ""
    
    # Предложить установить
    if command -v adb &> /dev/null; then
        DEVICES=$(adb devices | grep -w "device" | wc -l)
        if [ "$DEVICES" -gt "0" ]; then
            echo -e "${GREEN}✓${NC} Обнаружено подключенное устройство"
            read -p "Установить APK сейчас? (y/n): " INSTALL_NOW
            
            if [ "$INSTALL_NOW" == "y" ]; then
                echo ""
                echo "📲 Устанавливаю..."
                adb install -r "$APK_PATH"
                echo ""
                echo -e "${GREEN}✓${NC} Установка завершена!"
                echo "Приложение появилось на устройстве"
            fi
        fi
    fi
    
    echo ""
    echo "🎉 Готово! Приятного использования!"
    
else
    echo "=================================="
    echo -e "${RED}✗ Ошибка сборки!${NC}"
    echo "=================================="
    echo ""
    echo "Проверьте логи выше для деталей"
    echo ""
    echo "Частые проблемы:"
    echo "1. Android SDK не найден → создайте local.properties"
    echo "2. Нет интернета → нужен для скачивания зависимостей"
    echo "3. Мало места на диске → нужно минимум 5 GB"
    echo "4. Неправильная версия Java → нужна JDK 17"
    echo ""
    echo "Подробнее: смотрите BUILD_APK.md"
    exit 1
fi
