package com.rickmorty.Services;

import com.rickmorty.DTO.UserDto;
import com.rickmorty.Models.UserModel;
import com.rickmorty.Repository.UserRepository;
import com.rickmorty.exceptions.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(UserDto userDto) {
        UserModel userModel = new UserModel();
        userModel.setName(userDto.name());
        userModel.setSurname(userDto.surname());
        userModel.setEmail(userDto.email());
        userModel.setPassword(userDto.password());
        userModel.setDate_register(LocalDateTime.now());
        userRepository.save(userModel);
    }

    public void updateUser(Long id, UserDto userDto) {
        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            user.setName(userDto.name());
            user.setSurname(userDto.surname());
            user.setEmail(userDto.email());
            user.setPassword(userDto.password());
            user.setDate_update(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void patchUser(Long id, UserDto userDto) {
        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();
            if (userDto.name() != null) {
                user.setName(userDto.name());
            }
            if (userDto.surname() != null) {
                user.setSurname(userDto.surname());
            }
            if (userDto.email() != null) {
                user.setEmail(userDto.email());
            }
            if (userDto.password() != null) {
                user.setPassword(userDto.password());
            }
            user.setDate_update(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new InvalidInputException("ID não pode ser nulo.");
        }
        if (id == 0) {
            throw new InvalidInputException("ID não pode ser 0.");
        }

        Optional<UserModel> optionalUser = userRepository.findByIdAndActive(id, 1);
        if (optionalUser.isPresent()) {
            UserModel userModel = optionalUser.get();
            userModel.setActive(0);
            userModel.setDeleted_at(LocalDateTime.now());
            userRepository.save(userModel);
        } else {
            throw new InvalidInputException("Usuário não encontrado ou já inativo.");
        }

    }

}