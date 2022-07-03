 package eu.Rationence.pat.model;

import eu.Rationence.pat.model.compositeKeys.CompiledStandardActivityCompositeKey;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

 @Entity(name = "CompiledStandardActivity")
 @Table(name = "PAT_CompiledStandardActivities")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 @IdClass(CompiledStandardActivityCompositeKey.class)
 public class CompiledStandardActivity implements Serializable {

     @Id
     @Column(name = "c_Username", nullable = false, length=64)
     private String username;

     @Id
     @Column(name = "c_Location", nullable = false, length=16)
     private String locationName;

     @Id
     @Column(name="c_Activity", length=32, nullable = false)
     private String activityKey;

     @Id
     @Column(name="d_Date", nullable = false)
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date date;

     @ManyToOne
     @JoinColumn(name = "c_Activity", foreignKey = @ForeignKey(name = "fk_Activity_CompiledStandardActivity"), insertable = false, updatable = false)
     private StandardActivity c_Activity;

     @ManyToOne
     @JoinColumn(name = "c_Username",
             nullable = false,
             foreignKey = @ForeignKey(name = "fk_CompiledStandardActivity_User"), insertable = false, updatable = false)
     private User c_Username;

     @ManyToOne
     @JoinColumn(name = "c_Location",
             nullable = false,
             foreignKey = @ForeignKey(name = "fk_CompiledStandardActivity_Location"), insertable = false, updatable = false)
     private Location c_Location;

     @Column(name="n_Hours", nullable = false)
     private int hours;

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
