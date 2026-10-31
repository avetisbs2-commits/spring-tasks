package com.example.productvalidation;

import com.example.productvalidation.entity.Product;
import com.example.productvalidation.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** POST tests pass at the start; the other endpoint tests guide the student tasks. */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductApiTest {
    private static final String VALID = """
            {"name":"Notebook","sku":"BOOK-101","price":12.50,"category":"Stationery"}
            """;

    @Autowired MockMvc mockMvc;
    @Autowired ProductRepository repository;

    private Product save(String name, String sku, String price, String category) {
        return repository.save(new Product(name, sku, new BigDecimal(price), category));
    }

    @Test
    void createReturns201LocationAndResponseDto() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/products/[0-9]+")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Notebook"))
                .andExpect(jsonPath("$.sku").value("BOOK-101"));
        assertEquals(1, repository.count());
    }

    @Test
    void invalidBodyReturnsFieldErrorsAndDoesNotSave() throws Exception {
        String invalid = """
                {"name":" ","sku":"bad sku","price":0,"category":""}
                """;
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON).content(invalid))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.sku").exists())
                .andExpect(jsonPath("$.errors.price").exists())
                .andExpect(jsonPath("$.errors.category").exists());
        assertEquals(0, repository.count());
    }

    @Test
    void missingPriceIsInvalid() throws Exception {
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"sku\":\"BOOK-101\",\"category\":\"Stationery\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.price").exists());
    }

    @Test
    void duplicateSkuReturns409EvenWhenCaseDiffers() throws Exception {
        save("Existing", "BOOK-101", "8.00", "Stationery");
        mockMvc.perform(post("/api/products").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\",\"sku\":\"book-101\",\"price\":12,\"category\":\"Stationery\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").isNotEmpty());
        assertEquals(1, repository.count());
    }

    @Test
    void listCombinesCaseInsensitiveCategoryAndInclusivePriceBounds() throws Exception {
        save("Cheap pen", "PEN-1", "9.99", "Stationery");
        save("Notebook", "BOOK-101", "10.00", "Stationery");
        save("Pencil set", "PEN-2", "20.00", "STATIONERY");
        save("Other", "TOY-1", "15.00", "Toys");
        mockMvc.perform(get("/api/products")
                        .param("category", "stationery")
                        .param("minPrice", "10")
                        .param("maxPrice", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void emptyListIs200WithArray() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void invalidFilterReturns400() throws Exception {
        mockMvc.perform(get("/api/products").param("minPrice", "20").param("maxPrice", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").isNotEmpty());
        mockMvc.perform(get("/api/products").param("minPrice", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getExistingAndMissingProduct() throws Exception {
        Product product = save("Notebook", "BOOK-101", "12.50", "Stationery");
        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Notebook"));
        mockMvc.perform(get("/api/products/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void updateChangesExistingRowAndCanKeepOwnSku() throws Exception {
        Product product = save("Old", "BOOK-101", "10.00", "Stationery");
        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Notebook"));
        assertEquals(1, repository.count());
        assertEquals("Notebook", repository.findById(product.getId()).orElseThrow().getName());
    }

    @Test
    void updateRejectsInvalidBodyAndMissingId() throws Exception {
        Product product = save("Old", "BOOK-101", "10.00", "Stationery");
        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"\",\"sku\":\"BOOK-101\",\"price\":10,\"category\":\"Stationery\"}"))
                .andExpect(status().isBadRequest());
        assertEquals("Old", repository.findById(product.getId()).orElseThrow().getName());
        mockMvc.perform(put("/api/products/999999").contentType(MediaType.APPLICATION_JSON).content(VALID))
                .andExpect(status().isNotFound());
        assertEquals(1, repository.count());
    }

    @Test
    void updateRejectsSkuOwnedByAnotherProduct() throws Exception {
        Product first = save("First", "BOOK-101", "10.00", "Stationery");
        save("Second", "PEN-2", "5.00", "Stationery");
        mockMvc.perform(put("/api/products/{id}", first.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"First\",\"sku\":\"PEN-2\",\"price\":10,\"category\":\"Stationery\"}"))
                .andExpect(status().isConflict());
        assertEquals("BOOK-101", repository.findById(first.getId()).orElseThrow().getSku());
    }

    @Test
    void deleteReturns204Then404() throws Exception {
        Product product = save("Notebook", "BOOK-101", "12.50", "Stationery");
        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string(""));
        assertFalse(repository.existsById(product.getId()));
        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNotFound());
    }
}
