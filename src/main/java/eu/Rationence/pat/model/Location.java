package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Location")
@Table(name = "PAT_Locations")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Location implements Serializable {

    @Id
    @Column(name="c_Location", length=16, unique = true, nullable = false)
    private String locationName;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Location utente = (Location) o;
        return locationName != null && Objects.equals(locationName, utente.locationName);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
