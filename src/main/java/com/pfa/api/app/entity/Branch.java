package com.pfa.api.app.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pfa.api.app.entity.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 45)
    private long id;

    @Column(name = "name")
    private String name;

    @OneToOne(targetEntity = User.class)
    @JoinColumn(name = "head_of_branch_id" )
    @JsonBackReference
    private User headOfBranch;

    @OneToMany(mappedBy = "studiedBranch")
    @JsonBackReference
    private List<User> students;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "branch_prof", 
      joinColumns = @JoinColumn(name = "branch_id", referencedColumnName = "id"), 
      inverseJoinColumns = @JoinColumn(name = "prof_id", 
      referencedColumnName = "id"))
    private List<User> profs;

    @OneToMany(mappedBy = "branch")
    @JsonManagedReference
    private List<Project> projects;

    @OneToMany(mappedBy = "branch")
    @JsonBackReference
    private List<Assignment> assignments;


}
