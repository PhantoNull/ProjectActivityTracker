 package eu.Rationence.pat.model;


import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

 @Entity(name = "StandardActivity")
 @Table(name = "PAT_StandardActivities")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 public class StandardActivity implements Serializable {

     @Id
     @Column(name="c_Activity", length=32, unique = true, nullable = false)
     private String activityKey;

     @Column(name="f_Internal", nullable = false)
     private boolean internal;

     @Column(name="f_Waged", nullable = false)
     private boolean waged;

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
         StandardActivity user = (StandardActivity) o;
         return activityKey != null && Objects.equals(activityKey, user.activityKey);
     }

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
