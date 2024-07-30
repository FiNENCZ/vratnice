package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.UzivatelVratnice;
import cz.diamo.vratnice.repository.UzivatelVratniceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class UzivatelVratniceService {

    @Autowired
    private UzivatelVratniceRepository uzivatelVratniceRepository;


    @PersistenceContext
    private EntityManager entityManager;

    public List<UzivatelVratnice> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from UzivatelVratnice s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<UzivatelVratnice> list = vysledek.getResultList();
        return list;
    }

    @Transactional
    public UzivatelVratnice save(UzivatelVratnice uzivatelVratnice){
        uzivatelVratnice.setCasZmn(Utils.getCasZmn());
        uzivatelVratnice.setZmenuProvedl(Utils.getZmenuProv());
        return uzivatelVratniceRepository.save(uzivatelVratnice);
    }

    public UzivatelVratnice getDetail(String idUzivatelVratnice) {
        return uzivatelVratniceRepository.getDetail(idUzivatelVratnice);
    }

    

}
