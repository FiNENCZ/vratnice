package cz.dp.share.services;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.entity.Budova;
import cz.dp.share.exceptions.BaseException;
import cz.dp.share.repository.BudovaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class BudovaServices {

    @Autowired
    private BudovaRepository budovaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Budova getDetail(String idBudova) {
        return budovaRepository.getDetail(idBudova);
    }

    public Budova getDetailByIdExterni(String idExterni) {
        return budovaRepository.getDetailByIdExterni(idExterni);
    }

    public List<Budova> getList(String idZavod, String idLokalita, Boolean aktivita) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Budova s");
        queryString.append(" left join fetch s.lokalita lok");
        queryString.append(" left join fetch lok.zavod zav");
        queryString.append(" where 1 = 1");

        if (StringUtils.isNotBlank(idZavod))
            queryString.append(" and zav.idZavod = :idZavod");

        if (StringUtils.isNotBlank(idLokalita))
            queryString.append(" and lok.idLokalita = :idLokalita");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");
 
        Query vysledek = entityManager.createQuery(queryString.toString());

        if (StringUtils.isNotBlank(idZavod))
            vysledek.setParameter("idZavod", idZavod);
        
        if (StringUtils.isNotBlank(idLokalita))
            vysledek.setParameter("idLokalita", idLokalita);
        
        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
        
        @SuppressWarnings("unchecked")
        List<Budova> list = vysledek.getResultList();
        
        return list;
    }

    public Budova detail(String idBudova) {
        return budovaRepository.getDetail(idBudova);
    }

    @TransactionalWrite
    public Budova save(Budova budova) throws BaseException {

        budova.setCasZmn(Utils.getCasZmn());
        budova.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(budova);

        budova = budovaRepository.save(budova);

        return budova;
    }

    /**
     * Odstranění zázmamu
     * 
     * @param idBudova
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void odstranit(String idBudova) {
        budovaRepository.zmenaAktivity(idBudova, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Obnovení záznamu
     * 
     * @param idBudova
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void obnovit(String idBudova) {
        budovaRepository.zmenaAktivity(idBudova, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }
}
