package com.pfa.api.app.controller;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.entity.PresentationsPlan;
import com.pfa.api.app.entity.PresentationsPlan;
import com.pfa.api.app.repository.PresentationsPlanRepository;
import com.pfa.api.app.service.PresentationService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presentations")
@RequiredArgsConstructor
public class PresentationController {

        private final PresentationService presentationService;
        private final PresentationsPlanRepository validPresentationRepository;

        @PostMapping
        @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
        public ResponseEntity<JsonResponse> addNewPresentation(@RequestBody PresentationDTO presentationDTO) {
                PresentationResponseDTO savedPresentation = presentationService.addPresentation(presentationDTO);
                // return ResponseEntity.ok(savedPresentation);
                return new ResponseEntity<JsonResponse>(
                                new JsonResponse(200,
                                                "Presentation Planned successfully!"),
                                HttpStatus.OK);
        }

        @GetMapping
        public ResponseEntity<List<PresentationResponseDTO>> getAllPresentations() {
                return ResponseEntity.ok(presentationService.getAllPresentations());
        }

        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
        public ResponseEntity<JsonResponse> updatePresentation(@PathVariable Long id,
                        @RequestBody PresentationDTO presentationDTO) {
                PresentationResponseDTO updatedPresentation = presentationService.updatePresentation(id,
                                presentationDTO);
                return new ResponseEntity<JsonResponse>(
                                new JsonResponse(200,
                                                "Presentation updated successfully!"),
                                HttpStatus.OK);
        }

        @PostMapping("/plan/validate")
        @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
        public ResponseEntity<JsonResponse> validatePresentationsPlan() {
                presentationService.validatePresentationsPlan();
                return new ResponseEntity<JsonResponse>(
                                new JsonResponse(200,
                                                "Presentations validated successfully!"),
                                HttpStatus.OK);
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
        public ResponseEntity<JsonResponse> deletePresentation(@PathVariable Long id) {
                presentationService.deletePresentation(id);
                return new ResponseEntity<JsonResponse>(
                                new JsonResponse(200,
                                                "Presentation deleted successfully!"),
                                HttpStatus.OK);
        }

        @GetMapping("/plan")
        public ResponseEntity<PresentationsPlan> getPresentationsPlan() throws NotFoundException {
                        String academicYear = "";
            LocalDate currentDate = LocalDate.now();
            int year = currentDate.getYear();
            int month = currentDate.getMonthValue();

            if (month >= 9 && month <= 12) {
                academicYear = year + "/" + (year + 1);
            } else if (month >= 1 && month <= 7) {
                academicYear = (year - 1) + "/" + year;
            }
            return ResponseEntity.ok(presentationService.getPresentationsPlan(academicYear));
        }

        @PostMapping("/plan")
        @PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
        public ResponseEntity<JsonResponse> addPresentationsPlan() {
                presentationService.addPresentationsPlan();
                return new ResponseEntity<JsonResponse>(
                                new JsonResponse(200,
                                                "Presentations Plan added successfully!"),
                                HttpStatus.OK);
        }


}
