package cz.dp.vratnice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cz.dp.vratnice.entity.HistorieKlicAkce;
import cz.dp.vratnice.enums.HistorieKlicAkceEnum;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service
public class HistorieKlicAkceService {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Vrátí seznam akcí historie klíče
     *
     * @param uzivatelskeAkce
     * @return Seznam {@link HistorieKlicAkce} objektů. Pokud nejsou nalezeny žádné
     *         akce, vrátí se prázdný seznam.
     */
    public List<HistorieKlicAkce> getList(Boolean uzivatelskeAkce) {
        StringBuilder queryString = new StringBuilder();

        queryString.append("select s from HistorieKlicAkce s");
        queryString.append(" where 1 = 1");

        if (uzivatelskeAkce != null)
            if (uzivatelskeAkce)
                queryString.append(
                        " and s.nazevResx <> :vytvoren and s.nazevResx <> :obnoven and s.nazevResx <> :blokovan and s.nazevResx <> :odstranen and s.nazevResx <> :upraven");

        Query vysledek = entityManager.createQuery(queryString.toString());
        if (uzivatelskeAkce != null) {
            if (uzivatelskeAkce) {
                vysledek.setParameter("vytvoren", HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_VYTVOREN.toString());
                vysledek.setParameter("obnoven", HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_OBNOVEN.toString());
                vysledek.setParameter("blokovan", HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_BLOKOVAN.toString());
                vysledek.setParameter("odstranen", HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_ODSTRANEN.toString());
                vysledek.setParameter("upraven", HistorieKlicAkceEnum.HISTORIE_KLIC_AKCE_UPRAVEN.toString());
            }
        }

        @SuppressWarnings("unchecked")
        List<HistorieKlicAkce> list = vysledek.getResultList();
        return list;
    }

}
