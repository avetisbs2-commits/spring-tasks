# Student checklist

Read [README.md](README.md) for the flow and local GitHub setup. Complete the TODO comments in this order:

- [ ] In `SecurityConfig`, enable `oauth2Login` and redirect successful logins to `/profile`. Keep the existing authorization rules and CSRF protection.
- [ ] In `GitHubProfileService`, map GitHub's numeric `id`, required `login`, and optional `name` into `GitHubProfile`. Fall back to `login` when `name` is missing or blank.
- [ ] In `ProfileController`, put the mapped profile in the model as `"profile"` and render `profile.html`.
- [ ] Run `mvn test`. All six tests should pass; the tests do not need GitHub credentials.
- [ ] Register a GitHub OAuth app, set `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET`, then complete a real browser login at `http://127.0.0.1:8086/`.
- [ ] Sign out and verify that `/profile` requires login again.

The callback URL is `http://127.0.0.1:8086/login/oauth2/code/github`. Spring Security handles it; do not create a controller for it.
