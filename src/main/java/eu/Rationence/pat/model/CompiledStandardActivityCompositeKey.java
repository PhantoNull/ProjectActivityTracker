package eu.Rationence.pat.model;

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
public class CompiledStandardActivityCompositeKey implements Serializable {

    private String activityKey;
    private String username;
    private String locationName;
    private Date date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompiledStandardActivityCompositeKey act = (CompiledStandardActivityCompositeKey) o;
        return Objects.equals(activityKey, act.activityKey)
                && Objects.equals(username, act.username) && Objects.equals(locationName, act.locationName)
                && Objects.equals(date, act.date);
    }
    @Override
    public int hashCode() {
        return Objects.hash(activityKey, username, locationName, date);
    }
}
