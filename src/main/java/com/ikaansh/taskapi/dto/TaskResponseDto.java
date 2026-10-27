package com.ikaansh.taskapi.dto;

import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {
	private Long id;
	private String title;
	private String description;
	private TaskStatus status;
	private TaskPriority priority;
	private LocalDate dueDate;
	private boolean overdue;
	private Long userId;
	private String userName;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}