\# Medical Ledger



Medical Ledger is a medical record management application built with Spring Boot, PostgreSQL, Apache Kafka, and React.



The application provides medical record management with event-based processing using Apache Kafka.



\---



\## Project Structure



```text

MedicalLedger-Updated/

│

├── MedicalLedger/                  # Spring Boot backend

│

├── medical-ledger-frontend/        # React frontend

│

├── startup.bat                     # Starts Kafka and application services

│

├── pom.xml

│

└── README.md

```



\---



\# Prerequisites



Install the following before running the application:



\- Java

\- PostgreSQL

\- Node.js and npm

\- Apache Kafka 3.6.2



The provided `startup.bat` is designed for Windows.



\---



\# 1. Java Setup



Make sure Java is installed.



Check the installed version:



```powershell

java -version

```



The backend contains the Maven Wrapper, so Maven does not need to be installed separately.



The Maven Wrapper is located at:



```text

MedicalLedger/mvnw.cmd

```



\---



\# 2. PostgreSQL Setup



Install PostgreSQL and make sure the PostgreSQL server is running.



Create the database:



```sql

CREATE DATABASE medicalledger;

```



The application expects the following configuration:



```text

Host: localhost

Port: 5432

Database: medicalledger

Username: postgres

```



\## Database Password



The PostgreSQL password should not be committed to GitHub.



Create the following file locally:



```text

MedicalLedger/src/main/resources/application-secret.properties

```



Add:



```properties

spring.datasource.password=YOUR\_POSTGRES\_PASSWORD

```



Replace `YOUR\_POSTGRES\_PASSWORD` with your local PostgreSQL password.



This file is ignored by Git and should remain local.



\---



\# 3. Apache Kafka Setup



Kafka is required for event processing in the application.



The application expects Kafka to run on:



```text

localhost:9092

```



The provided `startup.bat` expects Apache Kafka to be installed at:



```text

C:\\kafka

```



After installing Kafka, verify the installation:



```powershell

Test-Path C:\\kafka\\bin\\windows\\kafka-server-start.bat

```



The result should be:



```text

True

```



Also verify ZooKeeper:



```powershell

Test-Path C:\\kafka\\bin\\windows\\zookeeper-server-start.bat

```



The result should be:



```text

True

```



\---



\# 4. Kafka Topics



The application uses the following Kafka topics:



```text

consent-events

record-events

audit-events

notification-events

```



These topics are automatically created by `startup.bat`.



You do not need to create them manually during normal setup.



To manually check the topics:



```powershell

C:\\kafka\\bin\\windows\\kafka-topics.bat --list --bootstrap-server localhost:9092

```



Expected output:



```text

audit-events

consent-events

notification-events

record-events

```



\---



\# 5. Frontend Setup



The frontend is located inside:



```text

medical-ledger-frontend

```



After cloning the repository, open PowerShell in the project directory and run:



```powershell

cd medical-ledger-frontend

```



Install the frontend dependencies:



```powershell

npm install

```



The frontend can then be started with:



```powershell

npm run dev

```



The frontend runs at:



```text

http://localhost:3000

```



\---



\# 6. Running the Complete Application



After completing the PostgreSQL and Kafka setup, return to the project root:



```powershell

cd ..

```



Run:



```powershell

.\\startup.bat

```



The startup script automatically:



1\. Checks whether Kafka is installed.

2\. Checks whether the backend directory exists.

3\. Checks whether the frontend directory exists.

4\. Starts ZooKeeper.

5\. Starts Kafka.

6\. Creates the required Kafka topics.

7\. Starts the Spring Boot backend.

8\. Starts the React frontend.



Keep the ZooKeeper and Kafka windows running while using the application.



\---



\# 7. Application URLs



After the application starts:



| Service | URL |

|---|---|

| Frontend | http://localhost:3000 |

| Backend | http://localhost:8081 |

| Swagger UI | http://localhost:8081/swagger-ui.html |

| Kafka | localhost:9092 |

| ZooKeeper | localhost:2181 |



\---



\# 8. Verify Kafka Before Uploading Medical Records



Kafka must be running when uploading medical records.



If Kafka is not available, the backend may fail while publishing the record event.



Check Kafka using:



```powershell

Test-NetConnection localhost -Port 9092

```



A working Kafka installation should show:



```text

TcpTestSucceeded : True

```



If it shows:



```text

TcpTestSucceeded : False

```



Kafka is not running or is not accessible on port `9092`.



\---



\# 9. Medical Record Upload



The medical record upload process uses the following general flow:



```text

Patient Medical Record

&#x20;       |

&#x20;       v

Spring Boot Backend

&#x20;       |

&#x20;       v

File Encryption

&#x20;       |

&#x20;       v

Storage / IPFS Service

&#x20;       |

&#x20;       v

Medical Record Database Entry

&#x20;       |

&#x20;       v

Audit Log

&#x20;       |

&#x20;       v

Kafka Event

```



