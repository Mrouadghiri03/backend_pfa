package com.pfa.api.app.entity.user;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pfa.api.app.entity.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = { "email", "cin", "inscription_number" }))
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 45)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "cin", unique = true)
    private String cin;

    
    @Column(name = "inscription_number", unique = true)
    private String inscriptionNumber;

    @Column(name = "password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;

    @Column(name = "enabled")
    private Boolean enabled;

    private String resetCode;

    @ManyToOne(targetEntity = Branch.class)
    @JoinColumn(name = "studied_branch_id")
    @JsonManagedReference
    private Branch studiedBranch;

    @OneToOne(mappedBy = "headOfBranch")
    @JsonManagedReference
    private Branch branch;

    @OneToOne(mappedBy = "user")
    private Confirmation confirmation;

    @ManyToMany(mappedBy = "profs")
    private List<Branch> branches;

    @ManyToMany(mappedBy = "supervisors")
    @JsonIgnore
    private List<Project> projects;

    @ManyToOne(targetEntity = Team.class)
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;

    @OneToOne(mappedBy = "responsible")
    @JsonBackReference
    private Team teamInResponsibility;

    @OneToOne(targetEntity = JoinRequest.class)
    @JoinColumn(name = "join_request_id", nullable = true)
    private JoinRequest joinRequest;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Notification> notifications;

    @OneToMany(mappedBy = "developer")
    @JsonManagedReference
    private List<UserStory> userStories;

    @ManyToMany(mappedBy = "juryMembers")
    private List<Presentation> presentations;

    @OneToMany(mappedBy = "supervisor")
    @JsonManagedReference
    private List<SupervisorAvailability> availabilities;

    @Column(name = "profile_image")
    private String profileImage;

    @Override
    /*public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }
        return authorities;
    }*/ // i a modifie

    public Collection<GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }
        if(!this.passwordChanged) {
            authorities.add(new SimpleGrantedAuthority("FORCE_PASSWORD_CHANGE"));
        }
        return authorities;
    }

    @Override
    public String getUsername() {

        // return email; i a change
        return this.inscriptionNumber;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;

    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;

    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // @Override
    // public int hashCode() {
    // final int prime = 31;
    // int result = 1;
    // result = prime * result + ((email == null) ? 0 : email.hashCode());
    // return result;
    // }

    @Column(name = "password_changed", nullable = false)
    private boolean passwordChanged = false;

    public boolean isPasswordChanged() {
        return passwordChanged;
    }

    public void setPasswordChanged(boolean passwordChanged) {
        this.passwordChanged = passwordChanged;
    }
}
