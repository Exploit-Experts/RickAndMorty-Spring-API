package com.rickmorty.interfaces;

import com.rickmorty.DTO.UserDto;
import com.rickmorty.DTO.UserPatchDto;
import org.springframework.validation.BindingResult;

public interface UserServiceInterface {
  void saveUser(UserDto userDto, BindingResult result);

  void updateUser(Long id, UserDto userDto, BindingResult result);

  void patchUser(Long id, UserPatchDto userPatchDto, BindingResult result);

  void deleteUser(Long id);
}
