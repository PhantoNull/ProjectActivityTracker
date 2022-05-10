 package eu.Rationence.pat.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

 @Entity(name = "Activity")
 @Table(name = "PAT_Activities")
 @NoArgsConstructor
 @AllArgsConstructor
 @Builder
 @Getter
 @Setter
 @ToString
 @IdClass(ActivityCompositeKey.class)
 public class Activity implements Serializable {

     @Id
     @Column(name = "c_Project", nullable = false, length=16)
     private String project;

     @Id
     @Column(name="c_Activity", length=16, nullable = false)
     private String activityKey;

     @ManyToOne(fetch= FetchType.LAZY)
     @JoinColumn(name = "c_Project", foreignKey = @ForeignKey(name = "fk_Project_Activity"), insertable = false, updatable = false)
     private Project c_Project;

     @OneToMany(mappedBy = "activity")
     private List<UserActivity> userActivities;

     /*
     @ManyToMany(fetch= FetchType.LAZY)
     @JoinTable(name="PAT_UserActivities",
                joinColumns = { @JoinColumn(name = "c_Activity", foreignKey = @ForeignKey(name = "fk_Activity_UserActivity")),
                                @JoinColumn(name = "c_Project", foreignKey = @ForeignKey(name = "fk_Project_UserActivity")) },
                inverseJoinColumns = { @JoinColumn(name = "c_Username", foreignKey = @ForeignKey(name = "fk_User_UserActivity")) })
     Set<User> users  = new HashSet<>();
     */

     @ManyToOne(fetch=FetchType.LAZY)
     @JoinColumn(name = "c_ActivityType", nullable = false, foreignKey = @ForeignKey(name = "fk_ActivityType_Activity"))
     private ActivityType activityType;

     @Column(name="d_Start", nullable = false, length=16)
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateStart;

     @Column(name="d_End")
     @Temporal(TemporalType.DATE)
     @DateTimeFormat(pattern="yyyy-MM-dd")
     private Date dateEnd;

     @Column(name="f_Charged", nullable = false)
     private boolean charged;

     @Column(name="i_ManDays", nullable = false)
     private int manDays;

     @Override
     public int hashCode() {
         return getClass().hashCode();
     }
 }
