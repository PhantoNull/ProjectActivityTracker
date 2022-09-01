package eu.rationence.pat.model.dto;

import eu.rationence.pat.model.ProjectActivity;
import eu.rationence.pat.model.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserActivityDTO implements Serializable {
    private String project;

    private String activityKey;

    private String username;

    private ProjectActivity c_Activity;

    private User c_Username;

    private Integer dailyRate;
}
