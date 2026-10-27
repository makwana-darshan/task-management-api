package com.ikaansh.taskapi.dto;

import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskRequestDto {

	@NotBlank(message = "Task title is required")
	@Size(min = 3, max = 150, message = "Title must be between 3 and 150 characters")
	private String title;

	@Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;

	// Optional on creation (defaults to PENDING in service), used on update
	private TaskStatus status;

	@NotNull(message = "Task priority is required (LOW, MEDIUM, HIGH)")
	private TaskPriority priority;

	@NotNull(message = "Due date is required")
	@FutureOrPresent(message = "Due date cannot be in the past")
	private LocalDate dueDate;
}