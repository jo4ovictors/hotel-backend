package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
import br.edu.ifmg.hotelbao.dtos.StayUpdateDTO;
import br.edu.ifmg.hotelbao.util.Factory;
import br.edu.ifmg.hotelbao.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class StayResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TokenUtil tokenUtil;
    private String tokenAdmin;
    private String tokenEmployee;

    private Long existingStayId;
    private Long nonExistingStayId;

    private Long existingUserId;
    private Long existingRoomId;

    @BeforeEach
    void setUp() throws Exception {
        existingStayId = 1L;
        nonExistingStayId = 9999L;

        existingUserId = 1L;
        existingRoomId = 1L;

        tokenAdmin = tokenUtil.obtainAccessToken(mockMvc, "alex", "12345678");
        tokenEmployee = tokenUtil.obtainAccessToken(mockMvc, "bruno", "12345678");
    }

    @Test
    public void findAllShouldReturnPagedStays() throws Exception {
        mockMvc.perform(get("/stays")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void findByIdShouldReturnStayWhenIdExistsAndAuthorized() throws Exception {
        mockMvc.perform(get("/stays/{id}", existingStayId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingStayId));
    }

    @Test
    public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(get("/stays/{id}", nonExistingStayId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByUserShouldReturnStaysForGivenUser() throws Exception {
        mockMvc.perform(get("/stays/user/{userId}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void createShouldReturnCreatedWhenValidDataAndAuthorized() throws Exception {
        StayCreateDTO dto = Factory.createStayDTO(existingUserId, existingRoomId);
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/stays")
                        .header("Authorization", "Bearer " + tokenEmployee)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void updateShouldReturnUpdatedStayWhenValidData() throws Exception {
        StayUpdateDTO dto = Factory.createUpcomingStayUpdateDTO(existingUserId, existingRoomId);

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/stays/{id}", existingStayId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingStayId));
    }

    @Test
    public void deleteShouldReturnNoContentWhenAdminAndIdExists() throws Exception {
        mockMvc.perform(delete("/stays/{id}", existingStayId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/stays/{id}", nonExistingStayId)
                        .header("Authorization", "Bearer " + tokenAdmin))
                .andExpect(status().isNotFound());
    }

    @Test
    public void cheapestStayShouldReturnOkWhenUserExists() throws Exception {
        mockMvc.perform(get("/stays/report/min/{userId}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void mostExpensiveStayShouldReturnOkWhenUserExists() throws Exception {
        mockMvc.perform(get("/stays/report/max/{userId}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void totalSpentByUserShouldReturnOkAndTotalExists() throws Exception {
        mockMvc.perform(get("/stays/report/total/{userId}", existingUserId)
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());
    }

}
