package ru.salnikov.ssesubscriptiondemo.domain.dto;

import java.util.UUID;

/**
 * ДТО события.
 * @author Anton Salnikov
 * @since 19.02.2025
 */
public record EventDto(UUID id, String title, String description) {
}
