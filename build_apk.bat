@echo off
REM Fashion Assistant - Build APK Script for Windows

echo ==================================
echo Fashion Assistant - Build APK
echo ==================================
echo.

REM Check Java
echo Checking environment...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java not found!
    echo Please install JDK 17: https://adoptium.net/
    pause
    exit /b 1
)

echo [OK] Java found
java -version 2>&1 | findstr /R "version"

REM Check Android SDK
if "%ANDROID_HOME%"=="" (
    if "%ANDROID_SDK_ROOT%"=="" (
        echo [WARNING] Android SDK not found in environment variables
        echo.
        set /p SDK_PATH="Enter Android SDK path (or press Enter to exit): "
        if "%SDK_PATH%"=="" exit /b 1
        
        echo sdk.dir=%SDK_PATH% > local.properties
        echo [OK] local.properties created
    ) else (
        echo [OK] Android SDK: %ANDROID_SDK_ROOT%
    )
) else (
    echo [OK] Android SDK: %ANDROID_HOME%
)

echo.
echo ==================================
echo Building APK...
echo ==================================
echo.

REM Choose build type
echo Select build type:
echo 1) Debug APK (fast, for testing)
echo 2) Release APK (slow, optimized)
echo.
set /p BUILD_TYPE="Your choice (1 or 2): "

REM Clean
echo.
echo Cleaning previous builds...
call gradlew.bat clean

REM Build
echo.
if "%BUILD_TYPE%"=="2" (
    echo Building Release APK...
    call gradlew.bat assembleRelease
    set APK_PATH=app\build\outputs\apk\release\app-release.apk
) else (
    echo Building Debug APK...
    call gradlew.bat assembleDebug
    set APK_PATH=app\build\outputs\apk\debug\app-debug.apk
)

REM Check result
if exist "%APK_PATH%" (
    echo.
    echo ==================================
    echo [SUCCESS] Build completed!
    echo ==================================
    echo.
    echo APK file: %APK_PATH%
    echo.
    echo To install:
    echo 1. Copy to phone and open
    echo 2. Or use: adb install %APK_PATH%
    echo.
    
    REM Try to install
    adb devices 2>nul | find "device" >nul
    if %ERRORLEVEL% EQU 0 (
        echo Device detected!
        set /p INSTALL="Install now? (y/n): "
        if /i "%INSTALL%"=="y" (
            echo Installing...
            adb install -r "%APK_PATH%"
            echo Done!
        )
    )
) else (
    echo.
    echo ==================================
    echo [ERROR] Build failed!
    echo ==================================
    echo.
    echo Check the logs above for details
    echo See BUILD_APK.md for troubleshooting
)

echo.
pause
