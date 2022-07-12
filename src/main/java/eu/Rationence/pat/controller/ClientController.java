package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Client;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.model.ClientType;
import eu.Rationence.pat.service.UserService;
import eu.Rationence.pat.service.ClientService;
import eu.Rationence.pat.service.ClientTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class ClientController {
    private final ClientService clientService;
    private final UserService userService;
    private final ClientTypeService clientTypeService;

    @Autowired
    public ClientController(ClientService clientService, UserService userService, ClientTypeService clientTypeService) {
        this.clientService = clientService;
        this.userService = userService;
        this.clientTypeService = clientTypeService;
    }

    @GetMapping("/clients")
    public String clients(Model model, Principal principal) {
        model.addAttribute("clientList", clientService.findAll());
        model.addAttribute("clientTypeList", clientTypeService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUser(username);
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
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            if(clientService.find(client.getClientKey()) != null)
                return AdviceController.responseConflict(client.getClientKey() + " has been already created");
            else if(client.getClientKey().length() < 1)
                return AdviceController.responseBadRequest("Client key can't be blank");
            else if(client.getClientDesc().length() < 1)
                return AdviceController.responseBadRequest("Client description can't be blank");
            ClientType clientTypeRepo = clientTypeService.find(clientTypeKey);
            if(clientTypeRepo == null)
                return AdviceController.responseNotFound(clientTypeKey + " is not a valid client type. (not found)");
            clientService.save(client);
            return AdviceController.responseOk("Client '" + client.getClientKey() + "' saved.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/clients")
    public ResponseEntity<String> updateClient(@Valid Client client,
                                             @RequestParam(value="clientType") String clientTypeKey,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            ClientType clientTypeRepo = clientTypeService.find(clientTypeKey);
            if(clientTypeRepo == null)
                return AdviceController.responseNotFound(clientTypeKey + " is not a valid client type. (not found)");
            Client clientRepo = clientService.find(client.getClientKey());
            if(clientRepo == null)
                return AdviceController.responseNotFound(client.getClientKey() + " does not exists");
            else if(client.getClientDesc().length() < 1)
                return AdviceController.responseBadRequest("Client description can't be blank");
            clientService.save(client);
            return AdviceController.responseOk("Client '" + client.getClientKey() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/clients")
    public ResponseEntity<String> deleteClient(@Valid Client client,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Client clientRepo = clientService.find(client.getClientKey());
            if(clientRepo == null)
                return AdviceController.responseNotFound(client.getClientKey() + " does not exists");
            clientService.delete(client.getClientKey());
            return AdviceController.responseOk("Client '" + client.getClientKey() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete client '" + client.getClientKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
