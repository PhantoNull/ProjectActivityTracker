package eu.Rationence.pat;
import eu.Rationence.pat.model.MonthlyNote;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.EmailService;
import eu.Rationence.pat.service.MonthlyNoteService;
import eu.Rationence.pat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class PatApplication {
	private final EmailService emailService;
	private final UserService userService;
	private final MonthlyNoteService monthlyNoteService;

	@Autowired
	public PatApplication(EmailService emailService, UserService userService, MonthlyNoteService monthlyNoteService) {
		this.emailService = emailService;
		this.userService = userService;
		this.monthlyNoteService = monthlyNoteService;
	}


	public static void main(String[] args) {
		SpringApplication.run(PatApplication.class, args);
	}

	@Scheduled(cron = "0 0 12 L * *")
	public void sendReminderTimeSheetEmail() throws ParseException {
		LocalDate currentDate = LocalDate.now();
		List<User> uncompiledTimeSheetUserList = userService.findAll();
		for (User user : uncompiledTimeSheetUserList) {
			MonthlyNote monthlyNote = monthlyNoteService.find(user.getUsername(), new SimpleDateFormat("dd-MM-yyyy").parse("1-" + currentDate.getMonthValue() + "-" + currentDate.getYear()));
			if (user.isEnabled() && monthlyNote != null && !monthlyNote.isLocked()) {
				try {
					emailService.sendSimpleMessage(user.getEmail(), "PAT - Reminder TimeSheet",
							"Gentile " + user.getName() + "<br><br>" +
									"Essendo l'ultimo giorno del mese e risultando al sistema che il tuo Time Sheet non " +
									"è stato ancora confermato viene inviato automaticamente questo promemoria " +
									"per ricordarti di compilarlo e confermarlo entro oggi.<br><br>" +
									"Grazie!");
				} catch(Exception e){
					System.out.println(e.getMessage());
				}
			}
		}
	}
}
