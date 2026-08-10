package com.bracketops.infrastructure.config;

import com.bracketops.application.command.handler.CancelTournamentCommandHandler;
import com.bracketops.application.command.handler.CreateTournamentCommandHandler;
import com.bracketops.application.command.handler.DeleteTournamentCommandHandler;
import com.bracketops.application.command.handler.GenerateBracketCommandHandler;
import com.bracketops.application.command.handler.RegisterTeamCommandHandler;
import com.bracketops.application.command.handler.UpdateMatchScoreCommandHandler;
import com.bracketops.application.command.handler.WithdrawTeamCommandHandler;
import com.bracketops.application.query.handler.DashboardQueryHandler;
import com.bracketops.application.query.handler.MatchQueryHandler;
import com.bracketops.application.query.handler.TeamQueryHandler;
import com.bracketops.application.query.handler.TournamentQueryHandler;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.NotificationPort;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.domain.service.BracketGeneratorEngine;
import com.bracketops.domain.service.MatchAdvancementEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    // Domain Engines
    @Bean
    public BracketGeneratorEngine bracketGeneratorEngine() {
        return new BracketGeneratorEngine();
    }

    @Bean
    public MatchAdvancementEngine matchAdvancementEngine() {
        return new MatchAdvancementEngine();
    }

    // CQRS Command Handlers
    @Bean
    public CreateTournamentCommandHandler createTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        return new CreateTournamentCommandHandler(tournamentRepositoryPort);
    }

    @Bean
    public CancelTournamentCommandHandler cancelTournamentCommandHandler(
            TournamentRepositoryPort tournamentRepositoryPort, NotificationPort notificationPort) {
        return new CancelTournamentCommandHandler(tournamentRepositoryPort, notificationPort);
    }

    @Bean
    public DeleteTournamentCommandHandler deleteTournamentCommandHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        return new DeleteTournamentCommandHandler(tournamentRepositoryPort);
    }

    @Bean
    public RegisterTeamCommandHandler registerTeamCommandHandler(
            TeamRepositoryPort teamRepositoryPort, TournamentRepositoryPort tournamentRepositoryPort) {
        return new RegisterTeamCommandHandler(teamRepositoryPort, tournamentRepositoryPort);
    }

    @Bean
    public GenerateBracketCommandHandler generateBracketCommandHandler(
            TournamentRepositoryPort tournamentRepositoryPort,
            TeamRepositoryPort teamRepositoryPort,
            MatchRepositoryPort matchRepositoryPort,
            BracketGeneratorEngine bracketEngine) {
        return new GenerateBracketCommandHandler(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine);
    }

    @Bean
    public UpdateMatchScoreCommandHandler updateMatchScoreCommandHandler(
            MatchRepositoryPort matchRepositoryPort,
            TournamentRepositoryPort tournamentRepositoryPort,
            NotificationPort notificationPort,
            MatchAdvancementEngine advancementEngine) {
        return new UpdateMatchScoreCommandHandler(matchRepositoryPort, tournamentRepositoryPort, notificationPort, advancementEngine);
    }

    @Bean
    public WithdrawTeamCommandHandler withdrawTeamCommandHandler(
            TeamRepositoryPort teamRepositoryPort,
            TournamentRepositoryPort tournamentRepositoryPort,
            MatchRepositoryPort matchRepositoryPort,
            NotificationPort notificationPort,
            MatchAdvancementEngine advancementEngine) {
        return new WithdrawTeamCommandHandler(teamRepositoryPort, tournamentRepositoryPort, matchRepositoryPort, notificationPort, advancementEngine);
    }

    // CQRS Query Handlers
    @Bean
    public TournamentQueryHandler tournamentQueryHandler(TournamentRepositoryPort tournamentRepositoryPort) {
        return new TournamentQueryHandler(tournamentRepositoryPort);
    }

    @Bean
    public MatchQueryHandler matchQueryHandler(MatchRepositoryPort matchRepositoryPort) {
        return new MatchQueryHandler(matchRepositoryPort);
    }

    @Bean
    public TeamQueryHandler teamQueryHandler(TeamRepositoryPort teamRepositoryPort) {
        return new TeamQueryHandler(teamRepositoryPort);
    }

    @Bean
    public DashboardQueryHandler dashboardQueryHandler(
            TournamentRepositoryPort tournamentRepositoryPort,
            TeamRepositoryPort teamRepositoryPort,
            MatchRepositoryPort matchRepositoryPort) {
        return new DashboardQueryHandler(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort);
    }
}
