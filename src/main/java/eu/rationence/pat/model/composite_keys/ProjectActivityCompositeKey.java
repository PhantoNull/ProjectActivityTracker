package eu.rationence.pat.model.composite_keys;

import lombok.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ProjectActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;
}
