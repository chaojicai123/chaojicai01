@echo off
setlocal

set "MAVEN_PROJECTBASEDIR=%~dp0"
if not "%MAVEN_PROJECTBASEDIR%"=="" set "MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%"

if "%JAVA_HOME%"=="" (
  set "JAVA_EXE=java"
) else (
  if exist "%JAVA_HOME%\java.exe" (
    set "JAVA_EXE=%JAVA_HOME%\java.exe"
  ) else (
    set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
  )
)

if not exist "%JAVA_EXE%" (
  if "%JAVA_HOME%"=="" (
    echo Error: JAVA_HOME is not set. Please set JAVA_HOME to your JDK installation.
  ) else (
    echo Error: JAVA_HOME is set to "%JAVA_HOME%" but %JAVA_EXE% was not found.
  )
  exit /b 1
)

set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set "WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain"

if not exist "%WRAPPER_JAR%" (
  echo Downloading Maven Wrapper...
  if not exist "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper" mkdir "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper"
  powershell -NoProfile -ExecutionPolicy Bypass -Command "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; $u='https://repo.maven.apache.org/maven2/io/takari/maven-wrapper/0.5.6/maven-wrapper-0.5.6.jar'; $p='%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar'; (New-Object Net.WebClient).DownloadFile($u, $p)"
  if not exist "%WRAPPER_JAR%" (
    echo Download failed. Try: install Maven from https://maven.apache.org/download.cgi and add bin to PATH.
    exit /b 1
  )
)

cd /d "%MAVEN_PROJECTBASEDIR%"
"%JAVA_EXE%" -classpath ".\.mvn\wrapper\maven-wrapper.jar" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*
exit /b %ERRORLEVEL%
