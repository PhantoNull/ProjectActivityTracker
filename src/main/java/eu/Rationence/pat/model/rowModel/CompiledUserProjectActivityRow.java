package eu.Rationence.pat.model.rowModel;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@SuperBuilder
public class CompiledUserProjectActivityRow extends CompiledProjectActivityRow {
    private String username;

}
