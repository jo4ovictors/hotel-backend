package br.edu.ifmg.hotelbao.resources;


import br.edu.ifmg.hotelbao.dtos.NewPasswordDTO;
import br.edu.ifmg.hotelbao.dtos.RequestTokenDTO;
import br.edu.ifmg.hotelbao.services.PasswordRecoverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@Tag(name = "Authentication & Password Recovery", description = "Endpoints for recovering and resetting user passwords")
public class PasswordRecoverResource {

    @Autowired
    private PasswordRecoverService passwordRecoverService;

    @Operation(
            summary = "Request password recovery token",
            description = "Sends a recovery token to the user's email address based on the provided username or email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Recovery token sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    @PostMapping(value = "/recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RequestTokenDTO dto) {
        passwordRecoverService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Reset password using token",
            description = "Updates the user's password using a valid recovery token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid token or password format"),
            @ApiResponse(responseCode = "404", description = "Token not found or expired")
    })
    @PostMapping(value = "/new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        passwordRecoverService.savePassword(dto);
        return ResponseEntity.noContent().build();
    }

}
