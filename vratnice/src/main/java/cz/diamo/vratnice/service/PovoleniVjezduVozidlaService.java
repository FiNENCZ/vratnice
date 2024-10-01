package cz.diamo.vratnice.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
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

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.Ws02EmailDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.enums.TypOznameniEnum;
import cz.diamo.share.exceptions.AccessDeniedException;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.services.Wso2Services;
import cz.diamo.vratnice.configuration.VratniceProperties;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidlaZmenaStavu;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.entity.ZadostStav;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaRepository;
import cz.diamo.vratnice.repository.PovoleniVjezduVozidlaZmenaStavuRepository;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class PovoleniVjezduVozidlaService {

    final static Logger logger = LogManager.getLogger(PovoleniVjezduVozidlaService.class);

    @Autowired
    private PovoleniVjezduVozidlaRepository povoleniVjezduVozidlaRepository;

    @Autowired
    private RidicService ridicService;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;


    @Autowired
    private VratniceService vratniceService;

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @Autowired
    private SpolecnostService spolecnostService;

    @Autowired
    private PovoleniVjezduVozidlaZmenaStavuRepository povoleniVjezduVozidlaZmenaStavuRepository;

    @Autowired
    private VratniceBaseService vratniceBaseService;

    @Autowired
    private Wso2Services wso2Services;

    @Autowired
    private VratniceProperties vratniceProperties;

    @PersistenceContext
    private EntityManager entityManager;

    public List<PovoleniVjezduVozidla> getList(Boolean aktivita, ZadostStavEnum stavEnum, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        List<Zavod> zavody = vratniceBaseService.getAllZavodyUzivateleByPristup(appUserDto.getIdUzivatel());

        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM PovoleniVjezduVozidla s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");
        
        if (stavEnum != null) {
            queryString.append("AND s.stav.idZadostStav = :stav ");
        }

        if (!zavody.isEmpty()) {
            queryString.append("AND s.zavod.idZavod IN :zavody ");
        }

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null) {
            vysledek.setParameter("aktivita", aktivita);
        }

        if (stavEnum != null) {
            vysledek.setParameter("stav", stavEnum.getValue());
        }

        if (!zavody.isEmpty()) {
            // Převod závodů na seznam jejich ID
            List<String> zavodIds = zavody.stream()
                                        .map(Zavod::getIdZavod)
                                        .collect(Collectors.toList());
            vysledek.setParameter("zavody", zavodIds);
        }

        @SuppressWarnings("unchecked")
        List<PovoleniVjezduVozidla> list = vysledek.getResultList();

        if (list != null) {
            for (PovoleniVjezduVozidla povoleni : list) {
                povoleni = translatePovoleniVjezduVozidla(povoleni);
            }
        }

        return list;
    }

    private PovoleniVjezduVozidla translatePovoleniVjezduVozidla(PovoleniVjezduVozidla povoleni) throws RecordNotFoundException, NoSuchMessageException {

        for (VozidloTyp vozidloTyp : povoleni.getTypVozidla()) {
            if (vozidloTyp.getNazevResx() != null)
            vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vozidloTyp.getNazevResx()));
        }
        if (povoleni.getZemeRegistraceVozidla().getNazevResx() != null)
            povoleni.getZemeRegistraceVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleni.getZemeRegistraceVozidla().getNazevResx()));
        
        if (povoleni.getStav().getNazevResx() != null)
            povoleni.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleni.getStav().getNazevResx()));
        
        return povoleni;
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException, AccessDeniedException {
        PovoleniVjezduVozidla povoleni =  povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);

        zkontrolujOpravneniUzivateleVuciPovoleni(povoleni.getZavod().getIdZavod(), appUserDto.getIdUzivatel());

        if (povoleni != null) {
            povoleni = translatePovoleniVjezduVozidla(povoleni);
        }

        return povoleni;
    }
    

    public List<PovoleniVjezduVozidla> getByStav(String stav) {
        return povoleniVjezduVozidlaRepository.getByStav(stav);
    }

    public List<PovoleniVjezduVozidla> getByRzVozidla(String rzVozidla) {
        return povoleniVjezduVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public Optional<PovoleniVjezduVozidla> jeRzVozidlaPovolena(String rzVozidla, String idVratnice) throws RecordNotFoundException, NoSuchMessageException {
        Vratnice vratniceKamery = vratniceService.getDetail(idVratnice);

        if (vratniceKamery == null) 
            throw new RecordNotFoundException(
                String.format(messageSource.getMessage("vratnice.not_found", null, LocaleContextHolder.getLocale())));
        

        Lokalita zavodKameryLokalita = vratniceKamery.getLokalita();


        List<PovoleniVjezduVozidla> povoleniVjezduVozidlaList = getByRzVozidla(rzVozidla);
        
        // Pokud není žádné povolení, vrátí prázdný Optional
        if (povoleniVjezduVozidlaList.isEmpty()) {
            return Optional.empty();
        }
    
        LocalDate currentDate = LocalDate.now();
            
        return povoleniVjezduVozidlaList.stream()
            .filter(povoleni -> povoleni.getAktivita()) // Filtrovat pouze povolení s aktivita == true
            .filter(povoleni -> povoleni.getStav().getZadostStavEnum() == ZadostStavEnum.SCHVALENO) // Filtrovat pouze povolení s stav == SCHVALENO
            .filter(povoleni -> isPovoleniPlatne(povoleni, currentDate))
            .filter(povoleni -> obsahujeLokalitu(povoleni, zavodKameryLokalita.getIdLokalita()))
            .findFirst();
    }

    private boolean isPovoleniPlatne(PovoleniVjezduVozidla povoleni, LocalDate currentDate) {
        //Porovnání pouze data, nikoliv času
        LocalDate datumOd = convertToLocalDate(povoleni.getDatumOd());
        LocalDate datumDo = convertToLocalDate(povoleni.getDatumDo());
        
        return (currentDate.isEqual(datumOd) || currentDate.isAfter(datumOd)) 
                && (currentDate.isEqual(datumDo) || currentDate.isBefore(datumDo));
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private boolean obsahujeLokalitu(PovoleniVjezduVozidla povoleni, String idZavodKameryVratnice) {
        return povoleni.getLokality() != null && povoleni.getLokality().stream()
            .anyMatch(lokalita -> idZavodKameryVratnice.equals(lokalita.getIdLokalita()));
    }

    @Transactional
    public PovoleniVjezduVozidla create(AppUserDto appUserDto,PovoleniVjezduVozidla povoleniVjezduVozidla, HttpServletRequest request) throws NoSuchMessageException, BaseException {
        if (appUserDto != null) //Pro vratnice-public se neověřuje
            zkontrolujOpravneniUzivateleVuciPovoleni(povoleniVjezduVozidla.getZavod().getIdZavod(), appUserDto.getIdUzivatel());
        
        if (povoleniVjezduVozidla.getRidic() != null) {
            Ridic savedRidic =  ridicService.create(povoleniVjezduVozidla.getRidic());
            povoleniVjezduVozidla.setRidic(savedRidic);
        }

        Spolecnost savedSpolecnostZadatele = spolecnostService.save(povoleniVjezduVozidla.getSpolecnostZadatele());
        Spolecnost savedSpolecnostVozidla = spolecnostService.save(povoleniVjezduVozidla.getSpolecnostVozidla());

        povoleniVjezduVozidla.setSpolecnostZadatele(savedSpolecnostZadatele);
        povoleniVjezduVozidla.setSpolecnostVozidla(savedSpolecnostVozidla);

        povoleniVjezduVozidla.setCasZmn(Utils.getCasZmn());
        povoleniVjezduVozidla.setZmenuProvedl(Utils.getZmenuProv());

        String idPovoleniVjezduVozidla = povoleniVjezduVozidla.getIdPovoleniVjezduVozidla();
        Boolean novyZaznam = false;

        // Pokud se jedná o novou žádost
        if (idPovoleniVjezduVozidla == null) {
            povoleniVjezduVozidla.setDatumVytvoreni(new Date());
            novyZaznam = true;
        }
        else 
            povoleniVjezduVozidla.setDatumVytvoreni(povoleniVjezduVozidlaRepository.getDatumVytvoreni(idPovoleniVjezduVozidla));
        
 
        zkontrolujJestliMuzeBytDanyStav(povoleniVjezduVozidla);

        PovoleniVjezduVozidla savedPovoleni =  povoleniVjezduVozidlaRepository.save(povoleniVjezduVozidla);
        zkontrolovatNutnostOdeslaniOznameni(savedPovoleni, novyZaznam, request);

        return translatePovoleniVjezduVozidla(savedPovoleni);
    }

    @Transactional
    public PovoleniVjezduVozidla createFromPublic(PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto, HttpServletRequest request) throws NoSuchMessageException, BaseException {
        PovoleniVjezduVozidla povoleniEntity = povoleniVjezduVozidlaDto.toEntity();
        povoleniEntity.setStav(new ZadostStav(ZadostStavEnum.PRIPRAVENO));
        PovoleniVjezduVozidla savedPovoleni = create(null, povoleniEntity, request);

        return savedPovoleni;
    }

    private void zkontrolujJestliMuzeBytDanyStav(PovoleniVjezduVozidla povoleniVjezduVozidla) throws BaseException {
        
        if (povoleniVjezduVozidla.getIdPovoleniVjezduVozidla() != null) {
            PovoleniVjezduVozidlaZmenaStavu povoleniZmenaStavu = povoleniVjezduVozidlaZmenaStavuRepository.findLatestById(povoleniVjezduVozidla.getIdPovoleniVjezduVozidla());
            
            //Stav nemůže být nastaven na PRIPRAVENO, pokud již někdy jindy měl jiný stav (např. SCHVALENO)
            if (povoleniZmenaStavu != null && povoleniZmenaStavu.getStavNovy().getZadostStavEnum() != ZadostStavEnum.PRIPRAVENO
                    && povoleniVjezduVozidla.getStav().getZadostStavEnum() == ZadostStavEnum.PRIPRAVENO) {
                throw new BaseException(messageSource.getMessage("povoleni_vjezdu_vozidla.znovu_stav_pripraveno_error", null, LocaleContextHolder.getLocale()));
            }
        }
    }

    @Transactional
    public List<PovoleniVjezduVozidla> zneplatnitPovoleni(AppUserDto appUserDto, HttpServletRequest request, List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidlaList) throws NoSuchMessageException, BaseException {
        List<PovoleniVjezduVozidla> result = new ArrayList<PovoleniVjezduVozidla>();

        for (PovoleniVjezduVozidlaDto povoleniVjezduVozidla : povoleniVjezduVozidlaList) {
            povoleniVjezduVozidla.setAktivita(false);

            result.add(create(appUserDto, povoleniVjezduVozidla.toEntity(), request));
        }

        return result;
    }


    public Integer pocetVjezdu(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleni = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);

        if (povoleni == null || povoleni.getRzVozidla() == null || povoleni.getDatumOd() == null || povoleni.getDatumDo() == null) {
            throw new IllegalArgumentException("Povoleni nebo jeho klicove udaje nemohou byt null.");
        }

        // Pouziti pomoci metody s daty z povoleni
        return pocetVjezduProInterval(povoleni.getRzVozidla(), povoleni.getDatumOd(), povoleni.getDatumDo());
    }

    public Integer pocetVjezdu(String idPovoleniVjezduVozidla, Date datumOd, Date datumDo) {
        PovoleniVjezduVozidla povoleni = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);

        if (povoleni == null || povoleni.getRzVozidla() == null || datumOd == null || datumDo == null) {
            throw new IllegalArgumentException("Povoleni, jeho klicove udaje nebo datumy od a do nemohou byt null.");
        }

        // Pouziti pomoci metody s poskytnutymi daty
        return pocetVjezduProInterval(povoleni.getRzVozidla(), datumOd, datumDo);
    }

    private Integer pocetVjezduProInterval(List<String> rzVozidla, Date datumOd, Date datumDo) {
        // Nastavení datumOd na začátek dne (00:00:00)
        ZonedDateTime datumOdZoned = ZonedDateTime.ofInstant(datumOd.toInstant(), ZoneId.systemDefault())
                                                .with(LocalTime.MIN);

        // Nastavení datumDo na konec dne (23:59:59)
        ZonedDateTime datumDoZoned = ZonedDateTime.ofInstant(datumDo.toInstant(), ZoneId.systemDefault())
                                                .with(LocalTime.MAX);

        int pocetVjezdu = 0;

        for (String rz : rzVozidla) {
            // Ziskani vjezdu podle RZ vozidla v danem obdobi
            List<VjezdVozidla> vjezdy = vjezdVozidlaRepository.findByRzVozidlaAndDatumOdBetween(rz, datumOdZoned, datumDoZoned);

            pocetVjezdu += vjezdy.size();
        }

        return pocetVjezdu;
    }

    public PovoleniVjezduVozidla zmenitStavZadosti(AppUserDto appUserDto, HttpServletRequest request, PovoleniVjezduVozidla povoleniVjezduVozidla, ZadostStavEnum stavEnum) throws NoSuchMessageException, BaseException {        
        povoleniVjezduVozidla.setStav(new ZadostStav(stavEnum));
        PovoleniVjezduVozidla savedPovoleni = create(appUserDto, povoleniVjezduVozidla, request);

        return savedPovoleni;
    }


    public void zkontrolovatNutnostOdeslaniOznameni(PovoleniVjezduVozidla povoleni, Boolean novyZaznam, HttpServletRequest request) throws NoSuchMessageException, BaseException {
        if (novyZaznam) {
            if (povoleni.getStav().getZadostStavEnum().equals(ZadostStavEnum.PRIPRAVENO))
                oznamitObsluhuOVytvoreniZadosti(povoleni, request);

            zaslatEmailOZmeneZadostiZadateli(povoleni, novyZaznam);
            return;
        }

        PovoleniVjezduVozidlaZmenaStavu povoleniHistorie = povoleniVjezduVozidlaZmenaStavuRepository.findLatestById(povoleni.getIdPovoleniVjezduVozidla());

        //Pokud nedošlo ke změně aktivity nebo stavu
        if (povoleniHistorie == null || !povoleniHistorie.getCas().equals(povoleni.getCasZmn())) {
            return; 
        }

        zaslatEmailOZmeneZadostiZadateli(povoleni, novyZaznam);
    }

    public void oznamitObsluhuOVytvoreniZadosti(PovoleniVjezduVozidla povoleniVjezduVozidla, HttpServletRequest request) throws NoSuchMessageException, BaseException {        
        List<RoleEnum> pozadovaneRoleObsluhy = new ArrayList<RoleEnum>();
        pozadovaneRoleObsluhy.add(RoleEnum.ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA);

        List<Uzivatel> odpovidajiciObsluha = vratniceBaseService.listUzivateleDleOpravneniKZavoduARoliProCelyPodnik(pozadovaneRoleObsluhy, povoleniVjezduVozidla.getZavod().getIdZavod());

        //http://localhost:4206/#/private/zadost-povoleni-vjezdu-vozidla?typSeznamuZadostiInt=enum&idPovoleniVjezduVozidla=id
        String oznameniUrl = vratniceProperties.getNgServerUrl() 
            + String.format("#/private/zadost-povoleni-vjezdu-vozidla?typSeznamuZadostiInt=%s&idPovoleniVjezduVozidla=%s",
                povoleniVjezduVozidla.getStav().getZadostStavEnum().getValue().toString(), 
                povoleniVjezduVozidla.getIdPovoleniVjezduVozidla());

        String predmet = messageSource.getMessage("avizace.povoleni_vjezdu_vozidla.zadost_vytvorena.predmet", null, LocaleContextHolder.getLocale());
        String oznameniText = vytvorOznameniProObsluhu(povoleniVjezduVozidla);
        String emailUrl = String.format("\n\n<a href='%s'>Detail žádosti</a>", oznameniUrl);
        String telo = String.format("Dobrý den, \n") + oznameniText + emailUrl;

        vratniceBaseService.zaslatOznameniUzivateli(predmet, oznameniText, telo, oznameniUrl, odpovidajiciObsluha, TypOznameniEnum.DULEZITE_INFO, request);
    }

    private String vytvorOznameniProObsluhu(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        String celeJmenoZadatele = povoleniVjezduVozidla.getJmenoZadatele() + " " + povoleniVjezduVozidla.getPrijmeniZadatele();
        String zavodNazev = povoleniVjezduVozidla.getZavod().getNazev();

        String lokalityNazvy = ""; // Inicializace proměnné
        List<Lokalita> lokality = povoleniVjezduVozidla.getLokality();
    
        if (lokality != null && !lokality.isEmpty()) {
            lokalityNazvy = lokality.stream()
                .map(Lokalita::getNazev)
                .collect(Collectors.joining(", "));
        }

        String rzVozidelNazvy = ""; // Inicializace proměnné
        List<String> rzVozidel = povoleniVjezduVozidla.getRzVozidla();
        
        if (rzVozidel != null && !rzVozidel.isEmpty()) {
            // Není třeba použít .map(String), protože je to již List<String>
            rzVozidelNazvy = rzVozidel.stream()
                .collect(Collectors.joining(", "));
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        String datumVytvoreni = dateFormat.format(povoleniVjezduVozidla.getDatumVytvoreni());
        String datumOd = dateFormat.format(povoleniVjezduVozidla.getDatumOd());
        String datumDo = dateFormat.format(povoleniVjezduVozidla.getDatumDo());

        String datumPlatnosti = "<strong>"+ datumOd +"</strong> - <strong>" + datumDo +"</strong>";

        return String.format(
            "Byla vytvořena nová žádost o povolení vjezdu vozidla dne <strong>%s</strong>.\n" + 
            "Jméno žadalatele: <strong>%s</strong>\n" +
            "Závod: <strong>%s</strong>\n" +
            "Lokality: <strong>%s</strong>\n" +
            "RZ vozidel: <strong>%s</strong>\n"+
            "Datum platnosti: <strong>%s<strong>\n",
            datumVytvoreni,
            celeJmenoZadatele,
            zavodNazev,
            lokalityNazvy,
            rzVozidelNazvy,
            datumPlatnosti
        );

    }


    public String generateEmailJadroZadosti(PovoleniVjezduVozidla povoleniVjezduVozidla, Boolean novyZaznam) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String datumVytvoreni = dateFormat.format(povoleniVjezduVozidla.getDatumVytvoreni());
        String prijmeni = povoleniVjezduVozidla.getPrijmeniZadatele();
        StringBuilder emailBuilder = new StringBuilder();

        if (novyZaznam && povoleniVjezduVozidla.getStav().getZadostStavEnum().equals(ZadostStavEnum.PRIPRAVENO)) { //Vytvoření žádosti
            emailBuilder.append("Vážený/á pane/paní ").append(prijmeni).append(",\n\n")
                .append("rádi bychom Vás informovali, že Vaše žádost o povolení vjezdu vozidla byla úspěšně vytvořena \n")
                .append("dne <strong>").append(datumVytvoreni).append("</strong> a je v procesu schvalování. ");
        } 
        else { //změna stavu žádosti
            emailBuilder.append("Vážený/á pane/paní ").append(prijmeni).append(",\n\n")
                .append("Vaše žádost o povolení vjezdu vozidla ze dne <strong>").append(datumVytvoreni)
                .append("</strong> byla ").append(generateOznameniZmenyStavuAktivityZadosti(povoleniVjezduVozidla));
        }


        emailBuilder.append(generateEmailDetailZadosti(povoleniVjezduVozidla));

        return emailBuilder.toString();
    }

    public String generateEmailDetailZadosti(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        StringBuilder detailBuilder = new StringBuilder();

        detailBuilder.append("Níže naleznete podrobnosti k Vaší žádosti:\n\n")
            .append("<strong>Žadatel</strong>:\n")
            .append("Jméno: ").append(povoleniVjezduVozidla.getJmenoZadatele()).append("\n")
            .append("Příjmení: ").append(povoleniVjezduVozidla.getPrijmeniZadatele()).append("\n\n")
            .append("<strong>Registrační značky vozidel</strong>:\n");
        
        List<String> rzVozidla = povoleniVjezduVozidla.getRzVozidla();
        if (rzVozidla != null && !rzVozidla.isEmpty()) {
            for (String rz : rzVozidla) {
                detailBuilder.append(rz).append("\n");
            }
        } 
        
        detailBuilder.append("\n<strong>Závod</strong>:\n")
            .append(povoleniVjezduVozidla.getZavod().getNazev()).append("\n\n")
            .append("<strong>Lokality</strong>:\n");
        
        List<Lokalita> lokality = povoleniVjezduVozidla.getLokality();
        if (lokality != null && !lokality.isEmpty()) {
            for (Lokalita lokalita : lokality) {
                detailBuilder.append(lokalita.getNazev()).append("\n");
            }
        }

        detailBuilder.append("\n<strong>Období povolení vjezdu</strong>:\n")
            .append("Od: ").append(dateFormat.format(povoleniVjezduVozidla.getDatumOd())).append("\n")
            .append("Do: ").append(dateFormat.format(povoleniVjezduVozidla.getDatumDo())).append("\n\n");

        return detailBuilder.toString();
    }

    public String generateOznameniZmenyStavuAktivityZadosti(PovoleniVjezduVozidla povoleni) {
        ZadostStavEnum stavZadostiEnum = povoleni.getStav().getZadostStavEnum();
        PovoleniVjezduVozidlaZmenaStavu povoleniHistorie = povoleniVjezduVozidlaZmenaStavuRepository.findLatestById(povoleni.getIdPovoleniVjezduVozidla());

        boolean stavZmenen = !povoleniHistorie.getStavNovy().equals(povoleniHistorie.getStavPuvodni());
        boolean aktivitaZmenena = !povoleniHistorie.getAktivitaNovy().equals(povoleniHistorie.getAktivitaPuvodni());
    
        if (aktivitaZmenena) {
            if (povoleni.getAktivita()) {
                return "<strong>obnovena</strong> se stavem " + getOznameniStavZadosti(stavZadostiEnum);
            } else {
                return "<strong>zneplatněna</strong>. ";
            }
        }
    
        if (stavZmenen) {
            return getOznameniStavZadosti(stavZadostiEnum);
        }
    
        // Výchozí návratová hodnota, pokud nedojde ke změně aktivity ani stavu
        return null;
    }

    public String getOznameniStavZadosti(ZadostStavEnum novyStav) {
        switch (novyStav) {
            case PRIPRAVENO: return "<strong>připraveno ke schvalování</strong>. ";
            case SCHVALENO: return "<strong>schválena</strong>. ";
            case POZASTAVENO: return "<strong>pozastavena</strong>. ";
            case UKONCENO: return "<strong>ukončena</strong>. ";
            case ZAMITNUTO: return "<strong>zamítnuta</strong>. ";
            default: return null;
        }
    }

    @TransactionalRO
    public void zaslatEmailOZmeneZadostiZadateli(PovoleniVjezduVozidla povoleniVjezduVozidla, Boolean novyZaznam) throws NoSuchMessageException, BaseException {
        String predmet = messageSource.getMessage(novyZaznam ? "avizace.povoleni_vjezdu_vozidla.zadost_vytvorena.predmet" : 
                    "avizace.povoleni_vjezdu_vozidla.zadost_aktualizace.predmet", null, LocaleContextHolder.getLocale());
        
        String telo = generateEmailJadroZadosti(povoleniVjezduVozidla, novyZaznam);
        String prijemce = povoleniVjezduVozidla.getEmailZadatele();
        String odesilatel = "noreply@diamo.cz";

        Ws02EmailDto email = new Ws02EmailDto(prijemce, odesilatel, predmet, telo);

        wso2Services.poslatEmail(email);
    }

    public void zkontrolujOpravneniUzivateleVuciPovoleni(String idZavodPovoleni, String idUzivatel) throws AccessDeniedException, NoSuchMessageException {
        List<Zavod> zavodyUzivatele = vratniceBaseService.getAllZavodyUzivateleByPristup(idUzivatel);

        logger.info("---------");
        logger.info(idZavodPovoleni);
        logger.info(zavodyUzivatele);

        boolean povoleniNalezeno = zavodyUzivatele.stream()
            .anyMatch(zavod -> zavod.getIdZavod().equals(idZavodPovoleni));

        if (!povoleniNalezeno) {
            throw new AccessDeniedException(
						messageSource.getMessage("povoleni.vjezdu.vozidla.not_access", null, LocaleContextHolder.getLocale()));
        }


    }
}
