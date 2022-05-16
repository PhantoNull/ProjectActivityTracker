package eu.Rationence.pat.service;

import eu.Rationence.pat.model.ProjectActivity;
import eu.Rationence.pat.repository.ProjectActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class ProjectActivityService {
    @Autowired
    private ProjectActivityRepository projectActivityRepository;

    public ProjectActivity findActivityByActivityKeyAndProject(String activityKey, String project){
        return projectActivityRepository.getActivityByActivityKeyAndProject(activityKey, project);
    }
    public List<ProjectActivity> findActivitiesByProject(String project) {
        return projectActivityRepository.findActivitiesByProject(project);
    }

    public ProjectActivity saveActivity(ProjectActivity projectActivity){ return projectActivityRepository.save(projectActivity);}

    public void deleteActivityByActivityKeyAndProject(String string, String project){
        projectActivityRepository.deleteActivityByActivityKeyAndProject(string, project);
    }
}
