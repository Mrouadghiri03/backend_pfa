package com.pfa.api.app.dto.responses;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String cin;
    private String inscriptionNumber;
    private Boolean enabled;
    private Long studiedBranchId;
    private Long branchId;
    private Collection<GrantedAuthority> authorities;
    private Long teamId;
    private Long teamInResponsibilityId;
    private List<Long> projectsInSupervisionId;

    // You can include other fields as needed

    public static UserResponseDTO fromEntity(User user) {
        return UserResponseDTO.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .phoneNumber(user.getPhoneNumber())
            .cin(user.getCin())
            .inscriptionNumber(user.getInscriptionNumber())
            .enabled(user.getEnabled())
            .studiedBranchId(user.getStudiedBranch() != null ? user.getStudiedBranch().getId() : null)
            .branchId(user.getBranch() == null ? null : user.getBranch().getId())
            .teamId(user.getTeam() == null ? null : user.getTeam().getId())
            .authorities(user.getAuthorities())
            .teamInResponsibilityId(user.getTeamInResponsibility() == null ? null : user.getTeamInResponsibility().getId())
            .projectsInSupervisionId(user.getProjects() == null ? null : user.getProjects().stream().map(Project::getId).toList())
            .build();
    }
}
