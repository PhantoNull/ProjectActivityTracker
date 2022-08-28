 package eu.rationence.pat.model.dto;

 import eu.rationence.pat.model.Client;
 import eu.rationence.pat.model.ProjectType;
 import eu.rationence.pat.model.Team;
 import eu.rationence.pat.model.User;
 import lombok.*;
 import org.springframework.format.annotation.DateTimeFormat;

 import javax.persistence.*;
 import java.io.Serializable;
 import java.util.Date;

@Data
 public class ProjectDTO implements Serializable {

     private String projectKey;

     private String projectDesc;

     private Client client;

     private ProjectType projectType;

     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateStart;

     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateEnd;

     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateClose;

     private Team team;

     private User projectManager;

     private int value;
 }
