package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.ClientType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ClientTypeRepository extends CrudRepository<ClientType, String> {
    ClientType getClientTypeByClientTypeKey(String string);
    List<ClientType> findAll();
    ClientType save(ClientType client);
    @Transactional
    void deleteClientTypeByClientTypeKey(String string);
}


