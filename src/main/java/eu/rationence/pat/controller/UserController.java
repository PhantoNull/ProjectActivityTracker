package eu.rationence.pat.controller;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.MonthlyNote;
import eu.rationence.pat.model.Role;
import eu.rationence.pat.model.Team;
import eu.rationence.pat.model.User;
import eu.rationence.pat.model.dto.UserDTO;
import eu.rationence.pat.service.*;
import org.apache.commons.validator.routines.EmailValidator;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class UserController {
    private static final String CLASS_DESC = "User";
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TeamService teamService;
    private final RoleService roleService;
    private final EmailService emailService;
    private final MonthlyNoteService monthlyNoteService;
    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(ModelMapper modelMapper, UserService userService, TeamService teamService, RoleService roleService, EmailService emailService, MonthlyNoteService monthlyNoteService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.teamService = teamService;
        this.roleService = roleService;
        this.emailService = emailService;
        this.monthlyNoteService = monthlyNoteService;
    }

    @GetMapping("/users")
    public String utenti(Model model, Principal principal) throws ParseException {
        List<User> userList = userService.findAll();
        model.addAttribute("listaUtenti", userList);
        LocalDate currentDate = LocalDate.now();
        int month = currentDate.getMonthValue();
        int year = currentDate.getYear();
        if (currentDate.getDayOfMonth() <= 10) {
            month = currentDate.getMonthValue() - 1;
            if (month == 0) {
                month = 12;
                year = year - 1;
            }
        }
        Date monthlyNoteDate = new SimpleDateFormat("dd-MM-yyyy").parse("1-" + month + "-" + year);
        for (User user : userList) {
            if (!user.isEnabled()) {
                model.addAttribute("sheet." + user.getUsername(), "has-text-grey");
                continue;
            }
            MonthlyNote userMonthlyNote = monthlyNoteService.find(user.getUsername(), monthlyNoteDate);
            if (userMonthlyNote == null || !userMonthlyNote.isLocked())
                model.addAttribute("sheet." + user.getUsername(), "has-text-danger-dark");
            else
                model.addAttribute("sheet." + user.getUsername(), "has-text-primary");
        }

        model.addAttribute("listaTeams", teamService.findAll());
        model.addAttribute("listaRuoli", roleService.findAll());
        model.addAttribute("pageTitle", "PAT Prova");
        String username = principal.getName();
        User userRepo = userService.find(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "users";
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Valid UserDTO userDTO,
                                          @RequestParam(value = "team") String teamKey,
                                          @RequestParam(value = "role") String roleKey,
                                          @RequestParam(value = "cost") String cost,
                                          BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User user = modelMapper.map(userDTO, User.class);
            if (userService.find(user.getUsername()) != null)
                return AdviceController.responseConflict(user.getUsername() + " has been already created");
            ResponseEntity<String> validityError = checkUserValidity(user, teamKey, roleKey, cost);
            if (validityError != null)
                return validityError;
            else if (user.getUsername().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " key can't be blank");
            Pattern p = Pattern.compile("[^a-z0-9._-]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(user.getUsername());
            if(m.find())
                return AdviceController.responseBadRequest("Cannot create " + CLASS_DESC + ". Username should contain only allowed characters: [a-zA-Z0-9._-]");
            Team teamRepo = teamService.find(teamKey);
            Role roleRepo = roleService.find(roleKey);
            user.setTeam(teamRepo);
            user.setRole(roleRepo);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            Base64.Encoder encoderPsw = Base64.getUrlEncoder().withoutPadding();
            String token = encoderPsw.encodeToString(bytes);
            user.setPasswordHash(encoder.encode(token));
            userService.save(user);
            emailService.sendSimpleMessage(user.getEmail(), "Creazione Account - Amministrazione", "Gentile " + user.getName() + ", <br>" +
                    "<br><br>    ricevi questa mail a seguito della creazione del tuo account per l'applicativo <strong>Project Activity Tracking</strong>.<br>" +
                    "   Le credenziali di accesso sono username: <br><strong> " + user.getUsername() + "</strong><br>    e password generata casualmente: <br>   <strong>"
                    + token + "</strong><br><br>" +
                    "   Consigliamo di cambiarla dopo il primo accesso e di seguire le best-practices per tenere le password al sicuro.");
            return AdviceController.responseOk("'" + user.getUsername() + "' created. A random generated password has been sent to user email address.");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/users")
    public ResponseEntity<String> updateUser(@Valid UserDTO userDTO,
                                             @RequestParam(value = "team") String teamKey,
                                             @RequestParam(value = "role") String roleKey,
                                             @RequestParam(value = "cost") String cost,
                                             BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User user = modelMapper.map(userDTO, User.class);
            User userRepo = userService.find(user.getUsername());
            if (userRepo == null)
                return AdviceController.responseNotFound("Can't update " + CLASS_DESC + " '" + user.getUsername() + "'.");
            ResponseEntity<String> validityError = checkUserValidity(user, teamKey, roleKey, cost);
            if (validityError != null)
                return validityError;
            user.setPasswordHash(userRepo.getPasswordHash());
            Team teamRepo = teamService.find(teamKey);
            Role roleRepo = roleService.find(roleKey);
            user.setTeam(teamRepo);
            user.setRole(roleRepo);
            userService.save(user);
            return AdviceController.responseOk(CLASS_DESC + " '" + user.getUsername() + "' updated");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@Valid UserDTO userDTO,
                                             BindingResult result,
                                             Principal principal) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User user = modelMapper.map(userDTO, User.class);
            User userRepo = userService.find(user.getUsername());
            if (userRepo == null)
                return AdviceController.responseNotFound("Cannot delete " + CLASS_DESC + " '" + user.getUsername() + "'.");
            String username = principal.getName();
            if (userRepo.getUsername().equalsIgnoreCase(username))
                return AdviceController.responseForbidden("Cannot delete your account while logged in");
            userService.delete(user.getUsername());
            return AdviceController.responseOk(CLASS_DESC + " '" + user.getUsername() + "' successfully deleted");
        } catch (DataIntegrityViolationException e) {
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + "'" + userDTO.getUsername() + "' (Constraint violation)");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PostMapping("/resetPasswordUser")
    public ResponseEntity<String> resetPasswordUser(@Valid UserDTO userDTO,
                                                    BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User user = modelMapper.map(userDTO, User.class);
            User userRepo = userService.find(user.getUsername());
            if (userRepo == null)
                return AdviceController.responseNotFound("Can't reset " + user.getUsername() + "'s password");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[16];
            random.nextBytes(bytes);
            Base64.Encoder encoderPsw = Base64.getUrlEncoder().withoutPadding();
            String token = encoderPsw.encodeToString(bytes);
            userRepo.setPasswordHash(encoder.encode(token));

            userService.save(userRepo);

            emailService.sendSimpleMessage(userRepo.getEmail(), "Reset Password - Amministrazione", "Gentile " + userRepo.getName() + ", <br>" +
                    "<br>    ricevi questa mail a seguito di un reset della tua password lato amministrativo.<br><br>" +
                    "    La tua nuova password generata casualmente è la seguente: <br>  <strong> "
                    + token + "</strong> <br><br>" +
                    "   Consigliamo di cambiarla al prossimo accesso seguendo le best-practices per tenere le password al sicuro.");
            return AdviceController.responseOk("'" + user.getUsername() + "'s password reset.");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PostMapping("/changePasswordUser")
    public ResponseEntity<String> changePasswordUser(@RequestParam(value = "password") String newPass,
                                                     Principal principal) {
        try {
            String username = principal.getName();
            User userRepo = userService.find(username);
            if (userRepo == null)
                return AdviceController.responseNotFound("Can't reset " + CLASS_DESC + " " + username + "'s password");
            Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,64}$");
            Matcher matcher = pattern.matcher(newPass);
            if (!matcher.find())
                return AdviceController.responseBadRequest("Password does not match minimum requirements (server)");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepo.setPasswordHash(encoder.encode(newPass));
            userService.save(userRepo);
            return AdviceController.responseOk("Your password has been successfully changed");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PostMapping("/sendReminder")
    public ResponseEntity<String> changePasswordUser(Principal principal) throws ParseException {
        sendReminderTimeSheetEmail();
        return AdviceController.responseOk("Reminder sent to all users whom have not compiled yet");
    }

    @Scheduled(cron = "0 0 13 1-10,L-3 * *")
    public void sendReminderTimeSheetEmail() throws ParseException {
        LocalDate currentDate = LocalDate.now();
        if(currentDate.getDayOfMonth() <= 10){
            currentDate = currentDate.minusMonths(1);
        }
        List<User> allUserList = userService.findAll();
        for (User user : allUserList) {
            MonthlyNote monthlyNote = monthlyNoteService.find(user.getUsername(), new SimpleDateFormat("dd-MM-yyyy").parse("1-" + currentDate.getMonthValue() + "-" + currentDate.getYear()));
            if (user.isEnabled() && (monthlyNote == null || !monthlyNote.isLocked())) {
                try {
                    emailService.sendSimpleMessage(user.getEmail(), "PAT - Reminder TimeSheet",
                            "Gentile " + user.getName() + "<br><br>" +
                                    "Risultando al sistema che il tuo Time Sheet per la mensilità " + currentDate.getMonthValue() +"/"+ currentDate.getYear() + " non sia stato  " +
                                    "ancora confermato viene inviato automaticamente questo promemoria " +
                                    "per ricordarti di compilarlo e confermarlo il prima possibile.<br><br>" +
                                    "Grazie!");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }

    private ResponseEntity<String> checkUserValidity(User user, String teamKey, String roleKey, String cost) {
        if (!user.equals(userService.findUserByEmail(user.getEmail())) && userService.findUserByEmail(user.getEmail()) != null)
            return AdviceController.responseConflict(user.getEmail() + " email is already used by another user");
        if (!EmailValidator.getInstance().isValid(user.getEmail()))
            return AdviceController.responseBadRequest(user.getUsername() + "'s email '" + user.getEmail() + "' is not valid");
        if (!AdviceController.isStringPositiveDecimal(user.getTime()) || user.getTime().length() != 5)
            return AdviceController.responseBadRequest(user.getUsername() + "'s time '" + user.getTime() + "' is not valid");
        if (!AdviceController.isStringPositiveDecimal(cost))
            return AdviceController.responseBadRequest(user.getUsername() + "'s cost '" + cost + "' is not valid");
        Team teamRepo = teamService.find(teamKey);
        Role roleRepo = roleService.find(roleKey);
        if (teamRepo == null)
            return AdviceController.responseNotFound("Team " + teamKey + " does not exits");
        if (roleRepo == null)
            return AdviceController.responseNotFound("Role " + roleKey + " does not exist");
        return null;
    }
}
