package com.pfa.api.app.entity.user;

import com.pfa.api.app.entity.Project;
import com.pfa.api.app.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Table(name = "team_preferences")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ElementCollection
    @CollectionTable(name = "team_preference_project_rank",
            joinColumns = {@JoinColumn(name = "team_preference_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "project_id")
    @Column(name = "preference_rank", columnDefinition = "int default 0")
    private Map<Long, Integer> projectPreferenceRanks;

}