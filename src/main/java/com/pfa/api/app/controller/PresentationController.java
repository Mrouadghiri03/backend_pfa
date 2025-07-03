package com.pfa.api.app.controller;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.requests.PresentationDTO;
import com.pfa.api.app.dto.responses.PresentationResponseDTO;
import com.pfa.api.app.dto.responses.ProjectResponseDTO;
import com.pfa.api.app.dto.responses.TeamResponseDTO;
import com.pfa.api.app.dto.responses.UserResponseDTO;
import com.pfa.api.app.entity.PresentationsPlan;
import com.pfa.api.app.entity.PresentationsPlan;
import com.pfa.api.app.entity.Team;
import com.pfa.api.app.entity.user.User;
import com.pfa.api.app.repository.PresentationsPlanRepository;
import com.pfa.api.app.service.PresentationService;

import com.pfa.api.app.service.implementation.TeamServiceImplementation;
import com.pfa.api.app.service.implementation.UserServiceImplementation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
       // @Autowired
        private final TeamServiceImplementation teamServiceImplementation;
        private  final UserServiceImplementation userServiceImplementation;


    @PostMapping
    public ResponseEntity<?> addNewPresentation(@RequestBody PresentationDTO dto) {
        try {
            System.out.println("Début de création - DTO: " + dto);
            PresentationResponseDTO saved = presentationService.addPresentation(dto);
            System.out.println("Présentation sauvegardée en base: " + saved.getId());



            return ResponseEntity.ok(new JsonResponse(200, "Success"));
        } catch (Exception e) {
            System.out.println("Erreur après insertion: " + e.getMessage());
            throw e;
        }
    }
   /* @PostMapping("/generate-presentations")
    public ResponseEntity<?> generatePresentations() {
        // Configuration des dates/heures
        LocalDateTime startLdt = LocalDateTime.of(2025, 1, 7, 8, 0);
        LocalDateTime endLdt = LocalDateTime.of(2025, 1, 7, 10, 0);

        // Conversion en Date
        Date startTime = Date.from(startLdt.atZone(ZoneId.systemDefault()).toInstant());
        Date endTime = Date.from(endLdt.atZone(ZoneId.systemDefault()).toInstant());

        // Récupération du jury
        UserResponseDTO jury = userServiceImplementation.getUserById(2L);

        try {
            List<TeamResponseDTO> teams = teamServiceImplementation.getAllTeams("2024/2025");

            for (TeamResponseDTO team : teams) {
                System.out.println("Team ID: " + team.getId() + ", Name: " + team.getName());

                // Création du DTO correct (PresentationDTO)
                PresentationDTO presentationDTO = PresentationDTO.builder()
                        .teamId(team.getId())
                        .juryMemberIds(List.of(2L)) // ID du jury récupéré précédemment
                        .startTime(startTime)
                        .endTime(endTime)
                        .roomNumber("BR49")
                        .build();

                // Appel du service avec le bon type
                presentationService.addPresentation(presentationDTO);
            }

            return ResponseEntity.ok(new JsonResponse(200, "Presentations generated successfully"));
        } catch (Exception e) {
            System.out.println("Erreur lors de la génération: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new JsonResponse(500, "Error generating presentations"));
        }
    }

    */
    /*@PostMapping("/generate-presentations")
    public ResponseEntity<?> generatePresentations() {
        // Heure de départ initiale
        LocalDateTime currentStart = LocalDateTime.of(2025, 7, 7, 8, 0); // 07/07/2025 08:00
        LocalDateTime currentEnd = currentStart.plusHours(2); // Durée fixe de 2h

        List<TeamResponseDTO> teams = teamServiceImplementation.getAllTeams("2024/2025");
        List<Long> createdIds = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (TeamResponseDTO team : teams) {
            try {
                // Conversion en Date
                ProjectResponseDTO projectResponseDTO=new ProjectResponseDTO();
                projectResponseDTO=team.getProject();
                List<Long> juryMembersIds=new ArrayList<>();
                juryMembersIds.addAll(projectResponseDTO.getSupervisorIds());
                juryMembersIds.addAll(userServiceImplementation.getHeadsOFBranchIds());
                Date startTime = Date.from(currentStart.atZone(ZoneId.systemDefault()).toInstant());
                Date endTime = Date.from(currentEnd.atZone(ZoneId.systemDefault()).toInstant());

                PresentationDTO dto = PresentationDTO.builder()
                        .teamId(team.getId())
                        .juryMemberIds(juryMembersIds)
                        .startTime(startTime)
                        .endTime(endTime)
                        .roomNumber("BR49") // Salle fixe
                        .build();

                PresentationResponseDTO created = presentationService.addPresentation(dto);
                createdIds.add(created.getId());

                // Décalage de 2h pour la prochaine présentation
                currentStart = currentStart.plusHours(2);
                currentEnd = currentEnd.plusHours(2);

            } catch (Exception e) {
                errors.add("Équipe " + team.getId() + ": " + e.getMessage());
                // En cas d'erreur, on continue avec le même créneau suivant
                currentStart = currentStart.plusHours(2);
                currentEnd = currentEnd.plusHours(2);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("createdPresentations", createdIds.size());
        response.put("errors", errors);
        response.put("lastScheduledTime", currentEnd.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return ResponseEntity.ok(response);
    }

     */
   @PostMapping("/generate-presentations")
   public ResponseEntity<?> generatePresentations() {
       // Configuration des horaires
       LocalTime[] timeSlots = {
               LocalTime.of(10, 0), LocalTime.of(12, 0),  // Matin: 10h-12h
               LocalTime.of(14, 0), LocalTime.of(16, 0),  // Après-midi: 14h-16h
               LocalTime.of(16, 0), LocalTime.of(18, 0)   // Fin d'après-midi: 16h-18h
       };

       LocalDate currentDate = LocalDate.of(2025, 7, 7); // Date initiale
       int slotIndex = 0; // Index pour suivre le créneau actuel

       List<TeamResponseDTO> teams = teamServiceImplementation.getAllTeams("2024/2025");
       List<Long> createdIds = new ArrayList<>();
       List<String> errors = new ArrayList<>();

       for (TeamResponseDTO team : teams) {
           try {
               // Calcul du créneau actuel
               int startIndex = slotIndex % 6;
               int endIndex = startIndex + 1;

               LocalDateTime currentStart = LocalDateTime.of(currentDate, timeSlots[startIndex]);
               LocalDateTime currentEnd = LocalDateTime.of(currentDate, timeSlots[endIndex]);

               // Préparation des données
               ProjectResponseDTO project = team.getProject();
               List<Long> juryIds = new ArrayList<>();
               juryIds.addAll(project.getSupervisorIds());
               juryIds.addAll(userServiceImplementation.getHeadsOFBranchIds());

               PresentationDTO dto = PresentationDTO.builder()
                       .teamId(team.getId())
                       .juryMemberIds(juryIds)
                       .startTime(Date.from(currentStart.atZone(ZoneId.systemDefault()).toInstant()))
                       .endTime(Date.from(currentEnd.atZone(ZoneId.systemDefault()).toInstant()))
                       .roomNumber("BR49")
                       .build();

               // Création de la présentation
               PresentationResponseDTO created = presentationService.addPresentation(dto);
               createdIds.add(created.getId());

               // Passage au créneau suivant
               slotIndex += 2; // Avancer de 2 index (début et fin)

               // Si on a fait tous les créneaux de la journée (3 créneaux * 2 = 6)
               if (slotIndex >= 6) {
                   slotIndex = 0;
                   currentDate = currentDate.plusDays(1);
               }

           } catch (Exception e) {
               errors.add("Équipe " + team.getId() + ": " + e.getMessage());
               // Passer au créneau suivant même en cas d'erreur
               slotIndex += 2;
               if (slotIndex >= 6) {
                   slotIndex = 0;
                   currentDate = currentDate.plusDays(1);
               }
           }
       }

       // Construction de la réponse
       Map<String, Object> response = new HashMap<>();
       response.put("createdPresentations", createdIds.size());
       response.put("errors", errors);
       response.put("nextAvailableSlot", getNextSlotInfo(currentDate, slotIndex, timeSlots));

       return ResponseEntity.ok(response);
   }

    private String getNextSlotInfo(LocalDate date, int slotIndex, LocalTime[] slots) {
        if (slotIndex >= 6) {
            slotIndex = 0;
            date = date.plusDays(1);
        }
        return date.toString() + " " +
                slots[slotIndex].toString() + "-" +
                slots[slotIndex+1].toString();
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
