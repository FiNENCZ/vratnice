package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.NajemnikNavstevnickaKarta;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.NajemnikNavstevnickaKartaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class NajemnikNavstevnickaKartaService {

    @Autowired
    private NajemnikNavstevnickaKartaRepository najemnikNavstevnickaKartaRepository;

    @Autowired
    private SpolecnostService spolecnostService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    /**
     * Vytváří novou instanci {@link NajemnikNavstevnickaKarta} a ukládá ji do
     * databáze.
     *
     * @param najemnikNavstevnickaKarta Objekt {@link NajemnikNavstevnickaKarta},
     *                                  který se má vytvořit.
     * @return Uložený objekt {@link NajemnikNavstevnickaKarta}.
     * @throws UniqueValueException   Pokud již existuje záznam se stejným číslem
     *                                OP.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    @Transactional
    public NajemnikNavstevnickaKarta create(NajemnikNavstevnickaKarta najemnikNavstevnickaKarta)
            throws UniqueValueException, NoSuchMessageException {
        Spolecnost savedSpolecnost = spolecnostService.save(najemnikNavstevnickaKarta.getSpolecnost());
        najemnikNavstevnickaKarta.setSpolecnost((savedSpolecnost));

        if (najemnikNavstevnickaKarta.getIdNajemnikNavstevnickaKarta() == null
                || najemnikNavstevnickaKarta.getIdNajemnikNavstevnickaKarta().isEmpty()) {
            if (najemnikNavstevnickaKartaRepository.existsByCisloOp(najemnikNavstevnickaKarta.getCisloOp())) {
                throw new UniqueValueException(
                        messageSource.getMessage("navsteva_osoba.cislo_op.unique", null,
                                LocaleContextHolder.getLocale()));
            }
        }
        najemnikNavstevnickaKarta.setCasZmn(Utils.getCasZmn());
        najemnikNavstevnickaKarta.setZmenuProvedl(Utils.getZmenuProv());
        return najemnikNavstevnickaKartaRepository.save(najemnikNavstevnickaKarta);
    }

    /**
     * Vrací seznam {@link NajemnikNavstevnickaKarta} na základě zadané aktivity.
     *
     * @param aktivita
     * @return Seznam uložených objektů {@link NajemnikNavstevnickaKarta}.
     */
    public List<NajemnikNavstevnickaKarta> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from NajemnikNavstevnickaKarta s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<NajemnikNavstevnickaKarta> list = vysledek.getResultList();
        return list;
    }

    /**
     * Vrací detail {@link NajemnikNavstevnickaKarta} na základě zadaného ID.
     *
     * @param idNajemnikNavstevnickaKarta ID objektu
     *                                    {@link NajemnikNavstevnickaKarta},
     *                                    jehož detail se má vrátit.
     * @return Objekt {@link NajemnikNavstevnickaKarta} odpovídající zadanému ID.
     */
    public NajemnikNavstevnickaKarta getDetail(String idNajemnikNavstevnickaKarta) {
        return najemnikNavstevnickaKartaRepository.getDetail(idNajemnikNavstevnickaKarta);
    }

    /**
     * Vrací {@link NajemnikNavstevnickaKarta} na základě čísla OP.
     *
     * @param cisloOp Číslo OP, podle kterého se má vyhledat objekt
     *                {@link NajemnikNavstevnickaKarta}.
     * @return Objekt {@link NajemnikNavstevnickaKarta} odpovídající zadanému číslu
     *         OP.
     */
    public NajemnikNavstevnickaKarta getByCisloOp(String cisloOp) {
        return najemnikNavstevnickaKartaRepository.getByCisloOp(cisloOp);
    }

}
