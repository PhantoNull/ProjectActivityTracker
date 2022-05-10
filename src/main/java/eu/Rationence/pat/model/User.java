 package eu.Rationence.pat.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

 @Entity(name = "User")
@Table(name = "PAT_Users")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class User implements Serializable {

    @Id
    @Column(name="c_Username", length=64, unique = true, nullable = false)
    private String username;

     @OneToMany(mappedBy = "user")
     private List<UserActivity> userActivities;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "c_Role", nullable = false, foreignKey = @ForeignKey(name = "fk_Role_User"))
    private Role role;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "c_Team", nullable = false, foreignKey = @ForeignKey(name = "fk_Team_User"))
    @JsonManagedReference
    private Team team;

    @ManyToMany(mappedBy = "users")
    private Set<Activity> activities  = new HashSet<>();

    @Column(name="e_Email", unique = true, length=128, nullable = false)
    @Email
    private String email;

    @Column(name="x_Name", nullable = false, length=32)
    private String name;

    @Column(name="x_Surame", nullable = false, length=64)
    private String surname;

    @Column(name="x_Description", nullable = false, length=128)
    private String description;

    @Column(name="i_Cost", nullable = false)
    private int cost;

    @Column(name="x_Time", nullable = false, length=5)
    private String time;

    @Column(name="h_Password", nullable = false, length=128)
    private String passwordHash;

    @Column(name="f_Enabled", nullable = false)
    private boolean enabled;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return username != null && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
