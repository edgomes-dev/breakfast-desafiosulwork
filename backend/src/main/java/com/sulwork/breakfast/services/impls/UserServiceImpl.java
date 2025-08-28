package com.sulwork.breakfast.services.impls;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sulwork.breakfast.dtos.UserRequestDTO;
import com.sulwork.breakfast.dtos.UserResponseDTO;
import com.sulwork.breakfast.dtos.mappers.UserMapper;
import com.sulwork.breakfast.persistence.enuns.Role;
import com.sulwork.breakfast.persistence.model.User;
import com.sulwork.breakfast.persistence.repositories.UserRepository;
import com.sulwork.breakfast.services.UserService;
import com.sulwork.breakfast.services.exceptions.BadRequestException;
import com.sulwork.breakfast.services.exceptions.NotFoundException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;

    private void isCpf(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            throw new BadRequestException("CPF Incorreto!");
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            throw new BadRequestException("CPF Incorreto!");
        }
    }

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {
        isCpf(dto.getCpf());

        repository.findByCpfUser(dto.getCpf())
                .ifPresent(user -> {
                    throw new BadRequestException("Usuário com esse CPF já existe!");
                });

        repository.createUser(dto.getName(), dto.getCpf(), encoder.encode(dto.getPassword()), Role.USER.name());

        User savedUser = repository.findByCpfUser(dto.getCpf())
                .orElseThrow(() -> new RuntimeException("Falha ao recuperar usuário"));

        return UserMapper.toDto(savedUser);

    }

    @Override
    public List<UserResponseDTO> findAll() {
        List<User> list = repository.findAllUsers();

        return list.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User user = repository.findByIdUser(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));

        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDTO findByCpf(String cpf) {
        isCpf(cpf);
        User user = repository.findByCpfUser(cpf)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));

        return UserMapper.toDto(user);
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        isCpf(dto.getCpf());
        User existUser = repository.findByIdUser(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        if (existUser.getCpf() != dto.getCpf()) {
            repository.findByCpfUser(dto.getCpf())
                    .ifPresent(user -> {
                        throw new BadRequestException("CPF já em uso, por outro usuário!");
                    });
        }

        repository.updateUser(id, dto.getName(), dto.getCpf());

        existUser.setCpf(dto.getCpf());
        existUser.setName(dto.getName());

        return UserMapper.toDto(existUser);
    }

    @Override
    public void delete(Long id) {
        if (repository.findByIdUser(id).isEmpty()) {
            throw new NotFoundException("Usuário não encontrado!");
        }

        repository.deleteUser(id);
    }

}
