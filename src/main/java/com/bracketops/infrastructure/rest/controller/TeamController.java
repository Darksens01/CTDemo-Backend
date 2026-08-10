package com.bracketops.infrastructure.rest.controller;

import com.bracketops.application.command.handler.RegisterTeamCommandHandler;
import com.bracketops.application.command.handler.WithdrawTeamCommandHandler;
import com.bracketops.application.dto.RegisterTeamCommand;
import com.bracketops.application.dto.TeamResponseDto;
import com.bracketops.application.query.handler.TeamQueryHandler;
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

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "Teams & Rosters", description = "Team Registration & Roster Management REST API (CQRS)")
public class TeamController {

    private final RegisterTeamCommandHandler registerTeamCommandHandler;
    private final WithdrawTeamCommandHandler withdrawTeamCommandHandler;
    private final TeamQueryHandler queryHandler;

    public TeamController(RegisterTeamCommandHandler registerTeamCommandHandler,
                          WithdrawTeamCommandHandler withdrawTeamCommandHandler,
                          TeamQueryHandler queryHandler) {
        this.registerTeamCommandHandler = registerTeamCommandHandler;
        this.withdrawTeamCommandHandler = withdrawTeamCommandHandler;
        this.queryHandler = queryHandler;
    }

    @PostMapping
    @Operation(summary = "Register team roster into an open tournament (Command - Requires ROLE_CAPTAIN)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<TeamResponseDto> registerTeam(@Valid @RequestBody RegisterTeamCommand command, Principal principal) {
        RegisterTeamCommand commandWithCaptain = new RegisterTeamCommand(
                command.tournamentId(),
                command.teamName(),
                command.logoUrl(),
                principal != null ? principal.getName() : command.captainUsername(),
                command.players()
        );
        TeamResponseDto created = registerTeamCommandHandler.handle(commandWithCaptain);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{teamId}/tournament/{tournamentId}")
    @Operation(summary = "Withdraw team registration. If bracket is active, disqualifies team and awards Walkover W.O. (Command)")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> withdrawTeam(@PathVariable String teamId, @PathVariable String tournamentId) {
        withdrawTeamCommandHandler.handle(tournamentId, teamId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team roster details by ID (Public)")
    public ResponseEntity<TeamResponseDto> getTeamById(@PathVariable String id) {
        return ResponseEntity.ok(queryHandler.getById(id));
    }

    @GetMapping
    @Operation(summary = "List all registered teams (Public)")
    public ResponseEntity<List<TeamResponseDto>> getAllTeams() {
        return ResponseEntity.ok(queryHandler.getAll());
    }
}
