package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryEntryDto;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.DuplicateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository repository;
    private final EventRepository events;

    public Category addCategory(CategoryEntryDto entryDto) throws DuplicateException {
        validateName(entryDto.getName());
        return repository.save(Category.builder().name(entryDto.getName()).build());
    }

    public Category updateCategory(Long catId, CategoryEntryDto entryDto) throws DuplicateException {
        Category category = repository.findById(catId).orElseThrow(() -> new NotFoundException("Category not found!"));
        if (category.getName().equals(entryDto.getName())) {
            return category;
        }
        validateName(entryDto.getName());
        category.setName(entryDto.getName());
        return repository.save(category);
    }

    public Category getCategoryById(Long catId) {
        return repository.findById(catId).orElseThrow(() -> new NotFoundException("Category not found!"));
    }

    public List<Category> getAllCategories(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public void deleteCategory(Long catId) {
        if (events.existsByCategoryId(catId)) {
            throw new ConflictException("U cant delete it!");
        }
        if (repository.existsById(catId)) {
            repository.deleteById(catId);
        }
    }

    private void validateName(String name) throws DuplicateException {
        if (repository.existsByName(name)) {
            throw new DuplicateException("Such cat. already exists!");
        }
    }
}
