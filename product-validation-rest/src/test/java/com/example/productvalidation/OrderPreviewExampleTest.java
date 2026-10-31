package com.example.productvalidation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** The complete example should be green before any student TODO is done. */
@SpringBootTest
@AutoConfigureMockMvc
class OrderPreviewExampleTest {
    @Autowired MockMvc mockMvc;

    @Test
    void validInputReturnsCalculatedPreview() throws Exception {
        mockMvc.perform(post("/api/examples/order-previews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":3,\"unitPrice\":12.50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(37.5));
    }

    @Test
    void invalidInputReturnsFieldErrors() throws Exception {
        mockMvc.perform(post("/api/examples/order-previews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":0,\"unitPrice\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.quantity").exists())
                .andExpect(jsonPath("$.errors.unitPrice").exists());
    }
}
