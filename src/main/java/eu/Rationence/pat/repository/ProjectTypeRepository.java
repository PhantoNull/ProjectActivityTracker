package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.ProjectType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectTypeRepository extends CrudRepository<ProjectType, String> {
    ProjectType getProjectTypeByProjectTypeKey(String string);
    List<ProjectType> findAll();
    ProjectType save(ProjectType project);
    @Transactional
    void deleteProjectTypeByProjectTypeKey(String string);
}


