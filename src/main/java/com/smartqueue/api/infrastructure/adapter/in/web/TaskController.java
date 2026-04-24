package com.smartqueue.api.infrastructure.adapter.in.web;

import com.smartqueue.api.application.port.in.ProcessTaskUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "Endpoints for task submission and tracking")
public class TaskController {

    private final ProcessTaskUseCase processTaskUseCase;

    @PostMapping
    @Operation(summary = "Submit a new task to the queue")
    public ResponseEntity<TaskResponse> submitTask(@Valid @RequestBody TaskRequest request) {
        var task = processTaskUseCase.submitTask(request.getPayload());
        return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task status by ID")
    public ResponseEntity<TaskResponse> getTaskStatus(@PathVariable UUID id) {
        var task = processTaskUseCase.getTaskStatus(id);
        return ResponseEntity.ok(TaskResponse.from(task));
    }
}
