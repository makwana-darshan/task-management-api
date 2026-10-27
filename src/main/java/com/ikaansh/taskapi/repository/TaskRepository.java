package com.ikaansh.taskapi.repository;

import com.ikaansh.taskapi.entity.Task;
import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	// Fetch all tasks for a specific user
	List<Task> findByUserIdOrderByDueDateAsc(Long userId);

	// Filter user tasks by status
	List<Task> findByUserIdAndStatusOrderByDueDateAsc(Long userId, TaskStatus status);

	// Filter user tasks by priority
	List<Task> findByUserIdAndPriorityOrderByDueDateAsc(Long userId, TaskPriority priority);

	// Dashboard Reporting: Count user tasks by status
	long countByUserIdAndStatus(Long userId, TaskStatus status);

	// Dashboard Reporting: Count user tasks by priority
	long countByUserIdAndPriority(Long userId, TaskPriority priority);

	// Dashboard Reporting: Count total tasks for user
	long countByUserId(Long userId);

	// Dashboard Reporting: Count overdue tasks for a user (dueDate < today AND
	// status != COMPLETED)
	@Query("SELECT COUNT(t) FROM Task t WHERE t.user.id = :userId AND t.dueDate < :today AND t.status <> 'COMPLETED'")
	long countOverdueTasksByUserId(@Param("userId") Long userId, @Param("today") LocalDate today);

	// Admin Reporting: System-wide count by status
	long countByStatus(TaskStatus status);

	// Admin Reporting: System-wide overdue tasks
	@Query("SELECT COUNT(t) FROM Task t WHERE t.dueDate < :today AND t.status <> 'COMPLETED'")
	long countAllOverdueTasks(@Param("today") LocalDate today);
}