package eu.rationence.pat.model;

import eu.rationence.pat.model.composite_keys.CompiledStandardActivityCompositeKey;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "CompiledStandardActivity")
@Table(name = "PAT_CompiledStandardActivities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@IdClass(CompiledStandardActivityCompositeKey.class)
public class CompiledStandardActivity implements Serializable {

    @Id
    @Column(name = "c_Username", nullable = false, length = 64)
    private String username;

    @Id
    @Column(name = "c_Location", nullable = false, length = 16)
    private String locationName;

    @Id
    @Column(name = "c_Activity", length = 32, nullable = false)
    private String activityKey;

    @Id
    @Column(name = "d_Date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "c_Activity", foreignKey = @ForeignKey(name = "fk_Activity_CompiledStandardActivity"), insertable = false, updatable = false)
    private StandardActivity c_Activity;

    @ManyToOne
    @JoinColumn(name = "c_Username",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_CompiledStandardActivity_User"), insertable = false, updatable = false)
    private User c_Username;

    @ManyToOne
    @JoinColumn(name = "c_Location",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_CompiledStandardActivity_Location"), insertable = false, updatable = false)
    private Location c_Location;

    @Column(name = "n_Hours", nullable = false)
    private int hours;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompiledStandardActivity that = (CompiledStandardActivity) o;
        return username != null && Objects.equals(username, that.username)
                && locationName != null && Objects.equals(locationName, that.locationName)
                && activityKey != null && Objects.equals(activityKey, that.activityKey)
                && date != null && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username,
                locationName,
                activityKey,
                date);
    }
}
