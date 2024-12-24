package com.example.backspark.controller;

import com.example.backspark.model.ISocks;
import com.example.backspark.model.SocksTransaction;
import com.example.backspark.service.SocksService;
import com.example.backspark.util.CSVHelper;
import com.example.backspark.util.ComparisonOperator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Контроллер для учета носков на складе магазина")
@Validated
@RestController
@RequiredArgsConstructor
public class SocksController {

    private final SocksService socksService;

    @Operation(summary = "Получение общего количества носков")
    @GetMapping("/api/socks")
    public Integer countAll(
            @RequestParam(defaultValue = Strings.EMPTY) @Parameter(description = "Цвет носков") String color,
            @RequestParam(defaultValue = "MORE_THAN") @Parameter(description = "Оператор сравнения") ComparisonOperator operator,
            @RequestParam(defaultValue = "-1") @Parameter(description = "Содержание хлопка") Integer cottonPart) {
        return socksService.countAll(color, operator, cottonPart);
    }

    @Operation(summary = "Регистрация прихода носков")
    @PostMapping("/api/socks/income")
    public SocksTransaction doIncome(
            @RequestParam @NotBlank @Parameter(description = "Цвет носков") String color,
            @RequestParam @Min(0) @Max(100) @Parameter(description = "Содержание хлопка") Integer cottonPart,
            @RequestParam @Min(1) @Parameter(description = "Количество") Integer quantity) {
        return socksService.doIncome(color, cottonPart, quantity);
    }

    @Operation(summary = "Регистрация расхода носков")
    @PostMapping("/api/socks/outcome")
    public SocksTransaction doOutcome(
            @RequestParam @NotBlank @Parameter(description = "Цвет носков") String color,
            @RequestParam @Min(0) @Max(100) @Parameter(description = "Содержание хлопка") Integer cottonPart,
            @RequestParam @Max(-1) @Parameter(description = "Количество") Integer quantity) {
        return socksService.doOutcome(color, cottonPart, quantity);
    }

    @Operation(summary = "Обновление прихода/расхода")
    @PutMapping("/api/socks/{id}")
    public SocksTransaction update(
            @PathVariable @Parameter(description = "Id операции") Long id,
            @RequestParam @NotBlank @Parameter(description = "Цвет носков") String color,
            @RequestParam @Min(0) @Max(100) @Parameter(description = "Содержание хлопка") Integer cottonPart,
            @RequestParam @Parameter(description = "Количество") Integer quantity) {
        return socksService.update(id, color, cottonPart, quantity);
    }

    @Operation(summary = "Поиск носков")
    @GetMapping("/api/socks/list")
    public List<ISocks> findAll(
            @RequestParam(defaultValue = "0") @Parameter(description = "Минимальное содержаие хлопка") Integer minCottonPart,
            @RequestParam(defaultValue = "100") @Parameter(description = "Максимальное содержаие хлопка") Integer maxCottonPart,
            @RequestParam(defaultValue = "count") @Parameter(description = "Поле для сортировки") String sortBy,
            @RequestParam(defaultValue = "ASC") @Parameter(description = "Направление сортировки") Sort.Direction sortDirection
    ) {
        return socksService.findAll(minCottonPart, maxCottonPart, sortBy, sortDirection);
    }

    @Operation(summary = "Загрузка носков из файла")
    @PostMapping("/api/socks/batch")
    public void batchInsert(@RequestParam("file") MultipartFile file) {
        socksService.batchInsert(file);
    }
}