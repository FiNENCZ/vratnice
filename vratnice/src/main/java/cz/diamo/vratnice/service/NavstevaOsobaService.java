package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.NavstevaOsoba;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.NavstevaOsobaRepository;
import jakarta.transaction.Transactional;

@Service
public class NavstevaOsobaService {

    @Autowired
    private NavstevaOsobaRepository navstevaOsobaRepository;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SpolecnostService spolecnostService;

    /**
     * Vytváří novou instanci {@link NavstevaOsoba} a ukládá ji do databáze.
     *
     * @param navstevaOsoba Objekt {@link NavstevaOsoba}, který se má vytvořit.
     * @return Uložený objekt {@link NavstevaOsoba}.
     * @throws UniqueValueException   Pokud již existuje záznam se stejným číslem
     *                                OP.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public NavstevaOsoba create(NavstevaOsoba navstevaOsoba) throws UniqueValueException, NoSuchMessageException {
        Spolecnost savedSpolecnost = spolecnostService.save(navstevaOsoba.getSpolecnost());
        navstevaOsoba.setSpolecnost((savedSpolecnost));

        if (navstevaOsoba.getIdNavstevaOsoba() == null || navstevaOsoba.getIdNavstevaOsoba().isEmpty()) {
            if (navstevaOsobaRepository.existsByCisloOp(navstevaOsoba.getCisloOp())) {
                throw new UniqueValueException(
                        messageSource.getMessage("navsteva_osoba.cislo_op.unique", null,
                                LocaleContextHolder.getLocale()));
            }
        }
        return navstevaOsobaRepository.save(navstevaOsoba);
    }

    /**
     * Vrací seznam všech {@link NavstevaOsoba} z databáze.
     *
     * @return Seznam objektů {@link NavstevaOsoba}.
     */
    public List<NavstevaOsoba> list() {
        return navstevaOsobaRepository.findAll();
    }

    /**
     * Vrací detail {@link NavstevaOsoba} na základě zadaného ID.
     *
     * @param idNavstevaOsoba ID objektu {@link NavstevaOsoba},
     *                        jehož detail se má vrátit.
     * @return Objekt {@link NavstevaOsoba} odpovídající zadanému ID.
     */
    public NavstevaOsoba getDetail(String idNavstevaOsoba) {
        return navstevaOsobaRepository.getDetail(idNavstevaOsoba);
    }

    /**
     * Vrací {@link NavstevaOsoba} na základě čísla OP.
     *
     * @param cisloOp Číslo OP, podle kterého se má vyhledat objekt
     *                {@link NavstevaOsoba}.
     * @return Objekt {@link NavstevaOsoba} odpovídající zadanému číslu OP.
     */
    public NavstevaOsoba getByCisloOp(String cisloOp) {
        return navstevaOsobaRepository.geByCisloOp(cisloOp);
    }
}
