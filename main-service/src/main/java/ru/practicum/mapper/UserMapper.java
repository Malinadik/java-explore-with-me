package ru.practicum.mapper;

import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

public class UserMapper {
    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder().id(user.getId()).name(user.getName()).build();
    }
}
