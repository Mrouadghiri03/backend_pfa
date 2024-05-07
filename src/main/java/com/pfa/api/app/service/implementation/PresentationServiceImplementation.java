package com.pfa.api.app.service.implementation;

import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.entity.Presentation;
import com.pfa.api.app.repository.PresentationRepository;
import com.pfa.api.app.repository.TeamRepository;
import com.pfa.api.app.repository.UserRepository;
import com.pfa.api.app.service.PresentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.pfa.api.app.entity.user.User;
import java.util.List;

@Service
public class PresentationServiceImplementation implements PresentationService {

    @Autowired
    private PresentationRepository presentationRepository;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private UserRepository userRepository;

    public PresentationDTO addPresentation(PresentationDTO presentationDTO) {
        Presentation presentation = mapToEntity(presentationDTO);
        presentation = presentationRepository.save(presentation);
        return mapToDTO(presentation);
    }

    private Presentation mapToEntity(PresentationDTO dto) {
        Presentation presentation = Presentation.builder()
//                .id(dto.getId())
                .team(teamRepository.findById(dto.getTeamId()).orElseThrow())
                .juryMembers(userRepository.findAllById(dto.getJuryMemberIds()))
                .scheduledDate(dto.getScheduledDate())
                .scheduledTime(dto.getScheduledTime())
                .roomNumber(dto.getRoomNumber())
                .build();
        return presentation;
    }

    private PresentationDTO mapToDTO(Presentation presentation) {
        return PresentationDTO.builder()
//                .id(presentation.getId())
                .teamId(presentation.getTeam().getId())
                .juryMemberIds(presentation.getJuryMembers().stream().map(User::getId).toList())
                .scheduledDate(presentation.getScheduledDate())
                .scheduledTime(presentation.getScheduledTime())
                .roomNumber(presentation.getRoomNumber())
                .build();
    }
}













//
//import com.pfa.api.app.dto.requests.PresentationDTO;
//import com.pfa.api.app.entity.Presentation;
//import com.pfa.api.app.repository.PresentationRepository;
//import com.pfa.api.app.repository.TeamRepository;
//import com.pfa.api.app.repository.UserRepository;
//import com.pfa.api.app.service.PresentationService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class PresentationServiceImplementation implements PresentationService {
//    private final PresentationRepository presentationRepository;
//
//    public Presentation addPresentation(Presentation presentation) {
//        return presentationRepository.save(presentation);
//    }
//
//    public List<Presentation> getAllPresentations(){
//        return presentationRepository.findAll();
//    }
//
//}
