package eu.Rationence.pat.model.rowModel;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class CompiledStandardActivityRow {
    private String activityKey;
    private String location;
}
