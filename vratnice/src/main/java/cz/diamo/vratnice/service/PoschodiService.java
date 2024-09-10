package cz.diamo.vratnice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.diamo.share.annotation.TransactionalWrite;
import cz.diamo.share.base.Utils;
import cz.diamo.share.exceptions.BaseException;
import cz.diamo.vratnice.entity.Poschodi;
import cz.diamo.vratnice.repository.PoschodiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class PoschodiService {
    
    @Autowired
    private PoschodiRepository poschodiRepository;

    @PersistenceContext
    private EntityManager entityManager;
   

    public List<Poschodi> getList(String idBudova, Boolean aktivita) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from Poschodi s");
        queryString.append(" left join fetch s.budova bud");
        queryString.append(" where 1 = 1");

        if (StringUtils.isNotBlank(idBudova))
            queryString.append(" and bud.idBudova = :idBudova");

        if (aktivita != null)
            queryString.append(" and s.aktivita = :aktivita");

        Query vysledek = entityManager.createQuery(queryString.toString());

        if (StringUtils.isNotBlank(idBudova))
            vysledek.setParameter("idBudova", idBudova);

        if (aktivita != null)
            vysledek.setParameter("aktivita", aktivita);
            
        @SuppressWarnings("unchecked")
        List<Poschodi> list = vysledek.getResultList();
        return list;
    }

    public Poschodi getDetail(String idPoschodi) {
        return poschodiRepository.getDetail(idPoschodi);
    }

    @TransactionalWrite
    public Poschodi save(Poschodi poschodi) throws BaseException {

        poschodi.setCasZmn(Utils.getCasZmn());
        poschodi.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(poschodi);

        poschodi = poschodiRepository.save(poschodi);

        return poschodi;
    }

    /**
     * Odstranění zázmamu
     * 
     * @param idPoschodi
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void odstranit(String idPoschodi) {
        poschodiRepository.zmenaAktivity(idPoschodi, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Obnovení záznamu
     * 
     * @param idPoschodi
     * @throws EpoRequiredFieldException
     */
    @TransactionalWrite
    public void obnovit(String idPoschodi) {
        poschodiRepository.zmenaAktivity(idPoschodi, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }
}
