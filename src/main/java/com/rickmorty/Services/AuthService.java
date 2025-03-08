package com.rickmorty.Services;

import com.rickmorty.DTO.AuthenticationDto;
import com.rickmorty.DTO.UserDto;
import com.rickmorty.Models.UserModel;
import com.rickmorty.exceptions.InvalidCredentialsException;
import com.rickmorty.exceptions.UserInactiveException;
import com.rickmorty.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${api.security.token.secret}")
    private String SECRET_KEY;
    private static final long EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000; // 7 dias

    private AuthenticationManager authenticationManager;
    private UserService userService;
    private TokenService tokenService;

    AuthService(AuthenticationManager authenticationManager, UserService userService, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Transactional
    public String registerUser(UserDto userDto, BindingResult result) {
        userService.saveUser(userDto, result);
        return login(new AuthenticationDto(userDto.email(), userDto.password()));
    }

    public String login(AuthenticationDto loginDTO){
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

            if (user.isEmpty()) throw new UserNotFoundException();

            if (!user.get().isEnabled()) throw new UserInactiveException("Usuário inativo");

            if (!new BCryptPasswordEncoder().matches(loginDTO.password(), user.get().getPassword())) {
                throw new InvalidCredentialsException("Email ou senha não conferem");
            }

        } catch (InvalidCredentialsException e) {
            throw new InvalidCredentialsException(e.getMessage());
        }catch (UserNotFoundException e){
            throw new UserNotFoundException();
        } catch (UserInactiveException e) {
            throw new UserInactiveException(e.getMessage());
        }catch (Exception e) {
            throw new RuntimeException("Erro durante o login");
        }

    }

}
