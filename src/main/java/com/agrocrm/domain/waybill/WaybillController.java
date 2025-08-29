package com.agrocrm.domain.waybill;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/waybills")
@Tag(name = "Путевые листы", description = "API для управления путевыми листами техники")
@SecurityRequirement(name = "Bearer Authentication")
public class WaybillController {
    private static final Logger log = LoggerFactory.getLogger(WaybillController.class);

    private final WaybillRepository repo;

    public WaybillController(WaybillRepository repo) { this.repo = repo; }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC','AGRONOMIST')")
    @Operation(
        summary = "Создать путевой лист",
        description = "Создает новый путевой лист для техники"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Путевой лист успешно создан"),
        @ApiResponse(responseCode = "400", description = "Некорректные данные путевого листа"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для создания")
    })
    public Map<String, Object> create(@Valid @RequestBody Waybill w) {
        try {
            if (w.getStartTs() == null) w.setStartTs(OffsetDateTime.now());
            UUID id = repo.create(w);
            log.info("Created waybill: id={}", id);
            return Map.of("id", id);
        } catch (Exception e) {
            log.error("Failed to create waybill", e);
            throw e;
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC','AGRONOMIST','ACCOUNTANT','MANAGER')")
    @Operation(
        summary = "Получить список путевых листов",
        description = "Возвращает список путевых листов с возможностью фильтрации по статусу"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список путевых листов успешно получен"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для доступа")
    })
    public List<Waybill> list(
        @Parameter(description = "Статус путевого листа для фильтрации") @RequestParam(value = "status", required = false) String status) {
        try {
            List<Waybill> waybills = repo.list(status);
            log.debug("Found {} waybills: status={}", waybills.size(), status);
            return waybills;
        } catch (Exception e) {
            log.error("Failed to list waybills: status={}", status, e);
            throw e;
        }
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    @Operation(
        summary = "Закрыть путевой лист",
        description = "Завершает путевой лист и фиксирует конечные показатели"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Путевой лист успешно закрыт"),
        @ApiResponse(responseCode = "404", description = "Путевой лист не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для закрытия")
    })
    public void close(@Parameter(description = "ID путевого листа") @PathVariable("id") UUID id, @Valid @RequestBody Waybill w) {
        try {
            repo.close(id, w);
            log.info("Closed waybill: id={}", id);
        } catch (Exception e) {
            log.error("Failed to close waybill: id={}", id, e);
            throw e;
        }
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER','MECHANIC')")
    @Operation(
        summary = "Скачать путевой лист в PDF",
        description = "Генерирует и возвращает путевой лист в формате PDF"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF файл успешно сгенерирован"),
        @ApiResponse(responseCode = "404", description = "Путевой лист не найден"),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав для скачивания"),
        @ApiResponse(responseCode = "500", description = "Ошибка генерации PDF")
    })
    public ResponseEntity<byte[]> pdf(@PathVariable("id") UUID id) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();
            doc.add(new Paragraph("Путевой лист № " + id));
            doc.add(new Paragraph("Генерация демонстрационная (OpenPDF)"));
            doc.close();
            byte[] bytes = baos.toByteArray();
            log.info("Generated PDF for waybill: id={}, size={} bytes", id, bytes.length);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=waybill-" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(bytes);
        } catch (Exception e) {
            log.error("Failed to generate PDF for waybill: id={}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
