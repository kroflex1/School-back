package org.example.schoolback.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "login", nullable = false, unique = true)
	private String login;

	@Column(name = "password", nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false)
	private Role role;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "second_name", nullable = false)
	private String secondName;

	@Column(name = "patronymic_name")
	private String patronymicName;

	@Column(name = "email")
	private String email;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Column(name = "balance_coins")
	private Long coins;

	@OneToMany(mappedBy = "teacher")
	private Set<Group> teachingGroups = new HashSet<>();

	@ManyToMany(mappedBy = "participants")
	private Set<Group> groups = new HashSet<>();

	public String getFullName() {
		return firstName + " " + secondName + (patronymicName.isEmpty() ? "" : " " + patronymicName);
	}
}


