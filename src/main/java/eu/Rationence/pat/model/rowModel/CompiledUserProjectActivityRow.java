package eu.Rationence.pat.model.rowModel;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class CompiledUserProjectActivityRow {
    private String username;
    private String project;
    private String projectDesc;
    private String activityKey;
    private String location;
}
