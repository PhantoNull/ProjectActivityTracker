package eu.Rationence.pat.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Entity(name = "User")
@Table(name = "User")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Utente implements Serializable {

    @Id
    @Column(name="c_UserName", length=32, unique = true, nullable = false)
    private String username;

    @Column(name="e_Email", unique = true, nullable = false)
    @Email
    private String email;

    @Column(name="x_Name", nullable = false, length=32)
    private String name;

    @Column(name="x_Surame", nullable = false, length=64)
    private String surname;

    @Column(name="c_Role", nullable = false, length=16)
    private String role;

    @Column(name="i_Cost", nullable = false)
    private float cost;

    @Column(name="x_Time", nullable = false, length=5)
    private String time;

    @Column(name="h_Password", nullable = false, length=255)
    private String passwordHash;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="h_Salt", length=5, nullable = false)
    private String passwordSalt;
}
