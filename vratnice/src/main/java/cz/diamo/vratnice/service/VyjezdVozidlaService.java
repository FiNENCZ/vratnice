package cz.diamo.vratnice.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.dto.AppUserDto;
import cz.diamo.share.exceptions.RecordNotFoundException;
import cz.diamo.vratnice.entity.VjezdVozidla;
import cz.diamo.vratnice.entity.Vratnice;
import cz.diamo.vratnice.entity.VyjezdVozidla;
import cz.diamo.vratnice.repository.VjezdVozidlaRepository;
import cz.diamo.vratnice.repository.VyjezdVozidlaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class VyjezdVozidlaService {

    @Autowired
    private VyjezdVozidlaRepository vyjezdVozidlaRepository;

    @Autowired
    private VjezdVozidlaRepository vjezdVozidlaRepository;

    @PersistenceContext
    private EntityManager entityManager;

        @Autowired
    private UzivatelVsechnyVratniceService uzivatelVsechnyVratniceService;

    @Autowired
    private UzivatelVratniceService uzivatelVratniceService;

    public List<VyjezdVozidla> getList(Boolean aktivita, Boolean nevyporadaneVyjezdy, AppUserDto appUserDto) throws RecordNotFoundException, NoSuchMessageException {
        Boolean maVsechnyVratnice = uzivatelVsechnyVratniceService.jeNastavena(appUserDto);
        Vratnice nastavenaVratnice = uzivatelVratniceService.getNastavenaVratniceByUzivatel(appUserDto);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from VyjezdVozidla s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null) 
                queryString.append(" and s.vratnice = :vratnice");

        if (nevyporadaneVyjezdy != null) {
            if (!nevyporadaneVyjezdy) {
                queryString.append(" AND (s.zmenuProvedl <> 'kamery' AND s.zmenuProvedl IS NOT NULL)");
            } else {
                queryString.append(" AND (s.zmenuProvedl = 'kamery' OR s.zmenuProvedl IS NULL)");
            }
        }

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);

        if (!maVsechnyVratnice)
            if (nastavenaVratnice != null)
                vysledek.setParameter("vratnice", nastavenaVratnice);
        
        @SuppressWarnings("unchecked")
        List<VyjezdVozidla> list = vysledek.getResultList();
        return list;
    }
    public VyjezdVozidla getDetail(String idVyjezdVozidla) {
        return vyjezdVozidlaRepository.getDetail(idVyjezdVozidla);
    }

    public List<VyjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public List<VyjezdVozidla> getNevyporadaneVyjezdy(Boolean aktivita) {
        return vyjezdVozidlaRepository.getNevyporadaneVyjezdy(aktivita);
    }

    public Optional<VyjezdVozidla> jeMozneVyjet(String rzVozidla) {
        List<VjezdVozidla> vjezdVozidel = vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
        List<VyjezdVozidla> vyjezdVozidel = vyjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    
        if (vjezdVozidel.isEmpty()) {
            return Optional.empty();
        }
    
        VjezdVozidla posledniVjezdVozidla = vjezdVozidel.get(vjezdVozidel.size() - 1);
    
        if (vjezdVozidel.size() > vyjezdVozidel.size() || vyjezdVozidel.isEmpty()) {
            return Optional.of(mapToVyjezdVozidla(posledniVjezdVozidla));
        }
    
        return Optional.empty();
    }
    
    private VyjezdVozidla mapToVyjezdVozidla(VjezdVozidla posledniVjezdVozidla) {
        VyjezdVozidla vyjezdVozidla = new VyjezdVozidla();
        
        if (posledniVjezdVozidla.getOpakovanyVjezd() != null) {
            vyjezdVozidla.setOpakovanyVjezd(true);
        }
        
        vyjezdVozidla.setRzVozidla(posledniVjezdVozidla.getRzVozidla());
        return vyjezdVozidla;
    }

    @Transactional
    public VyjezdVozidla create(VyjezdVozidla vyjezdVozidla, Vratnice vratnice) {
        if (vyjezdVozidla.getZmenuProvedl() == null ) {        
            vyjezdVozidla.setCasZmn(Utils.getCasZmn());
            vyjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        }

        if (vyjezdVozidla.getVratnice() == null)
            if (vratnice != null)
                vyjezdVozidla.setVratnice(vratnice);

        return vyjezdVozidlaRepository.save(vyjezdVozidla);
    }


}
