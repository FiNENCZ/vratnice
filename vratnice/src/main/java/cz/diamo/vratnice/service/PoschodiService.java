package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.repository.BudovaRepository;
import cz.diamo.vratnice.repository.PoschodiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class PoschodiService {
    
    @Autowired
    private PoschodiRepository poschodiRepository;

    @Autowired
    private BudovaRepository budovaRepository;


    @PersistenceContext
    private EntityManager entityManager;
   

    public List<Poschodi> getList(String idBudova) {
        Budova budova = budovaRepository.getDetail(idBudova);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Poschodi s");
        queryString.append(" where 1 = 1");

        if (budova != null)
            queryString.append(" and s.budova = :budova");


        Query vysledek = entityManager.createQuery(queryString.toString());

        if (budova != null)
            vysledek.setParameter("budova", budova);
        
        @SuppressWarnings("unchecked")
        List<Poschodi> list = vysledek.getResultList();
        return list;
    }

    public Poschodi detail(String idPoschodi) {
        return poschodiRepository.getDetail(idPoschodi);
    }


}
