package nest.controller;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import nest.dto.book.AddToFavoriteRequestDto;
import nest.service.book.favorite.FavoriteServiceImpl;
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
public class FavoriteControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private FavoriteServiceImpl favoriteService;

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
            scripts = "classpath:database/controller/book/favorite/"
                    + "01-add-user-and-favorite-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/favorite/"
                    + "02-delete-user-and-favorite-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void getAllBooksFromFavorite_ValidData_ShouldReturnBooks() throws Exception {
        String expectedTitle = "title";
        String expectedAuthor = "author";

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/favorites"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertTrue(responseContent.contains(expectedTitle));
        assertTrue(responseContent.contains(expectedAuthor));
    }

    @Sql(
            scripts = "classpath:database/controller/book/donate/03-add-user-without-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/donate/04-delete-user.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void getAllBooksFromFavorite_EmptyData_ShouldReturnEmptyList() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/favorites"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertEquals("[]", responseContent);
    }

    @Sql(
            scripts = "classpath:database/controller/book/favorite/"
                    + "03-create-user-and-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/favorite/"
                    + "02-delete-user-and-favorite-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void addBookToFavorite_ValidData_ShouldAddBook() throws Exception {
        AddToFavoriteRequestDto requestDto = new AddToFavoriteRequestDto();
        requestDto.setBookId(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void addBookToFavorite_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        AddToFavoriteRequestDto requestDto = new AddToFavoriteRequestDto();
        requestDto.setBookId(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/favorites")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Sql(
            scripts = "classpath:database/controller/book/favorite/"
                    + "01-add-user-and-favorite-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/favorite/"
                    + "02-delete-user-and-favorite-book.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithUserDetails(value = "customer")
    @Test
    void removeBookFromFavorite_ValidData_ShouldRemoveBook() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/favorites/" + bookId))
                .andExpect(status().isOk());
    }

    @Test
    void removeBookFromFavorite_UnauthorizedUser_ShouldReturnForbidden() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/favorites/" + bookId))
                .andExpect(status().isForbidden());
    }

}