Kafka must be available at:



```text

localhost:9092

```



for the Kafka event publishing portion of the process.



\---



\# 10. Running Services Manually



If `startup.bat` is not used, the services can be started manually.



\## Start ZooKeeper



Open a PowerShell window:



```powershell

cd C:\\kafka

```



Then:



```powershell

.\\bin\\windows\\zookeeper-server-start.bat .\\config\\zookeeper.properties

```



Keep this window running.



\---



\## Start Kafka



Open another PowerShell window:



```powershell

cd C:\\kafka

```



Then:



```powershell

.\\bin\\windows\\kafka-server-start.bat .\\config\\server.properties

```



Keep this window running.



\---



\## Start Backend



Open another PowerShell window:



```powershell

cd <project-path>\\MedicalLedger

```



Run:



```powershell

.\\mvnw.cmd spring-boot:run

```



\---



\## Start Frontend



Open another PowerShell window:



```powershell

cd <project-path>\\medical-ledger-frontend

```



Run:



```powershell

npm run dev

```



\---



\# 11. Troubleshooting



\## Kafka Connection Error



If the backend displays an error similar to:



```text

Connection to node -1 (localhost/127.0.0.1:9092) could not be established

```



Kafka is not available on port `9092`.



Check:



```powershell

Test-NetConnection localhost -Port 9092

```



If:



```text

TcpTestSucceeded : False

```



start ZooKeeper first and then Kafka.



\---



\## Kafka Is Not Found



The startup script expects Kafka at:



```text

C:\\kafka

```



Check:



```powershell

Test-Path C:\\kafka

```



Expected:



```text

True

```



Check the Kafka executable:



```powershell

Test-Path C:\\kafka\\bin\\windows\\kafka-server-start.bat

```



Expected:



```text

True

```



If Kafka is installed in a different location, update the `KAFKA\_HOME` value in `startup.bat`.



\---



\# 12. Maven Setup



The backend contains Maven Wrapper files:



```text

MedicalLedger/mvnw

MedicalLedger/mvnw.cmd

```



Therefore, Maven does not need to be installed globally.



Run the backend with:



```powershell

cd MedicalLedger

.\\mvnw.cmd spring-boot:run

```



\---



\# 13. Frontend Dependencies



After cloning the repository, install the frontend dependencies:



```powershell

cd medical-ledger-frontend

npm install

```



Then start the frontend:



```powershell

npm run dev

```



The frontend is available at:



```text

http://localhost:3000

```



\---



\# 14. PostgreSQL Troubleshooting



If the backend cannot connect to PostgreSQL, check:



\- PostgreSQL is running.

\- Database `medicalledger` exists.

\- PostgreSQL is using port `5432`.

\- Username is correct.

\- Password in `application-secret.properties` is correct.



The local secret file should contain:



```properties

spring.datasource.password=YOUR\_POSTGRES\_PASSWORD

```



\---



\# 15. Security



Never commit passwords, API keys, tokens, or other secrets to GitHub.



The following file must remain local:



```text

MedicalLedger/src/main/resources/application-secret.properties

```



Before committing changes, check:



```powershell

git status

```



Review changes:



```powershell

git diff

```



Make sure no password or other secret is included in the commit.



\---



\# 16. Git Workflow



Check the current changes:



```powershell

git status

```



Review changes:



```powershell

git diff

```



Stage only the intended files.



For example:



```powershell

git add startup.bat README.md MedicalLedger/.gitignore

```



Check the staged files:



```powershell

git status

```



Commit:



```powershell

git commit -m "chore: improve local Kafka startup setup"

```



Push your branch:



```powershell

git push origin <your-branch>

```



\---



\# 17. Kafka Is Not Included in GitHub



Apache Kafka itself is not stored inside this repository.



The repository contains:



\- `startup.bat`

\- Kafka startup commands

\- Kafka topic creation commands

\- Application configuration

\- Setup instructions



Kafka must be installed separately on each developer's computer.



The repository does not contain Kafka installation files or Kafka's local data.



\---



\# 18. First-Time Setup After Cloning



On a new Windows system:



\## Step 1: Clone the repository



```powershell

git clone <repository-url>

```



\## Step 2: Enter the project



```powershell

cd MedicalLedger-Updated

```



\## Step 3: Install Kafka



Install Apache Kafka 3.6.2 and place it at:



```text

C:\\kafka

```



Verify:



```powershell

Test-Path C:\\kafka\\bin\\windows\\kafka-server-start.bat

```



Expected:



```text

True

```



\## Step 4: Configure PostgreSQL



Create:



```sql

CREATE DATABASE medicalledger;

```



Create the local secret file:



```text

MedicalLedger/src/main/resources/application-secret.properties

```



