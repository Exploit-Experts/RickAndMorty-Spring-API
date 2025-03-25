package com.rickmorty.Services;

import com.rickmorty.DTO.AuthenticationDto;
import com.rickmorty.DTO.UserDto;
import com.rickmorty.Models.UserModel;
import com.rickmorty.exceptions.InvalidCredentialsException;
import com.rickmorty.exceptions.InvalidResetCodeException;
import com.rickmorty.exceptions.UserInactiveException;
import com.rickmorty.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class AuthService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenService tokenService;
    private EmailService emailService;

    AuthService(AuthenticationManager authenticationManager, UserService userService, TokenService tokenService,EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    @Transactional
    public String registerUser(UserDto userDto, BindingResult result) {
        userService.saveUser(userDto, result);
        return login(new AuthenticationDto(userDto.email(), userDto.password()));
    }

    public String login(AuthenticationDto loginDTO) {
        validateLogin(loginDTO);
        return tokenService.createToken(loginDTO);
    }

    private void validateLogin(AuthenticationDto loginDTO) {
        try {
            if (loginDTO.email() == null || loginDTO.password() == null) {
                throw new InvalidCredentialsException("Email e senha são obrigatórios");
            } else if (!loginDTO.email().matches("^[^@]+@[^@]+$")) {
                throw new InvalidCredentialsException("Email no formato incorreto");
            }

            Optional<UserModel> user = userService.findByEmail(loginDTO.email());

            if (user.isEmpty())
                throw new UserNotFoundException();

            if (!user.get().isEnabled())
                throw new UserInactiveException("Usuário inativo");

            if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
                throw new InvalidCredentialsException("Email ou senha não conferem");
            }

        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        } catch (UserNotFoundException e) {
            throw new UserNotFoundException();
        } catch (UserInactiveException e) {
            throw new UserInactiveException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Erro durante o login");
        }

    }

    public void forgotPassword(String email) {
        if (email == null || !email.matches("^[^@]+@[^@]+$")) {
            throw new InvalidCredentialsException("Formato de e-mail inválido");
        }
        Optional<UserModel> userOpt = userService.findByEmail(email);
        if (userOpt.isPresent()) {
            UserModel user = userOpt.get();
            if (user.isEnabled()) {
                String resetCode = RandomCodeGenService.generateRandomCode(6);

                user.setResetPasswordCode(resetCode);
                user.setResetPasswordExpiration(LocalDateTime.now().plusMinutes(5));
                user.setDateUpdate(LocalDateTime.now());
                userService.updateUser(user);

                emailService.sendResetPasswordEmail(email, resetCode);
            }

        }
    }

    public void resetPassword(String email, String code, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("As senhas não conferem");
        }

        Optional<UserModel> userOpt = userService.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException();
        }

        UserModel user = userOpt.get();

        if (!user.isEnabled()) {
            throw new UserInactiveException("Usuário inativo");
        }

        if (user.getResetPasswordCode() == null || !user.getResetPasswordCode().equals(code)) {
            throw new InvalidResetCodeException("Código de redefinição inválido");
        }

        if (user.getResetPasswordExpiration() == null ||
                user.getResetPasswordExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Código expirado");
        }

        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setResetPasswordCode(null);
        user.setResetPasswordExpiration(null);
        user.setDateUpdate(LocalDateTime.now());

        userService.updateUser(user);
    }

}
