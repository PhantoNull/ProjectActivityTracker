package eu.Rationence.pat.service;


import eu.Rationence.pat.model.ProjectType;
import eu.Rationence.pat.repository.ProjectTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectTypeService {
    private final ProjectTypeRepository projectTypeRepository;
    @Autowired
    public ProjectTypeService(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;
    }

    public ProjectType find(String projectType){return projectTypeRepository.getProjectTypeByProjectTypeKey(projectType);
    }
    public List<ProjectType> findAll() {
        return projectTypeRepository.findAll();
    }

    public ProjectType save(ProjectType p){ return projectTypeRepository.save(p);}
}
