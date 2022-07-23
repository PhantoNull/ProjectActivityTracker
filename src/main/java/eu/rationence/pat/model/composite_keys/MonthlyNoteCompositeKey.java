package eu.rationence.pat.model.composite_keys;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class MonthlyNoteCompositeKey implements Serializable {

    private String username;
    private Date date;
}
