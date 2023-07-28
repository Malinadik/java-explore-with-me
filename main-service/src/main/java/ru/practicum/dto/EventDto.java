package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.model.Category;
import ru.practicum.model.EventState;
import ru.practicum.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    private Long id;
    private String annotation;

    private String title;
    private String description;

    private Category category;
    private Location location;

    private Long participantLimit;

    private Long confirmedRequests;

    private String eventDate;

    private String createdOn;
    private String publishedOn;

    private UserShortDto initiator;

    private Boolean paid;

    private Boolean requestModeration;

    private EventState state;

    private Long views;

}
