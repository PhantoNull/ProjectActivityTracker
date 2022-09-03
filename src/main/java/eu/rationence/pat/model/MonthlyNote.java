package eu.rationence.pat.model;

import eu.rationence.pat.model.composite_keys.MonthlyNoteCompositeKey;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity(name = "MonthlyNote")
@Table(name = "PAT_MonthlyNotes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@IdClass(MonthlyNoteCompositeKey.class)
public class MonthlyNote implements Serializable {

    @Id
    @Column(name = "c_Username", nullable = false, length = 64)
    private String username;

    @Id
    @Column(name = "d_Date", nullable = false)
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "MM-dd")
    private Date date;

    @ManyToOne
    @JoinColumn(name = "c_Username",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_MonthlyNote_User"), insertable = false, updatable = false)
    private User c_Username;

    @Column(name = "x_Note", length = 65000, nullable = false)
    private String note;

    @Column(name = "f_Locked", nullable = false)
    private boolean locked;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MonthlyNote that = (MonthlyNote) o;
        return username != null && Objects.equals(username, that.username)
                && date != null && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, date);
    }
}
