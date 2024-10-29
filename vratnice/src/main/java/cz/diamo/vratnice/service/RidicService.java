package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.exceptions.UniqueValueException;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.repository.RidicRepository;
import jakarta.transaction.Transactional;

@Service
public class RidicService {

    @Autowired
    private RidicRepository ridicRepository;

    @Autowired
    private SpolecnostService spolecnostService;

    @Autowired
    private MessageSource messageSource;

    /**
     * Vytváří nového řidiče a ukládá ho do databáze.
     *
     * @param ridic Objekt {@link Ridic}, který obsahuje informace o řidiči, který
     *              má být vytvořen.
     * @return Uložený objekt {@link Ridic} s aktualizovanými informacemi.
     * @throws UniqueValueException   Pokud již existuje řidič se stejným číslem OP.
     * @throws NoSuchMessageException Pokud není nalezena zpráva pro daného řidiče.
     */
    @Transactional
    public Ridic create(Ridic ridic) throws UniqueValueException, NoSuchMessageException {
        Spolecnost savedSpolecnost = spolecnostService.save(ridic.getSpolecnost());
        ridic.setSpolecnost((savedSpolecnost));

        if (ridic.getIdRidic() == null || ridic.getIdRidic().isEmpty()) {
            if (ridicRepository.existsByCisloOp(ridic.getCisloOp())) {
                throw new UniqueValueException(
                        messageSource.getMessage("ridic.cisloOp.unique", null, LocaleContextHolder.getLocale()));
            }
        }
        return ridicRepository.save(ridic);
    }

    /**
     * Vrací seznam všech řidičů.
     *
     * @return Seznam {@link Ridic} obsahující všechny řidiče v databázi.
     */
    public List<Ridic> list() {
        return ridicRepository.findAll();
    }

    /**
     * Vrací detail řidiče na základě jeho ID.
     *
     * @param idRidic Identifikátor řidiče, jehož detail se má vrátit.
     * @return Objekt {@link Ridic} obsahující detailní informace o řidiči.
     */
    public Ridic getDetail(String idRidic) {
        return ridicRepository.getDetail(idRidic);
    }

    /**
     * Vrací řidiče na základě čísla OP.
     *
     * @param cisloOp Číslo OP, podle kterého se má řidič vyhledat.
     * @return Objekt {@link Ridic} odpovídající zadanému číslu OP.
     */
    public Ridic getRidicByCisloOp(String cisloOp) {
        return ridicRepository.getRidicByCisloOp(cisloOp);
    }

}
