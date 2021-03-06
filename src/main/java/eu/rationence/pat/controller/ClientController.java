package eu.rationence.pat.controller;

import eu.rationence.pat.model.ClientType;
import eu.rationence.pat.model.User;
import eu.rationence.pat.service.ClientService;
import eu.rationence.pat.service.ClientTypeService;
import eu.rationence.pat.model.Client;
import eu.rationence.pat.service.UserService;
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
    private static final String CLASS_DESC = "Client";

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
                return AdviceController.responseBadRequest(CLASS_DESC + " key can't be blank");
            else if(client.getClientDesc().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " description can't be blank");
            ClientType clientTypeRepo = clientTypeService.find(clientTypeKey);
            if(clientTypeRepo == null)
                return AdviceController.responseNotFound(clientTypeKey + " is not a valid client type");
            clientService.save(client);
            return AdviceController.responseOk(CLASS_DESC + " '" + client.getClientKey() + "' saved");
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
                return AdviceController.responseNotFound(clientTypeKey + " is not a valid " +
                        this.getClass().getSimpleName() + " type");
            Client clientRepo = clientService.find(client.getClientKey());
            if(clientRepo == null)
                return AdviceController.responseNotFound(client.getClientKey() + " does not exists");
            else if(client.getClientDesc().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " description can't be blank");
            clientService.save(client);
            return AdviceController.responseOk(CLASS_DESC + " '" + client.getClientKey() + "' updated");
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
            return AdviceController.responseOk(CLASS_DESC + " '" + client.getClientKey() + "' successfully deleted");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + client.getClientKey() + "' (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
