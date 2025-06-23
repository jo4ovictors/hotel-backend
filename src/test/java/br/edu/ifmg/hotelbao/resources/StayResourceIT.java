package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
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

import java.time.LocalDate;

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
    public void createShouldReturnCreatedWhenValidDataAndAuthorized() throws Exception {
        StayCreateDTO dto = new StayCreateDTO();
        dto.setUserId(existingUserId);
        dto.setRoomId(existingRoomId);
        dto.setCheckIn(LocalDate.now());
        dto.setCheckOut(LocalDate.now().plusDays(1));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/stays")
                        .header("Authorization", "Bearer " + tokenEmployee)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
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
    public void findAllShouldReturnListOfStays() throws Exception {
        mockMvc.perform(get("/stays")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void reportTotalShouldReturnOkWithUserData() throws Exception {
        mockMvc.perform(get("/stays/report/total/{userId}", existingUserId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists());
    }

}
