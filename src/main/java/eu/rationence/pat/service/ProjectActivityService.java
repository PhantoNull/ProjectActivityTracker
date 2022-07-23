package eu.rationence.pat.service;

import eu.rationence.pat.model.ProjectActivity;
import eu.rationence.pat.repository.ProjectActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectActivityService {
    private final ProjectActivityRepository projectActivityRepository;
    @Autowired
    public ProjectActivityService(ProjectActivityRepository projectActivityRepository) {
        this.projectActivityRepository = projectActivityRepository;
    }

    public ProjectActivity find(String activityKey, String project){
        return projectActivityRepository.getActivityByActivityKeyAndProject(activityKey, project);
    }
    public List<ProjectActivity> findActivitiesByProject(String project) {
        return projectActivityRepository.findActivitiesByProject(project);
    }

    public ProjectActivity save(ProjectActivity projectActivity){ return projectActivityRepository.save(projectActivity);}

    public void delete(String activityKey, String project){
        projectActivityRepository.deleteActivityByActivityKeyAndProject(activityKey, project);
    }
}
