package eu.rationence.pat.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
public class CompiledUserProjectActivityRow extends CompiledProjectActivityRow {
    private String username;

}
