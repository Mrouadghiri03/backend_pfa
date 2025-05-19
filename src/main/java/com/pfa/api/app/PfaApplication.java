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
import org.springframework.security.crypto.password.PasswordEncoder;

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
CommandLineRunner initDatabaseWithUser(UserRepository userRepository,
									   RoleRepository roleRepository,
									   BranchRepository branchRepository,
									   PasswordEncoder passwordEncoder) {
	final String studentEmail = "test.student@example.com";

	return args -> {
		userRepository.findByEmail(studentEmail).ifPresentOrElse(
				user -> System.out.println("L'utilisateur avec l'email " + studentEmail + " existe déjà: " + user.getId()),
				() -> {
					// 1. Créer ou récupérer le rôle
					Role headOfBranchRole = roleRepository.findByName("ROLE_HEAD_OF_BRANCH")
							.orElseGet(() -> roleRepository.save(new Role("ROLE_HEAD_OF_BRANCH")));

					// 2. Créer un nouvel utilisateur
					User headUser = new User();
					headUser.setFirstName("Test");
					headUser.setLastName("Student");
					headUser.setEmail(studentEmail);
					headUser.setCin("TESTCIN123");
					headUser.setInscriptionNumber("TESTINS456");
					headUser.setPassword(passwordEncoder.encode("password"));
					headUser.setEnabled(true);
					headUser.setRoles(List.of(headOfBranchRole));

					// 3. Sauvegarder temporairement pour avoir un ID
					userRepository.save(headUser);

					// 4. Créer une nouvelle branche avec ce user comme head
					Branch newBranch = new Branch();
					newBranch.setName("Informatique"); // ou un autre nom de branche
					newBranch.setHeadOfBranch(headUser); // association

					// 5. Sauvegarder la branche
					branchRepository.save(newBranch);

					// 6. (Optionnel) mettre à jour le user avec la branche si nécessaire
					headUser.setBranch(newBranch);
					userRepository.save(headUser);

					System.out.println("L'utilisateur HEAD OF BRANCH et sa branche ont été créés.");
				}
		);
	};
}

}