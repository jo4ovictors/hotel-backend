package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.services.DatabaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/database")
@Tag(name = "Database", description = "Resource for deleting system data.")
public class DatabaseResource {

    @Autowired
    private DatabaseService databaseService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(
            summary = "Delete system data",
            description = "Deletes all system data. Only accessible to ADMIN users."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Data deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Access denied."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteAllData() {
        databaseService.deleteDatabase();
        return ResponseEntity.noContent().build();
    }

}
