package com.sulwork.breakfast.services.impls;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {

        repository.findByCpfUser(dto.getCpf())
                .ifPresent(user -> {
                    throw new BadRequestException("Usuário com esse CPF já existe!");
                });

        try {
            repository.createUser(dto.getName(), dto.getCpf(), dto.getPassword(), Role.USER.name());

            User savedUser = repository.findByCpfUser(dto.getCpf())
                    .orElseThrow(() -> new RuntimeException("Falha ao recuperar usuário"));

            return UserMapper.toDto(savedUser);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao criar usuário");
        }

    }

    @Override
    public List<UserResponseDTO> findAll() {
        try {
            List<User> list = repository.findAllUsers();

            return list.stream()
                    .map(UserMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao buscar todos os usuários");
        }
    }

    @Override
    public UserResponseDTO findById(Long id) {
        try {
            User user = repository.findByIdUser(id)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));

            return UserMapper.toDto(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao buscar usuário pelo ID");
        }
    }

    @Override
    public UserResponseDTO findByCpf(String cpf) {
        try {
            User user = repository.findByCpfUser(cpf)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado!"));

            return UserMapper.toDto(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao buscar usuário pelo CPF");
        }
    }

    @Override
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        try {
            User existUser = repository.findByIdUser(id)
                    .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

            if (existUser.getCpf() != dto.getCpf()) {
                repository.findByCpfUser(dto.getCpf())
                        .ifPresent(user -> {
                            new BadRequestException("CPF já em uso, por outro usuário!");
                        });
            }

            repository.updateUser(id, dto.getName(), dto.getCpf());

            User user = repository.findByIdUser(id)
                    .orElseThrow(() -> new NotFoundException("Falha ao recuperar usuário!"));

            return UserMapper.toDto(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao atualizar usuário!");
        }
    }

    @Override
    public void delete(Long id) {
        try {
            if (repository.findByIdUser(id).isEmpty()) {
                throw new NotFoundException("Usuário não encontrado!");
            }

            repository.deleteUser(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new BadRequestException("Erro ao deletetar usuário!");
        }
    }

}
