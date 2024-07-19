package cz.diamo.vratnice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.vratnice.entity.Stat;
import cz.diamo.vratnice.repository.StatRepository;

@Service
public class StatService {

    @Autowired
    private StatRepository statRepository;

    @Autowired
    private MessageSource messageSource;

    public Stat getByNazev(String nazev) {

        Stat stat = statRepository.getByNazev(nazev);
        try {
            if (stat == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("stat.nazev.not_found", null, LocaleContextHolder.getLocale()));
        
            return stat;
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

}
