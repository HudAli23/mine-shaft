@echo off
echo ===================================================
echo Android Project Build Helper
echo ===================================================

REM Save current JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM Check if JDK 11 exists at the default location
if exist "C:\Program Files\Java\jdk-11" (
    echo Found JDK 11 at default location
    set JAVA_HOME=C:\Program Files\Java\jdk-11
) else (
    echo JDK 11 not found at default location.
    echo Please enter the path to your JDK 11 installation:
    set /p JAVA_HOME=
)

echo Using Java:
"%JAVA_HOME%\bin\java" -version

REM Clean Gradle daemon
echo Stopping any running Gradle daemons...
call gradlew.bat --stop

REM Clean Gradle cache that might be causing issues
echo Cleaning problematic Gradle cache...
rmdir /S /Q "%USERPROFILE%\.gradle\caches\transforms-3"
rmdir /S /Q "%USERPROFILE%\.gradle\daemon"

REM Delete .gradle folder in project
echo Cleaning project .gradle folder...
rmdir /S /Q ".gradle"

REM Delete build folders
echo Cleaning build folders...
rmdir /S /Q "app\build"
rmdir /S /Q "build"

REM Set environment variables to help with the build
echo Setting environment variables...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxPermSize=512m"

REM Run Gradle clean
echo Running Gradle clean...
call gradlew.bat clean --no-daemon --info

REM If clean succeeded, try to build
if %ERRORLEVEL% EQU 0 (
    echo Clean succeeded, running build...
    call gradlew.bat build --no-daemon --info
)

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%

echo Done.
pause
