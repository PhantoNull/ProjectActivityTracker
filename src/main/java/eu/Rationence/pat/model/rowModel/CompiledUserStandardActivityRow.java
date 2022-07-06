package eu.Rationence.pat.model.rowModel;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class CompiledUserStandardActivityRow {
    private String username;
    private String activityKey;
    private String location;
}
