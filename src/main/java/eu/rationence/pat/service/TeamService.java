package eu.rationence.pat.service;

import eu.rationence.pat.repository.TeamRepository;
import eu.rationence.pat.model.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team find(String teamName){return teamRepository.getTeamByTeamName(teamName);
    }
    public List<Team> findAll() { return teamRepository.findAll();}

    public Team save(Team team){ return teamRepository.save(team);}

    public void deleteTeam(String teamName){teamRepository.deleteTeamByTeamName(teamName);}
}
