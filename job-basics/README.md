# Job basics: run once or run repeatedly

This module teaches what a background job is and why an HTTP request can finish before the work does. It needs Java 21+ and Maven, but no database.

## First, see the complete recurring example

`HeartbeatJob.tick()` has `@Scheduled` on it. Spring calls it once after the initial delay, then repeatedly **30 seconds after each previous run finishes**. `@EnableScheduling` in `JobBasicsApplication` turns this on. The job counts runs in memory; `GET /api/examples/heartbeat` shows its current count and last run time. Start the app, call the GET endpoint, wait about 10 seconds, and call it again. You can change the delays in `application.properties` for a faster demo. `HeartbeatExampleTest` is green before you edit anything.

This job has **no request body**: its schedule is fixed when the app starts. Typical uses include periodic cleanup, polling, and generating a daily summary.

## Your one-time job

Implement `POST /api/jobs`; `GET /api/jobs/{id}` is already complete as the first job API example. A client sends:

```json
{"upperBound":10,"delaySeconds":5}
```

The job waits five seconds, then computes `1 + 2 + ... + 10 = 55` once. The work is deliberately simple so you can focus on *when* it runs and how to report its state.

| Request | Expected response |
|---|---|
| `POST /api/jobs` with valid input | `202 Accepted`, `Location: /api/jobs/{id}`, JSON with `SCHEDULED` status and no result yet |
| `POST /api/jobs` with invalid input | `400 Bad Request` |
| `GET /api/jobs/{id}` for an existing job | `200 OK` with current state |
| `GET /api/jobs/{id}` for an unknown ID | `404 Not Found` |

The states are `SCHEDULED → RUNNING → COMPLETED`, or `SCHEDULED → RUNNING → FAILED` if work throws an exception. A result appears only when the job completes. `202` means the request was accepted; it does **not** promise the work has completed or succeeded.

## Files and tasks

1. Read `example/HeartbeatJob.java` and `config/SchedulerConfig.java`. The first is a **repeating** job. The scheduler bean runs tasks on background threads.
2. In `job/SumJobService.java`, implement `submit`: create an ID and immutable `JobSnapshot`, store it in the `ConcurrentHashMap`, call `scheduler.schedule(() -> run(id), runAt)` **once**, and return immediately. Calculate `runAt` using the injected `Clock`; this makes time predictable in tests.
3. Read the complete `get` methods in `SumJobService` and `JobController`. They return the latest snapshot or a `404` for an unknown ID.
4. Implement `run`: change the state to `RUNNING`, calculate the sum, then store `COMPLETED` with its result. On failure, store `FAILED` with an error message. Replace map values with `computeIfPresent` so other threads see complete snapshots.
5. In `job/JobController.java`, return `202` and `Location` for POST. Validation is already connected with `@Valid` and `CreateSumJobRequest`.

Run `HeartbeatExampleTest` and the missing-job API test first; they should pass. Then run `SumJobServiceTest` and `JobApiTest` as you complete the tasks. Tests capture the scheduled `Runnable` and invoke it directly, so no test waits for a timer. Finally run `mvn test` from this module.

## Start and try it

Run `JobBasicsApplication` in IntelliJ or run `mvn spring-boot:run` here. The app uses port **8083**. Open `requests.http` and submit a job; copy the returned ID into `@jobId`, then poll the GET request before and after the delay.

## What this example leaves out

The map and scheduler live in one app process. Restarting the app loses both pending jobs and job history. A process can also stop while a job is running. This module is for learning the basic flow, not guaranteed delivery. A later module could persist jobs in a database, resume work after restart, add retries, or use a dedicated job system. You can also compare `@Scheduled(initialDelay = ...)` for one fixed startup run with `TaskScheduler.schedule(...)` for a run time chosen by each request. See the [Spring scheduling guide](https://docs.spring.io/spring-framework/reference/integration/scheduling.html).
