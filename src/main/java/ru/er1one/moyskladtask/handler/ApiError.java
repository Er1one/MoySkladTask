package ru.er1one.moyskladtask.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ApiError {
    private String message;
    private String details;
    private final LocalDateTime timestamp = LocalDateTime.now();
}
