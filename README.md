Portal 2.0
==========

Dev Portal version 2.0

Step 1: Install Java JDK 1.8 and above *

    -Install the security policy jars into ${java.home}/jre/lib/security/. 
    Those can be found on the shared server 
    \\WABOTH9CDFILE05.itservices.sbc.com\DPGM_ONBOARD\Third Party App Installation Files\Java\jce_policy-8.zip

Step 2: Install Mysql 5.6 and above *

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
cmd> gradlew flywayMigrate -i npmInstall build
 
Environment is set to local
config [flyway:[password:dev_core, driver:com.mysql.jdbc.Driver, schemas:[dev_core], user:dev_core, version:1.0, url:jdbc:mysql://localhost:3306/]]
flyway.url = jdbc:mysql://localhost:3306/
flyway.url = [dev_core]
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

Step 7: Start embedded tomcat container using
``` gradle
Command Prompt
cmd> gradlew tomcatStop war tomcatRun
 
Environment is set to local
config [flyway:[password:dev_core, driver:com.mysql.jdbc.Driver, schemas:[dev_core], user:dev_core, version:1.0, url:jdbc:mysql://localhost:3306/]]
flyway.url = jdbc:mysql://localhost:3306/
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

Step 8: Access Portal 2.0 and start developing http://localhost:9080/developer/views/adminConsole/admin.html

* Many of the third party binaries (java, mysql) are located on this server :  \\WABOTH9CDFILE05.itservices.sbc.com\DPGM_ONBOARD\Third Party App Installation Files
