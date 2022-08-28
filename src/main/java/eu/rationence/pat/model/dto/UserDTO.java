 package eu.rationence.pat.model.dto;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import eu.rationence.pat.model.Role;
import eu.rationence.pat.model.Team;
import eu.rationence.pat.model.UserActivity;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDTO implements Serializable {
    private String username;

    private Set<UserActivity> activities  = new HashSet<>();

    private Role role;

    private Team team;

    private String email;

    private String name;

    private String surname;

    private String description;

    private int cost;

    private String time;

    private String passwordHash;

    private boolean enabled;
}
