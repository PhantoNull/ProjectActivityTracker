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
public class UserActivity implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "c_Activity", referencedColumnName = "c_Activity", nullable = false, foreignKey = @ForeignKey(name = "fk_UserActivity_Activity"))
    private Activity activity;

    @Id
    @ManyToOne
    @JoinColumn(name = "c_Username", referencedColumnName = "c_Username", nullable = false, foreignKey = @ForeignKey(name = "fk_UserActivity_User"))
    private User user;

    @Column(name="i_Rate", nullable = false)
    private Integer rate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserActivity userActivity = (UserActivity) o;
        return activity != null && user != null && Objects.equals(user, userActivity.user) && Objects.equals(activity, userActivity.activity);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
