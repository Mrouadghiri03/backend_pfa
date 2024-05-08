package com.pfa.api.app.controller;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.service.PresentationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presentations")
public class PresentationController {

    @Autowired
    private PresentationService presentationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> addNewPresentation(@RequestBody PresentationDTO presentationDTO) {
        PresentationResponseDTO savedPresentation = presentationService.addPresentation(presentationDTO);
//        return ResponseEntity.ok(savedPresentation);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(200,
                        "Presentation Planned successfully!"
                ),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<PresentationResponseDTO>> getAllPresentations() {
        return ResponseEntity.ok(presentationService.getAllPresentations());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
    public ResponseEntity<JsonResponse> updatePresentation(@PathVariable Long id, @RequestBody PresentationDTO presentationDTO) {
        PresentationResponseDTO updatedPresentation = presentationService.updatePresentation(id, presentationDTO);
        return new ResponseEntity<JsonResponse>(
                new JsonResponse(200,
                        "Presentation updated successfully!"
                ),
                HttpStatus.OK
        );
    }
}

