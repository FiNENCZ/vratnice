package cz.diamo.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.Uzivatel;
import cz.diamo.vratnice.entity.Klic;
import cz.diamo.vratnice.entity.ZadostKlic;

public interface ZadostKlicRepository extends JpaRepository<ZadostKlic, String> {
    static final String sqlSelect = "select s from ZadostKlic s ";

    @Query(sqlSelect + "where s.idZadostKlic = :idZadostKlic")
    ZadostKlic getDetail(String idZadostKlic);

    @Query(sqlSelect + "where s.zadostStav.idZadostStav = :idZadostStav and s.aktivita = :aktivita")
    List<ZadostKlic> getZadostiByStav(Integer idZadostStav, Boolean aktivita);

    @Query(sqlSelect + "where s.klic = :klic")
    List<ZadostKlic> findByKlic(Klic klic);

    @Query(sqlSelect + "where s.uzivatel = :uzivatel")
    List<ZadostKlic> findByUzivatel(Uzivatel uzivatel);

    @Query("select count(s) from ZadostKlic s where s.uzivatel = :uzivatel")
    long countByUzivatel(Uzivatel uzivatel);
}
