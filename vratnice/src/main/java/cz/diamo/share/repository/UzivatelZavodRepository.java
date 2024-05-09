package cz.diamo.share.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cz.diamo.share.entity.UzivatelZavod;
import cz.diamo.share.entity.Zavod;

public interface UzivatelZavodRepository extends JpaRepository<UzivatelZavod, UzivatelZavod> {

    @Query("select z from Zavod z where z.idZavod in (select v.idZavod from UzivatelZavod v where v.idUzivatel = :idUzivatel) order by z.nazev ASC")
    List<Zavod> listZavod(String idUzivatel);
}
