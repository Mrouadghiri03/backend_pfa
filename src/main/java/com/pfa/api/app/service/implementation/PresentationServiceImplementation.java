package com.pfa.api.app.service.implementation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.entity.Notification;
import com.pfa.api.app.entity.Presentation;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.PresentationsPlan;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.NotificationRepository;
import com.pfa.api.app.repository.PresentationRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.repository.PresentationsPlanRepository;
import com.pfa.api.app.service.EmailService;
import com.pfa.api.app.service.PresentationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PresentationServiceImplementation implements PresentationService {

    private final PresentationRepository presentationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PresentationsPlanRepository PresentationsPlanRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public PresentationResponseDTO addPresentation(PresentationDTO presentationDTO) {
        Presentation presentation = mapToEntity(presentationDTO);
        presentation = presentationRepository.save(presentation);
        Team team = presentation.getTeam();
        team.setPresentation(presentation);
        teamRepository.save(team);
        Long idOfSender = team.getMembers().get(0).getStudiedBranch().getHeadOfBranch().getId();
        for (User jury : presentation.getJuryMembers()) {
            Notification notification = Notification.builder()
                    .user(jury)
                    .creationDate(new Date())
                    .type("PROJECT")
                    .description("You have been assigned as a jury member for a presentation on "
                            + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                            + " you can now check it and contact the head of branch to change it,check more infos in the calendar.\n")
                    .idOfSender(idOfSender)
                    .build();

            notificationRepository.save(notification);
            emailService.sendInformingEmail(jury, "You have been assigned as a jury member for a presentation on "
                    + presentation.getStartTime() + " in room " + presentation.getRoomNumber()

                    + " you can now check it and contact the head of branch to change it,check more infos in the app.\n");
            System.out.println("start: "+presentation.getStartTime()+" end:"+presentation.getEndTime());
        }
//au cas ou w93 mochkil f backend 7aydo hadi dyal tsift email l team memebers ; 7it ana ann3s dorki w 3yit mafia li ytest
        List<User> currentMembers = presentation.getTeam().getMembers();
        for (User member : currentMembers) {
            Notification notification = Notification.builder()
                    .user(member)
                    .creationDate(new Date())
                    .type("PROJECT")
                    .description("You have been assigned as a jury member for a presentation on "
                            + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                            + " you can now check it and contact the head of branch to change it,check more infos in the calendar.\n")
                    .idOfSender(idOfSender)
                    .build();

            notificationRepository.save(notification);
            emailService.sendInformingEmail(member, "You have been assigned as a jury member for a presentation on "
                    + presentation.getStartTime() + " in room " + presentation.getRoomNumber()

                    + " check more infos in the app.\n");
            System.out.println("start: "+presentation.getStartTime()+" end:"+presentation.getEndTime());
        }
        // hna fou9 au cas ou kan mochkil 7aydoha khliw email ghir l jury

        return PresentationResponseDTO.fromEntity(presentation);
    }

    @Override
    public List<PresentationResponseDTO> getAllPresentations() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        System.out.println("Year: " + year);
        List<Presentation> presentations = presentationRepository.findAllByYear(year).orElseThrow();
        return presentations.stream().map(PresentationResponseDTO::fromEntity).toList();
    }

    private Presentation mapToEntity(PresentationDTO dto) {
        Presentation presentation = Presentation.builder()
                .team(teamRepository.findById(dto.getTeamId()).orElseThrow())
                .juryMembers(userRepository.findAllById(dto.getJuryMemberIds()))
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .roomNumber(dto.getRoomNumber())
                .build();
        return presentation;
    }

    @Override
    public PresentationResponseDTO updatePresentation(Long id, PresentationDTO presentationDTO) {
        Presentation presentation = presentationRepository.findById(id).orElseThrow();
        if (presentationDTO.getTeamId() != null) {
            presentation.setTeam(teamRepository.findById(presentationDTO.getTeamId()).orElseThrow());
        }
        if (presentationDTO.getJuryMemberIds() != null) {
            List<User> juryMembers = userRepository.findAllById(presentationDTO.getJuryMemberIds());
            presentation.setJuryMembers(juryMembers);
        }
        if (presentationDTO.getStartTime() != null) {
            presentation.setStartTime(presentationDTO.getStartTime());
        }
        if (presentationDTO.getEndTime() != null) {
            presentation.setEndTime(presentationDTO.getEndTime());
        }
        if (presentationDTO.getRoomNumber() != null) {
            presentation.setRoomNumber(presentationDTO.getRoomNumber());
        }
        presentation = presentationRepository.save(presentation);
        Team team = presentation.getTeam();
        Long idOfSender = team.getMembers().get(0).getStudiedBranch().getHeadOfBranch().getId();

        for (User jury : presentation.getJuryMembers()) {
            Notification notification = Notification.builder()
                    .user(jury)
                    .creationDate(new Date())
                    .type("PROJECT")
                    .description("You have been assigned as a jury member for a presentation on "
                            + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                            + " you can now check it and contact the head of branch to change it,check more infos in the calendar.\n")
                    .idOfSender(idOfSender)
                    .build();

            notificationRepository.save(notification);
            emailService.sendInformingEmail(jury, "You have been assigned as a jury member for a presentation on "
                    + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                    + " you can now check it and contact the head of branch to change it,check more infos in the app.\n");
        }
        return PresentationResponseDTO.fromEntity(presentation);
    }

    @Override
    public void validatePresentationsPlan() {
        String academicYear = "";
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        if (month >= 9 && month <= 12) {
            academicYear = year + "/" + (year + 1);
        } else if (month >= 1 && month <= 7) {
            academicYear = (year - 1) + "/" + year;
        }

        PresentationsPlan presentationsPlan = PresentationsPlanRepository.findByAcademicYear(academicYear)
                .orElseThrow();
        presentationsPlan.setCompleted(true);
        PresentationsPlanRepository.save(presentationsPlan);
        List<Presentation> presentations = presentationRepository.findAll();
        for (Presentation presentation : presentations) {
            Team team = presentation.getTeam();
            Long idOfSender = team.getMembers().get(0).getStudiedBranch().getHeadOfBranch().getId();
            for (User member : presentation.getTeam().getMembers()) {
                Notification notification = Notification.builder()
                        .user(member)
                        .creationDate(new Date())
                        .type("TEAM")
                        .idOfSender(member.getStudiedBranch().getHeadOfBranch().getId())
                        .description("The presentation has been planned successfully for your team on "
                                + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                                + " ,check more infos in the calendar.\n" + "Good luck!")
                        .build();
                notificationRepository.save(notification);
                emailService.sendInformingEmail(member,
                        "The presentation has been planned successfully for your team on "
                                + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                                + " ,check more infos in the app.\n" + "Good luck!");
            }
            for (User jury : presentation.getJuryMembers()) {
                Notification notification = Notification.builder()
                        .user(jury)
                        .creationDate(new Date())
                        .type("PROJECT")
                        .description("You have been assigned as a jury member for a presentation on "
                                + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                                + " ,check more infos in the calendar.\n")
                        .idOfSender(idOfSender)
                        .build();
                notificationRepository.save(notification);
                emailService.sendInformingEmail(jury, "You have been assigned as a jury member for a presentation on "

                        + ",check more infos in the app.\n");
            }
        }
    }

    @Override
    public void deletePresentation(Long id) {
        Presentation presentation = presentationRepository.findById(id).orElseThrow();
        Team team = presentation.getTeam();
        team.setPresentation(null);
        presentationRepository.deleteById(id);

    }

    @Override
    public void addPresentationsPlan() {
        String academicYear = "";
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();

        if (month >= 9 && month <= 12) {
            academicYear = year + "/" + (year + 1);
        } else if (month >= 1 && month <= 7) {
            academicYear = (year - 1) + "/" + year;
        }
        PresentationsPlan presentationsPlan = PresentationsPlan.builder()
                .academicYear(academicYear)
                .completed(false)
                .build();
        PresentationsPlanRepository.save(presentationsPlan);
    }

    @Override
    public PresentationsPlan getPresentationsPlan(String academicYear) {
        return PresentationsPlanRepository.findByAcademicYear(academicYear).orElseGet(null);
    }

}
