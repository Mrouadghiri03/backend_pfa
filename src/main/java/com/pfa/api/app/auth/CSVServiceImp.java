package com.pfa.api.app.auth;

import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.util.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class CSVServiceImp {

        @Autowired
        UserRepository repository;

        @Autowired
        RoleRepository roleRepository;

        @Autowired
        PasswordEncoder passwordEncoder;

        public void save(MultipartFile file) {
            try {
                System.out.println("Début du traitement du fichier: " + file.getOriginalFilename());

                String content = new String(file.getBytes(), StandardCharsets.UTF_8);
                System.out.println("Contenu brut:\n" + content);

                List<User> students = CSVHelper.csvToStudents(file.getInputStream(),passwordEncoder);
                System.out.println("Nombre d'étudiants lus: " + students.size());

                // Récupérer le rôle ROLE_STUDENT depuis la base de données
                Optional<Role> studentRoleOptional = roleRepository.findByName("ROLE_STUDENT");
                Role studentRole;

                if (studentRoleOptional.isEmpty()) {
                    // Si le rôle n'existe pas, le créer et le sauvegarder
                    studentRole = new Role("ROLE_STUDENT");
                    roleRepository.save(studentRole);
                } else {
                    studentRole = studentRoleOptional.get();
                }

                // Associer le rôle récupéré (ou créé) à chaque utilisateur
                Role finalStudentRole = studentRole;
                students.forEach(user -> {
                    user.setRoles(List.of(finalStudentRole)); // Utilisation de List.of pour une liste immutable
                });

                List<User> savedStudents = repository.saveAll(students);
                System.out.println("Nombre d'étudiants sauvegardés: " + savedStudents.size());

            } catch (Exception e) {
                System.err.println("Erreur lors de la sauvegarde:");
                e.printStackTrace();
                throw new RuntimeException("Échec du stockage des données CSV: " + e.getMessage(), e);
            }
        }

        public List<User> getAllStudents() {
            return repository.findAll();
        }
    }


