package com.sulwork.breakfast.dtos.mappers;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;
import com.sulwork.breakfast.persistence.model.User;

public class UserMapper {
	public static User toDomain(UserRequestDTO dto) {
		return User.builder()
				.name(dto.getName())
				.cpf(dto.getCpf())
				.password(dto.getPassword())
				.build();
	}

	public static UserResponseDTO toDto(User user) {
		return UserResponseDTO.builder()
				.id(user.getId())
				.name(user.getName())
				.cpf(user.getCpf())
				.build();
	}

}
