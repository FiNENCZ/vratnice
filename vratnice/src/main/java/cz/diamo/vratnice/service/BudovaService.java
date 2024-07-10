package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.vratnice.entity.Budova;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.repository.BudovaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class BudovaService {

    @Autowired
    private LokalitaService lokalitaService;

    @Autowired
    private BudovaRepository budovaRepository;

    @PersistenceContext
    private EntityManager entityManager;


    public List<Budova> getList(String idLokalita) {
        Lokalita lokalita = lokalitaService.detail(idLokalita);


        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Budova s");
        queryString.append(" where 1 = 1");

        if (lokalita != null)
            queryString.append(" and s.lokalita = :lokalita");

 
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (lokalita != null)
            vysledek.setParameter("lokalita", lokalita);
        
        @SuppressWarnings("unchecked")
        List<Budova> list = vysledek.getResultList();
        return list;
    }

    public Budova detail(String idBudova) {
        return budovaRepository.getDetail(idBudova);
    }


}
