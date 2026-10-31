# Product API: validation and endpoint design

This is a small Spring Boot application that students **complete inside the project**. It follows the two user CRUD modules. Here, the lesson is the path from HTTP input to validation, business rules, database changes, and HTTP output.

## Complete example first

Open `example/OrderPreviewRequest.java`, `OrderPreviewController.java`, and `OrderPreviewService.java`. This small endpoint is already finished: `POST /api/examples/order-previews` with `{"quantity":3,"unitPrice":12.50}` returns `200` and `{"total":37.50}`. It calculates a preview and saves nothing, so `200` is appropriate. Invalid input returns `400` with field errors through the complete `invalidBody` method in `ApiExceptionHandler`.

Then follow the **complete product endpoint**: `POST /api/products`. Read `ProductRequest` for validation rules, `ProductController.create` for `@Valid` and the `201 Location` response, `ProductService.create` for the duplicate check and entity mapping, and `ApiExceptionHandler.duplicateSku` for the `409` response. This is the pattern for the other product endpoints. Run `OrderPreviewExampleTest`, `ProductRequestValidationTest`, and `ProductServiceTest` before editing; they should be green.

```text
JSON request -> ProductRequest -> @Valid -> ProductController -> ProductService
                                                        |              |
                              HTTP response <- ProductResponse <- ProductRepository
                                      ^
                         ApiExceptionHandler for errors
```

## Contract

| Request | Success | Other outcomes |
|---|---|---|
| `POST /api/products` | `201 Created`, response DTO, `Location` header | `400` invalid body, `409` duplicate SKU |
| `GET /api/products` | `200 OK`, array (possibly empty) | `400` invalid filter |
| `GET /api/products/{id}` | `200 OK`, response DTO | `404` missing product |
| `PUT /api/products/{id}` | `200 OK`, response DTO | `400` invalid body, `404` missing product, `409` SKU used by another product |
| `DELETE /api/products/{id}` | `204 No Content` | `404` missing product |

The list accepts optional `category`, `minPrice`, and `maxPrice` query parameters. All supplied filters apply together. Category comparison ignores case; price bounds include their endpoints. Empty results are `200 []`, not `404`. A negative bound or `minPrice > maxPrice` is `400`. A nonnumeric price query parameter also gets `400` from Spring's type conversion.

The JSON body for create and update is:

```json
{"name":"Notebook","sku":"BOOK-101","price":12.50,"category":"Stationery"}
```

The response has these fields plus a generated `id`. The body does not accept an `id`; the URL identifies which product to update. SKU uniqueness ignores case. For this exercise, compare the SKU before saving. In a production system, also enforce uniqueness in the database so simultaneous requests cannot create duplicates.

## Start

Use JDK 21+, Maven, and PostgreSQL. Create a separate database:

```sql
CREATE DATABASE product_catalog;
```

The default connection is `postgres` / `postgres` at `localhost:5432`. Change `src/main/resources/application.properties` if needed. Run `ProductValidationApplication` in IntelliJ or `mvn spring-boot:run` from this folder. The API uses port **8082**. Tests use H2 and need no PostgreSQL.

## Student tasks

Work in the following order. The TODOs mark the exact places to edit. Keep `Product`, `ProductRepository`, the complete POST flow, and its tests as references.

1. **Read and filter** — Implement `ProductService.list` and `get`, then their controller methods. Apply all supplied filters together. A missing item throws `ProductNotFoundException`; invalid price bounds throw `InvalidFilterException`. Empty lists are `200 []`.
2. **Update** — Add `@Valid` to the update request, as in the complete POST endpoint. Implement `ProductService.update` and `ProductController.update`. Keep the existing row ID, allow its own SKU, and reject another product's SKU with `DuplicateSkuException`.
3. **Delete** — Implement `ProductService.delete` and `ProductController.delete`. Return `204` with no body when it succeeds; throw `ProductNotFoundException` when the row is missing.
4. **Error responses** — `invalidBody` and `duplicateSku` are complete examples. Implement the remaining temporary handlers in `ApiExceptionHandler`: `400 ProblemDetail` for invalid filters and `404 ProblemDetail` for missing products. Use the exception message as `detail`.

Run `ProductApiTest` after each task, then `mvn test` for the whole module. The complete POST tests and the small example should be green at the start; tests for the other product endpoints are intentionally red. Use `requests.http` for manual checks as each endpoint starts working.

## Why this design

`ProductRequest` is for client input, `Product` represents a database row, and `ProductResponse` controls the public output. That separation prevents an incoming JSON `id` from changing which row is updated. The controller handles HTTP, the service handles product rules, the repository handles storage, and the exception handler turns failures into consistent responses. This is a small teaching project, so the list filters use `findAll()`; a later exercise can move filtering and pagination to the database.

## Optional extensions after the core tests pass

- Add `description` with a length rule and update both DTOs and tests.
- Add pagination and sorting to `GET /api/products`.
- Add a database unique constraint for SKU and handle the resulting race condition.
- Add a `PATCH` endpoint and decide how absent fields differ from explicit `null`.
