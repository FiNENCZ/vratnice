package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.JmenoKorektura;
import cz.diamo.vratnice.repository.JmenoKorekturaRepository;
import jakarta.transaction.Transactional;

@Service
public class JmenoKorekturaService {

    @Autowired
    private JmenoKorekturaRepository jmenoKorekturaRepository;

    @Autowired
	private MessageSource messageSource;

    @Transactional
    public JmenoKorektura create(JmenoKorektura jmenoKorektura) throws UniqueValueException, NoSuchMessageException {
        if (jmenoKorektura.getIdJmenoKorektura() == null || jmenoKorektura.getIdJmenoKorektura().isEmpty()){
            if(jmenoKorekturaRepository.existsByJmenoVstup(jmenoKorektura.getJmenoVstup())) 
                throw new UniqueValueException(
                    messageSource.getMessage("jmeno_korektura.jmeno_vstup.unique", null, LocaleContextHolder.getLocale()));
            
        }
        return jmenoKorekturaRepository.save(jmenoKorektura);
    }

    public List<JmenoKorektura> list() {
        return jmenoKorekturaRepository.findAll();
    }

    public JmenoKorektura getByJmenoVstup(String jmenoVstup) {
        return jmenoKorekturaRepository.getByJmenoVstup(jmenoVstup);
    }

}
