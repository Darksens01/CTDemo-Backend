package com.bracketops.infrastructure.rest.controller;

import com.bracketops.application.command.handler.UpdateMatchScoreCommandHandler;
import com.bracketops.application.dto.MatchResponseDto;
import com.bracketops.application.dto.UpdateMatchScoreCommand;
import com.bracketops.application.query.handler.MatchQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@Tag(name = "Matches & Brackets", description = "Match Score Updates, Bracket Views & Push Notifications (CQRS)")
public class MatchController {

    private final UpdateMatchScoreCommandHandler updateScoreCommandHandler;
    private final MatchQueryHandler queryHandler;

    public MatchController(UpdateMatchScoreCommandHandler updateScoreCommandHandler, MatchQueryHandler queryHandler) {
        this.updateScoreCommandHandler = updateScoreCommandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping("/score")
    @Operation(summary = "Update live match score, advance winner to next round & trigger Push Notification (Command - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MatchResponseDto> updateMatchScore(@Valid @RequestBody UpdateMatchScoreCommand command) {
        MatchResponseDto updated = updateScoreCommandHandler.handle(command);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match details by ID (Public)")
    public ResponseEntity<MatchResponseDto> getMatchById(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getById(id));
    }

    @GetMapping("/tournament/{tournamentId}")
    @Operation(summary = "Get public bracket tree matches for a tournament (Public)")
    public ResponseEntity<List<MatchResponseDto>> getMatchesByTournamentId(@PathVariable String tournamentId) {
        return ResponseEntity.ok(queryHandler.getByTournamentId(tournamentId));
    }
}
