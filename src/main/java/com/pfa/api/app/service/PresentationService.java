package com.pfa.api.app.service;


import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.entity.Presentation;
import com.pfa.api.app.entity.PresentationsPlan;

import java.util.List;

public interface PresentationService {
    PresentationResponseDTO addPresentation(PresentationDTO presentationDTO);
    List<PresentationResponseDTO> getAllPresentations();
    PresentationResponseDTO updatePresentation(Long id, PresentationDTO presentationDTO);
    void deletePresentation(Long id);
    void validatePresentationsPlan();
    void addPresentationsPlan();
    PresentationsPlan getPresentationsPlan(String academicYear);


}
