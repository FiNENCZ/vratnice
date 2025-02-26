package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.NavstevniListekStav;
import cz.diamo.vratnice.entity.NavstevniListekUzivatelStav;
import cz.diamo.vratnice.enums.NavstevniListekStavEnum;
import cz.diamo.vratnice.repository.NavstevniListekUzivatelStavRepository;
import cz.diamo.vratnice.zadosti.services.ZadostiServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class NavstevniListekUzivatelStavService {

    @Autowired
    private NavstevniListekUzivatelStavRepository navstevaUzivatelStavRepository;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ZadostiServices zadostiServices;

    /**
     * Vytváří nový stav návštěvního lístku pro daného uživatele.
     *
     * @param navstevaUzivatelStav Objekt {@link NavstevniListekUzivatelStav}, který
     *                             se má vytvořit a uložit.
     * @return Uložený objekt {@link NavstevniListekUzivatelStav}.
     * 
     */
    @Transactional
    public NavstevniListekUzivatelStav create(NavstevniListekUzivatelStav navstevaUzivatelStav) {
        navstevaUzivatelStav.setCasZmn(Utils.getCasZmn());
        navstevaUzivatelStav.setZmenuProvedl(Utils.getZmenuProv());
        return navstevaUzivatelStavRepository.save(navstevaUzivatelStav);
    }

    /**
     * Získává detaily návštěvního lístku uživatele na základě jeho ID.
     *
     * @param idNavstevniListekUzivatelStav ID návštěvního lístku uživatele,
     *                                      pro který se mají získat detaily.
     * @return Objekt {@link NavstevniListekUzivatelStav} odpovídající zadanému ID.
     */
    public NavstevniListekUzivatelStav getDetail(String idNavstevniListekUzivatelStav) {
        return navstevaUzivatelStavRepository.getDetail(idNavstevniListekUzivatelStav);
    }

    /**
     * Přidává poznámku k návštěvnímu lístku uživatele na základě jeho ID.
     *
     * @param request                       HTTP požadavek
     * @param appUserDto                    DTO objekt reprezentující uživatele
     *                                      aplikace.
     * @param idNavstevniListekUzivatelStav ID návštěvního lístku uživatele, ke
     *                                      kterému se má přidat poznámka.
     * @param poznamka                      Poznámka, která se má přidat k
     *                                      návštěvnímu lístku.
     * @return Uložený objekt {@link NavstevniListekUzivatelStav} s přidanou
     *         poznámkou.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy z
     *                                 message source.
     * @throws BaseException           Pokud dojde k chybě při zpracování.
     * @throws RecordNotFoundException Pokud návštěvní lístek uživatele nebyl
     *                                 nalezen.
     */
    @Transactional
    public NavstevniListekUzivatelStav pridatPoznamku(HttpServletRequest request, AppUserDto appUserDto,
            String idNavstevniListekUzivatelStav, String poznamka) throws NoSuchMessageException, BaseException {

        NavstevniListekUzivatelStav navstevniListekUzivatelStav = getDetail(idNavstevniListekUzivatelStav);

        if (navstevniListekUzivatelStav == null)
            throw new RecordNotFoundException(messageSource.getMessage("navstevni_listek_uzivatel_stav.not_found", null,
                    LocaleContextHolder.getLocale()));

        navstevniListekUzivatelStav.setPoznamka(poznamka);
        NavstevniListekUzivatelStav savedNavstevniListekUzivatelStav = create(navstevniListekUzivatelStav);

        // TODO: -- ŽÁDOSTI -- napojit na žádosti
        // zadostiServices.navstevniListekPridatPoznamku(request, appUserDto, new NavstevniListekUzivatelStavDto(savedNavstevniListekUzivatelStav));

        return savedNavstevniListekUzivatelStav;
    }

    /**
     * Získává seznam všech návštěvních lístků uživatelů.
     *
     * @return Seznam objektů {@link NavstevniListekUzivatelStav}.
     */
    public List<NavstevniListekUzivatelStav> list() {
        return navstevaUzivatelStavRepository.findAll();
    }

    /**
     * Získává seznam stavů návštěvních lístků uživatelů na základě ID návštěvního
     * lístku.
     *
     * @param idNavstevniListe ID návštěvního lístku, pro který se mají získat stavy
     *                         uživatelů.
     * @return Seznam objektů {@link NavstevniListekUzivatelStav} odpovídající
     *         zadanému ID návštěvního lístku.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné stavy pro zadané
     *                                 ID návštěvního lístku.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy z
     *                                 message source.
     */
    public List<NavstevniListekUzivatelStav> getByNavstevniListek(String idNavstevniListe)
            throws RecordNotFoundException, NoSuchMessageException {
        List<NavstevniListekUzivatelStav> uzivateleStav = navstevaUzivatelStavRepository
                .getByNavstevniListek(idNavstevniListe);

        if (uzivateleStav != null) {
            for (NavstevniListekUzivatelStav uzivatelStav : uzivateleStav) {
                uzivatelStav.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        uzivatelStav.getStav().getNazevResx()));
            }
        }

        return uzivateleStav;
    }

    /**
     * Mění stav návštěvního lístku uživatele na základě zadaného ID návštěvního
     * lístku a ID uživatele.
     *
     * @param idNavstevniListek ID návštěvního lístku, jehož stav se má změnit.
     * @param idUzivatel        ID uživatele, jehož stav se má změnit.
     * @param novyStavEnum      Nový stav, který se má nastavit pro návštěvní lístek
     *                          uživatele.
     * @return Uložený objekt {@link NavstevniListekUzivatelStav} s novým stavem.
     * @throws RecordNotFoundException Pokud nebyl nalezen návštěvní lístek
     *                                 uživatele.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy z
     *                                 message source.
     */
    public NavstevniListekUzivatelStav zmenitStav(String idNavstevniListek, String idUzivatel,
            NavstevniListekStavEnum novyStavEnum) throws RecordNotFoundException, NoSuchMessageException {
        NavstevniListekUzivatelStav uzivatelStav = navstevaUzivatelStavRepository
                .getByNavstevniListekAndUzivatel(idNavstevniListek, idUzivatel);

        if (uzivatelStav == null)
            throw new RecordNotFoundException(messageSource.getMessage("navstevni_listek_uzivatel_stav.not_found", null,
                    LocaleContextHolder.getLocale()));

        uzivatelStav.setStav(new NavstevniListekStav(novyStavEnum));
        NavstevniListekUzivatelStav savedUzivatelStav = create(uzivatelStav);

        return savedUzivatelStav;
    }

}
