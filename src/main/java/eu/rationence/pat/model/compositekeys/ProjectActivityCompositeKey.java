package eu.rationence.pat.model.compositekeys;

import lombok.*;
import org.hibernate.Hibernate;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProjectActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectActivityCompositeKey act = (ProjectActivityCompositeKey) o;
        return Objects.equals(project, act.project) && Objects.equals(activityKey, act.activityKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activityKey);
    }
}
