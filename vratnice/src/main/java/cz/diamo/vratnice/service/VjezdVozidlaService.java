package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.Ridic;
import cz.diamo.vratnice.entity.VjezdVozidla;
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

    public List<VjezdVozidla> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from VjezdVozidla s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<VjezdVozidla> list = vysledek.getResultList();
        return list;
    }

    public VjezdVozidla getDetail(String idVjezdVozidla) {
        return vjezdVozidlaRepository.getDetail(idVjezdVozidla);
    }

    public List<VjezdVozidla> getByRzVozidla(String rzVozidla) {
        return vjezdVozidlaRepository.getByRzVozidla(rzVozidla);
    }

    public List<VjezdVozidla> getByRidic(Ridic ridic) {
        return vjezdVozidlaRepository.getByRidic(ridic);
    }

    @Transactional
    public VjezdVozidla create(VjezdVozidla vjezdVozidla) {
        vjezdVozidla.setCasZmn(Utils.getCasZmn());
        vjezdVozidla.setZmenuProvedl(Utils.getZmenuProv());
        return vjezdVozidlaRepository.save(vjezdVozidla);
    }

}
