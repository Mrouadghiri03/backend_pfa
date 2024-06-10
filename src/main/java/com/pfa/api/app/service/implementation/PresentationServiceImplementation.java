package com.pfa.api.app.service.implementation;

import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.entity.Presentation;
import com.pfa.api.app.entity.ValidPresentation;
import com.pfa.api.app.repository.PresentationRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.repository.ValidPresentationRepository;
import com.pfa.api.app.service.EmailService;
import com.pfa.api.app.service.PresentationService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pfa.api.app.entity.user.User;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresentationServiceImplementation implements PresentationService {

    private final PresentationRepository presentationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final ValidPresentationRepository validPresentationRepository;

    @Override
    public PresentationResponseDTO addPresentation(PresentationDTO presentationDTO) {
        Presentation presentation = mapToEntity(presentationDTO);
        presentation = presentationRepository.save(presentation);
        for (User jury : presentation.getJuryMembers()) {
            emailService.sendInformingEmail(jury,"You have been assigned as a jury member for a presentation on " 
                + presentation.getStartTime() + " in room " + presentation.getRoomNumber() + " you can now check it and contact the head of branch to change it,check more infos in the app.\n");
        }
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
        return PresentationResponseDTO.fromEntity(presentation);
    }


    @Override
    public void validatePresentations() {
        String academicYear = "";
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue();

            if (month >= 9 && month <= 12) {
                academicYear = year + "/" + (year + 1);
            } else if (month >= 1 && month <= 7) {
                academicYear = (year - 1) + "/" + year;
            }

        ValidPresentation validPresentation = ValidPresentation.builder()
                .academicYear(academicYear)
                .completed(true)
                .build();
        validPresentationRepository.save(validPresentation);
        List<Presentation> presentations = presentationRepository.findAll();
        for (Presentation presentation : presentations) {
            for (User member : presentation.getTeam().getMembers()) {
                emailService.sendInformingEmail(member,
                        "The presentation has been planned successfully for your team on "
                                + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                                + " ,check more infos in the app.\n" + "Good luck!");
            }
            for (User jury : presentation.getJuryMembers()) {
                emailService.sendInformingEmail(jury, "You have been assigned as a jury member for a presentation on "
                        + presentation.getStartTime() + " in room " + presentation.getRoomNumber()
                        + ",check more infos in the app.\n");
            }
        }
    }


	@Override
	public void deletePresentation(Long id) {
        presentationRepository.deleteById(id);
        
	}



}

