package com.sulwork.breakfast.persistence.model;

import org.hibernate.validator.constraints.UUID;

import com.sulwork.breakfast.persistence.enuns.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	public String name;
	@Column(nullable = false, unique = true, length = 11)
	public String cpf;
	@Column(nullable = false)
	public String password;
	@Enumerated(EnumType.STRING)
	public Role role;
	
}
