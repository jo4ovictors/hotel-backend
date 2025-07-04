package br.edu.ifmg.hotelbao.resources;

import br.edu.ifmg.hotelbao.services.DatabaseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/database")
@Tag(name = "Database", description = "Resource for Delete system emails.")
public class DatabaseResource {

    @Autowired
    protected DatabaseService databaseService;


    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void>delete(@PathVariable Long id) {
        databaseService.deleteDatabase(id);
        return ResponseEntity.noContent().build();
    }




}
