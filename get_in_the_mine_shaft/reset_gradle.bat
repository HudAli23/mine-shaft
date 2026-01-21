@echo off
echo ===================================================
echo Android Project Gradle Reset Tool
echo ===================================================

REM Save current JAVA_HOME
set OLD_JAVA_HOME=%JAVA_HOME%

REM Try to find Java 8
if exist "C:\Program Files\Java\jdk1.8.0_*" (
    for /d %%i in ("C:\Program Files\Java\jdk1.8.0_*") do (
        echo Found JDK 8: %%i
        set JAVA_HOME=%%i
        goto java_found
    )
)

if exist "C:\Program Files\Java\jre1.8.0_*" (
    for /d %%i in ("C:\Program Files\Java\jre1.8.0_*") do (
        echo Found JRE 8: %%i
        set JAVA_HOME=%%i
        goto java_found
    )
)

echo JDK 8 not found at default location.
echo Please enter the path to your JDK 8 installation:
set /p JAVA_HOME=

:java_found
echo Using Java:
"%JAVA_HOME%\bin\java" -version

REM Stop any running Gradle daemons
echo Stopping any running Gradle daemons...
call gradlew.bat --stop

REM Clean Gradle caches
echo Cleaning Gradle caches...
rmdir /S /Q "%USERPROFILE%\.gradle\caches"
rmdir /S /Q "%USERPROFILE%\.gradle\daemon"
rmdir /S /Q "%USERPROFILE%\.gradle\wrapper"

REM Delete .gradle folder in project
echo Cleaning project .gradle folder...
rmdir /S /Q ".gradle"

REM Delete build folders
echo Cleaning build folders...
rmdir /S /Q "app\build"
rmdir /S /Q "build"

REM Delete existing wrapper files
echo Deleting existing wrapper files...
del /F /Q "gradlew.bat"
del /F /Q "gradlew"
rmdir /S /Q "gradle\wrapper"

REM Create gradle/wrapper directory
mkdir "gradle\wrapper"

REM Create a new gradle-wrapper.properties file
echo Creating new gradle-wrapper.properties file...
echo distributionBase=GRADLE_USER_HOME > "gradle\wrapper\gradle-wrapper.properties"
echo distributionPath=wrapper/dists >> "gradle\wrapper\gradle-wrapper.properties"
echo distributionUrl=https\://services.gradle.org/distributions/gradle-6.5-bin.zip >> "gradle\wrapper\gradle-wrapper.properties"
echo zipStoreBase=GRADLE_USER_HOME >> "gradle\wrapper\gradle-wrapper.properties"
echo zipStorePath=wrapper/dists >> "gradle\wrapper\gradle-wrapper.properties"

REM Create a new gradlew.bat file
echo Creating new gradlew.bat file...
echo @rem >> "gradlew.bat"
echo @rem  Gradle startup script for Windows >> "gradlew.bat"
echo @rem >> "gradlew.bat"
echo @rem Set local scope for the variables with windows NT shell >> "gradlew.bat"
echo if "%%OS%%"=="Windows_NT" setlocal >> "gradlew.bat"
echo. >> "gradlew.bat"
echo set DIRNAME=%%~dp0 >> "gradlew.bat"
echo if "%%DIRNAME%%" == "" set DIRNAME=. >> "gradlew.bat"
echo set APP_BASE_NAME=%%~n0 >> "gradlew.bat"
echo set APP_HOME=%%DIRNAME%% >> "gradlew.bat"
echo. >> "gradlew.bat"
echo @rem Add default JVM options here. >> "gradlew.bat"
echo set DEFAULT_JVM_OPTS= >> "gradlew.bat"
echo. >> "gradlew.bat"
echo @rem Find java.exe >> "gradlew.bat"
echo if defined JAVA_HOME goto findJavaFromJavaHome >> "gradlew.bat"
echo. >> "gradlew.bat"
echo set JAVA_EXE=java.exe >> "gradlew.bat"
echo %% ... rest of standard gradlew.bat content ... %% >> "gradlew.bat"
echo. >> "gradlew.bat"
echo @rem Execute Gradle >> "gradlew.bat"
echo "%%JAVA_EXE%%" %%DEFAULT_JVM_OPTS%% %%JAVA_OPTS%% %%GRADLE_OPTS%% "-Dorg.gradle.appname=%%APP_BASE_NAME%%" -classpath "%%CLASSPATH%%" org.gradle.wrapper.GradleWrapperMain %%* >> "gradlew.bat"
echo. >> "gradlew.bat"
echo :end >> "gradlew.bat"
echo @rem End local scope for the variables with windows NT shell >> "gradlew.bat"
echo if "%%ERRORLEVEL%%"=="0" goto mainEnd >> "gradlew.bat"
echo. >> "gradlew.bat"
echo :fail >> "gradlew.bat"
echo rem Set variable GRADLE_EXIT_CONSOLE if you need the _script_ return code instead of >> "gradlew.bat"
echo rem the _cmd.exe /c_ return code! >> "gradlew.bat"
echo if not "" == "%%GRADLE_EXIT_CONSOLE%%" exit 1 >> "gradlew.bat"
echo exit /b 1 >> "gradlew.bat"
echo. >> "gradlew.bat"
echo :mainEnd >> "gradlew.bat"
echo if "%%OS%%"=="Windows_NT" endlocal >> "gradlew.bat"
echo. >> "gradlew.bat"
echo :omega >> "gradlew.bat"

REM Create simple build.gradle files
echo Creating simple build.gradle files...

