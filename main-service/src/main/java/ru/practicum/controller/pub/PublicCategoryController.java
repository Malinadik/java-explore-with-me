package ru.practicum.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.Category;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<Category> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from, @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Получение списка категорий публичное");
        Pageable pageable = PageRequest.of(from / size, size);
        return categoryService.getAllCategories(pageable);
    }

    @GetMapping("/{catId}")
    public Category getCategory(@PathVariable Long catId) {
        log.info("Получение конкретной категории: " + catId);
        return categoryService.getCategoryById(catId);
    }
}
