package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.SluzebniVozidlo;
import cz.diamo.vratnice.repository.SluzebniVozidloRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class SluzebniVozidloService {

    @Autowired
    private SluzebniVozidloRepository sluzebniVozidloRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    public List<SluzebniVozidlo> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from SluzebniVozidlo s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<SluzebniVozidlo> list = vysledek.getResultList();
        return list;
    }

    @Transactional
    public SluzebniVozidlo create(SluzebniVozidlo sluzebniVozidlo) {
        sluzebniVozidlo.setCasZmn(Utils.getCasZmn());
        sluzebniVozidlo.setZmenuProvedl(Utils.getZmenuProv());
        return sluzebniVozidloRepository.save(sluzebniVozidlo);
    }

    public SluzebniVozidlo getDetail(String id) {
        return sluzebniVozidloRepository.getDetail(id);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByStav(String stav) {
        return sluzebniVozidloRepository.getSluzebniVozidloByStav(stav);
    }

    public List<SluzebniVozidlo> getSluzebniVozidloByAktivita(Boolean aktivita) {
        return sluzebniVozidloRepository.findByAktivita(aktivita);
    }

}