echo // Top-level build file > "build.gradle"
echo buildscript {>> "build.gradle"
echo     repositories {>> "build.gradle"
echo         google()>> "build.gradle"
echo         jcenter()>> "build.gradle"
echo     }>> "build.gradle"
echo     dependencies {>> "build.gradle"
echo         classpath 'com.android.tools.build:gradle:4.1.3'>> "build.gradle"
echo         classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21">> "build.gradle"
echo         classpath 'com.google.dagger:hilt-android-gradle-plugin:2.38.1'>> "build.gradle"
echo     }>> "build.gradle"
echo }>> "build.gradle"
echo. >> "build.gradle"
echo allprojects {>> "build.gradle"
echo     repositories {>> "build.gradle"
echo         google()>> "build.gradle"
echo         jcenter()>> "build.gradle"
echo     }>> "build.gradle"
echo }>> "build.gradle"

echo // App build file > "app\build.gradle"
echo apply plugin: 'com.android.application'>> "app\build.gradle"
echo apply plugin: 'kotlin-android'>> "app\build.gradle"
echo apply plugin: 'kotlin-kapt'>> "app\build.gradle"
echo apply plugin: 'dagger.hilt.android.plugin'>> "app\build.gradle"
echo. >> "app\build.gradle"
echo android {>> "app\build.gradle"
echo     compileSdkVersion 31>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     defaultConfig {>> "app\build.gradle"
echo         applicationId "com.example.pickaxeinthemineshaft">> "app\build.gradle"
echo         minSdkVersion 24>> "app\build.gradle"
echo         targetSdkVersion 31>> "app\build.gradle"
echo         versionCode 1>> "app\build.gradle"
echo         versionName "1.0">> "app\build.gradle"
echo. >> "app\build.gradle"
echo         testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner">> "app\build.gradle"
echo     }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     buildTypes {>> "app\build.gradle"
echo         release {>> "app\build.gradle"
echo             minifyEnabled false>> "app\build.gradle"
echo             proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'>> "app\build.gradle"
echo         }>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     compileOptions {>> "app\build.gradle"
echo         sourceCompatibility JavaVersion.VERSION_1_8>> "app\build.gradle"
echo         targetCompatibility JavaVersion.VERSION_1_8>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     kotlinOptions {>> "app\build.gradle"
echo         jvmTarget = '1.8'>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     buildFeatures {>> "app\build.gradle"
echo         compose true>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     composeOptions {>> "app\build.gradle"
echo         kotlinCompilerExtensionVersion '1.0.1'>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo dependencies {>> "app\build.gradle"
echo     implementation "org.jetbrains.kotlin:kotlin-stdlib:1.5.21">> "app\build.gradle"
echo     implementation 'androidx.core:core-ktx:1.6.0'>> "app\build.gradle"
echo     implementation 'androidx.appcompat:appcompat:1.3.1'>> "app\build.gradle"
echo     implementation 'com.google.android.material:material:1.4.0'>> "app\build.gradle"
echo. >> "app\build.gradle"
echo     // Compose>> "app\build.gradle"
echo     implementation "androidx.compose.ui:ui:1.0.1">> "app\build.gradle"
echo     implementation "androidx.compose.material:material:1.0.1">> "app\build.gradle"
echo     implementation "androidx.compose.ui:ui-tooling-preview:1.0.1">> "app\build.gradle"
echo     implementation 'androidx.activity:activity-compose:1.3.1'>> "app\build.gradle"
echo     debugImplementation "androidx.compose.ui:ui-tooling:1.0.1">> "app\build.gradle"
echo. >> "app\build.gradle"
echo     // Room>> "app\build.gradle"
echo     implementation "androidx.room:room-runtime:2.3.0">> "app\build.gradle"
echo     implementation "androidx.room:room-ktx:2.3.0">> "app\build.gradle"
echo     kapt "androidx.room:room-compiler:2.3.0">> "app\build.gradle"
echo. >> "app\build.gradle"
echo     // Hilt>> "app\build.gradle"
echo     implementation "com.google.dagger:hilt-android:2.38.1">> "app\build.gradle"
echo     kapt "com.google.dagger:hilt-android-compiler:2.38.1">> "app\build.gradle"
echo     implementation "androidx.hilt:hilt-navigation-compose:1.0.0-alpha03">> "app\build.gradle"
echo. >> "app\build.gradle"
echo     // Testing>> "app\build.gradle"
echo     testImplementation 'junit:junit:4.13.2'>> "app\build.gradle"
echo     androidTestImplementation 'androidx.test.ext:junit:1.1.3'>> "app\build.gradle"
echo     androidTestImplementation 'androidx.test.espresso:espresso-core:3.4.0'>> "app\build.gradle"
echo     androidTestImplementation "androidx.compose.ui:ui-test-junit4:1.0.1">> "app\build.gradle"
echo }>> "app\build.gradle"
echo. >> "app\build.gradle"
echo kapt {>> "app\build.gradle"
echo     correctErrorTypes true>> "app\build.gradle"
echo     arguments {>> "app\build.gradle"
echo         arg("room.schemaLocation", "$projectDir/schemas")>> "app\build.gradle"
echo     }>> "app\build.gradle"
echo }>> "app\build.gradle"

echo // Settings file > "settings.gradle"
echo include ':app'>> "settings.gradle"
echo rootProject.name = "PICKAXE IN THE MINESHAFT">> "settings.gradle"

REM Set environment variables to help with the build
echo Setting environment variables...
set GRADLE_OPTS=-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxPermSize=512m"

REM Download the Gradle wrapper
echo Downloading Gradle wrapper...
curl -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v6.5.0/gradle/wrapper/gradle-wrapper.jar

echo.
echo ===================================================
echo Setup complete! Now try running:
echo gradlew.bat clean build --no-daemon
echo ===================================================

REM Restore original JAVA_HOME
set JAVA_HOME=%OLD_JAVA_HOME%
echo Restored original JAVA_HOME: %JAVA_HOME%

pause
