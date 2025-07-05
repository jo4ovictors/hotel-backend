package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.RoomDTO;
import br.edu.ifmg.hotelbao.services.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;

@RestController
@RequestMapping(value = "/room")
@Tag(name = "Room", description = "Endpoints for managing hotel rooms")
public class RoomResource {

    @Autowired
    private RoomService roomService;

    @Operation(
            summary = "List all rooms",
            description = "Returns a paginated list of all hotel rooms.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomDTO.class)))
            })
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<RoomDTO>> findAll(@ParameterObject Pageable pageable) {
        Page<RoomDTO> page = roomService.findAll(pageable);
        page.forEach(this::addRoomLinks);
        return ResponseEntity.ok().body(page);
    }

    @Operation(
            summary = "Find room by ID",
            description = "Returns the room with the given ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Room found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Room not found")
            })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<RoomDTO> findById(
            @Parameter(description = "ID of the room to retrieve") @PathVariable Long id) {
        RoomDTO dto = roomService.findById(id);
        addRoomLinks(dto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Insert a new room",
            description = "Creates a new room. Only ADMIN and EMPLOYEE users can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Room created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            })
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<RoomDTO> insert(
            @Parameter(description = "Room data to insert") @Valid @RequestBody RoomDTO dto) {
        dto = roomService.insert(dto);
        addRoomLinks(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @Operation(summary = "Update room",
            description = "Updates the room with the given ID. Only ADMIN and EMPLOYEE users can perform this operation.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Room updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = RoomDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Room not found")
            })
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<RoomDTO> update(
            @Parameter(description = "ID of the room to update") @PathVariable Long id,
            @Parameter(description = "Updated room data") @Valid @RequestBody RoomDTO dto) {
        dto = roomService.update(id, dto);
        addRoomLinks(dto);
        return ResponseEntity.ok().body(dto);
    }

    @Operation(
            summary = "Delete room",
            description = "Deletes the room with the given ID. Only ADMIN users can perform this operation.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Room deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Room not found")
            })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the room to delete") @PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void addRoomLinks(RoomDTO dto) {
        dto.add(linkTo(methodOn(RoomResource.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(RoomResource.class).findAll(Pageable.unpaged())).withRel("all-rooms"));
        dto.add(linkTo(methodOn(RoomResource.class).delete(dto.getId())).withRel("delete"));
        dto.add(linkTo(methodOn(RoomResource.class).update(dto.getId(), dto)).withRel("update"));
    }

}
