package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Role")
@Table(name = "PAT_Roles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Role implements Serializable {

    @Id
    @Column(name="c_Role", length=50, unique = true, nullable = false)
    private String role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Role utente = (Role) o;
        return role != null && Objects.equals(role, utente.role);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
