package eu.Rationence.pat.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "ClientType")
@Table(name = "PAT_ClientTypes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ClientType implements Serializable {

    @Id
    @Column(name="c_ClientType", length=16, unique = true, nullable = false)
    private String clientType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ClientType utente = (ClientType) o;
        return clientType != null && Objects.equals(clientType, utente.clientType);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
