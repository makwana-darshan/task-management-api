package com.ikaansh.taskapi.controller;

import com.ikaansh.taskapi.dto.DashboardReportDto;
import com.ikaansh.taskapi.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardReportDto> getUserDashboard() {
		return ResponseEntity.ok(reportService.getUserDashboardReport());
	}
}