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
public class UserActivityCompositeKey implements Serializable {

    private String project;
    private String activityKey;
    private String username;
}
