package com.pfa.api.app.dto.requests;

import java.util.List;

import lombok.Data;

@Data
public class FilesDTO {
    private List<Long> documentIds;
}
