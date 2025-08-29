package com.agrocrm.domain.material;

import com.agrocrm.security.SecurityUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/materials")
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
    public List<Material> list() { return materials.list(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public Map<String, Object> create(@RequestBody Material m) {
        UUID id = materials.create(m);
        return Map.of("id", id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public void update(@PathVariable UUID id, @RequestBody Material m) { materials.update(id, m); }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable UUID id) { materials.delete(id); }

    @GetMapping("/{materialId}/batches")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','AGRONOMIST')")
    public List<MaterialBatch> batches(@PathVariable UUID materialId) {
        return batches.listByMaterial(materialId);
    }

    @PostMapping("/batches")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT')")
    public Map<String, Object> addBatch(@RequestBody MaterialBatch b) {
        UUID id = batches.create(b);
        return Map.of("id", id);
    }

    @PostMapping("/issue")
    @PreAuthorize("hasAnyRole('ADMIN','AGRONOMIST','ACCOUNTANT')")
    public Map<String, Object> issue(@RequestBody MaterialIssue mi) {
        UUID userId = sec.currentUserIdOrNull();
        UUID id = issues.issue(mi, userId);
        return Map.of("id", id);
    }
}
