package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.Enum.RolesEnum;
import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.*;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.repository.StayRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @Transactional(readOnly = true)
    public Page<StayResponseDTO> findAll(Pageable pageable) {
        return stayRepository.findAll(pageable).map(StayResponseDTO::new);
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

        boolean isClient = user.getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(RolesEnum.ROLE_CLIENT.name()));

        if (isClient && !dto.getUserId().equals(user.getId())) {
            throw new ResourceNotFound("Operation not permitted");
        }

        Room room = roomRepository.findActiveById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));

        if (stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(),null)) {
            throw new ResourceNotFound("[!] -> Room is already occupied in this period.");
        }

        this.validateCheckInCheckout(dto.getCheckIn(),dto.getCheckOut());

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

    public void validateCheckInCheckout(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (checkIn.isBefore(today)) {
            throw new ResourceNotFound("[!] -> Check-in is in the past.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new ResourceNotFound("[!] -> Check-out must be after check-in.");
        }

    }


    @Transactional
    public StayResponseDTO update(Long id, @NotNull StayUpdateDTO dto) {
        Stay stay = stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));

        Room room = roomRepository.findActiveById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        boolean isClient = user.getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(RolesEnum.ROLE_CLIENT.name()));

        if (isClient && !dto.getUserId().equals(user.getId())) {
            throw new ResourceNotFound("Operation not permitted");
        }

        if (dto.getCheckIn() != null && dto.getCheckOut() != null){
            this.validateCheckInCheckout(dto.getCheckIn(),dto.getCheckOut());
        }else if (dto.getCheckIn() == null){
            this.validateCheckInCheckout(stay.getCheckIn(),dto.getCheckOut());
        }else if (dto.getCheckOut() == null){
            this.validateCheckInCheckout(dto.getCheckIn(),stay.getCheckOut());
        }

        boolean isOccupied = stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), id);
        if (isOccupied) {
            throw new ResourceNotFound("[!] -> Room is already occupied in this period.");
        }

        long dias = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());

        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(dias));

        this.copyDTOToEntity(dto,stay, totalPrice, room);

        Stay saved = stayRepository.save(stay);
        return new StayResponseDTO(saved);
    }

    @Transactional
    public void delete(Long id) {
        Stay stay = stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));

        User user = userRepository.findById(stay.getUser().getId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        boolean isClient = user.getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(RolesEnum.ROLE_CLIENT.name()));

        if (isClient && !stay.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFound("Operation not permitted");
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

    private void copyDTOToEntity(StayUpdateDTO dto, Stay entity, BigDecimal price, Room room) {

        if (dto.getCheckIn() != null) entity.setCheckIn(dto.getCheckIn());
        if (dto.getCheckOut() != null) entity.setCheckOut(dto.getCheckOut());
        if (room != null) {
            entity.setRoom(room);
        }
        if (price != null) entity.setPrice(price);

    }

}
