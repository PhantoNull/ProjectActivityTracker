 package eu.Rationence.pat.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

 @Entity(name = "MonthlyNote")
 @Table(name = "PAT_MonthlyNotes")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 @IdClass(MonthlyNoteCompositeKey.class)
 public class MonthlyNote implements Serializable {

     @Id
     @Column(name = "c_Username", nullable = false, length=64)
     private String username;

     @Id
     @Column(name="d_Date", nullable = false)
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="MM-dd")
     private Date date;

     @ManyToOne
     @JoinColumn(name = "c_Username",
             nullable = false,
             foreignKey = @ForeignKey(name = "fk_MonthlyNote_User"), insertable = false, updatable = false)
     private User c_Username;

     @Column(name="x_Note", length = 65000, nullable = false)
     private String note;

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
