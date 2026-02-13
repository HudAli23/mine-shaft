@echo off
echo Setting up Java 11 environment for building...

REM Save the current JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM Set JAVA_HOME to JDK 11
set JAVA_HOME=C:\Program Files\Java\jdk-11

REM Verify Java version
echo Using Java version:
"%JAVA_HOME%\bin\java" -version

REM Run Gradle command
echo Running Gradle command...
call gradlew clean --info

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%

pause
