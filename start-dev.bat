@echo off
setlocal

set ROOT_DIR=%~dp0
set BACKEND_DIR=%ROOT_DIR%backend
set FRONTEND_DIR=%ROOT_DIR%frontend
set ENV_FILE=%ROOT_DIR%.env

set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_DATABASE=campushub
set MYSQL_USER=root

if exist "%ENV_FILE%" (
    echo Loading local environment from .env
    for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%ENV_FILE%") do (
        if not "%%A"=="" set "%%A=%%B"
    )
    echo.
)

echo ========================================
echo CampusHub development startup
echo ========================================
echo.
echo Backend:  http://localhost:8080
echo Frontend: http://localhost:5173
echo.
echo Make sure MySQL is running before starting the backend.
echo If this is the first run, initialize the database first:
echo   database\init_database.bat
echo.
echo MySQL: %MYSQL_USER%@%MYSQL_HOST%:%MYSQL_PORT%/%MYSQL_DATABASE%
echo.

if not exist "%BACKEND_DIR%\pom.xml" (
    echo Backend project not found: %BACKEND_DIR%
    exit /b 1
)

if not exist "%FRONTEND_DIR%\package.json" (
    echo Frontend project not found: %FRONTEND_DIR%
    exit /b 1
)

start "CampusHub Backend" /D "%BACKEND_DIR%" cmd /k "mvn spring-boot:run"
start "CampusHub Frontend" /D "%FRONTEND_DIR%" cmd /k "if exist node_modules (npm run dev) else (npm install && npm run dev)"

echo Started backend and frontend in separate windows.
echo Close those windows to stop the services.

endlocal
