package com.pfa.api.app.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "assignment")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", length = 45)
    private Long id;

    @Column(name = "date")
    private Date date;
    
    @Column(name = "initiated")
    private Boolean initiated;

    @Column(name = "completed")
    private Boolean completed;

    @ManyToOne(targetEntity = Branch.class)
    @JsonManagedReference
    @JoinColumn(name = "branch_id")
    private Branch branch;

    
}
