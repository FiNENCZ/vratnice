package cz.diamo.vratnice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;
import cz.diamo.vratnice.repository.ZadostKlicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

@Service
public class ZadostKlicService {

    @Autowired
    private ZadostKlicRepository zadostiKlicRepository;

      @PersistenceContext
    private EntityManager entityManager;


    public List<ZadostKlic> getList(Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from ZadostKlic s");
        queryString.append(" where 1 = 1");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        
        @SuppressWarnings("unchecked")
        List<ZadostKlic> list = vysledek.getResultList();
        return list;
    }

    @Transactional
    public ZadostKlic create(ZadostKlic zadostKlic) {
        zadostKlic.setCasZmn(Utils.getCasZmn());
        zadostKlic.setZmenuProvedl(Utils.getZmenuProv());
        return zadostiKlicRepository.save(zadostKlic);
    }

    public ZadostKlic getDetail(String idZadostiKlic) {
        return zadostiKlicRepository.getDetail(idZadostiKlic);
    }

    public List<ZadostKlic> getZadostiByStav(String stav) {
        return zadostiKlicRepository.getZadostiByStav(stav);
    }

    public List<ZadostKlic> findByKlic(Klic klic){
        return zadostiKlicRepository.findByKlic(klic);
    }

    public List<ZadostKlic> findByUzivatel(Uzivatel uzivatel){
        return zadostiKlicRepository.findByUzivatel(uzivatel);
    }

    public long countByUzivatel(Uzivatel uzivatel) {
        return zadostiKlicRepository.countByUzivatel(uzivatel);
    }

}
