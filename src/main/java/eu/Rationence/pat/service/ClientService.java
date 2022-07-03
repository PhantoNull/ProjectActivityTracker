package eu.Rationence.pat.service;



import eu.Rationence.pat.model.Client;
import eu.Rationence.pat.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client find(String client){return clientRepository.getClientByClientKey(client);}

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client save(Client client){ return clientRepository.save(client);}

    public void delete(String client){ clientRepository.deleteClientByClientKey(client);}
}
