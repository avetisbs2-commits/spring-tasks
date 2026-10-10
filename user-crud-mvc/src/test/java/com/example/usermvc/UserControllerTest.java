package com.example.usermvc;

import com.example.usermvc.entity.User;
import com.example.usermvc.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Checks the student's UserController. Run this class (or `mvn test`).
 * Green = the task is done. Each test is one page / one button.
 *
 * PROVIDED CODE - students do not need to change it.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    private User saveJohn() {
        return userRepository.save(new User("John", "Smith", "john@test.com", 25));
    }

    // ---------- Task 1: list ----------

    @Test
    void listUsers_showsTheListPageWithAllUsers() throws Exception {
        saveJohn();
        userRepository.save(new User("Anna", "Brown", "anna@test.com", 31));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attribute("users", hasSize(2)))
                .andExpect(content().string(containsString("john@test.com")))
                .andExpect(content().string(containsString("anna@test.com")));
    }

    // ---------- Task 2: empty form ----------

    @Test
    void showCreateForm_showsAnEmptyForm() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeExists("user"))
                .andExpect(content().string(containsString("Add user")));
    }

    // ---------- Task 3: create ----------

    @Test
    void createUser_savesAndRedirectsToTheList() throws Exception {
        mockMvc.perform(post("/users")
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("email", "john@test.com")
                        .param("age", "25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        assertEquals(1, userRepository.count());
    }

    @Test
    void createUser_withBadData_showsTheFormAgainAndSavesNothing() throws Exception {
        mockMvc.perform(post("/users")
                        .param("firstName", "")
                        .param("lastName", "Smith")
                        .param("email", "not-an-email")
                        .param("age", "25"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName", "email"));

        assertEquals(0, userRepository.count());
    }

    // ---------- Task 4: edit form ----------

    @Test
    void showEditForm_showsTheFormFilledWithTheUser() throws Exception {
        User john = saveJohn();

        mockMvc.perform(get("/users/" + john.getId() + "/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attribute("user", hasProperty("firstName", is("John"))))
                .andExpect(content().string(containsString("Edit user")));
    }

    @Test
    void showEditForm_whenUserIsMissing_redirectsToTheList() throws Exception {
        mockMvc.perform(get("/users/999999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));
    }

    // ---------- Task 5: update ----------

    @Test
    void updateUser_changesTheDataAndRedirects() throws Exception {
        User john = saveJohn();

        mockMvc.perform(post("/users/" + john.getId())
                        .param("firstName", "Johnny")
                        .param("lastName", "Smith")
                        .param("email", "john@test.com")
                        .param("age", "26"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        assertEquals(1, userRepository.count());
        assertEquals("Johnny", userRepository.findById(john.getId()).get().getFirstName());
        assertEquals(26, userRepository.findById(john.getId()).get().getAge());
    }

    @Test
    void updateUser_withBadData_showsTheEditFormAgainAndChangesNothing() throws Exception {
        User john = saveJohn();

        mockMvc.perform(post("/users/" + john.getId())
                        .param("firstName", "")
                        .param("lastName", "Smith")
                        .param("email", "john@test.com")
                        .param("age", "26"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeHasFieldErrors("user", "firstName"))
                // the id must be kept, so the page is still the "Edit user" form
                .andExpect(model().attribute("user", hasProperty("id", is(john.getId()))));

        assertEquals("John", userRepository.findById(john.getId()).get().getFirstName());
    }

    @Test
    void updateUser_whenUserIsMissing_redirectsAndCreatesNothing() throws Exception {
        mockMvc.perform(post("/users/999999")
                        .param("firstName", "John")
                        .param("lastName", "Smith")
                        .param("email", "john@test.com")
                        .param("age", "25"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        assertEquals(0, userRepository.count());
    }

    // ---------- Task 6: delete ----------

    @Test
    void deleteUser_removesItAndRedirects() throws Exception {
        User john = saveJohn();

        mockMvc.perform(post("/users/" + john.getId() + "/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        assertFalse(userRepository.existsById(john.getId()));
    }
}
