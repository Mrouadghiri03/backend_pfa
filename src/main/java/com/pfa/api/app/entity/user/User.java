package com.pfa.api.app.entity.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.pfa.api.app.entity.Branch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "user",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email","cin","inscription_number"})
)
public class User implements UserDetails{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" , length = 45)
    private long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email" , nullable = false)
    private String email;

    @Column(name = "phone_number" )
    private String phoneNumber;

    @Column(name = "cin" )
    private String cin;

    @Column(name = "inscription_number" )
    private String inscriptionNumber;

    @Column(name = "password")
    @JsonProperty(access = Access.WRITE_ONLY)
    private String password;
    
    @Column(name = "roles", nullable = false )
    private String roles;
    
    @Column(name = "enabled")
    private Boolean enabled;

    @ManyToOne(targetEntity = Branch.class)
    @JoinColumn(name = "branch_id")
    private Branch studiedBranch;
    
    @OneToOne(mappedBy = "headOfBranch")
    private Branch branch;

    @OneToOne(mappedBy = "user")
    private Confirmation confirmation;

    @ManyToMany(mappedBy = "profs")
    private List<Branch> branches;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(roles.split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    @Override
    public String getUsername() {
        return email;
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



}
