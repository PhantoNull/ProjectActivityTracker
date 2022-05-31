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

    public User findUser(String username){
        return userRepository.getUserByUsername(username);
    }

    public User findUserByEmail(String email){ return userRepository.getUsernameByEmail(email);}

    public List<User> findAll() { return userRepository.findAll();}

    public User save(User user){ return userRepository.save(user);}

    public void delete(String username){ userRepository.deleteUserByUsername(username);}
}
