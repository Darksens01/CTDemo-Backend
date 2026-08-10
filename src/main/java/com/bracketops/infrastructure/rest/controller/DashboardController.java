package com.bracketops.infrastructure.rest.controller;

import com.bracketops.application.dto.DashboardMetricsDto;
import com.bracketops.application.query.handler.DashboardQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard & Analytics", description = "Platform KPIs & Tournament Metrics REST API")
public class DashboardController {

    private final DashboardQueryHandler queryHandler;

    public DashboardController(DashboardQueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    @GetMapping("/metrics")
    @Operation(summary = "Get overall esports platform KPIs, match counts, and notification stats")
    public ResponseEntity<DashboardMetricsDto> getDashboardMetrics() {
        return ResponseEntity.ok(queryHandler.getMetrics());
    }
}
