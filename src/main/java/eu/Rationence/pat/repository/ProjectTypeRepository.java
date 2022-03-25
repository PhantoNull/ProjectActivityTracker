package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.ProjectType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTypeRepository extends CrudRepository<ProjectType, String> {
    ProjectType getProjectTypeByProjectType(String string);
    List<ProjectType> findAll();
    ProjectType save(ProjectType team);
    
}


