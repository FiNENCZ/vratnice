package cz.dp.vratnice.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.dp.share.annotation.TransactionalWrite;
import cz.dp.share.base.Utils;
import cz.dp.share.exceptions.BaseException;
import cz.dp.vratnice.entity.Poschodi;
import cz.dp.vratnice.repository.PoschodiRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class PoschodiService {

    @Autowired
    private PoschodiRepository poschodiRepository;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Získává seznam poschodí na základě ID budovy a stavu aktivity.
     *
     * @param idBudova ID budovy, pro kterou se mají získat poschodí.
     * @param aktivita Stav aktivity.
     * @return Seznam objektů {@link Poschodi} odpovídající zadaným parametrům.
     */
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

    /**
     * Získává detail poschodí na základě jeho ID.
     *
     * @param idPoschodi ID poschodí, jehož detail se má získat.
     * @return Objekt {@link Poschodi} odpovídající zadanému ID
     */
    public Poschodi getDetail(String idPoschodi) {
        return poschodiRepository.getDetail(idPoschodi);
    }

    /**
     * Ukládá objekt poschodí do databáze.
     *
     * @param poschodi Objekt {@link Poschodi}, který se má uložit.
     * @return Uložený objekt {@link Poschodi} s aktualizovanými informacemi.
     * @throws BaseException Pokud dojde k chybě při validaci nebo ukládání objektu.
     */
    @TransactionalWrite
    public Poschodi save(Poschodi poschodi) throws BaseException {

        poschodi.setCasZmn(Utils.getCasZmn());
        poschodi.setZmenuProvedl(Utils.getZmenuProv());

        Utils.validate(poschodi);

        poschodi = poschodiRepository.save(poschodi);

        return poschodi;
    }

    /**
     * Odstraňuje záznam poschodí z databáze.
     *
     * @param idPoschodi Identifikátor {@link Poschodi}, který se má odstranit.
     * @throws EpoRequiredFieldException Pokud je identifikátor prázdný nebo
     *                                   neplatný.
     */
    @TransactionalWrite
    public void odstranit(String idPoschodi) {
        poschodiRepository.zmenaAktivity(idPoschodi, false, Utils.getCasZmn(), Utils.getZmenuProv());
    }

    /**
     * Obnovuje záznam poschodí v databázi.
     *
     * @param idPoschodi Identifikátor {@link Poschodi}, který se má obnovit.
     * @throws EpoRequiredFieldException Pokud je identifikátor prázdný nebo
     *                                   neplatný.
     */
    @TransactionalWrite
    public void obnovit(String idPoschodi) {
        poschodiRepository.zmenaAktivity(idPoschodi, true, Utils.getCasZmn(), Utils.getZmenuProv());
    }
}
