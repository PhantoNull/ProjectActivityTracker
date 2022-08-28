package eu.rationence.pat.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class CompiledProjectActivityRow {
    private String project;
    private String projectDesc;
    private String activityKey;
    private String location;
}