Add:



```properties

spring.datasource.password=YOUR\_POSTGRES\_PASSWORD

```



\## Step 5: Install frontend dependencies



```powershell

cd medical-ledger-frontend

npm install

```



Return to the root:



```powershell

cd ..

```



\## Step 6: Start the application



```powershell

.\\startup.bat

```



The startup sequence is:



```text

ZooKeeper

&#x20;   |

&#x20;   v

Kafka

&#x20;   |

&#x20;   v

Kafka Topics

&#x20;   |

&#x20;   v

Spring Boot Backend

&#x20;   |

&#x20;   v

React Frontend

```



\---



\# 19. Kafka Verification



After running `startup.bat`, check port `9092`:



```powershell

Test-NetConnection localhost -Port 9092

```



Expected:



```text

TcpTestSucceeded : True

```



Then check the Kafka topics:



```powershell

C:\\kafka\\bin\\windows\\kafka-topics.bat --list --bootstrap-server localhost:9092

```



Expected:



```text

audit-events

consent-events

notification-events

record-events

```



If these topics are present, Kafka is ready for the application.



\---



\# 20. Project Path Independence



The `startup.bat` file automatically determines the location of the cloned project.



Therefore, the project does not need to be cloned to a specific user directory.



For example, the project can be located at:



```text

C:\\Users\\User\\projects\\MedicalLedger-Updated

```



or:



```text

D:\\Projects\\MedicalLedger-Updated

```



The script automatically finds:



```text

MedicalLedger

medical-ledger-frontend

```



inside the project directory.



The external Kafka installation is expected at:



```text

C:\\kafka

```



\---



\# 21. Complete Quick Start



For a system where Java, PostgreSQL, Node.js, and Kafka are already installed:



```powershell

git clone <repository-url>



cd MedicalLedger-Updated



cd medical-ledger-frontend

npm install



cd ..



.\\startup.bat

```



Make sure the local PostgreSQL password is configured in:



```text

MedicalLedger/src/main/resources/application-secret.properties

```



Then open:



```text

http://localhost:3000

```



\---



\# 22. Service Architecture



```text

&#x20;                   +----------------------+

&#x20;                   |    React Frontend    |

&#x20;                   |   localhost:3000     |

&#x20;                   +----------+-----------+

&#x20;                              |

&#x20;                              v

&#x20;                   +----------------------+

&#x20;                   |   Spring Boot API    |

&#x20;                   |   localhost:8081     |

&#x20;                   +----------+-----------+

&#x20;                              |

&#x20;                +-------------+-------------+

&#x20;                |                           |

&#x20;                v                           v

&#x20;      +-------------------+       +-------------------+

&#x20;      |    PostgreSQL     |       |       Kafka       |

&#x20;      |   localhost:5432  |       |   localhost:9092  |

&#x20;      +-------------------+       +---------+---------+

&#x20;                                            |

&#x20;                                            v

&#x20;                                  +-------------------+

&#x20;                                  |     ZooKeeper     |

&#x20;                                  |   localhost:2181  |

&#x20;                                  +-------------------+

```



\---



\# 23. Kafka Events



The application uses the following Kafka topics:



| Topic | Purpose |

|---|---|

| `consent-events` | Consent-related events |

| `record-events` | Medical record-related events |

| `audit-events` | Audit-related events |

| `notification-events` | Notification-related events |



\---



\# 24. Final Setup Checklist



Before running the application on a new system:



```text

\[ ] Java installed

\[ ] PostgreSQL installed

\[ ] PostgreSQL server running

\[ ] medicalledger database created

\[ ] application-secret.properties created locally

\[ ] Node.js installed

\[ ] npm dependencies installed

\[ ] Apache Kafka 3.6.2 installed

\[ ] Kafka located at C:\\kafka

\[ ] ZooKeeper starts successfully

\[ ] Kafka starts successfully

\[ ] localhost:9092 is reachable

\[ ] Required Kafka topics exist

\[ ] Backend starts successfully

\[ ] Frontend starts successfully

```



Once the requirements are satisfied, run:



```powershell

.\\startup.bat

```



Then access:



```text

Frontend:

http://localhost:3000



Backend:

http://localhost:8081



Swagger:

http://localhost:8081/swagger-ui.html

```



Kafka:



```text

localhost:9092

```



ZooKeeper:



```text

localhost:2181

```



\---



\# 25. Important



The repository provides the automation required to start the application, but external services still need to be installed on the developer's machine.



In particular:



```text

Apache Kafka

PostgreSQL

Node.js

Java

```



must be available locally.



Once Kafka is installed at:



```text

C:\\kafka

```



and PostgreSQL is configured, a developer can clone the repository, create their local database credentials, run `npm install`, and use:



```powershell

.\\startup.bat

```



to start the application services.

