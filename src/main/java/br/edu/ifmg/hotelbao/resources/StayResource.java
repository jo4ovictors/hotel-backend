package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.services.StayService;
import jakarta.validation.Valid;
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

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        stayService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<StayResponseDTO>> findAll() {
        return ResponseEntity.ok(stayService.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StayResponseDTO>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(stayService.findByUser(userId));
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<StayResponseDTO> update(@PathVariable Long id, @Valid @RequestBody StayUpdateDTO dto) {
        return ResponseEntity.ok().body(stayService.update(id, dto));
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
