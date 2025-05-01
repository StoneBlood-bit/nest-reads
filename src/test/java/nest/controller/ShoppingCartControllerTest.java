package nest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import nest.dto.shoppingcart.ShoppingCartResponseDto;
import nest.service.shoppingcart.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShoppingCartControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ShoppingCartServiceImpl shoppingCartService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/01-create-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @WithUserDetails(value = "customer")
    void getByUserId_ValidData_ShouldReturnCart() throws Exception {
        Long userId = 1L;
        ShoppingCartResponseDto expected = new ShoppingCartResponseDto();
        expected.setId(1L);
        expected.setUserId(userId);
        expected.setBooks(List.of());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/shopping-carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        ShoppingCartResponseDto actual = objectMapper
                .readValue(jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    void getByUserId_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        Long userId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.get("/shopping-carts"))
                .andExpect(status().isForbidden());
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/01-create-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void addBookToShoppingCart_ValidRequest_ShouldAddBook() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(post("/shopping-carts/books/{bookId}", bookId))
                .andExpect(status().isOk());
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/01-create-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void addBookToShoppingCart_BookNotFound_ShouldReturnNotFound() throws Exception {
        Long invalidBookId = 999L;

        mockMvc.perform(post("/shopping-carts/books/{bookId}", invalidBookId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addBookToShoppingCart_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(post("/shopping-carts/books/{bookId}", bookId))
                .andExpect(status().isForbidden());
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/03-add-book-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void removeBookFromShoppingCart_ValidId_ShouldRemoveBook() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete("/shopping-carts/books/remove/{bookId}", bookId))
                .andExpect(status().isOk());
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/03-add-book-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void removeBookFromShoppingCart_InvalidId_ShouldReturnNotFound() throws Exception {
        Long invalidId = 999L;

        mockMvc.perform(delete("/shopping-carts/books/remove/{bookId}", invalidId))
                .andExpect(status().isNotFound());
    }

    @Sql(
            scripts = "classpath:database/controller/shoppingcart/03-add-book-to-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/shoppingcart/02-delete-shopping-cart.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void removeBookFromShoppingCart_BookIsNotInCart_ShouldReturnBadRequest() throws Exception {
        Long bookNotInCartId = 2L;

        mockMvc.perform(delete("/shopping-carts/books/remove/{bookId}", bookNotInCartId))
                .andExpect(status().isBadRequest());
    }

    @Test
    void removeBookFromShoppingCart_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete("/shopping-carts/books/remove/{bookId}", bookId))
                .andExpect(status().isForbidden());
    }
}
