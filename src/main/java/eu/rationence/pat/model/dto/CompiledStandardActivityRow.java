package eu.rationence.pat.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
