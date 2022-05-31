package eu.Rationence.pat.service;

import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class TeamService {
    @Autowired
    private TeamRepository teamRepository;

    public Team findTeamByTeamName(String teamName){return teamRepository.getTeamByTeamName(teamName);
    }
    public List<Team> findAll() { return teamRepository.findAll();}

    public Team saveTeam(Team team){ return teamRepository.save(team);}

    public void deleteTeam(String teamName){teamRepository.deleteTeamByTeamName(teamName);}
}
