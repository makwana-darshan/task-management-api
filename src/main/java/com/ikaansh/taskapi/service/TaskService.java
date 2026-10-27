package com.ikaansh.taskapi.service;

import com.ikaansh.taskapi.dto.TaskRequestDto;
import com.ikaansh.taskapi.dto.TaskResponseDto;
import com.ikaansh.taskapi.entity.Task;
import com.ikaansh.taskapi.entity.User;
import com.ikaansh.taskapi.enums.Role;
import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import com.ikaansh.taskapi.exception.ResourceNotFoundException;
import com.ikaansh.taskapi.exception.UnauthorizedAccessException;
import com.ikaansh.taskapi.repository.TaskRepository;
import com.ikaansh.taskapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

	private final TaskRepository taskRepository;
	private final UserRepository userRepository;

	// Helper: Get currently authenticated User from JWT SecurityContext
	public User getAuthenticatedUser() {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));
	}

	// Helper: Verify that the logged-in user owns the task OR is an ADMIN
	private void verifyTaskOwnershipOrAdmin(Task task, User currentUser) {
		boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;
		boolean isOwner = task.getUser().getId().equals(currentUser.getId());

		if (!isOwner && !isAdmin) {
			throw new UnauthorizedAccessException("You do not have permission to access or modify this task");
		}
	}

	@Transactional
	public TaskResponseDto createTask(TaskRequestDto request) {
		User currentUser = getAuthenticatedUser();

		Task task = Task.builder().title(request.getTitle()).description(request.getDescription())
				.status(request.getStatus() != null ? request.getStatus() : TaskStatus.PENDING)
				.priority(request.getPriority()).dueDate(request.getDueDate()).user(currentUser).build();

		Task savedTask = taskRepository.save(task);
		return mapToDto(savedTask);
	}

	@Transactional(readOnly = true)
	public List<TaskResponseDto> getMyTasks(TaskStatus status, TaskPriority priority) {
		User currentUser = getAuthenticatedUser();
		List<Task> tasks;

		if (status != null) {
			tasks = taskRepository.findByUserIdAndStatusOrderByDueDateAsc(currentUser.getId(), status);
		} else if (priority != null) {
			tasks = taskRepository.findByUserIdAndPriorityOrderByDueDateAsc(currentUser.getId(), priority);
		} else {
			tasks = taskRepository.findByUserIdOrderByDueDateAsc(currentUser.getId());
		}

		return tasks.stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public TaskResponseDto getTaskById(Long taskId) {
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

		verifyTaskOwnershipOrAdmin(task, currentUser);
		return mapToDto(task);
	}

	@Transactional
	public TaskResponseDto updateTask(Long taskId, TaskRequestDto request) {
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

		verifyTaskOwnershipOrAdmin(task, currentUser);

		task.setTitle(request.getTitle());
		task.setDescription(request.getDescription());
		if (request.getStatus() != null) {
			task.setStatus(request.getStatus());
		}
		task.setPriority(request.getPriority());
		task.setDueDate(request.getDueDate());

		Task updatedTask = taskRepository.save(task);
		return mapToDto(updatedTask);
	}

	@Transactional
	public void deleteTask(Long taskId) {
		User currentUser = getAuthenticatedUser();
		Task task = taskRepository.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

		verifyTaskOwnershipOrAdmin(task, currentUser);
		taskRepository.delete(task);
	}

	// ADMIN ONLY: Fetch all tasks across all users
	@Transactional(readOnly = true)
	public List<TaskResponseDto> getAllTasksForAdmin() {
		return taskRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	private TaskResponseDto mapToDto(Task task) {
		boolean isOverdue = task.getDueDate().isBefore(LocalDate.now()) && task.getStatus() != TaskStatus.COMPLETED;

		return TaskResponseDto.builder().id(task.getId()).title(task.getTitle()).description(task.getDescription())
				.status(task.getStatus()).priority(task.getPriority()).dueDate(task.getDueDate()).overdue(isOverdue)
				.userId(task.getUser().getId()).userName(task.getUser().getName()).createdAt(task.getCreatedAt())
				.updatedAt(task.getUpdatedAt()).build();
	}
}