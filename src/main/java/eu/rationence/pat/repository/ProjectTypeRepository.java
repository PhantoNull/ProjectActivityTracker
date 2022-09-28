package eu.rationence.pat.repository;

import eu.rationence.pat.model.ProjectType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectTypeRepository extends CrudRepository<ProjectType, String> {
    ProjectType getProjectTypeByProjectTypeKey(String projectTypeKey);

    List<ProjectType> findAll();

    <S extends ProjectType> S save(S project);

    @Transactional
    void deleteProjectTypeByProjectTypeKey(String projectTypeKey);
}


