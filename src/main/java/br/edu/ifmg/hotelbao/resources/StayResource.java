package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.*;
import br.edu.ifmg.hotelbao.services.StayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
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

    @Operation(
            summary = "List all stays",
            description = """
                    Returns a paginated list of stays.
                    
                    - **ADMIN**: Can view all stays
                    - **EMPLOYEE**: Can view own and client stays
                    - **CLIENT**: Can view their own stays only"""
    )
    @ApiResponse(responseCode = "200", description = "Stays listed successfully")
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<Page<StayResponseDTO>> findAll(@ParameterObject Pageable pageable) {
        Page<StayResponseDTO> stays = stayService.findAll(pageable);

        stays.forEach(dto -> {
            dto.add(linkTo(methodOn(StayResource.class).findById(dto.getId())).withSelfRel());
        });

        return ResponseEntity.ok(stays);
    }

    @Operation(
            summary = "Find stay by ID",
            description = """
                    Retrieves a stay by its ID.
                    
                    - **ADMIN**: Can view any stay
                    - **EMPLOYEE**: Can view own and client stays
                    - **CLIENT**: Can view their own stays only"""
    )
    @ApiResponse(responseCode = "200", description = "Stay found successfully")
    @ApiResponse(responseCode = "404", description = "Stay not found")
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayResponseDTO> findById(
            @Parameter(description = "ID of the stay") @PathVariable Long id) {
        StayResponseDTO dto = stayService.findById(id);

        dto.add(linkTo(methodOn(StayResource.class).findById(id)).withSelfRel());
        dto.add(linkTo(methodOn(StayResource.class).delete(id)).withRel("delete"));
        dto.add(linkTo(methodOn(StayResource.class).update(id, null)).withRel("update"));

        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "List stays by user",
            description = """
                    Returns all stays linked to a specific user.
                    
                    - **ADMIN**: Can view stays of any user
                    - **EMPLOYEE**: Can view their own and client stays
                    - **CLIENT**: Can view their own stays only"""
    )
    @ApiResponse(responseCode = "200", description = "Stays found for the user")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<List<StayResponseDTO>> findByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        List<StayResponseDTO> stays = stayService.findByUser(userId);

        stays.forEach(dto -> {
            dto.add(linkTo(methodOn(StayResource.class).findById(dto.getId())).withSelfRel());
        });

        return ResponseEntity.ok(stays);
    }

    @Operation(
            summary = "Create a new stay",
            description = """
                    Creates a new stay record.
                    
                    - **ADMIN** and **EMPLOYEE**: Can create stays for any user
                    - **CLIENT**: Can create stays only for themselves"""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Stay created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayResponseDTO> create(
            @Parameter(description = "Stay creation data") @Valid @RequestBody StayCreateDTO dto) {
        StayResponseDTO created = stayService.create(dto);

        created.add(linkTo(methodOn(StayResource.class).findById(created.getId())).withSelfRel());
        created.add(linkTo(methodOn(StayResource.class).delete(created.getId())).withRel("delete"));
        created.add(linkTo(methodOn(StayResource.class).update(created.getId(), null)).withRel("update"));

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Update a stay",
            description = """
                    Updates a stay record by its ID (Can only update upcoming stays).
                    
                    - **ADMIN**: Can update any stay
                    - **EMPLOYEE**: Can update their own and client stays
                    - **CLIENT**: Can update their own stays only"""
    )
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

    @Operation(
            summary = "Delete a stay",
            description = """
                    Deletes a stay by its ID.
                    
                    - **ADMIN**: Can delete any stay
                    - **EMPLOYEE**: Can delete any stay (only upcoming stays)
                    - **CLIENT**: Can delete their own stays only (only upcoming stays)"""
    )
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

    @Operation(
            summary = "Report - Cheapest stay",
            description = """
                    Returns the cheapest stay for a given user.
                    
                    - **ADMIN**: Can access any user's report
                    - **EMPLOYEE**: Can access their own and client reports
                    - **CLIENT**: Can access their own report only"""
    )
    @GetMapping("/report/min/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayReportDTO> cheapestStay(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayReportDTO report = stayService.findCheapestStay(userId);

        report.add(linkTo(methodOn(StayResource.class).cheapestStay(userId)).withSelfRel());
        report.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));
        report.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("most-expensive"));
        report.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withRel("total-spent"));

        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Report - Most expensive stay",
            description = """
                    Returns the most expensive stay for a given user.
                    
                    - **ADMIN**: Can access any user's report
                    - **EMPLOYEE**: Can access their own and client reports
                    - **CLIENT**: Can access their own report only"""
    )
    @GetMapping("/report/max/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayReportDTO> mostExpensiveStay(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayReportDTO report = stayService.findMostExpensiveStay(userId);

        report.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withSelfRel());
        report.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));
        report.add(linkTo(methodOn(StayResource.class).cheapestStay(userId)).withRel("cheapest"));
        report.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withRel("total-spent"));

        return ResponseEntity.ok(report);
    }

    @Operation(
            summary = "Report - Total spent by user",
            description = """
                    Returns the total amount spent by a specific user.
                    
                    - **ADMIN**: Can view totals for any user
                    - **EMPLOYEE**: Can view totals for themselves and client users
                    - **CLIENT**: Can view their own totals only"""
    )
    @GetMapping("/report/total/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<StayTotalDTO> totalSpentByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        StayTotalDTO total = stayService.calculateTotalSpentByUser(userId);

        total.add(linkTo(methodOn(StayResource.class).totalSpentByUser(userId)).withSelfRel());
        total.add(linkTo(methodOn(StayResource.class).cheapestStay(userId)).withRel("cheapest"));
        total.add(linkTo(methodOn(StayResource.class).mostExpensiveStay(userId)).withRel("most-expensive"));
        total.add(linkTo(methodOn(StayResource.class).findByUser(userId)).withRel("user-stays"));

        return ResponseEntity.ok(total);
    }

}
