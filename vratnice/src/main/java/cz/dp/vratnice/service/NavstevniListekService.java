package cz.dp.vratnice.service;

import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.dp.share.base.Utils;
import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.exceptions.AccessDeniedException;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.dto.NavstevaOsobaDto;
import cz.dp.vratnice.dto.NavstevniListekDto;
import cz.dp.vratnice.dto.NavstevniListekUzivatelStavDto;
import cz.dp.vratnice.entity.NavstevaOsoba;
import cz.dp.vratnice.entity.NavstevniListek;
import cz.dp.vratnice.entity.NavstevniListekTyp;
import cz.dp.vratnice.entity.NavstevniListekUzivatelStav;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.enums.NavstevniListekTypEnum;
import cz.dp.vratnice.filter.FilterPristupuVratnice;
import cz.dp.vratnice.repository.NavstevniListekRepository;
import cz.dp.vratnice.repository.NavstevniListekTypRepository;
import cz.dp.vratnice.repository.UzivatelNavstevniListekTypRepository;
import cz.dp.vratnice.zadosti.services.ZadostiServices;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekService {

    @Autowired
    private NavstevniListekRepository navstevniListekRepository;

    @Autowired
    private UzivatelNavstevniListekTypRepository uzivatelNavstevniListekTypRepository;

    @Autowired
    private NavstevniListekTypRepository navstevniListekTypRepository;

    @Autowired
    private NavstevniListekUzivatelStavService navstevniListekUzivatelStavService;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NavstevaOsobaService navstevaOsobaService;

    @Autowired
    private ZadostiServices zadostiServices;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Vytváří nový {@link NavstevniListek} a ukládá ho do databáze.
     *
     * @param request            HTTP požadavek.
     * @param appUserDto         DTO uživatele.
     * @param navstevniListekDto DTO objekt, který obsahuje informace o návštěvním
     *                           lístku.
     * @param vratnice           Objekt {@link Vratnice}, který se má přiřadit k
     *                           návštěvnímu lístku.
     * @return Uložený objekt {@link NavstevniListek}.
     * @throws AccessDeniedException  Pokud se pokusíte upravit existující návštěvní
     *                                lístek.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     * @throws BaseException          Pokud dojde k jiné chybě během zpracování.
     */
    @Transactional
    public NavstevniListek create(HttpServletRequest request, AppUserDto appUserDto,
            NavstevniListekDto navstevniListekDto, Vratnice vratnice)
            throws AccessDeniedException, NoSuchMessageException, BaseException {

        // Zamezení editace
        if (navstevniListekDto.getIdNavstevniListek() != null)
            throw new AccessDeniedException(messageSource.getMessage("navstevni_listek.cannot_be_edited", null,
                    LocaleContextHolder.getLocale()));

        // Vytvoření NavstevaOsoba jako entity (záznam v databázi)
        List<NavstevaOsoba> savedNavstevyOsoby = createNavstevyOsobyIfPresent(navstevniListekDto.getNavstevaOsoba());

        NavstevniListekTyp navstevniListekTyp = getNavstevniListekTypDleVsechUzivatelu(
                navstevniListekDto.getUzivateleStav());

        NavstevniListek navstevniListek = navstevniListekDto.toEntity();
        navstevniListek.setTyp(navstevniListekTyp);
        navstevniListek.setNavstevaOsoba(savedNavstevyOsoby);
        navstevniListek.setCasVytvoreni(new Timestamp(System.currentTimeMillis()));
        navstevniListek.setCasZmn(Utils.getCasZmn());
        navstevniListek.setZmenuProvedl(Utils.getZmenuProv());

        if (navstevniListek.getVratnice() == null)
            if (vratnice != null)
                navstevniListek.setVratnice(vratnice);

        NavstevniListek savedNavstevniListek = navstevniListekRepository.save(navstevniListek);

        List<NavstevniListekUzivatelStav> savedUzivateleStav = createNavstevniListekUzivatelStavy(savedNavstevniListek);
        savedNavstevniListek.setUzivateleStav(savedUzivateleStav);

        zadostiServices.saveNavstevniListek(request, appUserDto, new NavstevniListekDto(savedNavstevniListek));

        return savedNavstevniListek;
    }

    /**
     * Vytváří seznam {@link NavstevaOsoba} na základě zadaného seznamu DTO objektů.
     *
     * @param navstevyOsobyDto Seznam DTO objektů {@link NavstevaOsobaDto}, které se
     *                         mají vytvořit.
     * @return Seznam uložených objektů {@link NavstevaOsoba}.
     */
    private List<NavstevaOsoba> createNavstevyOsobyIfPresent(List<NavstevaOsobaDto> navstevyOsobyDto) {
        if (navstevyOsobyDto == null || navstevyOsobyDto.isEmpty()) {
            return new ArrayList<NavstevaOsoba>();
        }

        List<NavstevaOsoba> navstevaOsobaEntities = navstevyOsobyDto.stream()
                .map(NavstevaOsobaDto::toEntity)
                .collect(Collectors.toList());

        List<NavstevaOsoba> savedNavstevaOsoby = navstevaOsobaEntities.stream()
                .map(navstevaOsoba -> {
                    try {
                        return navstevaOsobaService.create(navstevaOsoba);
                    } catch (UniqueValueException | NoSuchMessageException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());

        return savedNavstevaOsoby;

    }

    /**
     * Vytváří a ukládá seznam {@link NavstevniListekUzivatelStav} pro daný
     * návštěvní lístek.
     * 
     * @param navstevniListek Objekt {@link NavstevniListek}, pro který se mají
     *                        vytvořit stavy uživatelů.
     * @return Seznam uložených objektů {@link NavstevniListekUzivatelStav}.
     */
    private List<NavstevniListekUzivatelStav> createNavstevniListekUzivatelStavy(NavstevniListek navstevniListek) {

        List<NavstevniListekUzivatelStav> savedUzivateleStavy = new ArrayList<NavstevniListekUzivatelStav>();
        ;

        for (NavstevniListekUzivatelStav uzivatelStav : navstevniListek.getUzivateleStav()) {
            uzivatelStav.setNavstevniListek(new NavstevniListek(navstevniListek.getIdNavstevniListek()));
            NavstevniListekUzivatelStav savedUzivatelStav = navstevniListekUzivatelStavService.create(uzivatelStav);
            savedUzivateleStavy.add(savedUzivatelStav);
        }

        return savedUzivateleStavy;

    }

    /**
     * Získává typ návštěvního lístku na základě stavů uživatelů.
     *
     * @param uzivateleStavDto Seznam DTO objektů
     *                         {@link NavstevniListekUzivatelStavDto},
     *                         které obsahují informace o uživatelích a jejich
     *                         stavech.
     * @return Objekt {@link NavstevniListekTyp} odpovídající určenému typu
     *         návštěvního lístku.
     */
    private NavstevniListekTyp getNavstevniListekTypDleVsechUzivatelu(
            List<NavstevniListekUzivatelStavDto> uzivateleStavDto) {
        NavstevniListekTypEnum currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_ELEKTRONICKY;

        // Pokud nějaký z návštěvníků má nastaven papirový návštěvní lístek, tak ním
        // stává automaticky pro všechny (navstevní listek je tedy papírový)
        for (NavstevniListekUzivatelStavDto uzivatelDto : uzivateleStavDto) {
            NavstevniListekTyp navstevniListekTyp = getNavstevniListekTypByUzivatel(uzivatelDto.getUzivatel().getId());
            if (navstevniListekTyp.getNavstevniListekTypEnum() == NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY) {
                currentNavstevniListekTypEnum = NavstevniListekTypEnum.NAVSTEVNI_LISTEK_PAPIROVY;
            }

        }

        return new NavstevniListekTyp(currentNavstevniListekTypEnum);
    }

    /**
     * Vrací seznam {@link NavstevniListek} na základě zadané aktivity a
     * uživatelského kontextu.
     * 
     * @param aktivita   Boolean hodnota.
     * @param appUserDto DTO objekt uživatele.
     * @return Seznam objektů {@link NavstevniListek} odpovídajících zadaným
     *         kritériím.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */

    public List<NavstevniListek> getList(Boolean aktivita, AppUserDto appUserDto)
            throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM NavstevniListek s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        @SuppressWarnings("unchecked")
        List<NavstevniListek> list = vysledek.getResultList();

        if (list != null) {
            for (NavstevniListek navstevniListek : list) {
                navstevniListek.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        navstevniListek.getTyp().getNazevResx()));
                navstevniListek.setUzivateleStav(navstevniListekUzivatelStavService
                        .getByNavstevniListek(navstevniListek.getIdNavstevniListek()));
            }
        }

        return list;
    }

    /**
     * Vrací detail {@link NavstevniListek} na základě zadaného ID.
     *
     * @param idNavstevniListek ID objektu {@link NavstevniListek}, jehož detail se
     *                          má vrátit.
     * @return Objekt {@link NavstevniListek} odpovídající zadanému ID.
     * @throws RecordNotFoundException Pokud nebyl nalezen žádný záznam s daným ID.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public NavstevniListek getDetail(String idNavstevniListek) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListek navstevniListek = navstevniListekRepository.getDetail(idNavstevniListek);
        navstevniListek.setUzivateleStav(
                navstevniListekUzivatelStavService.getByNavstevniListek(navstevniListek.getIdNavstevniListek()));
        return navstevniListek;
    }

    /**
     * Získává typ návštěvního lístku pro daného uživatele na základě jeho ID.
     *
     * @param idUzivatel ID uživatele, pro kterého se má získat typ návštěvního
     *                   lístku.
     * @return Objekt {@link NavstevniListekTyp} odpovídající uživatelskému ID.
     * @throws ResponseStatusException Pokud dojde k chybě při získávání typu
     *                                 návštěvního lístku.
     */
    public NavstevniListekTyp getNavstevniListekTypByUzivatel(String idUzivatel) {
        NavstevniListekTyp navstevniListekTypUzivatele = uzivatelNavstevniListekTypRepository
                .findNavstevniListekTypByUzivatelId(idUzivatel);

        try {
            if (navstevniListekTypUzivatele == null) // pokud není nalezen, nastaví se ELEKTRONICKY jako výchozí
                navstevniListekTypUzivatele = navstevniListekTypRepository
                        .getDetail(NavstevniListekTypEnum.NAVSTEVNI_LISTEK_ELEKTRONICKY.getValue());

            navstevniListekTypUzivatele.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    navstevniListekTypUzivatele.getNazevResx()));
            return navstevniListekTypUzivatele;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
        }
    }

}
