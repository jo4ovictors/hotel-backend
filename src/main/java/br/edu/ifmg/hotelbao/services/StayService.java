package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
import br.edu.ifmg.hotelbao.dtos.StayReportDTO;
import br.edu.ifmg.hotelbao.dtos.StayResponseDTO;
import br.edu.ifmg.hotelbao.dtos.StayTotalDTO;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.entities.Stay;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.repository.StayRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
public class StayService {

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Transactional
    public StayResponseDTO createStay(@NotNull StayCreateDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("[!] -> User not found!"));

        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("[!] -> Room not found!"));

        Stay stay = new Stay();
        stay.setUser(user);
        stay.setRoom(room);
        stay.setCheckIn(dto.getCheckIn());
        stay.setCheckOut(dto.getCheckOut());
        stay.setPrice(dto.getPrice());

        Stay saved = stayRepository.save(stay);
        return new StayResponseDTO(saved);
    }

    public List<StayResponseDTO> findByUser(Long userid) {
        return stayRepository.findByUserId(userid).stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    public List<StayResponseDTO> findAll() {
        return stayRepository.findAll().stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    public StayReportDTO findCheapestStay(Long userId) {
        Stay stay = stayRepository.findTopByUserIdOrderByPriceAsc(userId)
                .orElseThrow(() -> new RuntimeException("[!] -> Nenhuma estadia encontrada!"));
        return new StayReportDTO(stay);
    }

    public StayReportDTO findMostExpensiveStay(Long userId) {
        Stay stay = stayRepository.findTopByUserIdOrderByPriceDesc(userId)
                .orElseThrow(() -> new RuntimeException("[!] -> Nenhuma estadia encontrada!"));
        return new StayReportDTO(stay);
    }

    public StayTotalDTO calculateTotalSpentByUser(Long userId) {
        BigDecimal total = stayRepository.findTotalAmountSpentByUser(userId);
        return new StayTotalDTO(total);
    }

}
