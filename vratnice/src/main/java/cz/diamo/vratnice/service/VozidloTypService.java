package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import cz.diamo.vratnice.entity.VozidloTyp;
import cz.diamo.vratnice.enums.VozidloTypEnum;
import cz.diamo.vratnice.repository.VozidloTypRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class VozidloTypService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VozidloTypRepository vozidloTypRepository;

   public List<VozidloTyp> getList(Boolean withIZS) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from VozidloTyp s");
        queryString.append(" where 1 = 1");

        if (withIZS != null)
            if (!withIZS)
                queryString.append(" and s.nazevResx <> :nazevResx");

 
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (withIZS != null)
            if (!withIZS)
                vysledek.setParameter("nazevResx", VozidloTypEnum.VOZIDLO_IZS.toString());
        
        @SuppressWarnings("unchecked")
        List<VozidloTyp> list = vysledek.getResultList();
        return list;
    }

    public VozidloTyp detail(Integer idVozidloTyp) {
        return vozidloTypRepository.getDetail(idVozidloTyp);
    }

}
