@echo off
echo ===================================================
echo   Starting Smart Expense Tracker Application...
echo ===================================================

REM Check if system mvn exists, otherwise use local portable Maven
where mvn >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo Using system Maven...
    mvn spring-boot:run
) else (
    if exist "%~dp0..\maven\apache-maven-3.9.6\bin\mvn.cmd" (
        echo Using bundled Maven...
        "%~dp0..\maven\apache-maven-3.9.6\bin\mvn.cmd" spring-boot:run
    ) else (
        echo Error: Maven not found in PATH or parent directory.
        echo Please install Maven or run via IntelliJ IDEA / Eclipse.
        pause
    )
)
