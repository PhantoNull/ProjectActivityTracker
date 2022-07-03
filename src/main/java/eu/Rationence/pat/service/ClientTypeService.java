package eu.Rationence.pat.service;

import eu.Rationence.pat.model.ClientType;
import eu.Rationence.pat.repository.ClientTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientTypeService {
    private final ClientTypeRepository clientTypeRepository;
    @Autowired
    public ClientTypeService(ClientTypeRepository clientTypeRepository) {
        this.clientTypeRepository = clientTypeRepository;
    }

    public ClientType find(String clientType){return clientTypeRepository.getClientTypeByClientTypeKey(clientType);
    }
    public List<ClientType> findAll() {
        return clientTypeRepository.findAll();
    }

    public ClientType save(ClientType client){ return clientTypeRepository.save(client);}

    public void delete(String clientType){ clientTypeRepository.deleteClientTypeByClientTypeKey(clientType);}
}
