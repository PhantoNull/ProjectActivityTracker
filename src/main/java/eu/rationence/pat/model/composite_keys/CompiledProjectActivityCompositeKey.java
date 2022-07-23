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
public class CompiledProjectActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;
    private String username;
    private String locationName;
    private Date date;
}