package com.sulwork.breakfast.persistence.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sulwork.breakfast.persistence.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Transactional
	@Modifying
	@Query(value = "INSERT INTO users (name, cpf, password, role) VALUES (:name, :cpf, :password, :role)", nativeQuery = true)
	void createUser(String name, String cpf, String password, String role);

	@Query(value = "SELECT * FROM users", nativeQuery = true)
	List<User> findAllUsers();

	@Query(value = "SELECT * FROM users WHERE cpf = :cpf", nativeQuery = true)
	Optional<User> findByCpfUser(String cpf);

	@Query(value = "SELECT * FROM users WHERE id = :id", nativeQuery = true)
	Optional<User> findByIdUser(Long id);

	@Transactional
	@Modifying
	@Query(value = "UPDATE users SET name = :name, cpf = :cpf WHERE id = :id", nativeQuery = true)
	void updateUser(Long id, String name, String cpf);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
	void deleteUser(Long id);

}
