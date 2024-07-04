package cz.diamo.vratnice.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.vratnice.entity.Klic;
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

    public List<Klic> getAllKeys() {
        return klicRepository.findAll();
    }

    @Transactional
    public Klic createKey(Klic klic) {
        klic.setCasZmn(Utils.getCasZmn());
        klic.setZmenuProvedl(Utils.getZmenuProv());
        return klicRepository.save(klic);
    }

    public List<Klic> getList(Boolean aktivita, Boolean specialni) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Klic s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        if (specialni != null)
            queryString.append(" and s.specialni = :specialni");
        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        if (specialni != null)
            vysledek.setParameter("specialni", specialni);
        
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

}
