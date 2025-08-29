package com.agrocrm.domain.maintenance;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenance")
public class MaintenanceController {

    private final MaintenanceRepository repo;

    public MaintenanceController(MaintenanceRepository repo) { this.repo = repo; }

    @PostMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public Map<String, Object> create(@RequestBody MaintenanceOrder o) {
        UUID id = repo.create(o);
        return Map.of("id", id);
    }

    @PostMapping("/orders/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC')")
    public void setStatus(@PathVariable UUID id, @RequestParam String status) {
        repo.setStatus(id, status);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasAnyRole('ADMIN','MECHANIC','MANAGER')")
    public List<MaintenanceOrder> list(@RequestParam(required = false) String status) {
        return repo.list(status);
    }
}
