package eu.Rationence.pat.service;



import eu.Rationence.pat.model.ClientType;
import eu.Rationence.pat.repository.ClientTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class ClientTypeService {
    @Autowired
    private ClientTypeRepository clientTypeRepository;

    public ClientType findClientTypeByClientTypeKey(String string){return clientTypeRepository.getClientTypeByClientTypeKey(string);
    }
    public List<ClientType> findAll() {
        return clientTypeRepository.findAll();
    }

    public ClientType saveClientType(ClientType client){ return clientTypeRepository.save(client);};

    public void deleteClientTypeByClientTypeKey(String clientType){ clientTypeRepository.deleteClientTypeByClientTypeKey(clientType);}
}
