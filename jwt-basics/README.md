# JWT basics: login and protected endpoints

This module teaches a complete access-token flow inside a small Spring Boot app. It uses Spring Security's JWT support, not a custom token parser. It has no database: two demo users are kept in memory so the lesson stays focused on authentication.

## The flow

```text
POST /api/auth/login with username + password
  -> AuthenticationManager checks the encoded password
  -> TokenService signs a short-lived JWT
  -> client sends Authorization: Bearer <token>
  -> Spring Security verifies signature, issuer, and expiry
  -> GET /api/me reads the verified subject
```

A JWT has a header, payload (claims), and signature. The payload is **readable**, so it must never contain a password or other secret. A signature lets the server detect changes to the token; expiry limits how long a stolen token works. This app signs with HS256 using a 256-bit or longer secret key. It also checks the token's issuer. The token lifetime is 15 minutes.

| Request | Expected result |
|---|---|
| `GET /api/public` | `200`, no token needed |
| `POST /api/auth/login` with valid credentials | `200`, `tokenType`, `accessToken`, `expiresIn` |
| `POST /api/auth/login` with wrong credentials | `401`, no token |
| `GET /api/me` with a valid token | `200`, verified username |
| `GET /api/me` with no, expired, altered, or wrong-issuer token | `401` |
| `GET /api/admin` with a USER token | `403` |
| `GET /api/admin` with an ADMIN token | `200` |

`401` means the caller has not proved their identity with a valid token. `403` means the caller is authenticated but lacks permission. A `200` login response is appropriate because login creates no persistent resource.

## What is already complete

Read `config/SecurityConfig.java`: it permits only public info and login anonymously, requires a token elsewhere, and restricts `/api/admin` to `ADMIN`. The app is stateless: no server session is created. Passwords are encoded before they are stored in the demo user service. Spring Security's resource server verifies bearer tokens before protected controller methods run.

Read `config/JwtConfig.java`: the encoder and decoder share one key. The decoder accepts HS256 only and checks the issuer and standard time claims. `resource/ResourceController.java` is a complete endpoint example; it reads the *verified* subject from `@AuthenticationPrincipal Jwt`. `auth/AuthExceptionHandler.java` is a complete error example for bad credentials.

Try `GET /api/public` and `GET /api/me` without a token before editing. Run `SecurityBoundaryTest`; its tests should be green before student work begins.

## Student tasks

The TODOs mark the exact places to edit:

1. **Issue a token** in `auth/TokenService.java`. Build `JwtClaimsSet` with `iss`, `sub`, `iat`, `exp`, and a `roles` claim, then sign it with `JwtEncoder` and a `JwsHeader` using HS256. Use the injected `Clock` and configured lifetime. Run `TokenServiceTest`.
2. **Authenticate the credentials** in `auth/AuthService.java`. Call the provided `AuthenticationManager` with `UsernamePasswordAuthenticationToken`. Only after authentication succeeds, take the username and roles from the returned `Authentication` and call `TokenService.issue`. Never take roles from the request body. The role strings in the token are `USER` or `ADMIN`; the configured converter turns them into Spring authorities like `ROLE_USER`.
3. **Return the login response** in `auth/AuthController.java`. Replace the temporary `501` with `200` and the `LoginResponse`. Run `LoginFlowTest`.

Finally run `mvn test` from this module. Tests for TODOs start red; protection of the other endpoints is already working. `requests.http` lets you manually try the flow.

## Start and try it

Run `JwtBasicsApplication` in IntelliJ or `mvn spring-boot:run` here. The app uses port **8085**. Demo accounts are `student` / `learn-jwt` and `admin` / `learn-admin`. These are only for local teaching; do not deploy them.

If `JWT_SECRET_BASE64` is absent, the app generates a fresh key on startup. Restarting then invalidates earlier tokens. For a stable local key, set a Base64 string that decodes to at least 32 random bytes, for example:

```bash
export JWT_SECRET_BASE64="$(openssl rand -base64 32)"
mvn spring-boot:run
```

Use HTTPS when sending real credentials or bearer tokens. Do not log tokens or store them in source control. A production system also needs durable users, secret management, key rotation, revocation strategy, and usually a dedicated identity provider. This module deliberately stays small enough to understand the flow.

## References

- [Spring Security JWT resource server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Spring Security password storage](https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html)
