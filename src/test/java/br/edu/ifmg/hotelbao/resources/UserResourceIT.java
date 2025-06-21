package br.edu.ifmg.hotelbao.resources;


import br.edu.ifmg.hotelbao.dtos.UserDTO;
import br.edu.ifmg.hotelbao.dtos.UserInsertDTO;
import br.edu.ifmg.hotelbao.util.Factory;
import br.edu.ifmg.hotelbao.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;
    private String token;

    private Long existingId;
    private Long nonExistingId;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 9999L;

        token = tokenUtil.obtainAccessToken(mockMvc, "alex", "12345678");
    }

    @Test
    void findAllShouldReturnListWhenAdmin() throws Exception {
        mockMvc.perform(get("/user")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void findByIdShouldReturnUserWhenIdExists() throws Exception {
        mockMvc.perform(get("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/user/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldCreateUserWhenAdminOrEmployee() throws Exception {
        UserInsertDTO dto = Factory.createUserDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/user")
                        .header("Authorization", "Bearer " + token)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("login"));
    }

    @Test
    void insertShouldReturnForbiddenWhenNoToken() throws Exception {
        UserInsertDTO dto = Factory.createUserDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/user")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signupShouldCreateUserWithoutAuth() throws Exception {
        UserInsertDTO dto = Factory.createUserDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/user/signup")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.login").value("login"));
    }

    @Test
    void updateShouldUpdateUserWhenIdExists() throws Exception {
        UserDTO dto = Factory.createUserDTO();
        dto.setFirstName("Updated");
        dto.setLastName("User");
        dto.setPhone("99887766");
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"));
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/user/{id}", existingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/user/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

}
