package com.sulwork.breakfast.services;

import java.util.List;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;

public interface UserService {
	UserResponseDTO create(UserRequestDTO dto);

	List<UserResponseDTO> findAll();

	UserResponseDTO findById(Long id);

	UserResponseDTO findByCpf(String cpf);

	UserResponseDTO update(Long id, UserRequestDTO dto);

	void delete(Long id);
}
