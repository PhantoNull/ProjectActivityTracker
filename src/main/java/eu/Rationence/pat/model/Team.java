package eu.Rationence.pat.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Team")
@Table(name = "PAT_Team")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Team implements Serializable {

    @Id
    @Column(name="c_Team", length=5, unique = true, nullable = false)
    private String teamName;

    @Column(name="x_Team", length=128, nullable = false)
    private String teamDesc;

    //JsonIgnoreProperties(value = {"c_Team"}, allowSetters = true)
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "c_Administrator")
    @JsonBackReference
    private User administrator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Team utente = (Team) o;
        return teamName != null && Objects.equals(teamName, utente.teamName);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
