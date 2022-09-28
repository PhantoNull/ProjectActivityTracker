package eu.rationence.pat.repository;

import eu.rationence.pat.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectRepository extends CrudRepository<Project, String> {
    Project getProjectByProjectKey(String projectKey);

    List<Project> findAll();

    <S extends Project> S save(S project);

    @Transactional
    void deleteProjectByProjectKey(String projectKey);
}


