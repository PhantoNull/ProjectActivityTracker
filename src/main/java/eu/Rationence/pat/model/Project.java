 package eu.Rationence.pat.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;
import java.util.Objects;

 @Entity(name = "Project")
 @Table(name = "PAT_Projects")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 public class Project implements Serializable {

     @Id
     @Column(name="c_Project", length=16, unique = true, nullable = false)
     private String project;

     @Column(name="x_Project", nullable = false, length=32)
     private String projectDesc;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_Client", nullable = false)
     private Client client;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_ProjectType", nullable = false)
     private ProjectType projectType;

     @Column(name="d_Start", nullable = false)
     @DateTimeFormat
     private String dateStart;

     @Column(name="d_End")
     @DateTimeFormat
     private String dateEnd;

     @Column(name="d_Close")
     @DateTimeFormat
     private String dateClose;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_Team", nullable = false)
     private Team team;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_ProjectManager", nullable = false)
     private User projectManager;

     @Column(name="i_Value", nullable = false)
     private double value;

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
         Project user = (Project) o;
         return project != null && Objects.equals(project, user.project);
     }

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
