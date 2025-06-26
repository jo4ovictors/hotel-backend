package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.services.StayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;

@RestController
@RequestMapping("/stays")
@Tag(name = "Stay", description = "Endpoints for managing hotel stays")
public class StayResource {

    @Autowired
    private StayService stayService;

    @Operation(summary = "List all stays", description = "Returns all stays in the system.")
    @ApiResponse(responseCode = "200", description = "Stays listed successfully")
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<Page<StayResponseDTO>> findAll(Pageable pageable) {
        Page<StayResponseDTO> page = stayService.findAll(pageable);

        page.forEach(dto -> {
            dto.add(linkTo(methodOn(StayResource.class).findById(dto.getId())).withSelfRel());
        });

        return ResponseEntity.ok().body(page);
    }

    @Operation(summary = "Find stay by ID", description = "Returns a single stay by its ID.")
    @ApiResponse(responseCode = "200", description = "Stay found successfully")
    @ApiResponse(responseCode = "404", description = "Stay not found")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<StayResponseDTO> findById(
            @Parameter(description = "ID of the stay") @PathVariable Long id) {
        StayResponseDTO dto = stayService.findById(id);

        dto.add(linkTo(methodOn(StayResource.class).findById(id)).withSelfRel());
        dto.add(linkTo(methodOn(StayResource.class).delete(id)).withRel("delete"));
        dto.add(linkTo(methodOn(StayResource.class).update(id, null)).withRel("update"));

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "List stays by user", description = "Returns all stays associated with a specific user.")
    @ApiResponse(responseCode = "200", description = "Stays found for the user")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StayResponseDTO>> findByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<StayResponseDTO> stays = stayService.findByUser(userId);

        stays.forEach(dto -> {
            dto.add(linkTo(methodOn(StayResource.class).findById(dto.getId())).withSelfRel());
        });

        return ResponseEntity.ok(stays);
    }

    @Operation(summary = "Create a new stay", description = "Creates a new stay. Accessible by admins and employees.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stay created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayResponseDTO> create(
            @Parameter(description = "Stay creation data") @RequestBody StayCreateDTO dto) {
        StayResponseDTO created = stayService.createStay(dto);

        created.add(linkTo(methodOn(StayResource.class).findById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(StayResource.class).delete(created.getId())).withRel("delete"));
        created.add(linkTo(methodOn(StayResource.class).update(created.getId(), null)).withRel("update"));

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Delete a stay", description = "Deletes a stay by ID. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Stay deleted"),
            @ApiResponse(responseCode = "404", description = "Stay not found")
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the stay to delete") @PathVariable Long id) {
        stayService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a stay", description = "Updates a stay by ID. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stay updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Stay not found")
    })
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayResponseDTO> update(
            @Parameter(description = "ID of the stay to update") @PathVariable Long id,
            @Parameter(description = "Stay update data") @Valid @RequestBody StayUpdateDTO dto) {
        StayResponseDTO updated = stayService.update(id, dto);
        updated.add(linkTo(methodOn(StayResource.class).findById(id)).withSelfRel());
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Report - Cheapest stay", description = "Returns the cheapest stay for a given user.")
    @GetMapping("/report/min/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayReportDTO> cheapestStay(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayReportDTO report = stayService.findCheapestStay(userId);

        report.add(linkTo(methodOn(StayResource.class).cheapestStay(userId)).withSelfRel());
        report.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));
        report.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("most-expensive"));
        report.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withRel("total-spent"));

        return ResponseEntity.ok(stayService.findCheapestStay(userId));
    }

    @Operation(summary = "Report - Most expensive stay", description = "Returns the most expensive stay for a given user.")
    @GetMapping("/report/max/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayReportDTO> mostExpensiveStay(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayReportDTO report = stayService.findCheapestStay(userId);

        report.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withSelfRel());
        report.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));
        report.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("cheapest"));
        report.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withRel("total-spent"));

        return ResponseEntity.ok(stayService.findMostExpensiveStay(userId));
    }

    @Operation(summary = "Report - Total spent by user", description = "Returns the total amount spent by a user.")
    @GetMapping("/report/total/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayTotalDTO> totalSpentByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayTotalDTO total = stayService.calculateTotalSpentByUser(userId);

        total.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withSelfRel());
        total.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("cheapest"));
        total.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("most-expensive"));
        total.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));

        return ResponseEntity.ok(total);
    }

}
