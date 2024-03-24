package com.pfa.api.app.util;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.RoleName;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.UserRepository;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserUtils {


    public User getCurrentUser(UserRepository userRepository) throws NotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Long id = ((User) userDetails).getId();
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException());
    }
    
    public boolean isSupervisor(User user) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_SUPERVISOR.toString()));
    }
    public boolean isSupervisorOfProject(User user, Project project) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_SUPERVISOR.toString()))
                && project.getSupervisors().contains(user);
    }
    public boolean isHeadOfBranch(User user) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_HEAD_OF_BRANCH.toString()));
    }
    public boolean isResponsible(User user) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_RESPONSIBLE.toString()));
    }
    public boolean isProjectResponsible(User user , Project project) {
        return user.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_RESPONSIBLE.toString()));
    }

}
