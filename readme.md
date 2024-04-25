# Simple Mock - Service Async Local

## To build

```shell
chmod +x gradlew
./gradlew clean build
```

## Properties

| Key                                                | Description                                                                               | Default       |
|----------------------------------------------------|-------------------------------------------------------------------------------------------|---------------|
| `service.local.async.parallel-tasks`               | Number of possible parallel executed tasks. 0 - means take number of available processors | 0             |
| `service.local.async.executor.rate`                | Task execution rate                                                                       | 100           |
| `service.local.async.clean-up.cron`                | Clean Up service start up cron                                                            | 0 0 */6 * * * |
| `service.local.async.clean-up.max-removed-at-once` | Max removed at once                                                                       | 32            |
| `service.local.async.clean-up.task-ttl-type`       | Type clean up of TTL. Values see ChronoUnit                                               | DAYS          |
| `service.local.async.clean-up.task-ttl`            | Value of clean up TTL.                                                                    | 3             |

