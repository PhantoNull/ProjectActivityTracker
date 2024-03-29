package eu.rationence.pat.repository;

import eu.rationence.pat.model.Client;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientRepository extends CrudRepository<Client, String> {
    Client getClientByClientKey(String clientKey);

    List<Client> findAll();

    <S extends Client> S save(S client);

    @Transactional
    void deleteClientByClientKey(String string);
}


