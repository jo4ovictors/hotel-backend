package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.StayCreateDTO;
import br.edu.ifmg.hotelbao.dtos.StayReportDTO;
import br.edu.ifmg.hotelbao.dtos.StayResponseDTO;
import br.edu.ifmg.hotelbao.dtos.StayTotalDTO;
import br.edu.ifmg.hotelbao.services.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stays")
public class StayResource {

    @Autowired
    private StayService stayService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<StayResponseDTO> create(@RequestBody StayCreateDTO dto) {
        StayResponseDTO created = stayService.createStay(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<StayResponseDTO>> findAll() {
        return ResponseEntity.ok(stayService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StayResponseDTO>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(stayService.findByUser(userId));
    }

    @GetMapping("/report/min/{userId}")
    public ResponseEntity<StayReportDTO> cheapestStay(@PathVariable Long userId) {
        return ResponseEntity.ok(stayService.findCheapestStay(userId));
    }

    @GetMapping("/report/max/{userId}")
    public ResponseEntity<StayReportDTO> mostExpensiveStay(@PathVariable Long userId) {
        return ResponseEntity.ok(stayService.findMostExpensiveStay(userId));
    }

    @GetMapping("/report/total/{userId}")
    public ResponseEntity<StayTotalDTO> totalSpentByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(stayService.calculateTotalSpentByUser(userId));
    }

}
