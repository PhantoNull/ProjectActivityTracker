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

    public ClientType findClientTypeByClientType(String string){return clientTypeRepository.getClientTypeByClientType(string);
    }
    public List<ClientType> findAll() {
        return clientTypeRepository.findAll();
    }

    public ClientType saveClientType(ClientType client){ return clientTypeRepository.save(client);};

    public void deleteClientTypeByClientType(String clientType){ clientTypeRepository.deleteClientTypeByClientType(clientType);}
}
