package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientRepository extends CrudRepository<Client, String> {
    Client getClientByClient(String string);
    List<Client> findAll();
    Client save(Client client);
    @Transactional
    void deleteClientByClient(String string);
}

