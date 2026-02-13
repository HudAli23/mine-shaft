@echo off
echo Setting up build environment with Java 11...

REM Save current JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM Set JAVA_HOME to JDK 11 (adjust this path if needed)
set JAVA_HOME=C:\Program Files\Java\jdk-11

echo Using Java:
"%JAVA_HOME%\bin\java" -version

REM Clean Gradle cache that might be causing issues
echo Cleaning problematic Gradle cache...
rmdir /S /Q "%USERPROFILE%\.gradle\caches\transforms-3\7df4e920901899c4bd8614fdb30711d6"

REM Run Gradle clean
echo Running Gradle clean...
call gradlew.bat clean --no-daemon

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%

echo Done.
pause
