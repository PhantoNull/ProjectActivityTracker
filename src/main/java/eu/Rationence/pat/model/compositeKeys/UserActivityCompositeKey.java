package eu.Rationence.pat.model.compositeKeys;

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
public class UserActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;
    private String username;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserActivityCompositeKey act = (UserActivityCompositeKey) o;
        return Objects.equals(project, act.project) && Objects.equals(activityKey, act.activityKey)
                && Objects.equals(username, act.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(project, activityKey, username);
    }
}
