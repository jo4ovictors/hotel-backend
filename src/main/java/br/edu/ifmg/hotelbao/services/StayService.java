package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.constants.RoleLevel;
import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.entities.Room;
import br.edu.ifmg.hotelbao.entities.Stay;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repositories.RoomRepository;
import br.edu.ifmg.hotelbao.repositories.StayRepository;
import br.edu.ifmg.hotelbao.repositories.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.AccessDeniedException;
import br.edu.ifmg.hotelbao.services.exceptions.BusinessValidationException;
import br.edu.ifmg.hotelbao.services.exceptions.DatabaseException;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFoundException;
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

    @Autowired private StayRepository stayRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private AuthService authService;

    private static final String NOT_FOUND_STAY = "[!] -> Stay not found!";
    private static final String NOT_FOUND_USER = "[!] -> User not found!";
    private static final String ACCESS_DENIED = "[!] -> Access denied!";

    @Transactional(readOnly = true)
    public Page<StayResponseDTO> findAll(Pageable pageable) {
        RoleLevel role = authService.getHighestRoleLevel()
                .orElseThrow(() -> new AccessDeniedException("[!] -> No role assigned to the authenticated user."));

        Page<Stay> stays;

        if (role == RoleLevel.ROLE_ADMIN) {
            stays = stayRepository.findAll(pageable);
        } else if (role == RoleLevel.ROLE_EMPLOYEE) {
            stays = stayRepository.findByUserRole(RoleLevel.ROLE_CLIENT.name(), pageable);
        } else if (role == RoleLevel.ROLE_CLIENT) {
            Long userId = authService.getAuthenticatedUser().getId();
            stays = stayRepository.findByUserId(userId, pageable);
        } else {
            throw new AccessDeniedException("[!] -> You are not authorized to view stays.");
        }

        return stays.map(StayResponseDTO::new);
    }

    @Transactional(readOnly = true)
    public StayResponseDTO findById(Long id) {
        Stay stay = getStayById(id);
        validateUserAccess(stay.getUser().getId());
        return new StayResponseDTO(stay);
    }

    @Transactional(readOnly = true)
    public List<StayResponseDTO> findByUser(Long userId) {
        validateUserAccess(userId);
        return stayRepository.findByUserId(userId).stream()
                .map(StayResponseDTO::new)
                .toList();
    }

    @Transactional
    public StayResponseDTO create(@NotNull StayCreateDTO dto) {
        User requester = authService.getAuthenticatedUser();
        RoleLevel role = getCurrentRoleLevel();

        User user = getUserById(dto.getUserId());
        Room room = getAvailableRoom(dto.getRoomId());

        if (role == RoleLevel.ROLE_CLIENT && !requester.getId().equals(user.getId())) {
            throw new AccessDeniedException("[!] -> CLIENTS can only create stays for themselves.");
        }

        validateCheckInCheckout(dto.getCheckIn(), dto.getCheckOut());
        validateRoomAvailability(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), null);

        Stay stay = buildStay(user, room, dto.getCheckIn(), dto.getCheckOut());
        return new StayResponseDTO(stayRepository.save(stay));
    }

    @Transactional
    public StayResponseDTO update(Long id, StayUpdateDTO dto) {
        Stay stay = getStayById(id);
        User requester = authService.getAuthenticatedUser();
        RoleLevel role = getCurrentRoleLevel();

        if (role == RoleLevel.ROLE_CLIENT && !dto.getUserId().equals(requester.getId())) {
            throw new AccessDeniedException("[!] -> CLIENTS can only update their own stays.");
        }

        if (isNotUpcomingStay(stay)) {
            throw new AccessDeniedException("[!] -> Only upcoming stays can be updated.");
        }

        Room room = getAvailableRoom(dto.getRoomId());

        validateCheckInCheckout(dto.getCheckIn(), dto.getCheckOut());
        validateRoomAvailability(dto.getRoomId(), dto.getCheckIn(), dto.getCheckOut(), id);

        stay.setRoom(room);
        stay.setCheckIn(dto.getCheckIn());
        stay.setCheckOut(dto.getCheckOut());
        stay.setPrice(calculateTotalPrice(room.getPrice(), dto.getCheckIn(), dto.getCheckOut()));

        if (!stay.getUser().getId().equals(dto.getUserId())) {
            if (role == RoleLevel.ROLE_ADMIN || role == RoleLevel.ROLE_EMPLOYEE) {
                User newUser = userRepository.findById(dto.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("[!] -> New user not found!"));
                stay.setUser(newUser);
            } else {
                throw new AccessDeniedException("[!] -> Only ADMIN or EMPLOYEE can change the stay's user!");
            }
        }

        return new StayResponseDTO(stayRepository.save(stay));
    }

    @Transactional
    public void delete(Long id) {
        Stay stay = getStayById(id);
        User requester = authService.getAuthenticatedUser();
        RoleLevel role = getCurrentRoleLevel();

        switch (role) {
            case ROLE_ADMIN -> performDelete(id);

            case ROLE_EMPLOYEE -> {
                if (!stay.getUser().hasOnlyRole(RoleLevel.ROLE_CLIENT)) {
                    throw new AccessDeniedException("[!] -> EMPLOYEES can only delete CLIENT stays.");
                }
                if (isNotUpcomingStay(stay)) {
                    throw new AccessDeniedException("[!] -> Only upcoming stays can be deleted.");
                }
                performDelete(id);
            }

            case ROLE_CLIENT -> {
                if (!requester.getId().equals(stay.getUser().getId())) {
                    throw new AccessDeniedException("[!] -> CLIENTS can only delete their own stays.");
                }
                if (isNotUpcomingStay(stay)) {
                    throw new AccessDeniedException("[!] -> Only upcoming stays can be deleted.");
                }
                performDelete(id);
            }

            default -> throw new AccessDeniedException(ACCESS_DENIED);
        }
    }

    @Transactional(readOnly = true)
    public StayReportDTO findCheapestStay(Long userId) {
        validateUserAccess(userId);
        Stay stay = stayRepository.findTopByUserIdOrderByPriceAsc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> No stays found!"));
        return new StayReportDTO(stay);
    }

    @Transactional(readOnly = true)
    public StayReportDTO findMostExpensiveStay(Long userId) {
        validateUserAccess(userId);
        Stay stay = stayRepository.findTopByUserIdOrderByPriceDesc(userId)
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> No stays found!"));
        return new StayReportDTO(stay);
    }

    @Transactional(readOnly = true)
    public StayTotalDTO calculateTotalSpentByUser(Long userId) {
        validateUserAccess(userId);
        BigDecimal total = stayRepository.findTotalAmountSpentByUser(userId);
        return new StayTotalDTO(total);
    }

    private Stay getStayById(Long id) {
        return stayRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_STAY));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOT_FOUND_USER));
    }

    private Room getAvailableRoom(Long roomId) {
        return roomRepository.findByIdAndIsActiveTrue(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("[!] -> Room not found or inactive!"));
    }

    private boolean isNotUpcomingStay(Stay stay) {
        return stay.getCheckIn().isBefore(LocalDate.now());
    }

    private void validateRoomAvailability(Long roomId, LocalDate checkIn, LocalDate checkOut, Long excludeStayId) {
        boolean occupied = stayRepository.isRoomOccupied(roomId, checkIn, checkOut, excludeStayId);
        if (occupied) {
            throw new BusinessValidationException("[!] -> Room is already occupied in this period.");
        }
    }

    private void validateCheckInCheckout(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();

        if (checkIn.isBefore(today)) {
            throw new BusinessValidationException("[!] -> Check-in is in the past!");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new BusinessValidationException("[!] -> Check-out must be after check-in!");
        }
    }

    private void validateUserAccess(Long targetUserId) {
        User current = authService.getAuthenticatedUser();

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_ADMIN)) return;

        if (authService.hasMinimumAuthority(RoleLevel.ROLE_EMPLOYEE)) {
            if (current.getId().equals(targetUserId)) return;

            User target = getUserById(targetUserId);
            if (target.hasOnlyRole(RoleLevel.ROLE_CLIENT)) return;

            throw new AccessDeniedException("[!] -> EMPLOYEE can only access CLIENT data or their own.");
        }

        if (!current.getId().equals(targetUserId)) {
            throw new AccessDeniedException("[!] -> CLIENT can only access their own data.");
        }
    }

    private Stay buildStay(User user, Room room, LocalDate checkIn, LocalDate checkOut) {
        Stay stay = new Stay();
        stay.setUser(user);
        stay.setRoom(room);
        stay.setCheckIn(checkIn);
        stay.setCheckOut(checkOut);
        stay.setPrice(calculateTotalPrice(room.getPrice(), checkIn, checkOut));
        return stay;
    }

    private BigDecimal calculateTotalPrice(BigDecimal dailyRate, LocalDate checkIn, LocalDate checkOut) {
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        return dailyRate.multiply(BigDecimal.valueOf(days));
    }

    private RoleLevel getCurrentRoleLevel() {
        return authService.getHighestRoleLevel()
                .orElseThrow(() -> new AccessDeniedException("[!] -> No role assigned to the authenticated user."));
    }

    private void performDelete(Long id) {
        try {
            stayRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("[!] -> Integrity Violation!");
        }
    }

}

