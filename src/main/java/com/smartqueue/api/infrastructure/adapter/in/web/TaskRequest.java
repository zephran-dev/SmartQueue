package com.smartqueue.api.infrastructure.adapter.in.web;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TaskRequest {
    @NotBlank(message = "Payload is required")
    private String payload;
}
