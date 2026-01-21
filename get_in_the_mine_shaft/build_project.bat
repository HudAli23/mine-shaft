@echo off
echo ===================================================
echo Android Project Build Helper
echo ===================================================

REM Save current JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM Check for Java 8 or 11
if exist "C:\Program Files\Java\jdk1.8.0_*" (
    for /d %%i in ("C:\Program Files\Java\jdk1.8.0_*") do (
        echo Found JDK 8: %%i
        set JAVA_HOME=%%i
        goto java_found
    )
)

if exist "C:\Program Files\Java\jdk-11*" (
    for /d %%i in ("C:\Program Files\Java\jdk-11*") do (
        echo Found JDK 11: %%i
        set JAVA_HOME=%%i
        goto java_found
    )
)

echo JDK 8 or 11 not found at default location.
echo Please enter the path to your JDK 8 or 11 installation:
set /p JAVA_HOME=

:java_found
echo Using Java:
"%JAVA_HOME%\bin\java" -version

REM Stop any running Gradle daemons
echo Stopping any running Gradle daemons...
call gradlew.bat --stop

REM Clean Gradle caches that might be causing issues
echo Cleaning problematic Gradle cache...
rmdir /S /Q "%USERPROFILE%\.gradle\caches\transforms-3"
rmdir /S /Q "%USERPROFILE%\.gradle\daemon"

REM Set environment variables to help with the build
echo Setting environment variables...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxPermSize=512m"

REM Clean project
echo Cleaning project...
if exist ".gradle" (
    echo Removing .gradle directory...
    rmdir /S /Q .gradle
)

if exist "build" (
    echo Removing build directory...
    rmdir /S /Q build
)

if exist "app\build" (
    echo Removing app\build directory...
    rmdir /S /Q app\build
)

REM Clean Gradle cache for this project
echo Cleaning Gradle cache for this project...
set GRADLE_USER_HOME=%USERPROFILE%\.gradle
if exist "%GRADLE_USER_HOME%\caches\modules-2\files-2.1\androidx.compose" (
    echo Removing Compose cache...
    rmdir /S /Q "%GRADLE_USER_HOME%\caches\modules-2\files-2.1\androidx.compose"
)

REM Stop Gradle daemon
echo Stopping Gradle daemon...
call .\gradlew.bat --stop

REM Run Gradle clean and build
echo Running Gradle with clean build...
call .\gradlew.bat clean build --refresh-dependencies --no-daemon --stacktrace

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%

echo Done.
pause
