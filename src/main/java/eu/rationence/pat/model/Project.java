 package eu.rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
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
     private String projectKey;

     @Column(name="x_Project", nullable = false, length=32)
     private String projectDesc;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_Client", nullable = false, foreignKey = @ForeignKey(name = "fk_Client_Project"))
     private Client client;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_ProjectType", nullable = false, foreignKey = @ForeignKey(name = "fk_ProjectType_Project"))
     private ProjectType projectType;

     @Column(name="d_Start", nullable = false, length=16)
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateStart;

     @Column(name="d_End")
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateEnd;

     @Column(name="d_Close")
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateClose;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_Team", nullable = false, foreignKey = @ForeignKey(name = "fk_Team_Project"))
     private Team team;

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_ProjectManager", nullable = false, foreignKey = @ForeignKey(name = "fk_ProjectManager_Project"))
     private User projectManager;

     @Column(name="i_Value", nullable = false)
     private int value;

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
         Project user = (Project) o;
         return projectKey != null && Objects.equals(projectKey, user.projectKey);
     }

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
