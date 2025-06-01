package com.pfa.api.app;

import com.pfa.api.app.entity.Branch;
import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.repository.BranchRepository;
import com.pfa.api.app.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "JWT apis",version = "1.0"))
public class PfaApplication {

	public static void main(String[] args) {
		SpringApplication.run(PfaApplication.class, args);
	}
//pour la creatoin des user via csv il reste des choses de securité et des path a verfifer apres
	// surtout avec hamdaoui car il faut que je  pose des question sur la securité de lapplication et
	// surtout la creation des branches comme il nous a dit dans le meet , aussi jattends qui me push le backend de cette application la
	/*@Bean
	CommandLineRunner initDatabaseWithUser(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		final String studentEmail = "test.student@example.com"; // Email de l'utilisateur test
		return args -> {
			// Vérifier si un utilisateur avec cet email existe déjà
			userRepository.findByEmail(studentEmail).ifPresentOrElse(
					user -> System.out.println("L'utilisateur avec l'email " + studentEmail + " existe déjà: " + user.getId()),
					() -> {
						// Créer le rôle ROLE_STUDENT s'il n'existe pas
						Role studentRole = roleRepository.findByName("ROLE_HEAD_OF_BRANCH").orElseGet(() -> {
							Role newRole = new Role("ROLE_HEAD_OF_BRANCH");
							return roleRepository.save(newRole);
						});

						// Créer un nouvel utilisateur étudiant
						User studentUser = new User();
						studentUser.setFirstName("Test");
						studentUser.setLastName("Student");
						studentUser.setEmail(studentEmail);
						studentUser.setCin("TESTCIN123");
						studentUser.setInscriptionNumber("TESTINS456");
						studentUser.setPassword(passwordEncoder.encode("password")); // Encodez le mot de passe
						studentUser.setEnabled(true);
						studentUser.setRoles(List.of(studentRole));

						// Sauvegarder l'utilisateur
						userRepository.save(studentUser);
						System.out.println("L'utilisateur étudiant avec l'email " + studentEmail + " a été créé.");
					}
			);
		};



	}

	 */
@Bean
@Transactional
CommandLineRunner initDatabaseWithUser(UserRepository userRepository,
									   RoleRepository roleRepository,
									   BranchRepository branchRepository,
									   PasswordEncoder passwordEncoder) {
	final String studentEmail = "islam.elkhadir23@ump.ac.ma".trim().toLowerCase();


	return args -> {
		userRepository.findByEmail(studentEmail).ifPresentOrElse(
				user -> System.out.println("Utilisateur existant - ID: " + user.getId()),
				() -> {
					try {
						// 1. Création du rôle
						Role headOfBranchRole = roleRepository.findByName("ROLE_HEAD_OF_BRANCH")
								.orElseGet(() -> roleRepository.save(new Role("ROLE_HEAD_OF_BRANCH")));

						// 2. Création de l'utilisateur
						User newUser = User.builder()

								.firstName("Test")
								.lastName("Student")
								.email(studentEmail)
								.cin("TESTCIN123")
								.inscriptionNumber("TESTINS456")
								.password(passwordEncoder.encode("password"))
								.enabled(true)
								.roles(List.of(headOfBranchRole))
								.build();

						User savedUser = userRepository.save(newUser);
						System.out.println("[DEBUG] Utilisateur créé : " + savedUser);

						// 3. Création de la branche
						Branch newBranch = new Branch();
						newBranch.setName("Informatique");
						newBranch.setHeadOfBranch(savedUser);
						branchRepository.save(newBranch);

						// 4. Mise à jour bidirectionnelle
						savedUser.setBranch(newBranch);
						userRepository.save(savedUser);

						System.out.println("Création réussie - User ID: " + savedUser.getId());

					} catch (DataIntegrityViolationException e) {
						String errorMsg = "ERREUR: " + e.getMostSpecificCause().getMessage();
						System.err.println(errorMsg);
						throw new IllegalStateException(errorMsg);
					}
				}
		);
	};
}
}
