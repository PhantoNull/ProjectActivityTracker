package eu.Rationence.pat.service;



import eu.Rationence.pat.model.Project;
import eu.Rationence.pat.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class ProjectService {
    @Autowired
    private ProjectRepository projectRepository;

    public Project find(String project){return projectRepository.getProjectByProjectKey(project);
    }
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project save(Project client){ return projectRepository.save(client);}

    public void deleteProjectByProject(String client){ projectRepository.deleteProjectByProjectKey(client);}
}
