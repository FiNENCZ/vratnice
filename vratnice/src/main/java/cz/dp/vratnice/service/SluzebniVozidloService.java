package cz.dp.vratnice.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.dp.share.base.Utils;
import cz.dp.share.component.ResourcesComponent;
import cz.dp.share.dto.AppUserDto;
import cz.dp.share.entity.Lokalita;
import cz.dp.share.entity.Zavod;
import cz.dp.share.exceptions.AccessDeniedException;
import cz.dp.share.exceptions.RecordNotFoundException;
import cz.dp.share.exceptions.UniqueValueException;
import cz.dp.vratnice.controller.SluzebniVozidloController;
import cz.dp.vratnice.entity.SluzebniVozidlo;
import cz.dp.vratnice.entity.SluzebniVozidloFunkce;
import cz.dp.vratnice.entity.SluzebniVozidloKategorie;
import cz.dp.vratnice.entity.SluzebniVozidloStav;
import cz.dp.vratnice.entity.VozidloTyp;
import cz.dp.vratnice.entity.Vratnice;
import cz.dp.vratnice.enums.SluzebniVozidloKategorieEnum;
import cz.dp.vratnice.repository.SluzebniVozidloFunkceRepository;
import cz.dp.vratnice.repository.SluzebniVozidloKategorieRepository;
import cz.dp.vratnice.repository.SluzebniVozidloRepository;
import cz.dp.vratnice.repository.SluzebniVozidloStavRepository;
import cz.dp.vratnice.repository.VozidloTypRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class SluzebniVozidloService {

    final static Logger logger = LogManager.getLogger(SluzebniVozidloController.class);

    @Autowired
    private SluzebniVozidloRepository sluzebniVozidloRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private VratniceService vratniceService;

    @Autowired
    private VratniceBaseService vratniceBaseService;

    @Autowired
    private VozidloTypRepository vozidloTypRepository;

    @Autowired
    private SluzebniVozidloKategorieRepository sluzebniVozidloKategorieRepository;

    @Autowired
    private SluzebniVozidloFunkceRepository sluzebniVozidloFunkceRepository;

    @Autowired
    private SluzebniVozidloStavRepository sluzebniVozidloStavRepository;

    /**
     * Vrací seznam služebních vozidel na základě uživatelského přístupu a aktivity.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o uživateli.
     * @param aktivita   Boolean hodnota určující, zda se mají vrátit pouze aktivní
     *                   vozidla.
     * @return Seznam {@link SluzebniVozidlo} odpovídajících kritériím.
     * @throws RecordNotFoundException Pokud nebyly nalezeny žádné záznamy.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public List<SluzebniVozidlo> getList(AppUserDto appUserDto, Boolean aktivita)
            throws RecordNotFoundException, NoSuchMessageException {
        List<Zavod> zavodyUzivatele = vratniceBaseService.getAllZavodyUzivateleByPristup(appUserDto.getIdUzivatel(),
                null);

        logger.info(zavodyUzivatele);

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM SluzebniVozidlo s");
        queryString.append(" WHERE 1 = 1");

        if (aktivita != null)
            queryString.append(" AND s.aktivita = :aktivita");

        if (!zavodyUzivatele.isEmpty())
            queryString.append(" AND (s.zavod.idZavod IN :zavody OR s.zavod.idZavod IS NULL) ");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (!zavodyUzivatele.isEmpty()) {
            // Převod závodů na seznam jejich ID
            List<String> zavodIds = zavodyUzivatele.stream()
                    .map(Zavod::getIdZavod)
                    .collect(Collectors.toList());
            vysledek.setParameter("zavody", zavodIds);
        }

        @SuppressWarnings("unchecked")
        List<SluzebniVozidlo> list = vysledek.getResultList();

        if (list != null) {
            for (SluzebniVozidlo sluzebniVozidlo : list) {
                sluzebniVozidlo = translateSluzebniVozidlo(sluzebniVozidlo);
            }
        }

        return list;
    }

    /**
     * Vytváří nové služební vozidlo a ukládá ho do databáze.
     *
     * @param appUserDto      Objekt {@link AppUserDto} obsahující informace o
     *                        uživateli.
     * @param sluzebniVozidlo Objekt {@link SluzebniVozidlo}, který se má vytvořit.
     * @return Uložený objekt {@link SluzebniVozidlo} s aktualizovanými informacemi.
     * @throws UniqueValueException    Pokud již existuje vozidlo se stejným
     *                                 registračním číslem.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam.
     * @throws AccessDeniedException   Pokud uživatel nemá přístup k danému vozidlu.
     */
    @Transactional
    public SluzebniVozidlo create(AppUserDto appUserDto, SluzebniVozidlo sluzebniVozidlo)
            throws UniqueValueException, NoSuchMessageException, RecordNotFoundException, AccessDeniedException {
        if (sluzebniVozidlo.getKategorie()
                .getSluzebniVozidloKategorieEnum() != SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE)
            zkontrolujPristupKeSluzebnimVozidlu(sluzebniVozidlo.getZavod().getIdZavod(), appUserDto.getIdUzivatel());

        if (sluzebniVozidlo.getIdSluzebniVozidlo() == null || sluzebniVozidlo.getIdSluzebniVozidlo().isEmpty()) {
            if (sluzebniVozidloRepository.existsByRz(sluzebniVozidlo.getRz()))
                throw new UniqueValueException(
                        messageSource.getMessage("sluzebni_vozidlo.rz.unique", null, LocaleContextHolder.getLocale()));
        }
        sluzebniVozidlo.setCasZmn(Utils.getCasZmn());
        sluzebniVozidlo.setZmenuProvedl(Utils.getZmenuProv());

        SluzebniVozidlo savedSluzebniVozidlo = sluzebniVozidloRepository.save(sluzebniVozidlo);

        return translateSluzebniVozidlo(savedSluzebniVozidlo);
    }

    /**
     * Vrací detail služebního vozidla na základě jeho ID.
     *
     * @param appUserDto Objekt {@link AppUserDto} obsahující informace o uživateli.
     * @param id         ID služebního vozidla, jehož detail se má vrátit.
     * @return Objekt {@link SluzebniVozidlo} s detaily vozidla.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam o vozidle.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     * @throws AccessDeniedException   Pokud uživatel nemá přístup k danému vozidlu.
     */
    public SluzebniVozidlo getDetail(AppUserDto appUserDto, String id)
            throws RecordNotFoundException, NoSuchMessageException, AccessDeniedException {
        SluzebniVozidlo sluzebniVozidlo = sluzebniVozidloRepository.getDetail(id);

        if (sluzebniVozidlo.getKategorie()
                .getSluzebniVozidloKategorieEnum() != SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE)
            zkontrolujPristupKeSluzebnimVozidlu(sluzebniVozidlo.getZavod().getIdZavod(), appUserDto.getIdUzivatel());

        if (sluzebniVozidlo != null) {
            sluzebniVozidlo = translateSluzebniVozidlo(sluzebniVozidlo);
        }

        return sluzebniVozidlo;
    }

    /**
     * Kontroluje, zda má uživatel přístup k danému služebnímu vozidlu na základě
     * jeho ID závodu.
     *
     * @param idZavodSluzebniVozidlo ID závodu služebního vozidla, ke kterému se
     *                               kontroluje přístup.
     * @param idUzivatel             ID uživatele, jehož přístup se kontroluje.
     * @throws AccessDeniedException  Pokud uživatel nemá přístup k danému vozidlu.
     * @throws NoSuchMessageException Pokud dojde k chybě při získávání zprávy.
     */
    private void zkontrolujPristupKeSluzebnimVozidlu(String idZavodSluzebniVozidlo, String idUzivatel)
            throws AccessDeniedException, NoSuchMessageException {
        List<Zavod> zavodyUzivatele = vratniceBaseService.getAllZavodyUzivateleByPristup(idUzivatel, null);

        boolean povoleniNalezeno = zavodyUzivatele.stream()
                .anyMatch(zavod -> zavod.getIdZavod().equals(idZavodSluzebniVozidlo));

        if (!povoleniNalezeno) {
            throw new AccessDeniedException(
                    messageSource.getMessage("sluzebni_vozidlo.not_access", null, LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Převádí objekt služebního vozidla na jeho lokalizovanou verzi.
     *
     * @param sluzebniVozidlo Objekt {@link SluzebniVozidlo}, který se má převést.
     * @return Převáděný objekt {@link SluzebniVozidlo} s lokalizovanými názvy.
     * @throws RecordNotFoundException Pokud nebyl nalezen záznam.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    private SluzebniVozidlo translateSluzebniVozidlo(SluzebniVozidlo sluzebniVozidlo)
            throws RecordNotFoundException, NoSuchMessageException {
        if (sluzebniVozidlo.getTyp().getNazevResx() != null)
            sluzebniVozidlo.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidlo.getTyp().getNazevResx()));
        else {
            Optional<VozidloTyp> sluzebniVozidloTyp = vozidloTypRepository
                    .findById(sluzebniVozidlo.getTyp().getIdVozidloTyp());
            sluzebniVozidlo.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidloTyp.get().getNazevResx()));
        }

        if (sluzebniVozidlo.getKategorie().getNazevResx() != null)
            sluzebniVozidlo.getKategorie().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidlo.getKategorie().getNazevResx()));
        else {
            Optional<SluzebniVozidloKategorie> sluzebniVozidloKategorie = sluzebniVozidloKategorieRepository
                    .findById(sluzebniVozidlo.getKategorie().getIdSluzebniVozidloKategorie());
            sluzebniVozidlo.getKategorie().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidloKategorie.get().getNazevResx()));
        }

        if (sluzebniVozidlo.getFunkce() != null) {
            if (sluzebniVozidlo.getFunkce().getNazevResx() != null)
                sluzebniVozidlo.getFunkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        sluzebniVozidlo.getFunkce().getNazevResx()));
            else {
                Optional<SluzebniVozidloFunkce> sluzebniVozidloFunkce = sluzebniVozidloFunkceRepository
                        .findById(sluzebniVozidlo.getFunkce().getIdSluzebniVozidloFunkce());
                sluzebniVozidlo.getFunkce().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                        sluzebniVozidloFunkce.get().getNazevResx()));
            }
        }

        if (sluzebniVozidlo.getStav().getNazevResx() != null)
            sluzebniVozidlo.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidlo.getStav().getNazevResx()));
        else {
            Optional<SluzebniVozidloStav> sluzebniVozidloStav = sluzebniVozidloStavRepository
                    .findById(sluzebniVozidlo.getStav().getIdSluzebniVozidloStav());
            sluzebniVozidlo.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(),
                    sluzebniVozidloStav.get().getNazevResx()));
        }

        return sluzebniVozidlo;
    }

    /**
     * Kontroluje, zda může služební vozidlo projet vratnicí na základě
     * registračního čísla a ID vratnice.
     *
     * @param rz         Registrační číslo služebního vozidla.
     * @param idVratnice ID vratnice, kterou se kontroluje.
     * @return Boolean hodnota určující, zda může vozidlo projet vratnicí.
     * @throws RecordNotFoundException Pokud nebyla nalezena vratnice nebo vozidlo.
     * @throws NoSuchMessageException  Pokud dojde k chybě při získávání zprávy.
     */
    public Boolean muzeSluzebniVozidloProjetVratnici(String rz, String idVratnice)
            throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratniceKamery = vratniceService.getDetail(idVratnice);

        if (vratniceKamery == null)
            throw new RecordNotFoundException(
                    String.format(
                            messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale())));

        Lokalita lokalitaKamery = vratniceKamery.getLokalita();

        SluzebniVozidlo sluzebniVozidlo = getByRz(rz);

        if (sluzebniVozidlo == null)
            return false;

        if (sluzebniVozidlo.getKategorie()
                .getSluzebniVozidloKategorieEnum() == SluzebniVozidloKategorieEnum.SLUZEBNI_VOZIDLO_KATEGORIE_MANAZERSKE)
            return true;

        if (sluzebniVozidlo.getLokality() != null) {
            for (Lokalita lokalita : sluzebniVozidlo.getLokality()) {
                if (lokalita.getIdLokalita().equals(lokalitaKamery.getIdLokalita()))
                    return true;
            }
        }

        return false;
    }

    /**
     * Vrací služební vozidlo na základě jeho registračního čísla.
     *
     * @param rz Registrační číslo služebního vozidla.
     * @return Objekt {@link SluzebniVozidlo} odpovídající danému registračnímu
     *         číslu.
     */
    public SluzebniVozidlo getByRz(String rz) {
        return sluzebniVozidloRepository.getByRz(rz);
    }
}
