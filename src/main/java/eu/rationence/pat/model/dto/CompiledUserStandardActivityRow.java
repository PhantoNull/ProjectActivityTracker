package eu.rationence.pat.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@SuperBuilder
public class CompiledUserStandardActivityRow extends CompiledStandardActivityRow {
    private String username;
}
