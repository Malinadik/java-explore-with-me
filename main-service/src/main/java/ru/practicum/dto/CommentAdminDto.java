package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentAdminDto {
    private Long id;
    private String created;
    private String updated;
    private EventShortDto event;
    private UserShortDto author;
    private String text;
    private Long usefulness = 0L;
}