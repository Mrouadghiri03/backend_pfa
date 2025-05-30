package com.pfa.api.app.service.implementation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.TeamDTO;
import com.pfa.api.app.dto.responses.TeamResponseDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.Role;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.repository.RoleRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.TeamService;
import com.pfa.api.app.util.UserUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamServiceImplementation implements TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public TeamResponseDTO createTeam(TeamDTO teamDTO) throws NotFoundException {
        // Obtenir l'utilisateur actuel en utilisant UserUtils
        User currentUser = UserUtils.getCurrentUser(userRepository);
        if (currentUser.getTeam() != null) {
            throw new RuntimeException("you're already in a team");
        }
        if (teamRepository.findByName(teamDTO.getName()).isPresent()) {
            throw new RuntimeException("team with this name already exists");
            
        }
        // Associer les rôles à l'utilisateur

        // Enregistrer l'utilisateur dans la base de données

        List<User> members = new ArrayList<>();
        for (Long memberId : teamDTO.getMembersIds()) {
            User member = userRepository.findById(memberId).orElse(null);
            if (member.getTeam() != null) {
                throw new RuntimeException("you entered a member that is already in a team");
            }
            members.add(member);
        }

        // Créer l'équipe en utilisant les données du DTO
        Team team = Team.builder()
                .name(teamDTO.getName())
                .members(members)
                .responsible(currentUser)
                .academicYear(teamDTO.getAcademicYear())
                .build();
        team.getMembers().add(currentUser);
        Team persistedTeam = teamRepository.save(team);
        if (!currentUser.getRoles().stream().map(Role::getName)
                .anyMatch(name -> name.equals(RoleName.ROLE_RESPONSIBLE.toString()))) {
            Optional<Role> optionalRole = roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString());
            if (optionalRole.isPresent()) {
                currentUser.getRoles().add(optionalRole.get());
            }else{
                Role role= roleRepository.save(Role.builder().name(RoleName.ROLE_RESPONSIBLE.toString()).build());
                currentUser.getRoles().add(role);

            }
        }
        currentUser.setTeam(persistedTeam);
        userRepository.save(currentUser);

        for (User member : members) {
            member.setTeam(persistedTeam);
        }
        userRepository.saveAll(members);

        // Créer une notification pour chaque membre de l'équipe
        for (User member : members) {
            // Vérifier si le membre n'est pas le responsable de l'équipe
            if (!member.equals(currentUser)) {
                Notification notification = Notification.builder()
                        .description("The User "
                                + currentUser.getFirstName() + " " + currentUser.getLastName() +
                                " has added you as a member of team " + persistedTeam.getName() +
                                ". Review and contact him please.")
                        .creationDate(new Date())
                        .idOfSender(currentUser.getId())
                        .user(member)
                        .type("team")
                        .build();

                // Enregistrer la notification dans la base de données
                notificationRepository.save(notification);
            }
        }

        // Enregistrer l'équipe dans la base de données
        return TeamResponseDTO.fromEntity(persistedTeam);
    }

    @SuppressWarnings("null")
    @Override
    public void deleteTeamById(Long teamId) {
        teamRepository.deleteById(teamId);
    }

    @Override
    public void deleteTeamByName(String teamName) {
        Team team = teamRepository.findByName(teamName).get();
        if (team != null) {
            teamRepository.delete(team);
        }
    }

    @Override
    public TeamResponseDTO updateTeam(Long teamId, TeamDTO teamDTO) throws NotFoundException {
        // Récupérer l'équipe à modifier de la base de données
        Team existingTeam = teamRepository.findById(teamId).orElseThrow(NotFoundException::new);

        // Vérifier si l'utilisateur actuel est le responsable de l'équipe
        User currentUser = UserUtils.getCurrentUser(userRepository);
        if (!currentUser.equals(existingTeam.getResponsible())) {
            throw new RuntimeException("Only the responsible of the team can update it");
        }

        // Supprimer les anciens membres de l'équipe et mettre à jour les références
        // croisées dans la table User
        for (User member : existingTeam.getMembers()) {
            member.setTeam(null); // Mettre à null la référence à l'équipe dans chaque utilisateur
        }
        existingTeam.setMembers(null);

        // Mettre à null le responsable de l'équipe et mettre à jour les références
        // croisées dans la table User
        if (existingTeam.getResponsible() != null) {
            existingTeam.getResponsible().setTeam(null); // Mettre à null la référence à l'équipe dans l'ancien
                                                         // responsable
            currentUser.getRoles().remove(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());

        }
        existingTeam.setResponsible(null);

        // Ajouter les nouveaux membres à l'équipe et mettre à jour les références
        // croisées dans la table User
        List<Long> newMembersIds = teamDTO.getMembersIds();
        List<User> newMembers = new ArrayList<>();
        for (Long memberId : newMembersIds) {
            User member = userRepository.findById(memberId).orElseThrow(NotFoundException::new);
            // Vérifier si le membre appartient déjà à d'autres équipes
            if (isUserInOtherTeams(member)) {
                throw new RuntimeException("User with id " + memberId + " is already a member of another team");
            }
            member.setTeam(existingTeam); // Mettre à jour la référence à l'équipe dans chaque nouveau membre
            newMembers.add(member);
        }
        existingTeam.setMembers(newMembers);

        // Vérifier que le nouveau responsable fait partie des nouveaux membres
        Long newResponsibleId = teamDTO.getNewResponsible();
        User newResponsible = newMembers.stream()
                .filter(user -> user.getId() == newResponsibleId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException());

        // Mettre à jour le responsable de l'équipe et mettre à jour les références
        // croisées dans la table User
        newResponsible.setTeam(existingTeam); // Mettre à jour la référence à l'équipe dans le nouveau responsable
        newResponsible.getRoles().add(roleRepository.findByName(RoleName.ROLE_RESPONSIBLE.toString()).get());
        existingTeam.setResponsible(newResponsible);
        existingTeam.setName(teamDTO.getName());
        // Enregistrer les modifications dans la base de données
        return TeamResponseDTO.fromEntity(teamRepository.save(existingTeam));
    }

    // Méthode pour vérifier si un utilisateur appartient déjà à d'autres équipes
    private boolean isUserInOtherTeams(User user) {
        List<Team> teams = teamRepository.findByMembersContaining(user);
        return teams.size() > 1; // Plus de 1 signifie qu'il est dans au moins une autre équipe
    }

    @Override
    public TeamResponseDTO getTeamById(Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        return TeamResponseDTO.fromEntity(team);
    }

    @Override
    public TeamResponseDTO getTeamByName(String teamName) {
        Team team = teamRepository.findByName(teamName).get();
        return TeamResponseDTO.fromEntity(team);
    }

    @Override
    public List<TeamResponseDTO> getAllTeams(String academicYear) {

        List<Team> teams = new ArrayList<>();
        System.out.println(academicYear.getClass());
        if (academicYear.equals("undefined")) {
            teams = teamRepository.findAll();
        } else {
            teams = teamRepository.findByAcademicYear(academicYear);
        }
        List<TeamResponseDTO> teamResponseDTOs = new ArrayList<>();
        for (Team team : teams) {
            TeamResponseDTO teamResponseDTO = TeamResponseDTO.fromEntity(team);
            teamResponseDTOs.add(teamResponseDTO);
        }
        return teamResponseDTOs;
    }
}