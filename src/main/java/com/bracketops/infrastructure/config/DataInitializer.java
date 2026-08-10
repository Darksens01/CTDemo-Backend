package com.bracketops.infrastructure.config;

import com.bracketops.domain.model.aggregate.Match;
import com.bracketops.domain.model.aggregate.Team;
import com.bracketops.domain.model.aggregate.Tournament;
import com.bracketops.domain.model.entity.Player;
import com.bracketops.domain.model.entity.UserDomain;
import com.bracketops.domain.model.valueobject.Role;
import com.bracketops.domain.model.valueobject.TeamStatus;
import com.bracketops.domain.model.valueobject.TournamentStatus;
import com.bracketops.domain.port.outbound.MatchRepositoryPort;
import com.bracketops.domain.port.outbound.TeamRepositoryPort;
import com.bracketops.domain.port.outbound.TournamentRepositoryPort;
import com.bracketops.domain.port.outbound.UserRepositoryPort;
import com.bracketops.domain.service.BracketGeneratorEngine;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepositoryPort userRepositoryPort,
                                       TournamentRepositoryPort tournamentRepositoryPort,
                                       TeamRepositoryPort teamRepositoryPort,
                                       MatchRepositoryPort matchRepositoryPort,
                                       BracketGeneratorEngine bracketEngine,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepositoryPort.existsByUsername("admin")) {
                userRepositoryPort.save(new UserDomain(
                        null, "admin", passwordEncoder.encode("admin123"), "Tournament Director Alex", "alex@bracketops.gg", Role.ROLE_ADMIN, true
                ));
            }

            if (!userRepositoryPort.existsByUsername("captain")) {
                userRepositoryPort.save(new UserDomain(
                        null, "captain", passwordEncoder.encode("captain123"), "Team Captain TenZ", "tenz@sentinels.gg", Role.ROLE_CAPTAIN, true
                ));
            }

            if (!userRepositoryPort.existsByUsername("spectator")) {
                userRepositoryPort.save(new UserDomain(
                        null, "spectator", passwordEncoder.encode("spectator123"), "Esports Fan Guest", "guest@bracketops.gg", Role.ROLE_SPECTATOR, true
                ));
            }

            // Seed tournaments for ALL 5 FORMATS if database is clean
            if (tournamentRepositoryPort.findAll().isEmpty()) {
                LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

                seedTournament(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine,
                        "Copa Overwatch 2 - Eliminación Directa", "Overwatch 2", "SINGLE_ELIMINATION", 5, "$1,500 USD",
                        now.plusHours(2),
                        List.of("Dallas Fuel", "San Francisco Shock", "Seoul Dynasty", "London Spitfire"));

                seedTournament(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine,
                        "Valorant Championship - Doble Eliminación", "Valorant", "DOUBLE_ELIMINATION", 5, "$5,000 USD",
                        now.plusHours(4),
                        List.of("Optic Gaming", "LOUD", "Sentinels", "Fnatic"));

                seedTournament(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine,
                        "Liga Tekken 8 - Todos Contra Todos", "Tekken 8", "ROUND_ROBIN", 1, "$2,000 USD",
                        now.plusDays(1).withHour(16),
                        List.of("Arslan Ash", "Knee", "Anakin", "Chikurin"));

                seedTournament(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine,
                        "CS2 Major Copenhagen - Sistema Suizo", "CS2", "SWISS", 5, "$10,000 USD",
                        now.plusDays(1).withHour(19),
                        List.of("FURIA Esports", "Team Vitality", "NaVi", "FaZe Clan"));

                seedTournament(tournamentRepositoryPort, teamRepositoryPort, matchRepositoryPort, bracketEngine,
                        "Torneo EA Sports FC 27 - Eliminación Directa", "EA Sports FC 27", "SINGLE_ELIMINATION", 1, "$3,000 USD",
                        now.plusDays(2).withHour(17),
                        List.of("Real Madrid Pro", "Manchester City FC", "Inter Miami", "FC Barcelona Esports"));
            }
        };
    }

    private void seedTournament(TournamentRepositoryPort tournamentRepositoryPort,
                                TeamRepositoryPort teamRepositoryPort,
                                MatchRepositoryPort matchRepositoryPort,
                                BracketGeneratorEngine bracketEngine,
                                String name, String gameName, String format, int playersPerTeam,
                                String prizePool, LocalDateTime startDate, List<String> teamNames) {
        String tournamentId = UUID.randomUUID().toString();

        Tournament tournament = new Tournament(
                tournamentId, name, gameName, format, 4, playersPerTeam,
                TournamentStatus.DRAFT, prizePool, null, null, new ArrayList<>(), null, null,
                startDate, LocalDateTime.now()
        );

        tournament.openRegistration();

        List<Team> teams = new ArrayList<>();
        for (String teamName : teamNames) {
            String teamId = UUID.randomUUID().toString();
            List<Player> players = List.of(new Player(UUID.randomUUID().toString(), teamName + " Pro", teamName + " Player", "Flex"));
            Team team = new Team(teamId, tournamentId, teamName, "", "captain", TeamStatus.REGISTERED, players, LocalDateTime.now());
            teamRepositoryPort.save(team);
            teams.add(team);
            tournament.registerTeam(teamId);
        }

        tournament.markBracketGenerated();
        tournament.startTournament();
        tournamentRepositoryPort.save(tournament);

        List<Match> matches = bracketEngine.generateBracket(tournament, teams);
        matchRepositoryPort.saveAll(matches);
    }
}
