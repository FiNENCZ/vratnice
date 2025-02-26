package cz.dp.vratnice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.Uzivatel;
import cz.dp.vratnice.entity.Klic;
import cz.dp.vratnice.entity.ZadostKlic;

public interface ZadostKlicRepository extends JpaRepository<ZadostKlic, String> {
    static final String sqlSelect = "select s from ZadostKlic s ";

    @Query(sqlSelect + "where s.idZadostKlic = :idZadostKlic")
    ZadostKlic getDetail(String idZadostKlic);

    @Query(sqlSelect + "where s.klic = :klic")
    List<ZadostKlic> findByKlic(Klic klic);

    @Query("select count(s) from ZadostKlic s where s.uzivatel = :uzivatel")
    long countByUzivatel(Uzivatel uzivatel);
}
