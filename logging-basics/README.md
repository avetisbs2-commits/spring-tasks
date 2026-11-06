# Logging basics: make the app explain what it did

This module teaches how a Spring Boot application writes useful, safe logs. The payment API already works, but it is silent: when something goes wrong, nobody can tell what happened. Your job is to add the missing logging using the standard Java tools, SLF4J and Logback. It needs Java 21+ and Maven, but no database.

## Part 1: what logging is

### Why we log

A log is a record of what the application did, written while it runs. When a user says "my payment failed at 14:05", you can't attach a debugger to a server in production. The logs are often the only evidence you have. Good logs answer these questions:

- **What happened?** "Payment approved", "Payment declined", "Provider call failed".
- **To which thing?** The id of the payment, order or job.
- **When, and where in the code?** The timestamp and the logger name do this for you.
- **Which request?** A correlation id links all lines of one request together.
- **Why did it fail?** The exception and its stack trace.

Logs aren't the same as metrics (numbers over time, like "payments per minute") or traces (the path of one request across several services). Together, the three are often called *observability*. This module is about logs.

### SLF4J and Logback

Java has several logging libraries. To avoid tying code to one of them, most projects write code against **SLF4J** (Simple Logging Facade for Java). It's only an API. A separate library does the actual writing. Spring Boot uses **Logback** for that by default, and `spring-boot-starter-web` already includes both.

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentService {
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
}
```

Use one logger per class, named after the class. The name appears in every line, so you can see where it came from. It also lets you change the level of one package or class without touching the others.

### Log levels

Each log line has a level. The configuration decides which levels are printed. If the level is `INFO`, then `INFO`, `WARN` and `ERROR` lines are printed, and `DEBUG` and `TRACE` are skipped.

| Level | Use it for | Example |
|---|---|---|
| `ERROR` | Something failed and needs attention. Usually include the exception. | The payment provider is unreachable. |
| `WARN` | Something unusual happened, but the app handled it. | A payment was declined over the limit; a client sent invalid input. |
| `INFO` | An important business event finished normally. | A payment was approved. |
| `DEBUG` | Details that help a developer, usually off in production. | The input values of a payment. |
| `TRACE` | Very detailed steps, rarely used. | Each loop iteration. |

If everything is `INFO`, the important lines get lost in noise. If everything is `ERROR`, people stop reacting to errors. Choose the level by asking "who needs to see this line, and when?".

### Parameterized messages

Write values with `{}` placeholders and pass them as arguments:

```java
log.info("Payment approved: paymentId={}, amount={}", id, amount);   // good
log.info("Payment approved: paymentId=" + id + ", amount=" + amount); // avoid
```

There are three reasons. The string is only built if the level is enabled, so skipped `DEBUG` lines cost almost nothing. The message template stays the same for every payment, so log tools can group lines by it. And the values stay separate, so structured formats can store them as fields. Writing `name=value` pairs makes lines easy to search: `paymentId=83c9...`.

To log an exception, pass it as the **last** argument, with no `{}` for it. Logback then prints the full stack trace, including the cause:

```java
log.error("Payment provider call failed: paymentId={}", id, exception);
```

### Log once, in the right place

A common mistake is to catch an exception, log it, and throw it again. Then the next layer logs it too, and one failure appears three times in the logs. Either handle the exception and log it, or let it go up to the place that handles it. In this app, `ApiExceptionHandler` turns exceptions into HTTP responses, so that is where the failure is logged.

### MDC and correlation ids

Many requests run at the same time, so their lines are mixed in the log. The **MDC** (Mapped Diagnostic Context) is a small map of values attached to the current thread. Logback can add MDC values to every line written by that thread.

A filter puts a **correlation id** into MDC at the start of a request. Now every line of that request carries the same id, even lines written deep inside a service that knows nothing about HTTP. If the client sends an `X-Correlation-Id` header, we reuse it; otherwise, we create one. We also return it in the response, so a user or another service can report it.

Web servers reuse threads. If you forget to remove the MDC value, the next request on that thread inherits the wrong id. So always remove it in a `finally` block.

### What never goes into a log

Logs are copied to many places: files, log servers, backups, and the screens of everyone who reads them. They're often kept for months and are less protected than the database. Never log:

- passwords, tokens, API keys, session cookies, or `Authorization` headers;
- full card numbers or CVV codes (the PCI DSS card security standard does not allow it);
- personal data that you don't need, such as full email addresses, phone numbers, or passport numbers.

If you need to identify something, log an id or a masked value: `************1111`, `a***@example.com`. Personal data protection law applies to logs too, including Armenia's Law on Protection of Personal Data.

Be careful with values that come from clients. A value with a newline in it can create a fake log line. That is why `CorrelationIdFilter` only accepts short ids made of letters, digits and dashes.

### Structured logs

A plain text line is easy for a person to read:

```text
2026-11-06T14:12:14.041Z  INFO --- [nio-8084-exec-1] [demo-1] c.e.l.payment.PaymentService : Payment approved: paymentId=83c9..., amount=250, currency=AMD
```

Production systems usually send logs to a central tool such as Elasticsearch/Kibana, Grafana Loki, or a cloud log service. These tools work best with **structured logs**: one JSON object per line, where the level, logger, MDC values and exception are separate fields.

```json
{"@timestamp":"2026-11-06T14:12:14.041Z","log":{"level":"INFO","logger":"com.example.loggingbasics.payment.PaymentService"},"message":"Payment approved: paymentId=83c9..., amount=250, currency=AMD","correlationId":"demo-1"}
```

Then you can search for `correlationId:"demo-1"` or `log.level:"ERROR"` across all servers. Spring Boot 3.4 and newer can write JSON in the ECS (Elastic Common Schema), GELF or Logstash format with one property. A common setup is plain text on a developer's computer and JSON in production. Here, a Spring profile named `json` switches between them.

### Configuration in Spring Boot

Most settings go into `application.properties`; no XML is needed:

```properties
logging.level.root=INFO                     # everything
logging.level.org.hibernate.SQL=DEBUG       # one package
```

For full control, you can add a `logback-spring.xml` file, but it's not needed in this module. Where logs go (console or file) is also configuration. In containers, the usual choice is to write to the console and let the platform collect the output.

## Part 2: the application

```text
HTTP request
   -> CorrelationIdFilter     puts correlationId into MDC                     (task 3)
   -> PaymentController       @Valid PaymentRequest                           (complete)
   -> PaymentService          DEBUG received, WARN declined, INFO approved    (task 2)
        uses SensitiveData    masks card and email                            (task 1)
        -> PaymentGateway     fake provider; a card ending with 0000 fails    (complete)
   -> ApiExceptionHandler     WARN for invalid input (complete), ERROR for provider failure (task 4)
