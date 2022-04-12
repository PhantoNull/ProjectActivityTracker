package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Client;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.model.ClientType;
import eu.Rationence.pat.service.UserService;
import eu.Rationence.pat.service.ClientService;
import eu.Rationence.pat.service.ClientTypeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@AllArgsConstructor
public class ClientController {
    private static final String ERROR_STR = "ERROR: ";

    @Autowired
    private final ClientService clientService;

    @Autowired
    private final UserService userService;

    @Autowired
    private final ClientTypeService clientTypeService;

    @GetMapping("/clients")
    public String clients(Model model, Principal principal) {
        model.addAttribute("clientList", clientService.findAll());
        model.addAttribute("clientTypeList", clientTypeService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "clients";
    }

    @PostMapping("/clients")
    public ResponseEntity<String> addClient(@Valid Client client,
                                          @RequestParam(value="clientType") String clientTypeKey,
                                          BindingResult result){
        try{

            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            if(clientService.findClientByClientKey(client.getClientKey()) != null)
                return ResponseEntity.status(409).body(ERROR_STR + client.getClientKey() + " has been already created");
            else if(client.getClientKey().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Client key can't be blank");
            else if(client.getClientDesc().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Client description can't be blank");
            ClientType clientTypeRepo = clientTypeService.findClientTypeByClientTypeKey(clientTypeKey);
            if(clientTypeRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + clientTypeKey + " is not a valid client type. (not found)");
            client.setClientType(clientTypeRepo);
            clientService.saveClient(client);
            return ResponseEntity.ok("Client '" + client.getClientKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @PutMapping("/clients")
    public ResponseEntity<String> updateClient(@Valid Client client,
                                             @RequestParam(value="clientType") String clientTypeKey,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            ClientType clientTypeRepo = clientTypeService.findClientTypeByClientTypeKey(clientTypeKey);
            if(clientTypeRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + clientTypeKey + " is not a valid client type. (not found)");
            Client clientRepo = clientService.findClientByClientKey(client.getClientKey());
            if(clientRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + client.getClientKey() + " does not exists");
            client.setClientType(clientTypeRepo);
            clientService.saveClient(client);
            return ResponseEntity.ok("Client '" + client.getClientKey() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @DeleteMapping("/clients")
    public ResponseEntity<String> deleteClient(@Valid Client client,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            Client clientRepo = clientService.findClientByClientKey(client.getClientKey());
            if(clientRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + client.getClientKey() + " does not exists");
            clientService.deleteClientByClientKey(client.getClientKey());
            return ResponseEntity.ok("Client '" + client.getClientKey() + "' successfully deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ERROR_STR + "Empty input or mismatched input type");
    }
}
