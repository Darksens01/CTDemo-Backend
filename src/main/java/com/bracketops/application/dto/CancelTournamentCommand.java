package com.bracketops.application.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelTournamentCommand(
    @NotBlank(message = "Tournament ID cannot be empty")
    String tournamentId,
    @NotBlank(message = "Cancellation reason cannot be empty")
    String reason
) {}
