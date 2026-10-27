package com.ikaansh.taskapi.service;

import com.ikaansh.taskapi.dto.DashboardReportDto;
import com.ikaansh.taskapi.entity.User;
import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import com.ikaansh.taskapi.repository.TaskRepository;
import com.ikaansh.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;
	private final TaskService taskService;

	// 1. User-Specific Dashboard Report
	@Transactional(readOnly = true)
	public DashboardReportDto getUserDashboardReport() {
		User currentUser = taskService.getAuthenticatedUser();
		Long userId = currentUser.getId();

		long totalTasks = taskRepository.countByUserId(userId);
		long pendingCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
		long inProgressCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
		long completedCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
		long overdueCount = taskRepository.countOverdueTasksByUserId(userId, LocalDate.now());

		Map<String, Long> byStatus = new LinkedHashMap<>();
		byStatus.put(TaskStatus.PENDING.name(), pendingCount);
		byStatus.put(TaskStatus.IN_PROGRESS.name(), inProgressCount);
		byStatus.put(TaskStatus.COMPLETED.name(), completedCount);

		Map<String, Long> byPriority = new LinkedHashMap<>();
		byPriority.put(TaskPriority.LOW.name(), taskRepository.countByUserIdAndPriority(userId, TaskPriority.LOW));
		byPriority.put(TaskPriority.MEDIUM.name(),
				taskRepository.countByUserIdAndPriority(userId, TaskPriority.MEDIUM));
		byPriority.put(TaskPriority.HIGH.name(), taskRepository.countByUserIdAndPriority(userId, TaskPriority.HIGH));

		double completionRate = (totalTasks > 0) ? Math.round(((double) completedCount / totalTasks) * 10000.0) / 100.0
				: 0.0;

		return DashboardReportDto.builder().totalTasks(totalTasks).overdueTasks(overdueCount)
				.completionRatePercentage(completionRate).tasksByStatus(byStatus).tasksByPriority(byPriority).build();
	}

	// 2. Admin System-Wide Dashboard Report
	@Transactional(readOnly = true)
	public DashboardReportDto getAdminSystemReport() {
		long totalUsers = userRepository.count();
		long totalTasks = taskRepository.count();
		long pendingCount = taskRepository.countByStatus(TaskStatus.PENDING);
		long inProgressCount = taskRepository.countByStatus(TaskStatus.IN_PROGRESS);
		long completedCount = taskRepository.countByStatus(TaskStatus.COMPLETED);
		long overdueCount = taskRepository.countAllOverdueTasks(LocalDate.now());

		Map<String, Long> byStatus = new LinkedHashMap<>();
		byStatus.put(TaskStatus.PENDING.name(), pendingCount);
		byStatus.put(TaskStatus.IN_PROGRESS.name(), inProgressCount);
		byStatus.put(TaskStatus.COMPLETED.name(), completedCount);

		double completionRate = (totalTasks > 0) ? Math.round(((double) completedCount / totalTasks) * 10000.0) / 100.0
				: 0.0;

		return DashboardReportDto.builder().totalUsers(totalUsers).totalTasks(totalTasks).overdueTasks(overdueCount)
				.completionRatePercentage(completionRate).tasksByStatus(byStatus).build();
	}
}