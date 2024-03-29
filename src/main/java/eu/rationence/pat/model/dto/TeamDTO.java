package eu.rationence.pat.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamDTO implements Serializable {

    private String teamName;

    private String teamDesc;

    private String teamAdmin;
}
