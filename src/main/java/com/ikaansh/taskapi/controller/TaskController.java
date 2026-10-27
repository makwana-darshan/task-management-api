package com.ikaansh.taskapi.controller;

import com.ikaansh.taskapi.dto.TaskRequestDto;
import com.ikaansh.taskapi.dto.TaskResponseDto;
import com.ikaansh.taskapi.enums.TaskPriority;
import com.ikaansh.taskapi.enums.TaskStatus;
import com.ikaansh.taskapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

	private final TaskService taskService;

	@PostMapping
	public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
	}

	@GetMapping
	public ResponseEntity<List<TaskResponseDto>> getMyTasks(@RequestParam(required = false) TaskStatus status,
			@RequestParam(required = false) TaskPriority priority) {
		return ResponseEntity.ok(taskService.getMyTasks(status, priority));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
		return ResponseEntity.ok(taskService.getTaskById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TaskResponseDto> updateTask(@PathVariable Long id,
			@Valid @RequestBody TaskRequestDto request) {
		return ResponseEntity.ok(taskService.updateTask(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Map<String, String>> deleteTask(@PathVariable Long id) {
		taskService.deleteTask(id);
		return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
	}
}