package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.UserEntryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public User addUser(UserEntryDto entryDto) {
        if (repository.existsByName(entryDto.getName())) {
            throw new ConflictException("Not unique name!");
        }
        return repository.save(User.builder()
                .email(entryDto.getEmail()).name(entryDto.getName()).build());
    }

    public List<User> getUsersById(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return repository.findAll(pageable).toList();
        }
        return repository.findAllByIdIn(ids, pageable);
    }

    public void deleteUser(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
        }
    }
}