application*.properties       level, console pattern, JSON profile            (task 5)
```

`POST /api/payments` accepts:

```json
{"customerEmail":"anna.petrosyan@example.com","cardNumber":"4111111111111111","amount":250.00,"currency":"AMD"}
```

| Case | Response | Expected log line |
|---|---|---|
| Amount up to 1000 | `200`, `status: APPROVED` | `DEBUG` received, `INFO` approved |
| Amount over 1000 | `200`, `status: DECLINED` | `DEBUG` received, `WARN` declined with `reason=LIMIT_EXCEEDED` |
| Card ends with `0000` | `502 Bad Gateway` | `DEBUG` received, one `ERROR` with stack trace in `ApiExceptionHandler` |
| Invalid body | `400 Bad Request` | `WARN` with the field names only |

The card number and email must never appear in full in any line.

## First, see the complete example

Open `example/ShippingQuoteService.java`. It declares a logger and uses `DEBUG`, `INFO` and `WARN` with `{}` placeholders. Then read `invalidBody` in `web/ApiExceptionHandler.java`: it logs the names of invalid fields, but not the values the client sent. Run `ShippingQuoteExampleTest`; it is green before you edit anything.

Also look at the test helper `LogCapture` in `src/test`. It attaches to one class's logger and records every event, so a test can check the level, the message template, the arguments, the MDC and the exception.

## Student tasks

The TODOs mark the exact places to edit. Do them in this order:

1. **Mask sensitive data**: implement both methods in `logsafe/SensitiveData.java`. Right now they return the real value. Test: `SensitiveDataTest`.
2. **Log the payment flow**: in `payment/PaymentService.java`, declare the logger and add the `DEBUG`, `WARN` and `INFO` lines. Use placeholders, include the `paymentId`, and use only masked values. Test: `PaymentServiceLoggingTest`.
3. **Correlation id**: in `web/CorrelationIdFilter.java`, put the id into MDC, add the response header, and remove the MDC value in `finally`. Test: `CorrelationIdTest`.
4. **Log the failure once**: in `gatewayFailure` in `web/ApiExceptionHandler.java`, log at `ERROR` and pass the exception so the stack trace and cause are printed. Don't add a `try`/`catch` in `PaymentService`. Test: `GatewayFailureLoggingTest`.
5. **Configure the output**: in `application.properties`, turn on `DEBUG` for `com.example.loggingbasics` only, and set `logging.pattern.correlation` so each console line shows `[correlationId]`. In `application-json.properties`, turn on ECS structured logs for the console. Test: `LoggingConfigTest`. See the [Spring Boot logging reference](https://docs.spring.io/spring-boot/reference/features/logging.html) for the property names.

Finally, run `mvn test` from this module. All tests should be green.

## Start and try it

Run `LoggingBasicsApplication` in IntelliJ or run `mvn spring-boot:run` here. The app uses port **8084**. Use `requests.http` and watch the console after each request. You should see your correlation id `demo-1` in the lines of the first payment request.

Then start the app with the JSON profile and send the same requests:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=json
```

In IntelliJ, set `Active profiles` to `json` in the run configuration.

## Check yourself

- Send the declined payment. Can you find every line of that request using only its correlation id?
- Search the console for `4111111111111111` and the full email. You should find nothing.
- Count how many times the provider failure appears. It should be once.
- Change `logging.level.com.example.loggingbasics` to `INFO` and restart. Which lines disappear?

## What this example leaves out

Logs stay in the console here. A real system also needs to decide where logs are stored, how long they are kept, who may read them, and how to alert on `ERROR` lines. Across several services, correlation ids are usually handled by tracing (Micrometer Tracing with OpenTelemetry), which adds `traceId` and `spanId` to MDC for you. A later module could add these, or write logs to a file with rotation.
