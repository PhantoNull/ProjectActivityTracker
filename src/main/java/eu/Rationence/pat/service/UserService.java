package eu.Rationence.pat.service;

import eu.Rationence.pat.model.User;
import eu.Rationence.pat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User findUserByUsername(String string){
        return userRepository.getUserByUsername(string);
    }

    public User findUserByEmail(String string){ return userRepository.getUsernameByEmail(string);}

    public List<User> findAll() { return userRepository.findAll();}

    public User saveUser(User user){ return userRepository.save(user);}

    public void deleteUserByUsername(String string){ userRepository.deleteUserByUsername(string);}
}
