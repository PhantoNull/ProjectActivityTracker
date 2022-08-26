package eu.rationence.pat.service;

import eu.rationence.pat.model.User;
import eu.rationence.pat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User find(String username){
        return userRepository.getUserByUsername(username);
    }

    public User findUserByEmail(String email){ return userRepository.getUserByEmail(email);}

    public List<User> findAll() { return userRepository.findAll();}

    public User save(User user){ return userRepository.save(user);}

    public void delete(String username){ userRepository.deleteUserByUsername(username);}
}
