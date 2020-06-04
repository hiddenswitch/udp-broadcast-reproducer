```
./gradlew test
```

results in

```
➜  udp-broadcast-reproducer git:(master) gradle test

> Task :test

AppTest > testUdpListensNullInterface(Vertx, VertxTestContext) FAILED
    java.util.concurrent.TimeoutException at VertxExtension.java:230

AppTest > [1] 0 FAILED
    java.util.concurrent.TimeoutException at VertxExtension.java:230

27 tests completed, 2 failed

> Task :test FAILED

FAILURE: Build failed with an exception.
```
