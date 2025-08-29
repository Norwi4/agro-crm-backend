package com.agrocrm.domain.material;

import com.agrocrm.security.SecurityUtil;
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
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
@Tag(name = "Материалы", description = "API для управления материалами и их выдачей")
@SecurityRequirement(name = "Bearer Authentication")
public class MaterialController {
    private final MaterialRepository materials;
    private final MaterialBatchRepository batches;
    private final MaterialIssueRepository issues;
    private final SecurityUtil sec;

    public MaterialController(MaterialRepository materials, MaterialBatchRepository batches, MaterialIssueRepository issues, SecurityUtil sec) {
        this.materials = materials;
        this.batches = batches;
        this.issues = issues;
        this.sec = sec;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','ACCOUNTANT','MANAGER')")
    @Operation(
        summary = "Получить список материалов",
        description = "Возвращает список всех материалов в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список материалов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Material> list() { return materials.list(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    @Operation(
        summary = "Создать новый материал",
        description = "Создает новый материал в системе"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Материал успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные материала"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@RequestBody Material m) {
        UUID id = materials.create(m);
        return Map.of("id", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    @Operation(
        summary = "Обновить материал",
        description = "Обновляет информацию о существующем материале"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Материал успешно обновлен"),
        @ApiResponse(responseCode = "404", description = "Материал не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для обновления")
    })
    public void update(@Parameter(description = "ID материала") @PathVariable UUID id, @RequestBody Material m) { 
        materials.update(id, m); 
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(
        summary = "Удалить материал",
        description = "Удаляет материал из системы (только для администраторов)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Материал успешно удален"),
        @ApiResponse(responseCode = "404", description = "Материал не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для удаления")
    })
    public void delete(@Parameter(description = "ID материала") @PathVariable UUID id) { 
        materials.delete(id); 
    }

    @GetMapping("/{materialId}/batches")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','AGRONOMIST')")
    @Operation(
        summary = "Получить партии материала",
        description = "Возвращает список всех партий конкретного материала"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список партий успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<MaterialBatch> batches(@Parameter(description = "ID материала") @PathVariable UUID materialId) {
        return batches.listByMaterial(materialId);
    }

    @PostMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    @Operation(
        summary = "Добавить партию материала",
        description = "Создает новую партию материала с указанием количества и даты"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Партия успешно добавлена"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные партии"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> addBatch(@RequestBody MaterialBatch b) {
        UUID id = batches.create(b);
        return Map.of("id", id);
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','ACCOUNTANT')")
    @Operation(
        summary = "Выдать материал",
        description = "Оформляет выдачу материала с указанием количества и получателя"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Материал успешно выдан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные выдачи"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для выдачи")
    })
    public Map<String, Object> issue(@RequestBody MaterialIssue mi) {
        UUID userId = sec.currentUserIdOrNull();
        UUID id = issues.issue(mi, userId);
        return Map.of("id", id);
    }
}
