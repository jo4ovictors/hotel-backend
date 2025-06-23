package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.entities.Stay;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.repository.StayRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
public class StayService {

    @Autowired
    private StayRepository stayRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    public List<StayResponseDTO> findAll() {
        return stayRepository.findAll().stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public StayResponseDTO findById(Long id) {
        Stay entity = stayRepository.findById(id).orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));
        return new StayResponseDTO(entity);
    }

    public List<StayResponseDTO> findByUser(Long userid) {
        return stayRepository.findByUserId(userid).stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    @Transactional
    public StayResponseDTO createStay(@NotNull StayCreateDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        Room room = roomRepository.findActiveById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));

        if (stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(),null)) {
            throw new ResourceNotFound("[!] -> Room is already occupied in this period.");
        }

        if (dto.getCheckOut().isBefore(dto.getCheckIn()) || dto.getCheckOut().isEqual(dto.getCheckIn())) {
            throw new ResourceNotFound("[!] -> Check-out must be after check-in.");
        }

        long dias = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(dias));

        Stay stay = new Stay();
        stay.setUser(user);
        stay.setRoom(room);
        stay.setCheckIn(dto.getCheckIn());
        stay.setCheckOut(dto.getCheckOut());
        stay.setPrice(totalPrice);

        Stay saved = stayRepository.save(stay);
        return new StayResponseDTO(saved);
    }

    @Transactional
    public StayResponseDTO update(Long id, @NotNull StayUpdateDTO dto) {
        Stay stay = stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));

        Room room = roomRepository.findActiveById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));

        if (dto.getCheckOut().isBefore(dto.getCheckIn()) || dto.getCheckOut().isEqual(dto.getCheckIn())) {
            throw new ResourceNotFound("[!] -> Check-out must be after check-in.");
        }

        boolean isOccupied = stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), id);
        if (isOccupied) {
            throw new ResourceNotFound("[!] -> Room is already occupied in this period.");
        }

        long dias = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());

        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(dias));

        stay.setRoom(room);
        stay.setCheckIn(dto.getCheckIn());
        stay.setCheckOut(dto.getCheckOut());
        stay.setPrice(totalPrice);

        Stay saved = stayRepository.save(stay);
        return new StayResponseDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!stayRepository.existsById(id)) {
            throw new ResourceNotFound("[!] -> Resource not found!");
        }

        try {
            stayRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ResourceNotFound("[!] -> Integrity Violation!");
        }
    }

    public StayReportDTO findCheapestStay(Long userId) {
        Stay stay = stayRepository.findTopByUserIdOrderByPriceAsc(userId)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Nenhuma estadia encontrada!"));
        return new StayReportDTO(stay);
    }

    public StayReportDTO findMostExpensiveStay(Long userId) {
        Stay stay = stayRepository.findTopByUserIdOrderByPriceDesc(userId)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Nenhuma estadia encontrada!"));
        return new StayReportDTO(stay);
    }

    public StayTotalDTO calculateTotalSpentByUser(Long userId) {
        BigDecimal total = stayRepository.findTotalAmountSpentByUser(userId);
        return new StayTotalDTO(total);
    }

}
