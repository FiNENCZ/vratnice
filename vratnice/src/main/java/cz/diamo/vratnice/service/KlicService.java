package cz.diamo.vratnice.service;

import java.util.List;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.KlicTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
import cz.diamo.vratnice.enums.ZadostStavEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.HistorieVypujcekRepository;
import cz.diamo.vratnice.repository.KlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class KlicService {

    @Autowired
    private KlicRepository klicRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
	private MessageSource messageSource;

    @Autowired
    private ResourcesComponent resourcesComponent;

    @Autowired
    private HistorieVypujcekRepository historieVypujcekRepository;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @Autowired
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    public List<Klic> getAllKeys() {
        return klicRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic klic, AppUserDto appUserDto) throws NoSuchMessageException, BaseException {
        maUzivatelPristupKeKlici(klic, appUserDto);

        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());
        return klicRepository.save(klic);
    }


    public void maUzivatelPristupKeKlici(Klic klic, AppUserDto appUserDto) throws NoSuchMessageException, BaseException {
        Boolean maVsechnyVratnice = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        if (!maVsechnyVratnice) {
            if (nastavenaVratnice != null) {
                if (!klic.getVratnice().getIdVratnice().equals(nastavenaVratnice.getIdVratnice())){
                    throw new BaseException(messageSource.getMessage("klic.save.no_access", null, LocaleContextHolder.getLocale()));
                }
            }
            else {
                throw new BaseException(messageSource.getMessage("klic.save.no_access", null, LocaleContextHolder.getLocale()));
            }
        }
        
    }

    public List<Klic> getList(Boolean aktivita, Boolean specialni, AppUserDto appUserDto)  {
        String idUzivatel = appUserDto.getIdUzivatel();

        StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT s FROM Klic s ")
        .append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (specialni != null)
            queryString.append("AND s.specialni = :specialni ");

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));


        Query vysledek = entityManager.createQuery(queryString.toString());

        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        @SuppressWarnings("unchecked")
        List<Klic> list = vysledek.getResultList();
        return list;

    }

    public List<Klic> getList(String idLokalita, String idBudova, String idPoschodi, Boolean aktivita, Boolean specialni) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Klic s");
        queryString.append(" left join fetch s.lokalita lok");
        queryString.append(" left join fetch s.budova bud");
        queryString.append(" left join fetch s.poschodi pos");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (specialni != null)
            queryString.append(" and s.specialni = :specialni");

        if (StringUtils.isNotBlank(idLokalita))
            queryString.append(" and lok.idLokalita = :idLokalita");
            
        if (StringUtils.isNotBlank(idBudova))
            queryString.append(" and bud.idBudova = :idBudova");
            
        if (StringUtils.isNotBlank(idPoschodi))
            queryString.append(" and pos.idPoschodi = :idPoschodi");
            
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        if (StringUtils.isNotBlank(idLokalita))
            vysledek.setParameter("idLokalita", idLokalita);
            
        if (StringUtils.isNotBlank(idBudova))
            vysledek.setParameter("idBudova", idBudova);
            
        if (StringUtils.isNotBlank(idPoschodi))
            vysledek.setParameter("idPoschodi", idPoschodi);
            
        @SuppressWarnings("unchecked")
        List<Klic> list = vysledek.getResultList();
        return list;

    }

    public Klic getDetail(String idKlic) {
        return klicRepository.getDetail(idKlic);
    }

    public Klic getDetailByChipCode(String kodCipu) {
        return klicRepository.getDetailByKodCipu(kodCipu);
    }

    public List<Klic> getBySpecialni(Boolean specialni) {
        return klicRepository.getBySpecialni(specialni);
    }


    public List<Klic> getKlicByAktivita(Boolean aktivita) {
        return klicRepository.findByAktivita(aktivita);
    }

    public KlicTyp getKlicTyp(String idKlic) {
        Klic klic = klicRepository.getDetail(idKlic);
        try {
            if (klic == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, messageSource.getMessage("record.not.found", null, LocaleContextHolder.getLocale()));
        
            klic.getTyp().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), klic.getTyp().getNazevResx()));
            return klic.getTyp();
        } catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.toString());
		}
    }

    public Boolean jeDostupny(ZadostKlic zadost) {


        HistorieVypujcekAkce vypujckaAkce = historieVypujcekRepository.findLastAkceByIdKlic(zadost.getKlic().getIdKlic());

        //Pokud byl klíč vypůjčen je ho možné vrátit
        if (vypujckaAkce != null){
            if (vypujckaAkce.getHistorieVypujcekAkceEnum() == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VYPUJCEN) {
                return false;
            }
        }


        // Zkontroluj, zda je žádost schválená
        if (zadost.getZadostStav().getZadostStavEnum() != ZadostStavEnum.SCHVALENO) {
            return null;
        }

        // Zkontroluj, zda žádost a klíč jsou aktivní
        if (!zadost.getAktivita() || !zadost.getKlic().getAktivita()) {
            return null;
        }

        // Pokud není žádost trvalá, zkontroluj platnost výpůjčky
        if (!zadost.getTrvala()) {
            Date currentDate = new Date();
            if (zadost.getDatumOd() != null && zadost.getDatumDo() != null &&
                (currentDate.before(zadost.getDatumOd()) || currentDate.after(zadost.getDatumDo()))) {
                return null;
            }
        }

        // Zkontroluj, zda byl klíč vrácen nebo je stále vypůjčen
        if (vypujckaAkce == null || vypujckaAkce.getHistorieVypujcekAkceEnum() == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) {
            return true;
        }

        return null;

        /* Return STATEMENTS
        false - je možné vrátit klíč (poslední akce byla vypůjčení)
        true - je možné klíč vypůjčit
        null - není možné klíč vypůjčit, ani vrátit (neaktivita, platnost vypršela, atd...)
         */
    }

} 
