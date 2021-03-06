Portal 2.0
==========

Dev Portal version 2.0

Step 1: Install Java JDK 1.8 and above *

    -Install the security policy jars into ${java.home}/jre/lib/security/. 
    Those can be found on the shared server 
    \\WABOTH9CDFILE05.itservices.sbc.com\DPGM_ONBOARD\Third Party App Installation Files\Java\jce_policy-8.zip

Step 2: Install oracle 11g and create an user with dev_core/dev_core

```
C:\>sqlplus / as sysdba;

After fresh installation:

SQL> create tablespace DATA01
logging
datafile 'C:/ORACLEXE/app/oracle/ORADATA/XE/DATA01.DBF'
size 32m
autoextend on
next 32m maxsize 2048m
extent management local;
-----

SQL> create user dev_core identified by dev_core;
SQL> GRANT ALL PRIVILEGES TO dev_core;

SQL> create user appdev_core identified by appdev_core;
SQL> GRANT ALL PRIVILEGES TO appdev_core;

SQL> grant select on sys.dba_pending_transactions to dev_core;
SQL> grant select on sys.pending_trans$ to dev_core;
SQL> grant select on sys.dba_2pc_pending to dev_core;
SQL> grant execute on sys.dbms_system to dev_core;

SQL> grant select on sys.dba_pending_transactions to appdev_core;
SQL> grant select on sys.pending_trans$ to appdev_core;
SQL> grant select on sys.dba_2pc_pending to appdev_core;
SQL> grant execute on sys.dbms_system to appdev_core;
```

Step 3: Install STS or Eclipse for IDE

Step 4: Pull master from github repository

Prefer: EGit from STS or Eclipse, below is command line option
Command Prompt or Git Bash
follow github clone instructions

(Send email to ss380m@att.com for permission for accessing the repository)

Step 5: Set Proxy
a) Set Windows environment variable for proxy
```
Key: HTTP_PROXY
Value: http://one.proxy.att.com:8080
```

b) Add Proxy to Git 
```
git config --global http.proxy http://one.proxy.att.com:8080
git config --global https.proxy http://one.proxy.att.com:8080
```

Step 6: Validate the project setup with
``` gradle
Command Prompt
cmd> gradlew cleanEclipse eclipse flywayRepair flywayMigrate -i npmInstall build
 
Environment is set to local
config [flyway:[mysql:[driver:com.mysql.jdbc.Driver, locations:[db.migration.mysql], password:dev_core, schemas:[dev_core], url:jdbc:mysql://localhost:3306, user:dev_core, version:1.0], oracle:[driver:oracle.jdbc.driver.OracleDriver, locations:[db/migration/oracle], password:dev_core, schemas:[dev_core], url:jdbc:oracle:thin:@localhost:1521:XE, user:dev_core, version:1.0]]]
:compileJava
:compileGroovy UP-TO-DATE
:processResources UP-TO-DATE
:classes
:war
:assemble
:compileTestJava
:compileTestGroovy UP-TO-DATE
:processTestResources UP-TO-DATE
:testClasses
:compileIntegrationJava
:compileIntegrationGroovy UP-TO-DATE
:processIntegrationResources UP-TO-DATE
:integrationClasses
:jar
:integrationTest
:test
:check
:build
 
BUILD SUCCESSFUL
 
Total time: 15.232 secs
```

Step 7: Start tomcat container using
``` gradle
Command Prompt
cmd> gradlew war cargoRunLocal --info
 
Environment is set to local
config [flyway:[mysql:[driver:com.mysql.jdbc.Driver, locations:[db.migration.mysql], password:dev_core, schemas:[dev_core], url:jdbc:mysql://localhost:3306, user:dev_core, version:1.0], oracle:[driver:oracle.jdbc.driver.OracleDriver, locations:[db/migration/oracle], password:dev_core, schemas:[dev_core], url:jdbc:oracle:thin:@localhost:1521:XE, user:dev_core, version:1.0]]]
flyway.url = [dev_core]
:tomcatStop
:compileJava UP-TO-DATE
:compileGroovy UP-TO-DATE
:processResources UP-TO-DATE
:classes UP-TO-DATE
:war UP-TO-DATE
:tomcatRun
No properties path set - looking for transactions.properties in classpath...
transactions.properties not found - looking for jta.properties in classpath...
Failed to open transactions properties file - using default values
0    [localhost-startStop-1] DEBUG org.jboss.logging  - Logging Provider: org.jboss.logging.Log4jLoggerProvider
Started Tomcat Server
The Server is running at http://localhost:9080/developer
```
Step 8: To stop tomcat server use CTRL+C

Step 9: Access Portal 2.0 and start developing http://localhost:9080/developer

* Many of the third party binaries (java, mysql) are located on this server :  \\WABOTH9CDFILE05.itservices.sbc.com\DPGM_ONBOARD\Third Party App Installation Files


Client
==========

1. Make dynamic change while the server is still running

``` gradle
Creates app.js with latest modifications

cmd> gradlew grunt_debug

Watch for changes and automatically build app.js (note: there is an issue watch doesn't start watching until HTML template file is touched)
cmd> gradlew grunt_watch

```

2. Running Karma unit test
```gradle
continously runs karma in the background

cmd> gradlew grunt_test
```

Code check-in
=============

1. Fork the project
2. Make required modifications
3. Create a pull request 

Java Remote Debug
=================
1. Eclipse remote_debug
2. Port to connect is 8787 
