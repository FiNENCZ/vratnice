package cz.diamo.vratnice.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.annotation.TransactionalRO;
import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.dto.Ws02EmailDto;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.entity.Opravneni;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.share.entity.Zavod;
import cz.diamo.share.enums.RoleEnum;
import cz.diamo.share.enums.TypOznameniEnum;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.share.repository.OpravneniZavodRepository;
import cz.diamo.share.repository.UzivatelOpravneniRepository;
import cz.diamo.share.services.Wso2Services;
import cz.diamo.vratnice.dto.PovoleniVjezduVozidlaDto;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidla;
import cz.diamo.vratnice.entity.PovoleniVjezduVozidlaZmenaStavu;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.Spolecnost;
import cz.diamo.vratnice.entity.Stat;
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
import jakarta.persistence.TypedQuery;
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
    private UzivatelOpravneniRepository uzivatelOpravneniRepository;

    @Autowired
    private OpravneniZavodRepository opravneniZavodRepository;

    @Autowired
    private VratniceBaseService vratniceBaseService;

    @Autowired
    private Wso2Services wso2Services;

    @PersistenceContext
    private EntityManager entityManager;

    public List<PovoleniVjezduVozidla> getList(Boolean aktivita, ZadostStavEnum stavEnum, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Set<Zavod> zavody = getAllZavodByPristup(appUserDto.getIdUzivatel());

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
        return list;
    }

    public PovoleniVjezduVozidla getDetail(String idPovoleniVjezduVozidla) {
        return povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
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
    public PovoleniVjezduVozidla create(PovoleniVjezduVozidla povoleniVjezduVozidla) throws NoSuchMessageException, BaseException {
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
        zkontrolovatNutnostOdeslaniOznameni(savedPovoleni, novyZaznam);

        return savedPovoleni;
    }

    @Transactional
    public PovoleniVjezduVozidla createFromPublic(PovoleniVjezduVozidlaDto povoleniVjezduVozidlaDto) throws NoSuchMessageException, BaseException {
        PovoleniVjezduVozidla povoleniEntity = povoleniVjezduVozidlaDto.toEntity();
        povoleniEntity.setStav(new ZadostStav(ZadostStavEnum.PRIPRAVENO));
        PovoleniVjezduVozidla savedPovoleni = create(povoleniEntity);

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
    public List<PovoleniVjezduVozidla> zneplatnitPovoleni(List<PovoleniVjezduVozidlaDto> povoleniVjezduVozidlaList) throws NoSuchMessageException, BaseException {
        List<PovoleniVjezduVozidla> result = new ArrayList<PovoleniVjezduVozidla>();

        for (PovoleniVjezduVozidlaDto povoleniVjezduVozidla : povoleniVjezduVozidlaList) {
            povoleniVjezduVozidla.setAktivita(false);

            result.add(create(povoleniVjezduVozidla.toEntity()));
        }

        return result;
    }

    public Stat getZemeRegistraceVozidla(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            povoleniVjezduVozidla.getZemeRegistraceVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleniVjezduVozidla.getZemeRegistraceVozidla().getNazevResx()));
            return povoleniVjezduVozidla.getZemeRegistraceVozidla();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public List<VozidloTyp> getTypyVozidel(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleniVjezduVozidla = povoleniVjezduVozidlaRepository.getDetail(idPovoleniVjezduVozidla);
        try {
            if (povoleniVjezduVozidla == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            List<VozidloTyp> typyVozidel = new ArrayList<VozidloTyp>();

            for (VozidloTyp vozidloTyp : povoleniVjezduVozidla.getTypVozidla()){
                vozidloTyp.setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vozidloTyp.getNazevResx()));
                typyVozidel.add(vozidloTyp);
            }

            return typyVozidel;

        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public ZadostStav getZadostStav(String idPovoleniVjezduVozidla) {
        PovoleniVjezduVozidla povoleni = getDetail(idPovoleniVjezduVozidla);

        try {
            if (povoleni == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
                povoleni.getStav().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), povoleni.getStav().getNazevResx()));
            return povoleni.getStav();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
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

    public PovoleniVjezduVozidla zmenitStavZadosti(PovoleniVjezduVozidla povoleniVjezduVozidla, ZadostStavEnum stavEnum) throws NoSuchMessageException, BaseException {
        povoleniVjezduVozidla.setStav(new ZadostStav(stavEnum));
        PovoleniVjezduVozidla savedPovoleni = create(povoleniVjezduVozidla);

        return savedPovoleni;
    }


    public void zkontrolovatNutnostOdeslaniOznameni(PovoleniVjezduVozidla povoleni, Boolean novyZaznam) throws NoSuchMessageException, BaseException {
        if (novyZaznam) {
            //oznamitObsluhuOVytvoreniZadosti(povoleni);
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

    //TODO: připojit na oznameniServices bez autentizovaného requestu
    public void oznamitObsluhuOVytvoreniZadosti(PovoleniVjezduVozidla povoleniVjezduVozidla) throws NoSuchMessageException, BaseException {
        List<RoleEnum> pozadovaneRoleObsluhy = new ArrayList<RoleEnum>();
        pozadovaneRoleObsluhy.add(RoleEnum.ROLE_SPRAVA_POVOLENI_VJEZDU_VOZIDLA);

        List<Uzivatel> odpovidajiciObsluha = listUzivateleDleOpravneniKZavoduARoliProCelyPodnik(pozadovaneRoleObsluhy, povoleniVjezduVozidla.getZavod().getIdZavod());


        String predmet = messageSource.getMessage("avizace.povoleni_vjezdu_vozidla.zadost_vytvorena.predmet", null, LocaleContextHolder.getLocale());
        String oznameniText = vytvorOznameniProObsluhu(povoleniVjezduVozidla);
        String telo = String.format("Dobrý den, \n") + oznameniText;

        vratniceBaseService.zaslatOznameniUzivateli(predmet, oznameniText, telo, null, odpovidajiciObsluha, TypOznameniEnum.DULEZITE_INFO, null);
    }

    private String vytvorOznameniProObsluhu(PovoleniVjezduVozidla povoleniVjezduVozidla) {
        String celeJmenoZadatele = povoleniVjezduVozidla.getJmenoZadatele() + povoleniVjezduVozidla.getPrijmeniZadatele();
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
            "Závod: <strong>%s</strong>" +
            "Lokality: <strong>%s</strong>" +
            "RZ vozidel: <strong>%s</strong>"+
            "Datum platnosti: <strong>%s<strong>",
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

        if (novyZaznam) { //Vytvoření žádosti
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

    public Set<Zavod> getAllZavodByPristup(String idUzivatel) throws RecordNotFoundException, NoSuchMessageException {
        Set<Zavod> result = new HashSet<Zavod>();

        List<Opravneni> opravneniUzivatele = uzivatelOpravneniRepository.listOpravneni(idUzivatel, true);

        if (opravneniUzivatele == null || opravneniUzivatele.isEmpty()) {
			return result;  // pokud uživatel nemá oprávnění, vracíme prázdný set
		}

        for (Opravneni opravneni : opravneniUzivatele) {
            result.addAll(opravneniZavodRepository.listZavod(opravneni.getIdOpravneni()));
        }

        return result;
    }


    public List<Uzivatel> listUzivateleDleOpravneniKZavoduARoliProCelyPodnik(List<RoleEnum> role, String idZavod) {
        // HQL dotaz na výběr uživatelů s odpovídajícími rolemi, modulem "vratnice" a přístupem k danému závodu
        String hql = "SELECT u FROM Uzivatel u " +
            "JOIN UzivatelModul um ON u.idUzivatel = um.idUzivatel " +
            "JOIN UzivatelOpravneni uo ON u.idUzivatel = uo.idUzivatel " +
            "JOIN Opravneni o ON uo.idOpravneni = o.idOpravneni " +
            "JOIN OpravneniRole orl ON o.idOpravneni = orl.idOpravneni " +
            "JOIN OpravneniZavod oz ON o.idOpravneni = oz.idOpravneni " +
            "WHERE orl.authority IN :roles " +
            "AND um.modul = 'vratnice' " +
            "AND oz.idZavod = :idZavod";

        TypedQuery<Uzivatel> query = entityManager.createQuery(hql, Uzivatel.class);
        query.setParameter("roles", role.stream().map(RoleEnum::toString).toList()); // Převod RoleEnum na String
        query.setParameter("idZavod", idZavod);

        return query.getResultList();
    }


}
