package cz.diamo.share.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.entity.Lokalita;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.share.repository.LokalitaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class LokalitaServices {

    @Autowired
    private LokalitaRepository lokalitaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Lokalita getDetail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

    public Lokalita getDetailByIdExterni(String idExterni) {
        return lokalitaRepository.getDetailByIdExterni(idExterni);
    }

    public List<Lokalita> getList(String idZavod, Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Lokalita s");
        queryString.append(" join fetch s.zavod zavod");
        queryString.append(" where 1 = 1");

        if (StringUtils.isNotBlank(idZavod))
            queryString.append(" and zavod.idZavod = :idZavod");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
            
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (StringUtils.isNotBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);
        
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        @SuppressWarnings("unchecked")
        List<Lokalita> list = vysledek.getResultList();
        
        return list;
    }

    public Lokalita detail(String idLokalita) {
        return lokalitaRepository.getDetail(idLokalita);
    }

    @TransactionalWrite
    public Lokalita save(Lokalita lokalita) throws BaseException {

        lokalita.setCasZmn(Utils.getCasZmn());
        lokalita.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(lokalita);

        lokalita = lokalitaRepository.save(lokalita);

        return lokalita;
    }

    /**
     * Odstranění zázmamu
     * 
     * @param idLokalita
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void odstranit(String idLokalita) {
        lokalitaRepository.zmenaAktivity(idLokalita, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Obnovení záznamu
     * 
     * @param idLokalita
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void obnovit(String idLokalita) {
        lokalitaRepository.zmenaAktivity(idLokalita, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }
}
