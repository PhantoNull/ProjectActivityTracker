package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "ProjectType")
@Table(name = "PAT_ProjectTypes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ProjectType implements Serializable {

    @Id
    @Column(name="c_ProjectType", length=4, unique = true, nullable = false)
    private String projectType;

    @Column(name="x_ProjectType", length=64, nullable = false)
    private String projectTypeDesc;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ProjectType utente = (ProjectType) o;
        return projectType != null && Objects.equals(projectType, utente.projectType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
