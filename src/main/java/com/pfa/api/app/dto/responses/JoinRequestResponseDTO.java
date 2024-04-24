package com.pfa.api.app.dto.responses;

import com.pfa.api.app.entity.JoinRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestResponseDTO {
    private Long id;
    private UserResponseDTO user;
    private Long branch;

    public static JoinRequestResponseDTO fromEntity(JoinRequest joinRequest) {
        return JoinRequestResponseDTO.builder()
                .id(joinRequest.getId())
                .user(UserResponseDTO.fromEntity(joinRequest.getUser()))
                .branch(joinRequest.getBranch().getId())
                .build();
    }
}
