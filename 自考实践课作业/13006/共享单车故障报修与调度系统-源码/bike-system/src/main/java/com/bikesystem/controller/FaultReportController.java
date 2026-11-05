package com.bikesystem.controller;

import com.bikesystem.dto.ApiResponse;
import com.bikesystem.model.FaultReport;
import com.bikesystem.service.FaultReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fault")
public class FaultReportController {

    private final FaultReportService faultReportService;

    public FaultReportController(FaultReportService faultReportService) {
        this.faultReportService = faultReportService;
    }

    @PostMapping
    public ApiResponse<FaultReport> create(@RequestBody FaultReport report) {
        return ApiResponse.success(faultReportService.create(report));
    }

    @GetMapping("/list")
    public ApiResponse<List<FaultReport>> list() {
        return ApiResponse.success(faultReportService.listPending());
    }
}
