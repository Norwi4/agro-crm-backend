package com.agrocrm.domain.fuel;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fuel")
public class FuelController {
    private final FuelRepository repo;

    public FuelController(FuelRepository repo) { this.repo = repo; }

    @PostMapping("/import")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    public void importTransactions(@RequestBody List<FuelTransaction> transactions) {
        repo.bulkInsert(transactions);
    }

    @GetMapping("/alerts/night")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    public List<Map<String,Object>> nightAlerts() {
        return repo.nightRefuels();
    }

    @PostMapping("/limit")
    @PreAuthorize("hasAnyRole('ADMIN','ACCOUNTANT','MANAGER')")
    public void setLimit(@RequestParam String cardNumber, @RequestParam double dailyLiters, FuelPolicyRepository pol) {
        pol.setDailyLimit(cardNumber, dailyLiters);
    }
}



