package com.ikaansh.taskapi.controller;

import com.ikaansh.taskapi.dto.DashboardReportDto;
import com.ikaansh.taskapi.dto.TaskResponseDto;
import com.ikaansh.taskapi.service.ReportService;
import com.ikaansh.taskapi.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

	private final TaskService taskService;
	private final ReportService reportService;

	@GetMapping("/tasks")
	public ResponseEntity<List<TaskResponseDto>> getAllSystemTasks() {
		return ResponseEntity.ok(taskService.getAllTasksForAdmin());
	}

	@GetMapping("/reports/dashboard")
	public ResponseEntity<DashboardReportDto> getSystemDashboard() {
		return ResponseEntity.ok(reportService.getAdminSystemReport());
	}
}