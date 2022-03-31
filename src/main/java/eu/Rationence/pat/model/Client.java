package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "Client")
@Table(name = "PAT_Clients")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Client implements Serializable {

    @Id
    @Column(name="c_Client", length=16, unique = true, nullable = false)
    private String client;

    @Column(name="x_Client", length=64, nullable = false)
    private String clientDesc;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "c_ClientType")
    private ClientType clientType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Client utente = (Client) o;
        return client != null && Objects.equals(client, utente.client);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
