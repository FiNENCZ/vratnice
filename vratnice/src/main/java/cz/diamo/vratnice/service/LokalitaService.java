package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.entity.Zavod;
import cz.diamo.share.services.ZavodServices;
import cz.diamo.vratnice.entity.Lokalita;
import cz.diamo.vratnice.repository.LokalitaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class LokalitaService {

    @Autowired
    private LokalitaRepository lokalitaRepository;

    @Autowired
    private ZavodServices zavodServices;

    @PersistenceContext
    private EntityManager entityManager;

    public List<Lokalita> getList(String idZavod) {
        Zavod zavod = zavodServices.getDetail(idZavod);

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Lokalita s");
        queryString.append(" where 1 = 1");

        if (zavod != null)
            queryString.append(" and s.zavod = :zavod");

 
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (zavod != null)
            vysledek.setParameter("zavod", zavod);
        
        @SuppressWarnings("unchecked")
        List<Lokalita> list = vysledek.getResultList();
        return list;
    }

    public Lokalita detail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

}
