package com.bracketops.infrastructure.rest.controller;

import com.bracketops.application.command.handler.CancelTournamentCommandHandler;
import com.bracketops.application.command.handler.CreateTournamentCommandHandler;
import com.bracketops.application.command.handler.DeleteTournamentCommandHandler;
import com.bracketops.application.command.handler.GenerateBracketCommandHandler;
import com.bracketops.application.dto.CancelTournamentCommand;
import com.bracketops.application.dto.CreateTournamentCommand;
import com.bracketops.application.dto.MatchResponseDto;
import com.bracketops.application.dto.TournamentResponseDto;
import com.bracketops.application.query.handler.TournamentQueryHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.bracketops.application.command.handler.UpdateTournamentCommandHandler;
import com.bracketops.application.dto.UpdateTournamentCommand;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1/tournaments")
@Tag(name = "Tournaments", description = "Esports Tournament Lifecycle Management REST API (CQRS)")
public class TournamentController {

    private final CreateTournamentCommandHandler createCommandHandler;
    private final UpdateTournamentCommandHandler updateCommandHandler;
    private final GenerateBracketCommandHandler generateBracketCommandHandler;
    private final CancelTournamentCommandHandler cancelCommandHandler;
    private final DeleteTournamentCommandHandler deleteCommandHandler;
    private final TournamentQueryHandler queryHandler;

    public TournamentController(CreateTournamentCommandHandler createCommandHandler,
                                UpdateTournamentCommandHandler updateCommandHandler,
                                GenerateBracketCommandHandler generateBracketCommandHandler,
                                CancelTournamentCommandHandler cancelCommandHandler,
                                DeleteTournamentCommandHandler deleteCommandHandler,
                                TournamentQueryHandler queryHandler) {
        this.createCommandHandler = createCommandHandler;
        this.updateCommandHandler = updateCommandHandler;
        this.generateBracketCommandHandler = generateBracketCommandHandler;
        this.cancelCommandHandler = cancelCommandHandler;
        this.deleteCommandHandler = deleteCommandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    @Operation(summary = "Create a new esports tournament (Command - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TournamentResponseDto> createTournament(@Valid @RequestBody CreateTournamentCommand command) {
        TournamentResponseDto created = createCommandHandler.handle(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tournament details (Command / PUT - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TournamentResponseDto> updateTournament(
            @PathVariable String id,
            @RequestBody UpdateTournamentCommand command) {
        UpdateTournamentCommand commandWithId = new UpdateTournamentCommand(
                id,
                command.name(),
                command.gameName(),
                command.format(),
                command.maxTeams(),
                command.playersPerTeam(),
                command.prizePool(),
                command.bannerUrl()
        );
        TournamentResponseDto updated = updateCommandHandler.handle(commandWithId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{id}/generate-bracket")
    @Operation(summary = "Generate single-elimination tournament bracket tree (Command - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<MatchResponseDto>> generateBracket(@PathVariable String id) {
        List<MatchResponseDto> matches = generateBracketCommandHandler.handle(id);
        return ResponseEntity.ok(matches);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an esports tournament with a reason (Command - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TournamentResponseDto> cancelTournament(
            @PathVariable String id,
            @RequestBody java.util.Map<String, String> body) {
        String reason = body != null && body.containsKey("reason") ? body.get("reason") : "Motivo no especificado";
        TournamentResponseDto result = cancelCommandHandler.handle(new CancelTournamentCommand(id, reason));
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a cancelled or draft esports tournament (Command - Requires ROLE_ADMIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteTournament(@PathVariable String id) {
        deleteCommandHandler.handle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tournament details by ID (Public)")
    public ResponseEntity<TournamentResponseDto> getTournamentById(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all esports tournaments (Public)")
    public ResponseEntity<List<TournamentResponseDto>> getAllTournaments() {
        return ResponseEntity.ok(queryHandler.getAll());
    }
}
