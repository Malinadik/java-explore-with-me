package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {
    public static UserShortDto toShortDto(User user) {
        return UserShortDto.builder().id(user.getId()).name(user.getName()).build();
    }
}
