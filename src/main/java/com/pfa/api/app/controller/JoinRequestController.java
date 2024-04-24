package com.pfa.api.app.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pfa.api.app.JsonRsponse.JsonResponse;
import com.pfa.api.app.dto.responses.JoinRequestResponseDTO;
import com.pfa.api.app.entity.JoinRequest;
import com.pfa.api.app.service.JoinRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/join-requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_HEAD_OF_BRANCH')")
public class JoinRequestController {
    private final JoinRequestService joinRequestService;

    @GetMapping
    public ResponseEntity<List<JoinRequestResponseDTO>> getAllJoinRequests() throws NotFoundException {
        List<JoinRequestResponseDTO> joinRequests = joinRequestService.getAllJoinRequests().stream()
                .map(JoinRequestResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(joinRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JoinRequestResponseDTO> getJoinRequestById(@PathVariable Long id) throws NotFoundException {
        JoinRequest joinRequest = joinRequestService.getJoinRequestById(id);
        return joinRequest != null ? ResponseEntity.ok(JoinRequestResponseDTO.fromEntity(joinRequest))
                : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<JsonResponse> acceptJoinRequest(@PathVariable Long id) throws NotFoundException {
        joinRequestService.acceptJoinRequest(id);
        return ResponseEntity.ok(new JsonResponse(200, "Join request accepted successfully"));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<JsonResponse> rejectJoinRequest(@PathVariable Long id) throws NotFoundException {
        joinRequestService.rejectJoinRequest(id);
        return ResponseEntity.ok(new JsonResponse(200, "Join request rejected successfully"));
    }

}
