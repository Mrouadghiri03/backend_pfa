package com.pfa.api.app.dto.responses;

import java.time.LocalDateTime;

import com.pfa.api.app.entity.Comment;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class CommentResponseDTO {
    private Long id;
    private String text;
    private LocalDateTime  date;
    private Long authorId;
    private String authorName;

    public static CommentResponseDTO fromEntity(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .date(comment.getDate())
                .authorName(comment.getAuthor().getFirstName()+" "+comment.getAuthor().getLastName())
                .authorId(comment.getAuthor().getId())
                .build();
    }
}
