package com.pfa.api.app.service.implementation;

import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.entity.Presentation;
import com.pfa.api.app.repository.PresentationRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pfa.api.app.entity.user.User;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PresentationServiceImplementation implements PresentationService {

    @Autowired
    private PresentationRepository presentationRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public PresentationResponseDTO addPresentation(PresentationDTO presentationDTO) {
        Presentation presentation = mapToEntity(presentationDTO);
        presentation = presentationRepository.save(presentation);
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



}

