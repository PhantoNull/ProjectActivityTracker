package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "UserActivity")
@Table(name = "PAT_UserActivities")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@IdClass(UserActivityCompositeKey.class)
public class UserActivity implements Serializable {


    @Id
    @Column(name = "c_Project", nullable = false, length=16)
    private String project;

    @Id
    @Column(name="c_Activity", length=16, nullable = false)
    private String activityKey;

    @Id
    @Column(name="c_Username", length=64, nullable = false)
    private String username;

    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "c_Activity", foreignKey = @ForeignKey(name = "fk_Activity_UserActivity"), insertable = false, updatable = false),
                    @JoinColumn(name = "c_Project", foreignKey = @ForeignKey(name = "fk_Project_UserActivity"), insertable = false, updatable = false) })
    private Activity c_Activity;

    @ManyToOne
    @JoinColumn(name = "c_Username",
                                    nullable = false,
                                    foreignKey = @ForeignKey(name = "fk_UserActivity_User"), insertable = false, updatable = false)
    private User c_Username;

    @Column(name="i_DailyRate", nullable = false)
    private Integer dailyRate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserActivity userActivity = (UserActivity) o;
        return userActivity != null && c_Username != null && Objects.equals(c_Username, userActivity.c_Username) && Objects.equals(c_Activity, userActivity.c_Activity);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
