package cz.dp.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.entity.JmenoKorektura;
import cz.dp.vratnice.repository.JmenoKorekturaRepository;
import jakarta.transaction.Transactional;

@Service
public class JmenoKorekturaService {

    @Autowired
    private JmenoKorekturaRepository jmenoKorekturaRepository;

    @Autowired
    private MessageSource messageSource;

    /**
     * Vytváří novou instanci {@link JmenoKorektura} nebo aktualizuje existující.
     *
     * @param jmenoKorektura Objekt {@link JmenoKorektura}, který se má vytvořit
     *                       nebo aktualizovat.
     * @return Uložený objekt {@link JmenoKorektura}.
     * @throws UniqueValueException   Pokud již existuje záznam se stejným vstupním
     *                                jménem.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public JmenoKorektura create(JmenoKorektura jmenoKorektura) throws UniqueValueException, NoSuchMessageException {
        if (jmenoKorektura.getIdJmenoKorektura() == null || jmenoKorektura.getIdJmenoKorektura().isEmpty()) {
            if (jmenoKorekturaRepository.existsByJmenoVstup(jmenoKorektura.getJmenoVstup()))
                throw new UniqueValueException(
                        messageSource.getMessage("jmeno_korektura.jmeno_vstup.unique", null,
                                LocaleContextHolder.getLocale()));

        }
        return jmenoKorekturaRepository.save(jmenoKorektura);
    }

    /**
     * Vrací seznam všech instancí {@link JmenoKorektura}.
     *
     * @return Seznam objektů {@link JmenoKorektura}.
     */
    public List<JmenoKorektura> list() {
        return jmenoKorekturaRepository.findAll();
    }

    /**
     * Vrací instanci {@link JmenoKorektura} na základě zadaného vstupního jména.
     *
     * @param jmenoVstup Vstupní jméno, podle kterého se hledá objekt
     *                   JmenoKorektura.
     * @return Objekt {@link JmenoKorektura} odpovídající zadanému vstupnímu jménu.
     */
    public JmenoKorektura getByJmenoVstup(String jmenoVstup) {
        return jmenoKorekturaRepository.getByJmenoVstup(jmenoVstup);
    }

}
