package br.edu.ifmg.hotelbao.resources;


import br.edu.ifmg.hotelbao.dtos.NewPasswordDTO;
import br.edu.ifmg.hotelbao.dtos.RequestTokenDTO;
import br.edu.ifmg.hotelbao.services.PasswordRecoverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class PasswordRecoverResource {

    @Autowired
    private PasswordRecoverService passwordRecoverService;

    @PostMapping(value = "recover-token")
    public ResponseEntity<Void> createRecoverToken(@Valid @RequestBody RequestTokenDTO dto) {
        passwordRecoverService.createRecoverToken(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "new-password")
    public ResponseEntity<Void> saveNewPassword(@Valid @RequestBody NewPasswordDTO dto) {
        passwordRecoverService.savePassword(dto);
        return ResponseEntity.noContent().build();
    }

}
