package eu.rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "ActivityType")
@Table(name = "PAT_ActivityTypes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ActivityType implements Serializable {

    @Id
    @Column(name = "c_ActivityType", length = 4, unique = true, nullable = false)
    private String activityTypeKey;

    @Column(name = "x_ActivityType", length = 64, nullable = false)
    private String activityTypeDesc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ActivityType utente = (ActivityType) o;
        return activityTypeKey != null && Objects.equals(activityTypeKey, utente.activityTypeKey);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
