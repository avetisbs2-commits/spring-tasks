# User CRUD With Spring MVC And Thymeleaf - Step By Step Guide

## The Idea

You build a small **web site** for users. You open it in a browser, see a table of users, and use buttons to add, edit and delete them. The data is saved in a **PostgreSQL** database.

```text
Browser ──>  UserController  ──>  UserRepository ──Hibernate──>  PostgreSQL
   ^         (you write this)     (already done)                 (table "users")
   │                │
   └── HTML page ───┘
       (Thymeleaf template, already done)
```

How one click works:

1. The browser sends a request, for example `GET /users`.
2. Your **controller method** runs. It gets data from the repository and puts it in the **Model**.
3. The method returns the **name of a page**, for example `"users/list"`.
4. **Thymeleaf** fills that HTML page with the data from the Model, and the browser shows it.

## What Is Different From The REST Module

| `user-crud-rest` | `user-crud-mvc` (this module) |
|---|---|
| Returns **JSON** data | Returns **HTML pages** |
| `@RestController` | `@Controller` |
| Answers with `ResponseEntity` (200, 404 ...) | Returns a **page name** or `redirect:/...` |
| Tested with Postman | Tested in the **browser** |

## What Is Already Done

Do not change these:

| File | What it does |
|---|---|
| `entity/User.java` | One user: `id`, `firstName`, `lastName`, `email`, `age`, with validation rules. |
| `repository/UserRepository.java` | Saves and loads users. You write **no SQL**. |
| `templates/users/list.html` | The page with the table of users. |
| `templates/users/form.html` | The form for a new user **and** for editing. |
| `application.properties` | The database address, user and password. |
| `UserControllerTest.java` | Tests that check your work. |

## Your Task

Open `controller/UserController.java`. It has **6 methods**. Each one has a `TODO` and a `TEMPORARY` line that shows a "Not implemented yet" page.

For each method: write the code from the `TODO`, then delete the `TEMPORARY` line.

| # | Request | Page / button |
|---|---|---|
| 1 | `GET /users` | the list of users |
| 2 | `GET /users/new` | an empty form |
| 3 | `POST /users` | the **Save** button on the empty form |
| 4 | `GET /users/{id}/edit` | the form filled with one user |
| 5 | `POST /users/{id}` | the **Save** button on the edit form |
| 6 | `POST /users/{id}/delete` | the **Delete** button |

## Things You Need

**Return values**

| You return | Meaning |
|---|---|
| `"users/list"` | show the page `templates/users/list.html` |
| `"users/form"` | show the page `templates/users/form.html` |
| `"redirect:/users"` | send the browser to `/users`, so the list is loaded again |

**The Model** is a box of data for the page. Name it exactly as the page expects:

```java
model.addAttribute("users", ...);   // list.html uses "users"
model.addAttribute("user", ...);    // form.html uses "user"
```

**Repository methods**

| Method | What it does |
|---|---|
| `userRepository.save(user)` | Saves a user (new or changed). |
| `userRepository.findAll()` | Returns the list of all users. |
| `userRepository.findById(id)` | Returns an `Optional<User>` (maybe empty). |
| `userRepository.existsById(id)` | `true` if the user exists. |
| `userRepository.deleteById(id)` | Deletes the user. |

**`BindingResult`** tells if the form data is wrong (for example an empty name). `bindingResult.hasErrors()` is `true` when something is wrong. Then you show the form again, and Thymeleaf prints the error messages.

---

## Step 1 - Create The Database

Make sure PostgreSQL is running on your computer. Then create a database called `user_crud` (if you already did it for `user-crud-rest`, use the same one):

```sql
CREATE DATABASE user_crud;
```

The application expects this by default:

```text
database: user_crud
username: postgres
password: postgres
port:     5432
```

If your username or password is different, change them in `src/main/resources/application.properties`.

## Step 2 - Run The Application

Run `UserMvcApplication` in IntelliJ.

Or from this folder in the terminal:

```bash
mvn spring-boot:run
```

Open the browser: **http://localhost:8081**

(This module uses port `8081`, so it can run together with `user-crud-rest` on `8080`.)

## Step 3 - See The Problem

You see the page **"Not implemented yet"**. Good, that is the starting point.

## Step 4 - Write The 6 Methods

Do them in this order. After each one, check it in the browser. (With `spring.thymeleaf.cache=false` the pages reload, but after you change **Java** code, restart the application.)

### Task 1 - `listUsers`

```java
model.addAttribute("users", _____);   // all users from the repository
return _____;                          // the list page
```

Check: open `/users`. You see an empty table and "There are no users yet."

### Task 2 - `showCreateForm`

```java
model.addAttribute("user", _____);    // a new empty User
return _____;                          // the form page
```

Check: click **Add user**. You see an empty form.

### Task 3 - `createUser`

```java
if (bindingResult.hasErrors()) {
    return _____;                      // show the form again
}
userRepository.save(user);
return _____;                          // go back to the list
```

Check: fill the form and click **Save**. The new user is in the table. Now try an empty first name or a wrong email: you must stay on the form and see a red message.

### Task 4 - `showEditForm`

```java
Optional<User> found = userRepository.findById(id);
if (found.isEmpty()) {
    return _____;                      // go back to the list
}
model.addAttribute("user", _____);     // the user from "found"
return _____;                          // the form page
```

Check: click **Edit**. The form is filled with the user.

### Task 5 - `updateUser`

```java
user.setId(_____);                     // the id from the URL
if (bindingResult.hasErrors()) {
    return _____;                      // show the form again
}
if (!userRepository.existsById(id)) {
    return _____;                      // go back to the list
}
userRepository.save(user);
return _____;                          // go back to the list
```

Why `user.setId(id)` first? The form does not send the id. Without it, `save` would create a **new** user instead of changing this one, and the "Edit user" form would turn into "Add user" after an error.

Check: change a name and click **Save**. The table shows the change and there is no extra row.

### Task 6 - `deleteUser`

```java
userRepository.deleteById(_____);
return _____;                          // go back to the list
```

Check: click **Delete**. The user disappears.

---

## Step 5 - Run The Tests

When all 6 methods are done, run the tests:

```bash
mvn test
```

Or run `UserControllerTest` in IntelliJ.

All 10 tests must be green. The tests use a small in-memory database, so they work even without PostgreSQL.

| If a test fails with | Check |
|---|---|
| view name `not-implemented` | You did not delete the `TEMPORARY` line, or did not write the method. |
| `Model attribute 'users' does not exist` | The name in `addAttribute` must be exactly `"users"` (or `"user"`). |
| status `200` instead of a redirect (`302`) | You returned a page name. Use `"redirect:/users"`. |
| the update test creates a second user | You forgot `user.setId(id)`. |

## Check The Database (optional)

In `psql` (or any database tool):

```sql
SELECT * FROM users;
```

Look at the console of the running application too. It prints the SQL that Hibernate runs (`insert into users ...`).

## Rules

Use Spring Boot, Spring MVC, Thymeleaf, Spring Data JPA (Hibernate) and PostgreSQL only. Do not write SQL by hand and do not change `User`, `UserRepository` or the HTML pages.
