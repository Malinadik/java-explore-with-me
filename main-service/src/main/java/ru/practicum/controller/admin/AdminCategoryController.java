package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryEntryDto;
import ru.practicum.exception.DuplicateException;
import ru.practicum.model.Category;
import ru.practicum.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@Valid @RequestBody CategoryEntryDto categoryEntryDto) throws DuplicateException {
        log.info("Добавление категории");
        return categoryService.addCategory(categoryEntryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Удаление категории с айди: " + catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public Category updateCategory(@PathVariable Long catId, @Valid @RequestBody CategoryEntryDto categoryEntryDto) throws DuplicateException {
        log.info("Обновление категории с айди: " + catId);
        return categoryService.updateCategory(catId, categoryEntryDto);
    }
}
