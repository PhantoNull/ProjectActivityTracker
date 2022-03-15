package eu.Rationence.pat.service;

import eu.Rationence.pat.model.Utente;
import eu.Rationence.pat.repository.UtentiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UtentiService {
    @Autowired
    private UtentiRepository utentiRepository;
    public Utente findUtenteByUsername(String string){return utentiRepository.getUtenteByUsername(string);}
    public List<Utente> findAll() {return utentiRepository.findAll();}
}
