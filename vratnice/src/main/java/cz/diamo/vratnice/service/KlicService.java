package cz.diamo.vratnice.service;

import java.util.List;

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
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.HistorieVypujcekAkce;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.KlicTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.enums.HistorieVypujcekAkceEnum;
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
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    @Autowired
    private HistorieVypujcekRepository historieVypujcekRepository;

    public List<Klic> getAllKeys() {
        return klicRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic klic) {
        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());
        return klicRepository.save(klic);
    }

    public List<Klic> getList(Boolean aktivita, Boolean specialni, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean maVsechnyVratnice = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Klic s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (specialni != null)
            queryString.append(" and s.specialni = :specialni");

        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null) 
                queryString.append(" and s.vratnice = :vratnice");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        if (specialni != null)
            vysledek.setParameter("specialni", specialni);

        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null)
                vysledek.setParameter("vratnice", nastavenaVratnice);
            else
                return null;
        
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

    public Boolean jeDostupny(String idKlic) {
        HistorieVypujcekAkce vypujckaAkce = historieVypujcekRepository.findLastAkceByIdKlic(idKlic);

        if (vypujckaAkce == null)
            return true;

        if (vypujckaAkce.getHistorieVypujcekAkceEnum() == HistorieVypujcekAkceEnum.HISTORIE_VYPUJCEK_VRACEN) 
            return true;
        else
            return false;
    }

}
