package com.rickmorty.Services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rickmorty.DTO.AuthenticationDto;
import com.rickmorty.Models.UserModel;
import com.rickmorty.exceptions.InvalidCredentialsException;
import com.rickmorty.exceptions.UserInactiveException;
import com.rickmorty.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private AuthenticationManager authenticationManager;
    private UserService userService;

    TokenService(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    public String login(AuthenticationDto loginDTO){
        validateLogin(loginDTO);
        try {
            var userPassword = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
            var auth = this.authenticationManager.authenticate(userPassword);
            var token = generateToken((UserModel) auth.getPrincipal());
            return token.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while logging in", e.getCause());
        }
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

    private String generateToken(UserModel user){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("api-v1-auth")
                    .withSubject(user.getEmail())
                    .withClaim("name", user.getName())
                    .withClaim("surname", user.getSurname())
                    .withArrayClaim("roles", new String[] {user.getRole().getRole()})
                    .withExpiresAt(genExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error while generating token", exception);
        }
    }
    private Instant genExpirationDate(){
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .withIssuer("api-v1-auth")
                    .build()
                    .verify(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new RuntimeException("Token expired");
            }

            return decodedJWT.getSubject();

        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Invalid token", exception);
        }
    }

}
