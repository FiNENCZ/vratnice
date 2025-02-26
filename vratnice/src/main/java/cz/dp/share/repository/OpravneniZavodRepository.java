package cz.dp.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.dp.share.entity.OpravneniZavod;
import cz.dp.share.entity.Zavod;

public interface OpravneniZavodRepository extends JpaRepository<OpravneniZavod, OpravneniZavod> {
    @Query("select z from Zavod z where z.idZavod in (select v.idZavod from OpravneniZavod v where v.idOpravneni = :idOpravneni) order by z.nazev ASC")
    List<Zavod> listZavod(String idOpravneni);

    @Query("select count(v) from OpravneniZavod v where v.idOpravneni = :idOpravneni and v.idZavod = :idZavod")
    Integer exists(String idOpravneni, String idZavod);

}
