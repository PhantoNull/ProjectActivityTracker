package eu.Rationence.pat.model;

import java.io.Serializable;

public class ActivityCompositeKey implements Serializable {
    private Project project;

    private String activityKey;

    public ActivityCompositeKey(Project project, String activityKey) {
        this.project = project;
        this.activityKey = activityKey;
    }
}
