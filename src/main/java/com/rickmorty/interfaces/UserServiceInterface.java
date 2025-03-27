package com.rickmorty.interfaces;

import com.rickmorty.dtos.UserDto;
import com.rickmorty.dtos.UserPatchDto;
import org.springframework.validation.BindingResult;

public interface UserServiceInterface {
  void saveUser(UserDto userDto, BindingResult result);

  void updateUser(Long id, UserDto userDto, BindingResult result);

  void patchUser(Long id, UserPatchDto userPatchDto, BindingResult result);

  void deleteUser(Long id);
}
