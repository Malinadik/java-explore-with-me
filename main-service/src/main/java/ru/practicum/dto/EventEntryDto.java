package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Location;
import ru.practicum.validator.CorrectDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventEntryDto {
    @NotNull
    @Size(max = 2000, min = 20)
    private String annotation;
    @NotNull
    @Size(max = 120, min = 3)
    private String title;
    @NotNull
    @Size(max = 7000, min = 20)
    private String description;
    @NotNull
    private Long category;

    @NotNull
    private Location location;
    private Long participantLimit;
    @CorrectDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Boolean requestModeration;
    private Boolean paid;
}
