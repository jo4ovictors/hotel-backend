package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.constants.RoleEnum;
import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.entities.Stay;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repository.RoomRepository;
import br.edu.ifmg.hotelbao.repository.StayRepository;
import br.edu.ifmg.hotelbao.repository.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.BusinessValidationException;
import br.edu.ifmg.hotelbao.services.exceptions.DatabaseException;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFound;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public Page<StayResponseDTO> findAll(Pageable pageable) {
        return stayRepository.findAll(pageable).map(StayResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public StayResponseDTO findById(Long id) {
        User currentUser = authService.getAuthenticatedUser();

        Stay entity = stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));

        if (authService.doesNotHaveAuthority("ROLE_ADMIN") && !currentUser.getId().equals(entity.getUser().getId())) {
            throw new AccessDeniedException("[!] -> You do not have permission to access this resource!");
        }

        return new StayResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public List<StayResponseDTO> findByUser(Long userId) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.doesNotHaveAuthority("ROLE_ADMIN") && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("[!] -> You do not have permission to access this resource!");
        }

        return stayRepository.findByUserId(userId).stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    @Transactional
    public StayResponseDTO create(@NotNull StayCreateDTO dto) {
        User currentUser = authService.getAuthenticatedUser();

        // Verifica se usuário atual é CLIENT
        boolean isCurrentUserClient = !authService.doesNotHaveAuthority("ROLE_CLIENT");

        // Se for CLIENT, só pode criar estadia para ele mesmo
        if (isCurrentUserClient && !currentUser.getId().equals(dto.getUserId())) {
            throw new AccessDeniedException("[!] -> Clients can only create stays for themselves!");
        }

        // Busca o usuário da estadia
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        // Busca o quarto
        Room room = roomRepository.findActiveById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> Room not found!"));

        // Validação: quarto já está ocupado no período?
        if (stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), null)) {
            throw new BusinessValidationException("[!] -> Room is already occupied in this period.");
        }

        // Validação: datas de check-in e check-out
        this.validateCheckInCheckout(dto.getCheckIn(), dto.getCheckOut());

        // Calcula valor total
        long dias = ChronoUnit.DAYS.between(dto.getCheckIn(), dto.getCheckOut());
        BigDecimal totalPrice = room.getPrice().multiply(BigDecimal.valueOf(dias));

        // Cria entidade
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
            throw new BusinessValidationException("[!] -> Check-out must be after check-in!");
        }

        boolean isOccupied = stayRepository.isRoomOccupied(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), id);
        if (isOccupied) {
            throw new BusinessValidationException("[!] -> Room is already occupied in this period!");
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
        Stay stay = stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("[!] -> Stay not found!"));

        User user = userRepository.findById(stay.getUser().getId())
                .orElseThrow(() -> new ResourceNotFound("[!] -> User not found!"));

        boolean isClient = user.getRoles().stream()
                .anyMatch(role -> role.getAuthority().equals(RoleEnum.ROLE_CLIENT.name()));

        if (isClient && !stay.getUser().getId().equals(user.getId())) {
            throw new BusinessValidationException("Operation not permitted");
        }

        try {
            stayRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("[!] -> Integrity Violation!");
        }
    }

    @Transactional(readOnly = true)
    public StayReportDTO findCheapestStay(Long userId) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.doesNotHaveAuthority("ROLE_ADMIN") && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("[!] -> You do not have permission to access this resource!");
        }

        Stay stay = stayRepository.findTopByUserIdOrderByPriceAsc(userId)
                .orElseThrow(() -> new ResourceNotFound("[!] -> No stays found!"));
        return new StayReportDTO(stay);
    }

    @Transactional(readOnly = true)
    public StayReportDTO findMostExpensiveStay(Long userId) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.doesNotHaveAuthority("ROLE_ADMIN") && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("[!] -> You do not have permission to access this resource!");
        }

        Stay stay = stayRepository.findTopByUserIdOrderByPriceDesc(userId)
                .orElseThrow(() -> new ResourceNotFound("[!] -> No stays found!"));
        return new StayReportDTO(stay);
    }

    @Transactional(readOnly = true)
    public StayTotalDTO calculateTotalSpentByUser(Long userId) {
        User currentUser = authService.getAuthenticatedUser();

        if (authService.doesNotHaveAuthority("ROLE_ADMIN") && !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("[!] -> You do not have permission to access this resource!");
        }

        BigDecimal total = stayRepository.findTotalAmountSpentByUser(userId);
        return new StayTotalDTO(total);
    }

    private void validateCheckInCheckout(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (checkIn.isBefore(today)) {
            throw new BusinessValidationException("[!] -> Check-in is in the past.");
        }

        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessValidationException("[!] -> Check-out must be after check-in.");
        }

    }

}
