package com.example.usermvc.controller;

import com.example.usermvc.entity.User;
import com.example.usermvc.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

/**
 * The web pages for users:   /users
 *
 * Every method returns a String:
 *   "users/list"       = show the HTML page templates/users/list.html
 *   "users/form"       = show the HTML page templates/users/form.html
 *   "redirect:/users"  = send the browser to /users (so the list is loaded again)
 *
 * The URLs and annotations are already written. The method bodies are the
 * student task. Until you write them, every method shows a
 * "Not implemented yet" page.
 */
@Controller
@RequestMapping("/users")
public class UserController {

    // Spring gives us the repository automatically (constructor injection).
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------------------------------------------------------------
    // TASK 1:  GET /users          -> show the list of all users
    // ------------------------------------------------------------------
    @GetMapping
    public String listUsers(Model model) {
        // TODO:
        // 1. Get all users: userRepository.findAll()
        // 2. Put them in the model with the name "users":
        //        model.addAttribute("users", ...)
        // 3. Return the page name "users/list".
        model.addAttribute("users", userRepository.findAll());

        return "users/list";
    }

    // ------------------------------------------------------------------
    // TASK 2:  GET /users/new      -> show an empty form
    // ------------------------------------------------------------------
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        // TODO:
        // 1. Put a new empty User in the model with the name "user":
        //        model.addAttribute("user", new User())
        // 2. Return the page name "users/form".
        model.addAttribute("user", new User());
        return "users/form";
    }

    // ------------------------------------------------------------------
    // TASK 3:  POST /users         -> save the new user from the form
    // ------------------------------------------------------------------
    @PostMapping
    public String createUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
        // `user` is filled from the form fields. `bindingResult` tells if the data is wrong.
        //
        // TODO:
        // 1. If bindingResult.hasErrors(), return "users/form"
        //    (the form is shown again, with the error messages).
        // 2. Save the user: userRepository.save(user)
        // 3. Return "redirect:/users".
        if (bindingResult.hasErrors()) {
            return "users/form";
        }
        userRepository.save(user);
        // 3. Redirect back to the user list
        return "redirect:/users";

    }

    // ------------------------------------------------------------------
    // TASK 4:  GET /users/{id}/edit -> show the form filled with one user
    // ------------------------------------------------------------------
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        // TODO:
        // 1. Find the user: Optional<User> found = userRepository.findById(id)
        // 2. If it does not exist, return "redirect:/users".
        // 3. Put the user in the model with the name "user".
        // 4. Return "users/form".

        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            return "redirect:/users";
        }

        model.addAttribute("user", found.get());
        return "users/form";
    }

    // ------------------------------------------------------------------
    // TASK 5:  POST /users/{id}    -> save the changes from the form
    // ------------------------------------------------------------------
    @PostMapping("/{id}")
    public String updateUser(@PathVariable Long id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult) {
        // TODO:
        // 1. Put the id from the URL into the user: user.setId(id)
        //    (the form does not send the id).
        // 2. If bindingResult.hasErrors(), return "users/form".
        // 3. If the user does not exist (userRepository.existsById(id)),
        //    return "redirect:/users".
        // 4. Save the user: userRepository.save(user)
        // 5. Return "redirect:/users".

        user.setId(id);
        if (bindingResult.hasErrors()) {
            return "users/form";
        }
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        userRepository.save(user);
        return "redirect:/users";
    }

    // ------------------------------------------------------------------
    // TASK 6:  POST /users/{id}/delete -> delete a user
    // ------------------------------------------------------------------
    // (An HTML form can only send GET or POST, so delete is a POST here.)
    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        // TODO:
        // 1. Delete the user: userRepository.deleteById(id)
        // 2. Return "redirect:/users".
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
