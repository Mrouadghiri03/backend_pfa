//package com.pfa.api.app.entity;
//
//import com.pfa.api.app.entity.user.User;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.Date;
//import java.util.List;
//
//@Table
//@Entity
//@Builder
//@Setter
//@Getter
//@NoArgsConstructor
//@AllArgsConstructor
//public class Presentation {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id")
//    private Long id;
//
//    @OneToOne(cascade = CascadeType.ALL)
//    private Team team;
//
//    @ManyToMany
//    @JoinTable(
//            name ="presentation_supervisors"
//            ,joinColumns = {
//                @JoinColumn(name = "presentation_id",referencedColumnName = "id")
//            }
//            ,inverseJoinColumns = {
//                @JoinColumn(name = "supervisor_id",referencedColumnName = "id")
//            }
//    )
//    private List<User> juryMembers;
//
//
//
//    @Column(name = "scheduled_date")
//    private LocalDate scheduledDate;
//
//
//    @Column(name = "scheduled_time")
//    private LocalTime scheduledTime;
//
//    @Column(name = "room_number")
//    private String roomNumber;
//
//
//}


package com.pfa.api.app.entity;

import com.pfa.api.app.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "presentations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany
    @JoinTable(
            name = "presentation_jury",
            joinColumns = @JoinColumn(name = "presentation_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> juryMembers;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    @Column(name = "room_number")
    private String roomNumber;
}
