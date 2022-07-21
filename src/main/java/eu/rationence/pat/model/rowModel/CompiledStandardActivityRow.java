package eu.rationence.pat.model.rowModel;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class CompiledStandardActivityRow {
    private String activityKey;
    private String location;
}
