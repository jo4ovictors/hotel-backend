package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.dtos.InvoiceDTO;
import br.edu.ifmg.hotelbao.dtos.UserDTO;
import br.edu.ifmg.hotelbao.dtos.UserInsertDTO;
import br.edu.ifmg.hotelbao.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping(value = "/user")
@Tag(name = "User", description = "Endpoints for managing users in the hotel system")
public class UserResource {

    @Autowired
    private UserService userService;

    @Operation(summary = "List all users", description = "Returns a list of all registered users. Admin access required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List returned successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden access")
    })
    @GetMapping(produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> page = userService.findAll(pageable);

        page.forEach(dto -> {
            dto.add(linkTo(methodOn(UserResource.class).findById(dto.getId())).withSelfRel());
        });

        return ResponseEntity.ok(page);
    }

    @Operation(summary = "Get user by ID", description = "Returns a user based on the provided ID. Admin access required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden access")
    })
    @GetMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<UserDTO> findById(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        UserDTO dto = userService.findById(id);

        dto.add(linkTo(methodOn(UserResource.class).findById(id)).withSelfRel());
        dto.add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("all-users"));
        dto.add(linkTo(methodOn(UserResource.class).update(id, dto)).withRel("update"));
        dto.add(linkTo(methodOn(UserResource.class).delete(id)).withRel("delete"));
        dto.add(linkTo(methodOn(UserResource.class).findInvoice(id)).withRel("invoice"));

        return ResponseEntity.ok().body(dto);
    }
    
    @Operation(summary = "Create a new user", description = "Registers a new user. Requires ADMIN or EMPLOYEE role.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    public ResponseEntity<UserDTO> insert(
            @Parameter(description = "User data to insert") @Valid @RequestBody UserInsertDTO dto) {
        UserDTO user = userService.insert(dto);

        user.add(linkTo(methodOn(UserResource.class).findById(user.getId())).withSelfRel());
        user.add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("all-users"));

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(summary = "Update a user", description = "Updates user data. Only accessible by admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data provided"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping(value = "/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> update(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "Updated user data") @Valid @RequestBody UserDTO dto) {
        dto = userService.update(id, dto);

        dto.add(linkTo(methodOn(UserResource.class).findById(dto.getId())).withSelfRel());
        dto.add(linkTo(methodOn(UserResource.class).findAll(null)).withRel("all-users"));

        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "Delete a user", description = "Deletes a user by ID. Only accessible by admins.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of the user to delete") @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Signup new user", description = "Public endpoint to register a new user into the system.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User signed up successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping(value= "/signup", produces = "application/json")
    public ResponseEntity<UserDTO> signup(
            @Parameter(description = "User data for signup") @Valid @RequestBody UserInsertDTO dto) {
        UserDTO user = userService.signup(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(summary = "Get user invoice", description = "Generates and retrieves a user invoice by user ID. Admin access required.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Invoice generated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping(value = "/invoice/{id}", produces = "application/json")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_EMPLOYEE', 'ROLE_CLIENT')")
    public ResponseEntity<InvoiceDTO> findInvoice(
            @Parameter(description = "ID of the user to generate invoice for") @PathVariable Long id) {
        return ResponseEntity.ok().body(userService.newInvoice(id));
    }

}
