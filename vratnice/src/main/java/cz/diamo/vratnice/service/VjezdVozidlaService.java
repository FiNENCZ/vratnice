package cz.diamo.vratnice.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.component.ResourcesComponent;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.enums.VozidloTypEnum;
import cz.diamo.vratnice.filter.FilterPristupuVratnice;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VjezdVozidlaService {

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ResourcesComponent resourcesComponent;

    public List<VjezdVozidla> getList(Boolean aktivita, Boolean nevyporadaneVjezdy, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        String idUzivatel = appUserDto.getIdUzivatel();
        
        StringBuilder queryString = new StringBuilder();

        queryString.append("SELECT s FROM VjezdVozidla s ");
        queryString.append("WHERE 1 = 1 ");

        if (aktivita != null)
            queryString.append("AND s.aktivita = :aktivita ");

        if (nevyporadaneVjezdy != null) {
            if (!nevyporadaneVjezdy) {
                queryString.append("AND (s.zmenuProvedl <> 'kamery' AND s.zmenuProvedl IS NOT NULL) ");
            } else {
                queryString.append("AND (s.zmenuProvedl = 'kamery' OR s.zmenuProvedl IS NULL) ");
            }
        }

        queryString.append(FilterPristupuVratnice.filtrujDlePrirazeneVratnice("s.vratnice.idVratnice"));

        Query vysledek = entityManager.createQuery(queryString.toString());
        vysledek.setParameter("idUzivatel", idUzivatel);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        @SuppressWarnings("unchecked")
        List<VjezdVozidla> list = vysledek.getResultList();

        if (list != null) {
            for (VjezdVozidla vjezdVozidla : list) {
                vjezdVozidla = translateVjezdVozidla(vjezdVozidla);
            }
        }


        return list;
    }

    private VjezdVozidla translateVjezdVozidla(VjezdVozidla vjezdVozidla) throws RecordNotFoundException, NoSuchMessageException {
        if (vjezdVozidla.getTypVozidla() != null && vjezdVozidla.getTypVozidla().getNazevResx() != null)
            vjezdVozidla.getTypVozidla().setNazev(resourcesComponent.getResources(LocaleContextHolder.getLocale(), vjezdVozidla.getTypVozidla().getNazevResx()));

        return vjezdVozidla;
    }

    public VjezdVozidla getDetail(String idVjezdVozidla) throws RecordNotFoundException, NoSuchMessageException {

        VjezdVozidla vjezdVozidla = vjezdVozidlaRepository.getDetail(idVjezdVozidla);

        if (vjezdVozidla != null) 
            vjezdVozidla = translateVjezdVozidla(vjezdVozidla);
        
        return vjezdVozidla;
    }

    @Transactional
    public VjezdVozidla create(VjezdVozidla vjezdVozidla, Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        if (vjezdVozidla.getZmenuProvedl() == null ) {        
            vjezdVozidla.setCasZmn(Utils.getCasZmn());
            vjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        }

        if (vjezdVozidla.getVratnice() == null)
            if (vratnice != null)
                vjezdVozidla.setVratnice(vratnice);

            
        VjezdVozidla saveVjezdVozidla =  vjezdVozidlaRepository.save(vjezdVozidla);
        return translateVjezdVozidla(saveVjezdVozidla);
    }

    @Transactional
    public VjezdVozidla createIZSVjezdVozidla(String rzVozidla, Vratnice vratnice) throws RecordNotFoundException, NoSuchMessageException {
        VjezdVozidla vjezdVozidlaIZS = new VjezdVozidla();
        vjezdVozidlaIZS.setRzVozidla(rzVozidla);
        vjezdVozidlaIZS.setCasPrijezdu(ZonedDateTime.now());
        vjezdVozidlaIZS.setTypVozidla(new VozidloTyp(VozidloTypEnum.VOZIDLO_IZS));

        return create(vjezdVozidlaIZS, vratnice);
    }
}
