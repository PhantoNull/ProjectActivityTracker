package eu.Rationence.pat.model.compositeKeys;

import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class MonthlyNoteCompositeKey implements Serializable {

    private String username;
    private Date date;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        MonthlyNoteCompositeKey act = (MonthlyNoteCompositeKey) o;
        return Objects.equals(username, act.username) && Objects.equals(date, act.date);
    }
    @Override
    public int hashCode() {
        return Objects.hash(username, date);
    }
}
