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
start "Zookeeper" cmd /k "cd %KAFKA_HOME% && bin\windows\zookeeper-server-start.bat config\zookeeper.properties"

echo Waiting for Zookeeper to start (15 seconds)...
timeout /t 15 /nobreak >nul

echo [2/6] Cleaning up stale Kafka registration...
cd %KAFKA_HOME%
echo delete /brokers/ids/0 | bin\windows\zookeeper-shell.bat localhost:2181 >nul 2>&1
echo Cleanup complete!

echo [3/6] Starting Kafka Server...
start "Kafka Server" cmd /k "cd %KAFKA_HOME% && bin\windows\kafka-server-start.bat config\server.properties"

echo Waiting for Kafka to start (30 seconds)...
timeout /t 30 /nobreak >nul

echo [4/6] Creating Kafka Topics...
start "Kafka Topics" cmd /k "cd %KAFKA_HOME% && echo. && echo Creating topics... && bin\windows\kafka-topics.bat --create --if-not-exists --topic consent-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic record-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic audit-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && bin\windows\kafka-topics.bat --create --if-not-exists --topic notification-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1 && echo. && echo All topics created! && pause"

timeout /t 5 /nobreak >nul

echo [5/6] Starting Spring Boot Backend...
start "Spring Boot Backend" cmd /k "cd %BACKEND_PATH% && mvn spring-boot:run"

echo Waiting for backend to start (45 seconds)...
timeout /t 45 /nobreak >nul

echo [6/6] Starting React Frontend...
start "React Frontend" cmd /k "cd %FRONTEND_PATH% && npm run dev"

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