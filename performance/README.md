Portal 2.0 - Performance
========================

Step 1: Enable performance monitoring
/portal-server/config/performanceMonitoring.properties

Step 2: Add spring beans you want to monitor

```
beansToMonitor=apiBundleService*,permissionManager*,blogService*
```

Step 3: Restart/Start tomcat
```
gradlew war cargoRunLocal
```

Step 4: view the statistics
<hostname>:/war/jamonadmin.jsp