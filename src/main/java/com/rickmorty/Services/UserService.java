package com.rickmorty.Services;

import com.rickmorty.DTO.UserDto;
import com.rickmorty.DTO.UserPatchDto;
import com.rickmorty.Models.UserModel;
import com.rickmorty.Repository.UserRepository;
import com.rickmorty.exceptions.*;
import com.rickmorty.interfaces.UserServiceInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void saveUser(UserDto userDto, BindingResult result) {
        validateFieldsWithCheckEmail(userDto, result);

        UserModel userModel = new UserModel();
        userModel.setName(userDto.name());
        userModel.setSurname(userDto.surname());
        userModel.setEmail(userDto.email());
        userModel.setPassword(userDto.password());
        userModel.setDate_register(LocalDateTime.now());
        userRepository.save(userModel);
    }

    @Override
    public void updateUser(Long id, UserDto userDto, BindingResult result) {
        if (id == null || id < 1) throw new InvalidIdException();

        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isEmpty()) throw new UserNotFoundException();

        UserModel user = optionalUser.get();
        validateFields(result);

        if (!Objects.equals(userDto.email(), optionalUser.get().getEmail())) {
            Optional<UserModel> checkEmailExists = userRepository.findByEmail(userDto.email());
            if (checkEmailExists.isPresent()) throw new ConflictException("Email já cadastrado");
            user.setEmail(userDto.email());
        }

        user.setName(userDto.name());
        user.setSurname(userDto.surname());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        user.setDate_update(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void patchUser(Long id, UserPatchDto userPatchDto, BindingResult result) {

        if (id == null || id < 1) throw new InvalidIdException();

        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isEmpty()) throw new UserNotFoundException();

        UserModel user = optionalUser.get();
        boolean isUpdated = false;

        if (userPatchDto.name() != null && !Objects.equals(user.getName(), userPatchDto.name())) {
            user.setName(userPatchDto.name());
            isUpdated = true;
        }
        if (userPatchDto.surname() != null && !Objects.equals(user.getSurname(), userPatchDto.surname())) {
            user.setSurname(userPatchDto.surname());
            isUpdated = true;
        }
        if (userPatchDto.email() != null && !Objects.equals(user.getEmail(), userPatchDto.email())) {
            user.setEmail(userPatchDto.email());
            isUpdated = true;
        }
        if (userPatchDto.password() != null && !Objects.equals(user.getPassword(), userPatchDto.password())) {
            user.setPassword(userPatchDto.password());
            isUpdated = true;
        }

        if (isUpdated) {
            validateFieldsPatch(userPatchDto, result);

            user.setDate_update(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id < 1) throw new InvalidIdException();
        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isEmpty()) throw new UserNotFoundException();

        UserModel userModel = optionalUser.get();
        userModel.setActive(0);
        userModel.setDeleted_at(LocalDateTime.now());
        userRepository.save(userModel);
    }

    public void validateFields(BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    public void validateFieldsWithCheckEmail(UserDto userDto, BindingResult result) {
        Optional<UserModel> checkEmailExists = userRepository.findByEmail(userDto.email());
        if (checkEmailExists.isPresent()) throw new ConflictException("Email já cadastrado");

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }

    public void validateFieldsPatch(UserPatchDto userPatchDto, BindingResult result) {
        if (userPatchDto.name() != null && userPatchDto.name().isBlank()) {
            throw new ValidationErrorException(List.of("Nome não pode estar vazio"));
        }
        if (userPatchDto.surname() != null && userPatchDto.surname().isBlank()) {
            throw new ValidationErrorException(List.of("Sobrenome não pode estar vazio"));
        }
        if (userPatchDto.email() != null) {
            if (userPatchDto.email().isBlank()) {
                throw new ValidationErrorException(List.of("Email não pode estar vazio"));
            }
            Optional<UserModel> checkEmailExists = userRepository.findByEmail(userPatchDto.email());
            if (checkEmailExists.isPresent()) throw new ConflictException("Email já cadastrado");
        }
        if (userPatchDto.password() != null && userPatchDto.password().isBlank()) {
            throw new ValidationErrorException(List.of("Senha não pode estar vazia"));
        }

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new ValidationErrorException(errors);
        }
    }
}