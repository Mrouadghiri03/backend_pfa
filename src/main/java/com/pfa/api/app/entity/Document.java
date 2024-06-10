package com.pfa.api.app.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "doc_name")
    private String docName;

    @Column(name = "file_size")
    private Integer fileSize;

    @ManyToOne(targetEntity = Project.class)
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Project project;

    private Date uploadDate;

    private String uploader;

    @OneToOne(mappedBy = "report")
    @JsonBackReference
    private Project reportOf;


    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @ManyToOne(targetEntity = Folder.class)
    @JoinColumn(name = "folder_id")
    @JsonBackReference
    private Folder folder;

}
