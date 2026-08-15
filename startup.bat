@echo off
echo ============================================
echo   HEALTHCHAIN - STARTING ALL SERVICES
echo ============================================
echo.

REM Set paths
set KAFKA_HOME=C:\Users\gurve\kafka
set BACKEND_PATH=C:\Users\gurve\IdeaProjects\MedicalLedger\MedicalLedger
set FRONTEND_PATH=C:\Users\gurve\IdeaProjects\MedicalLedger\medical-ledger-frontend

echo [1/6] Starting Zookeeper...
start "Zookeeper" cmd /k "cd /d %KAFKA_HOME% && bin\windows\zookeeper-server-start.bat config\zookeeper.properties"

echo Waiting for Zookeeper to start (20 seconds)...
timeout /t 20 /nobreak >nul

echo [2/6] Checking if Zookeeper is running...
netstat -an | findstr ":2181" >nul
if %errorlevel%==0 (
    echo Zookeeper is running on port 2181
) else (
    echo ERROR: Zookeeper is not running. Check the Zookeeper window.
    pause
    exit /b 1
)

echo [3/6] Starting Kafka Server...
start "Kafka Server" cmd /k "cd /d %KAFKA_HOME% && bin\windows\kafka-server-start.bat config\server.properties"

echo Waiting for Kafka to start (35 seconds)...
timeout /t 35 /nobreak >nul

echo [4/6] Creating Kafka Topics...
start "Kafka Topics" cmd /k "cd /d %KAFKA_HOME% && echo. && echo Creating topics... && bin\windows\kafka-topics.bat --create --if-not-exists --topic consent-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic record-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic audit-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic notification-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && echo. && echo All topics created! && pause"

timeout /t 10 /nobreak >nul

echo [5/6] Starting Spring Boot Backend...
start "Spring Boot Backend" cmd /k "cd /d %BACKEND_PATH% && mvn spring-boot:run"

echo Waiting for backend to start (45 seconds)...
timeout /t 45 /nobreak >nul

echo [6/6] Starting React Frontend...
start "React Frontend" cmd /k "cd /d %FRONTEND_PATH% && npm run dev"

echo.
echo ============================================
echo   ALL SERVICES STARTED!
echo ============================================
echo.
echo   Zookeeper:  localhost:2181
echo   Kafka:      localhost:9092
echo   Backend:    http://localhost:8081
echo   Frontend:   http://localhost:3000
echo   Swagger:    http://localhost:8081/swagger-ui.html
echo.
echo ============================================
pause