package br.edu.ifmg.hotelbao.resources;


import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.util.Factory;
import br.edu.ifmg.hotelbao.util.TokenUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RoomResourceIT {

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
    public void findAllShouldReturnPagedRooms() throws Exception {
        mockMvc.perform(get("/room")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    public void findByIdShouldReturnRoomWhenIdExists() throws Exception {
        ResultActions result = mockMvc.perform(get("/room/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    public void insertShouldCreateNewRoomWhenAuthorized() throws Exception {
        RoomDTO dto = Factory.createRoomDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(post("/room")
                .header("Authorization", "Bearer " + token)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated());
        result.andExpect(jsonPath("$.id").exists());
        result.andExpect(jsonPath("$.description").value(dto.getDescription()));
    }

    @Test
    public void insertShouldReturnForbiddenWhenNoToken() throws Exception {
        RoomDTO dto = Factory.createRoomDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/room")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void updateShouldReturnRoomDTOWhenIdExists() throws Exception {
        RoomDTO dto = Factory.createRoomDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        ResultActions result = mockMvc.perform(put("/room/{id}", existingId)
                .header("Authorization", "Bearer " + token)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(existingId));
    }

    @Test
    public void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        RoomDTO dto = Factory.createRoomDTO();
        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/room/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + token)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/room/{id}", existingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

}
