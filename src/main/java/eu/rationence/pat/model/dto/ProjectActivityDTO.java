package eu.rationence.pat.model.dto;

import eu.rationence.pat.model.ActivityType;
import eu.rationence.pat.model.Project;
import eu.rationence.pat.model.UserActivity;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ProjectActivityDTO implements Serializable {

    private String project;

    private String activityKey;

    private Project c_Project;

    private List<UserActivity> userActivities;

    private ActivityType activityType;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateStart;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dateEnd;

    private boolean charged;

    private int manDays;
}
