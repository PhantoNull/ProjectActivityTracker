package eu.rationence.pat.model.compositekeys;

import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class CompiledProjectActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;
    private String username;
    private String locationName;
    private Date date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompiledProjectActivityCompositeKey act = (CompiledProjectActivityCompositeKey) o;
        return Objects.equals(project, act.project) && Objects.equals(activityKey, act.activityKey)
                && Objects.equals(username, act.username) && Objects.equals(locationName, act.locationName)
                && Objects.equals(date, act.date);
    }
    @Override
    public int hashCode() {
        return Objects.hash(project, activityKey, username, locationName, date);
    }
}