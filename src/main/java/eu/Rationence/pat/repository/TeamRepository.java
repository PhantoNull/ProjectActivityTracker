package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface TeamRepository extends CrudRepository<Team, String> {
    Team getTeamByTeamName(String teamName);
    List<Team> findAll();
    Team save(Team team);
    @Transactional
    void deleteTeamByTeamName(String teamName);
}


