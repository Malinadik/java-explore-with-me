package ru.practicum.controller.pub;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.Sort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SearchEventParams {
    String text;

    List<Long> categories;

    Boolean paid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime rangeEnd;

    Boolean onlyAvailable;

    Sort sort;

    Integer from = 0;

    Integer size = 10;

    HttpServletRequest request;
}
