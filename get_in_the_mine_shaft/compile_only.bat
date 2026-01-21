@echo off
echo ===== PICKAXE IN THE MINESHAFT - Compile Only Script =====

echo.
echo Setting up environment...
set OLD_JAVA_HOME=%JAVA_HOME%
set JAVA_HOME=C:\Program Files\Java\jdk-11
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Java version:
java -version

echo.
echo Setting environment variables...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxPermSize=512m"

echo.
echo Stopping Gradle daemon...
call .\gradlew.bat --stop

echo.
echo Running compilation only...
call .\gradlew.bat compileDebugKotlin --refresh-dependencies --no-daemon

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%
echo Done.
pause
