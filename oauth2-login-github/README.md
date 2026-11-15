# OAuth2 login with GitHub

This module lets students complete a real browser login flow in a local Spring Boot app. It follows `jwt-basics` but teaches a different job: this app is an **OAuth2 client**. GitHub asks the person to sign in and approve access; Spring exchanges the returned authorization code and keeps the login in an HTTP session. Students do not collect a GitHub password or parse a JWT themselves.

```text
Browser -> / -> Continue with GitHub
        -> /oauth2/authorization/github
        -> GitHub sign-in and approval
        -> /login/oauth2/code/github (callback handled by Spring Security)
        -> /profile (student page, authenticated session)
```

## Before editing

Run `OAuthLoginApplication` or `mvn spring-boot:run` from this folder. It uses port **8086** and needs no database. Open `http://127.0.0.1:8086/`: the home page and its login link are provided. The link cannot complete login until you finish the security TODO and supply a GitHub OAuth app's client credentials.

Read these provided files:

- `web/HomeController.java` and `templates/home.html` show a complete public page.
- `application.properties` registers GitHub as the OAuth2 provider and asks only for `read:user` profile access.
- `profile/GitHubProfile.java` is the small result this app needs. It deliberately contains no access token.
- `templates/profile.html` shows the mapped result and contains a POST logout form. CSRF protection stays enabled.

The URL `/oauth2/authorization/github` starts the redirect. Spring Security handles the callback `/login/oauth2/code/github` and the code exchange. You should not implement a callback controller or call GitHub's token endpoint by hand.

## Student tasks

Use [TODO.md](TODO.md) as a short checklist.

1. In `config/SecurityConfig.java`, enable `oauth2Login` and send a successful login to `/profile`. The existing rules leave `/` and static files public and require authentication elsewhere. Keep CSRF enabled; this is a browser app with a session.
2. In `profile/GitHubProfileService.java`, map the authenticated GitHub user's attributes: numeric `id`, required `login`, and optional `name`. If `name` is missing or blank, show `login`. Use the stable numeric ID as the account identifier; login names can change. Never expose the provider access token.
3. In `profile/ProfileController.java`, add that mapped profile to the model under `"profile"` and render `profile.html`.

Run `GitHubProfileServiceTest` and `OAuthLoginWebTest`. The tests simulate provider data and the authorization redirect, so they need no real credentials or external network. At the start, the home page test passes and the student TODO tests are red. After the tasks, `mvn test` should be green.

## Set up a GitHub OAuth app for a real local login

1. In GitHub, go to **Settings → Developer settings → OAuth apps → New OAuth App**. Use an app name such as `Spring OAuth Lesson`.
2. Set **Homepage URL** to `http://127.0.0.1:8086/` and **Authorization callback URL** to `http://127.0.0.1:8086/login/oauth2/code/github`. Keep the callback exact; the browser and the app registration must use the same host and port.
3. Copy the Client ID. Generate a Client Secret in GitHub, then supply both to the local process as environment variables:

   ```bash
   export GITHUB_CLIENT_ID='your-client-id'
   export GITHUB_CLIENT_SECRET='your-client-secret'
   mvn spring-boot:run
   ```

   In IntelliJ, put the same two names in the run configuration's environment variables. Do not put the secret into `application.properties` or commit it.
4. Visit `http://127.0.0.1:8086/`, click **Continue with GitHub**, approve the request, and check `/profile`. Use **Sign out**, then revisit `/profile` to see that login is required again.

If the Client ID and Secret are absent, the app uses visible placeholders so it can start and tests can run; GitHub will reject a real login attempt. A real end-to-end browser test requires a GitHub account and the OAuth app registration above.

## What to notice

- This module uses a server-side **session** for browser login. `jwt-basics` uses a bearer token on each API request.
- The GitHub access token is for calling GitHub APIs. It is not automatically an access token for an API you own.
- An unauthenticated request to `/profile` starts login. A signed-in request receives the profile page.
- Spring Security's authorization request includes a `state` value to tie the callback to the browser session. Do not remove that protection.
- GitHub may omit `name`, so the page needs a fallback. We do not request email or repository access for this lesson.

For more detail, see [Spring Security OAuth2 login](https://docs.spring.io/spring-security/reference/servlet/oauth2/login/core.html), [Spring Security's OAuth2 test support](https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html), and [GitHub's OAuth app setup](https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/creating-an-oauth-app).
