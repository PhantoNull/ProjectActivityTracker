package eu.rationence.pat.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class StandardActivityDTO implements Serializable {

    private String activityKey;

    private boolean internal;

    private boolean waged;
}
