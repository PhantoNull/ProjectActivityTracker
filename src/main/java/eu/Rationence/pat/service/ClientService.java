package eu.Rationence.pat.service;



import eu.Rationence.pat.model.Client;
import eu.Rationence.pat.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    public Client findClientByClient(String string){return clientRepository.getClientByClient(string);
    }
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client saveClient(Client client){ return clientRepository.save(client);};

    public void deleteClientByClient(String client){ clientRepository.deleteClientByClient(client);}
}
