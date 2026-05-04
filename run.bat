@echo off
echo ==========================================
echo OCSMS - Supabase Enabled (Glassmorphism)
echo ==========================================
echo.
echo Compiling Java files...
cd src

:: Compile with Gson library in classpath
javac -encoding UTF-8 -cp ".;..\lib\gson-2.10.1.jar" ocsms\Main.java

if %ERRORLEVEL% GEQ 1 (
    echo.
    echo [ERROR] Compilation failed! Please check your code.
    pause
    exit /b
)

echo Compilation successful! Launching OCSMS...
java -cp ".;..\lib\gson-2.10.1.jar" ocsms.Main
pause
