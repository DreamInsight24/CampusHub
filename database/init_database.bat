@echo off
setlocal

rem CampusHub MySQL initialization script for Windows.
rem Modify MYSQL_USER and DB_NAME below if your local MySQL uses another account or database name.
set MYSQL_HOST=localhost
set MYSQL_PORT=3306
set MYSQL_USER=root
set DB_NAME=campushub

set SCRIPT_DIR=%~dp0
set ROOT_DIR=%SCRIPT_DIR%..
set ENV_FILE=%ROOT_DIR%\.env

if exist "%ENV_FILE%" (
    echo Loading local environment from .env
    for /f "usebackq eol=# tokens=1,* delims==" %%A in ("%ENV_FILE%") do (
        if not "%%A"=="" set "%%A=%%B"
    )
)

if not "%MYSQL_DATABASE%"=="" set DB_NAME=%MYSQL_DATABASE%
set MYSQL_ARGS=--default-character-set=utf8mb4 -h %MYSQL_HOST% -P %MYSQL_PORT% -u %MYSQL_USER%

echo ========================================
echo CampusHub database initialization
echo Database: %DB_NAME%
echo MySQL user: %MYSQL_USER%
echo ========================================
echo.
if "%MYSQL_PASSWORD%"=="" (
    set /p MYSQL_PASSWORD=Please enter MySQL password:
) else (
    echo Using MySQL password from MYSQL_PASSWORD environment variable.
)
if not "%MYSQL_PASSWORD%"=="" set MYSQL_ARGS=%MYSQL_ARGS% -p%MYSQL_PASSWORD%

echo.
echo [1/4] Dropping old tables...
mysql %MYSQL_ARGS% < "%SCRIPT_DIR%00_drop_tables.sql"
if errorlevel 1 goto error

echo [2/4] Creating database...
mysql %MYSQL_ARGS% < "%SCRIPT_DIR%01_create_database.sql"
if errorlevel 1 goto error

echo [3/4] Creating tables...
mysql %MYSQL_ARGS% %DB_NAME% < "%SCRIPT_DIR%02_create_tables.sql"
if errorlevel 1 goto error

echo [4/4] Inserting test data...
mysql %MYSQL_ARGS% %DB_NAME% < "%SCRIPT_DIR%03_insert_test_data.sql"
if errorlevel 1 goto error

echo.
echo CampusHub database initialization completed successfully.
echo You can verify with:
echo   mysql -u %MYSQL_USER% -p
echo   SHOW DATABASES;
echo   USE %DB_NAME%;
echo   SHOW TABLES;
echo   SELECT * FROM user;
goto end

:error
echo.
echo Database initialization failed. Please check the error message above.
echo If mysql is not recognized, add the MySQL bin directory to PATH.
exit /b 1

:end
endlocal
