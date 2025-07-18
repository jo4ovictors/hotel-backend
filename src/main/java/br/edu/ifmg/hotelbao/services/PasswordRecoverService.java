package br.edu.ifmg.hotelbao.services;

import br.edu.ifmg.hotelbao.dtos.EmailDTO;
import br.edu.ifmg.hotelbao.dtos.NewPasswordDTO;
import br.edu.ifmg.hotelbao.dtos.RequestTokenDTO;
import br.edu.ifmg.hotelbao.entities.PasswordRecover;
import br.edu.ifmg.hotelbao.entities.User;
import br.edu.ifmg.hotelbao.repositories.PasswordRecoverRepository;
import br.edu.ifmg.hotelbao.repositories.UserRepository;
import br.edu.ifmg.hotelbao.services.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class  PasswordRecoverService{

    @Value("${email.password-recover.token.minutes}")
    private int tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String uri;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void createRecoverToken(RequestTokenDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null) {
            throw new ResourceNotFoundException("[!] -> Email not found!");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover passwordRecover = new PasswordRecover();
        passwordRecover.setToken(token);
        passwordRecover.setEmail(user.getEmail());
        passwordRecover.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60L));
        passwordRecoverRepository.save(passwordRecover);

        String body = "Acesse o link para definir uma nova senha: (válido por "
                + tokenMinutes + " minutos)"
                + "\n\n" + uri + token;
        emailService.sendEmail(new EmailDTO(user.getEmail(), "Recuperação de Senha", body));
    }

    public void savePassword(@Valid NewPasswordDTO dto) {
        List<PasswordRecover> list = passwordRecoverRepository.searchValidToken(dto.getToken(), Instant.now());

        if (list.isEmpty()) {
            throw new ResourceNotFoundException("[!] -> Token not found!");
        }

        User user = userRepository.findByEmail(list.getFirst().getEmail());
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

}
