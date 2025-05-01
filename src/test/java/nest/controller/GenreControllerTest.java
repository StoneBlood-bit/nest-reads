package nest.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import nest.config.PageResponse;
import nest.dto.genre.GenreDto;
import nest.service.genre.GenreServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenreControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private GenreServiceImpl genreService;

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
            scripts = "classpath:database/controller/genre/01-delete-from-genres.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void create_ValidData_ShouldCreateGenre() throws Exception {
        GenreDto genreDto = new GenreDto();
        genreDto.setName("Genre");

        GenreDto expected = new GenreDto();
        expected.setName(genreDto.getName());

        String jsonRequest = objectMapper.writeValueAsString(genreDto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        GenreDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), GenreDto.class);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void create_InvalidData_ShouldReturnBadRequest() throws Exception {
        GenreDto genreDto = new GenreDto();
        genreDto.setName("");

        String jsonRequest = objectMapper.writeValueAsString(genreDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void create_ValidDataAndUserIsNotManager_ShouldReturnForbidden() throws Exception {
        GenreDto genreDto = new GenreDto();
        genreDto.setName("Genre");

        String jsonRequest = objectMapper.writeValueAsString(genreDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Sql(
            scripts = "classpath:database/controller/genre/02-add-two-genres.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/genre/01-delete-from-genres.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void findAll_ValidData_ShouldReturnGenres() throws Exception {
        GenreDto genreDto1 = new GenreDto();
        genreDto1.setId(1L);
        genreDto1.setName("first");

        GenreDto genreDto2 = new GenreDto();
        genreDto2.setId(2L);
        genreDto2.setName("second");

        List<GenreDto> expected = new ArrayList<>();
        expected.add(genreDto1);
        expected.add(genreDto2);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/genres")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        PageResponse<GenreDto> actualPage = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});

        List<GenreDto> actualList = actualPage.getContent();

        assertNotNull(actualPage);
        assertNotNull(actualPage.getContent());
        assertEquals(2, actualList.size());
        assertEquals(expected, actualList);
    }

    @Sql(
            scripts = "classpath:database/controller/genre/02-add-two-genres.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/genre/01-delete-from-genres.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void findById_ValidId_ShouldReturnGenre() throws Exception {
        Long validId = 1L;

        GenreDto expected = new GenreDto();
        expected.setId(validId);
        expected.setName("first");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/genres/{id}", validId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        GenreDto actual = objectMapper.readValue(jsonResponse, GenreDto.class);

        assertEquals(expected, actual);
    }

    @WithMockUser(username = "customer", roles = {"CUSTOMER"})
    @Test
    void findById_InvalidId_NotFound() throws Exception {
        Long invalidId = 999L;

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/genres/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Test
    void delete_ValidId_ShouldDeleteGenre() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete("/genres/{id}", bookId))
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    @Test
    void delete_ValidId_ShouldReturnForbidden() throws Exception {
        Long bookId = 1L;

        mockMvc.perform(delete("/genres/{id}", bookId))
                .andExpect(status().isForbidden());
    }
}
