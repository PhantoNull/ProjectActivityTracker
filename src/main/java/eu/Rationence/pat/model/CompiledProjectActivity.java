 package eu.Rationence.pat.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

 @Entity(name = "CompiledProjectActivity")
 @Table(name = "PAT_CompiledProjectActivities")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 @IdClass(CompiledProjectActivityCompositeKey.class)
 public class CompiledProjectActivity implements Serializable {

     @Id
     @Column(name = "c_Username", nullable = false, length=64)
     private String username;

     @Id
     @Column(name = "c_Location", nullable = false, length=16)
     private String locationName;

     @Id
     @Column(name = "c_Project", nullable = false, length=16)
     private String project;

     @Id
     @Column(name="c_Activity", length=16, nullable = false)
     private String activityKey;

     @Id
     @Column(name="d_Date", nullable = false)
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date date;

     @ManyToOne
     @JoinColumns({ @JoinColumn(name = "c_Activity", foreignKey = @ForeignKey(name = "fk_Activity_CompiledProjectActivity"), insertable = false, updatable = false),
                    @JoinColumn(name = "c_Project", foreignKey = @ForeignKey(name = "fk_Project_CompiledProjectActivity"), insertable = false, updatable = false) })
     private ProjectActivity c_Activity;

     @ManyToOne
     @JoinColumn(name = "c_Username",
             nullable = false,
             foreignKey = @ForeignKey(name = "fk_CompiledProjectActivity_User"), insertable = false, updatable = false)
     private User c_Username;

     @ManyToOne
     @JoinColumn(name = "c_Location",
             nullable = false,
             foreignKey = @ForeignKey(name = "fk_CompiledProjectActivity_Location"), insertable = false, updatable = false)
     private Location c_Location;

     @Column(name="n_Hours", nullable = false)
     private int hours;

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
