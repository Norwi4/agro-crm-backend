package com.agrocrm.domain.fuel;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fuel")
@Tag(name = "Топливо", description = "API для управления топливными транзакциями и лимитами")
@SecurityRequirement(name = "Bearer Authentication")
public class FuelController {
    private final FuelRepository repo;

    public FuelController(FuelRepository repo) { this.repo = repo; }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    @Operation(
        summary = "Импорт топливных транзакций",
        description = "Загружает список топливных транзакций в систему"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Транзакции успешно импортированы"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные транзакций"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для импорта")
    })
    public void importTransactions(@RequestBody List<FuelTransaction> transactions) {
        repo.bulkInsert(transactions);
    }

    @GetMapping("/alerts/night")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    @Operation(
        summary = "Получить ночные заправки",
        description = "Возвращает список подозрительных ночных заправок для контроля"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список ночных заправок получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Map<String,Object>> nightAlerts() {
        return repo.nightRefuels();
    }

    @PostMapping("/limit")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    @Operation(
        summary = "Установить дневной лимит",
        description = "Устанавливает дневной лимит расхода топлива для топливной карты"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Лимит успешно установлен"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для установки лимита")
    })
    public void setLimit(
        @Parameter(description = "Номер топливной карты") @RequestParam String cardNumber, 
        @Parameter(description = "Дневной лимит в литрах") @RequestParam double dailyLiters, 
        FuelPolicyRepository pol) {
        pol.setDailyLimit(cardNumber, dailyLiters);
    }
}



