package com.ikaansh.taskapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportDto {
	private Long totalUsers; 
	private long totalTasks;
	private long overdueTasks;
	private double completionRatePercentage;
	private Map<String, Long> tasksByStatus;
	private Map<String, Long> tasksByPriority;
}