package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalROE;
import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.exceptions.ValidationException;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.ZadostKlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
@TransactionalROE
public class ZadostKlicService {

    @Autowired
    private ZadostKlicRepository zadostiKlicRepository;

    @Autowired
    private BudovaVratniceService budovaVratniceService;

    @Autowired
    private MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    /**
     * Vrací seznam objektů {@link ZadostKlic} na základě zadaných filtrů.
     *
     * @param aktivita       Boolean hodnota.
     * @param idUzivatel     ID uživatele, jehož žádosti se mají vrátit.
     * @param zadostStavEnum Stav žádosti jako {@link ZadostStavEnum}.
     * @param appUserDto     Objekt {@link AppUserDto} obsahující informace o
     *                       uživateli.
     * @return Seznam objektů {@link ZadostKlic} odpovídajících zadaným filtrům.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<ZadostKlic> getList(Boolean aktivita, String idUzivatel, ZadostStavEnum zadostStavEnum,
            AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        String idVratny = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM ZadostKlic s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (idUzivatel != null)
            queryString.append("AND s.uzivatel.idUzivatel = :idUzivatelVypujcky ");

        if (zadostStavEnum != null)
            queryString.append("AND s.zadostStav.idZadostStav = :stav ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.klic.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idVratny);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (idUzivatel != null)
            vysledek.setParameter("idUzivatelVypujcky", idUzivatel);

        if (zadostStavEnum != null)
            vysledek.setParameter("stav", zadostStavEnum.getValue());

        @SuppressWarnings("unchecked")
        List<ZadostKlic> list = vysledek.getResultList();

        if (list != null) {
            for (ZadostKlic zadostKlic : list) {
                zadostKlic.getZadostStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        zadostKlic.getZadostStav().getNazevResx()));
            }
        }

        return list;
    }

    /**
     * Ukládá objekt {@link ZadostKlic} do databáze.
     *
     * @param zadostKlic Objekt {@link ZadostKlic}, který se má uložit.
     * @return Uložený objekt {@link ZadostKlic} s aktualizovanými informacemi.
     * @throws ValidationException     Pokud je vyžadován důvod pro speciální klíč,
     *                                 ale není poskytnut.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     * @throws AccessDeniedException  Pokud uživatel nemá oprávnění */
    @TransactionalWrite
    public ZadostKlic save(ZadostKlic zadostKlic, AppUserDto appUserDto)
                 throws ValidationException, RecordNotFoundException, NoSuchMessageException, AccessDeniedException {
        if (zadostKlic.getKlic().isSpecialni() && StringUtils.isBlank(zadostKlic.getDuvod()))
            throw new ValidationException(
                    messageSource.getMessage("klic.duvod.required", null, LocaleContextHolder.getLocale()));

        Boolean maUzivatelPristupKBudove = budovaVratniceService.maUzivatelPristupKBudove(zadostKlic.getKlic().getBudova(), appUserDto);
         if (!maUzivatelPristupKBudove)
            throw new AccessDeniedException(
                    messageSource.getMessage("zadost_klic.klic.save.no_access_budova", null, LocaleContextHolder.getLocale()));

        zadostKlic.setCasZmn(Utils.getCasZmn());
        zadostKlic.setZmenuProvedl(Utils.getZmenuProv());

        ZadostKlic savedZadostKlic = zadostiKlicRepository.save(zadostKlic);
        return translateZadostKlic(savedZadostKlic);
    }

    /**
     * Vrací detailní informace o objektu {@link ZadostKlic} na základě jeho ID.
     *
     * @param idZadostiKlic ID objektu {@link ZadostKlic}, jehož detail se má
     *                      vrátit.
     * @return Objekt {@link ZadostKlic} s detailními informacemi.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam s daným ID.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public ZadostKlic getDetail(String idZadostiKlic) throws RecordNotFoundException, NoSuchMessageException {
        ZadostKlic zadostKlic = zadostiKlicRepository.getDetail(idZadostiKlic);

        if (zadostKlic != null)
            zadostKlic = translateZadostKlic(zadostKlic);

        return zadostKlic;
    }

    /**
     * Převádí objekt {@link ZadostKlic} a aktualizuje jeho vlastnosti na základě
     * lokalizace.
     *
     * @param zadostKlic Objekt {@link ZadostKlic}, který se má převést.
     * @return Převáděný objekt {@link ZadostKlic} s aktualizovanými informacemi.
     * @throws RecordNotFoundException Pokud dojde k chybě při hledání záznamu.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    private ZadostKlic translateZadostKlic(ZadostKlic zadostKlic)
            throws RecordNotFoundException, NoSuchMessageException {
        if (zadostKlic.getZadostStav().getNazevResx() != null)
            zadostKlic.getZadostStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    zadostKlic.getZadostStav().getNazevResx()));

        return zadostKlic;
    }

    /**
     * Vrací seznam objektů {@link ZadostKlic} na základě zadaného klíče.
     *
     * @param klic Objekt {@link Klic}, podle kterého se mají hledat žádosti.
     * @return Seznam objektů {@link ZadostKlic} odpovídajících zadanému klíči.
     */
    public List<ZadostKlic> findByKlic(Klic klic) {
        return zadostiKlicRepository.findByKlic(klic);
    }

    /**
     * Počítá počet žádostí pro daného uživatele.
     *
     * @param uzivatel Objekt {@link Uzivatel}, pro kterého se má počet žádostí
     *                 vrátit.
     * @return Počet žádostí pro daného uživatele.
     */
    public long countByUzivatel(Uzivatel uzivatel) {
        return zadostiKlicRepository.countByUzivatel(uzivatel);
    }
}
