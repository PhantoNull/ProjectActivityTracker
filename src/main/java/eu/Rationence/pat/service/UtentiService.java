package eu.Rationence.pat.service;

import eu.Rationence.pat.model.User;
import eu.Rationence.pat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class UtentiService {
    @Autowired
    private UserRepository utentiRepository;

    public User findUtenteByUsername(String string){
        return utentiRepository.getUtenteByUsername(string);
    }

    public List<User> findAll() {
        return utentiRepository.findAll();
    }

    public User saveUser(User user){ return utentiRepository.save(user);};
}
