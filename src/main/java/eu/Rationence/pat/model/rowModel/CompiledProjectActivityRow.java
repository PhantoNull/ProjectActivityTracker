package eu.Rationence.pat.model.rowModel;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class CompiledProjectActivityRow {
    private String project;
    private String projectDesc;
    private String activityKey;
    private String location;
}
